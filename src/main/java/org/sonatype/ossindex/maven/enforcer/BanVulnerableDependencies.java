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
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Enforcer rule to ban vulnerable dependencies.
 *
 * @since ???
 */
public class BanVulnerableDependencies
    extends EnforcerRuleSupport
{
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

        private final MavenProject project;

        private final DependencyGraphBuilder graphBuilder;

        private final OssIndex index;

        public Task(final EnforcerRuleHelper helper) {
            this.helper = helper;
            this.log = helper.getLog();
            this.project = lookup(helper,"${project}", MavenProject.class);
            this.graphBuilder = lookup(helper, DependencyGraphBuilder.class);
            this.index = new OssIndex();
        }

        public void run() throws EnforcerRuleException {
            Set<Artifact> dependencies;
            try {
                dependencies = resolveDependencies();
            } catch (DependencyGraphBuilderException e) {
                throw new RuntimeException("Failed to resolve dependencies", e);
            }

            log.info("Checking for vulnerabilities:");
            Map<Artifact, PackageReport> vulnerableDependencies = new HashMap<>();
            for (Artifact artifact : dependencies) {
                log.info("  " + artifact);
                try {
                    // TODO: consider some form of caching to avoid hitting the service for multi-module builds over and over?

                    PackageReport report = index.request(artifact);
                    List<PackageReport.Vulnerability> vulnerabilities = report.getVulnerabilities();
                    if (!vulnerabilities.isEmpty()) {
                        vulnerableDependencies.put(artifact, report);
                    }
                }
                catch (Exception e) {
                    log.warn("Failed to fetch package-report for: " + artifact, e);
                }
            }

            if (!vulnerableDependencies.isEmpty()) {
                StringBuilder buff = new StringBuilder();
                buff.append("Detected ").append(vulnerableDependencies.size()).append(" vulnerable dependencies:\n");
                for (Map.Entry<Artifact, PackageReport> entry : vulnerableDependencies.entrySet()) {
                    Artifact artifact = entry.getKey();
                    PackageReport report = entry.getValue();

                    buff.append("  ")
                            .append(artifact).append("; ")
                            .append(index.packageUrl(report))
                            .append("\n");

                    for (PackageReport.Vulnerability vulnerability : report.getVulnerabilities()) {
                        buff.append("    * ")
                                .append(vulnerability.getTitle())
                                .append("; ").append(index.referenceUrl(vulnerability))
                                .append("\n");
                    }
                }
                throw new EnforcerRuleException(buff.toString());
            }
        }

        private Set<Artifact> resolveDependencies() throws DependencyGraphBuilderException {
            Set<Artifact> result = new HashSet<>();

            DependencyNode node = graphBuilder.buildDependencyGraph(project, null);
            includeChildren(result, node);

            return result;
        }

        private void includeChildren(final Set<Artifact> artifacts, final DependencyNode node) {
            if (node.getChildren() != null) {
                for (DependencyNode child : node.getChildren()) {
                    artifacts.add(child.getArtifact());
                    includeChildren(artifacts, child);
                }
            }
        }
    }
}
