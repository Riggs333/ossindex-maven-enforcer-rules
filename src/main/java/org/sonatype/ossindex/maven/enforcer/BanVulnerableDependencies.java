/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.ossindex.maven.enforcer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.sonatype.ossindex.client.OssIndex;
import org.sonatype.ossindex.client.PackageReport;
import org.sonatype.ossindex.client.PackageRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Enforcer rule to ban vulnerable dependencies.
 *
 * @since ???
 */
public class BanVulnerableDependencies
    extends EnforcerRuleSupport
{
    private ArtifactFilter filter;

    private boolean transitive = true;

    @Nullable
    public ArtifactFilter getFilter() {
        return filter;
    }

    public void setFilter(@Nullable final ArtifactFilter filter) {
        this.filter = filter;
    }

    public boolean isTransitive() {
        return transitive;
    }

    public void setTransitive(final boolean transitive) {
        this.transitive = transitive;
    }

    @Override
    public void execute(@Nonnull final EnforcerRuleHelper helper) throws EnforcerRuleException {
        new Task(helper).run();
    }

    /**
     * Encapsulates state for rule evaluation.
     */
    private class Task
    {
        private final EnforcerRuleHelper helper;

        private final Log log;

        private final MavenSession session;

        private final MavenProject project;

        private final DependencyGraphBuilder graphBuilder;

        private final OssIndex index;

        public Task(final EnforcerRuleHelper helper) {
            this.helper = helper;
            this.log = helper.getLog();
            this.session = lookup(helper, "${session}", MavenSession.class);
            this.project = lookup(helper,"${project}", MavenProject.class);
            this.graphBuilder = lookup(helper, DependencyGraphBuilder.class);
            this.index = lookup(helper, OssIndex.class);
        }

        public void run() throws EnforcerRuleException {
            // skip if maven is in offline mode
            if (session.isOffline()) {
                log.warn("Skipping " + BanVulnerableDependencies.class.getSimpleName() + " evaluation; Offline mode detected");
                return;
            }

            // skip if packaging is pom
            if ("pom".equals(project.getPackaging())) {
                log.debug("Skipping POM module");
                return;
            }

            // determine dependencies
            Set<Artifact> dependencies;
            try {
                dependencies = resolveDependencies();
            } catch (DependencyGraphBuilderException e) {
                throw new RuntimeException("Failed to resolve dependencies", e);
            }

            // skip if project has no dependencies
            if (dependencies.isEmpty()) {
                log.debug("Skipping; no dependencies found");
                return;
            }

            log.info("Checking for vulnerabilities:");

            // generate package requests and map back to artifacts for result handling
            Map<PackageRequest, Artifact> requests = new HashMap<>();
            for (Artifact artifact : dependencies) {
                log.info("  " + artifact);
                PackageRequest request = new PackageRequest("maven", artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
                requests.put(request, artifact);
            }

            Map<Artifact, PackageReport> vulnerableDependencies = new HashMap<>();
            try {
                Map<PackageRequest, PackageReport> reports = index.request(new ArrayList<>(requests.keySet()));
                for (Map.Entry<PackageRequest, PackageReport> entry : reports.entrySet()) {
                    PackageRequest request = entry.getKey();
                    Artifact artifact = requests.get(request);
                    PackageReport report = entry.getValue();

                    // if report contains any vulnerabilities then record artifact mapping
                    if (!report.getVulnerabilities().isEmpty()) {
                        vulnerableDependencies.put(artifact, report);
                    }
                }
            }
            catch (Exception e) {
                log.warn("Failed to fetch package-reports", e);
            }

            // if any vulnerabilities were detected, generate a report and complain
            if (!vulnerableDependencies.isEmpty()) {
                StringBuilder buff = new StringBuilder();
                buff.append("Detected ").append(vulnerableDependencies.size()).append(" vulnerable dependencies:\n");

                // include details about each vulnerable dependency
                for (Map.Entry<Artifact, PackageReport> entry : vulnerableDependencies.entrySet()) {
                    Artifact artifact = entry.getKey();
                    PackageReport report = entry.getValue();

                    // TODO: consider using some ANSI colors here to make things standout a bit more?

                    // describe artifact and link to package information
                    buff.append("  ")
                            .append(artifact).append("; ")
                            .append(index.packageUrl(report))
                            .append("\n");

                    // include terse details about vulnerability and link to more detailed information
                    Iterator<PackageReport.Vulnerability> iter = report.getVulnerabilities().iterator();
                    while (iter.hasNext()) {
                        PackageReport.Vulnerability vulnerability = iter.next();
                        buff.append("    * ")
                                .append(vulnerability.getTitle())
                                .append("; ").append(index.referenceUrl(vulnerability));
                        if (iter.hasNext()) {
                            buff.append("\n");
                        }
                    }
                }

                throw new EnforcerRuleException(buff.toString());
            }
        }

        /**
         * Resolve dependencies to inspect for vulnerabilities.
         */
        private Set<Artifact> resolveDependencies() throws DependencyGraphBuilderException {
            Set<Artifact> result = new HashSet<>();

            DependencyNode node = graphBuilder.buildDependencyGraph(project, filter);
            collectArtifacts(result, node);

            return result;
        }

        /**
         * Collect artifacts from dependency.
         *
         * Optionally including transitive dependencies if {@link #transitive} is {@code true}.
         */
        private void collectArtifacts(final Set<Artifact> artifacts, final DependencyNode node) {
            if (node.getChildren() != null) {
                for (DependencyNode child : node.getChildren()) {
                    artifacts.add(child.getArtifact());

                    if (transitive) {
                        collectArtifacts(artifacts, child);
                    }
                }
            }
        }
    }
}
