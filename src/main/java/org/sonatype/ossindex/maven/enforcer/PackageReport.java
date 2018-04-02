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

import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// NOTE: this is based on the current v2 REST api, but likely to change

/**
 * Package information gathered from Sonatype OSS Index.
 *
 * @since ???
 *
 * @see PackageRequest
 * @see Marshaller
 */
public class PackageReport
{
    private long id;

    @SerializedName("pm")
    private String format;

    private String group;

    private String name;

    private String version;

    @SerializedName("vulnerability-total")
    private int vulnerabilityTotal;

    @SerializedName("vulnerability-matches")
    private int vulnerabilityMatches;

    /**
     * Vulnerability details.
     */
    public static class Vulnerability
    {
        private long id;

        private URI resource;

        private String title;

        private String description;

        private List<String> versions = new ArrayList<>();

        private List<URL> references = new ArrayList<>();

        private DateTime published;

        private DateTime updated;

        private String cve;

        private Map<String,String> ids = new HashMap<>();

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public URI getResource() {
            return resource;
        }

        public void setResource(URI resource) {
            this.resource = resource;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getVersions() {
            return versions;
        }

        public void setVersions(List<String> versions) {
            this.versions = versions;
        }

        public List<URL> getReferences() {
            return references;
        }

        public void setReferences(List<URL> references) {
            this.references = references;
        }

        public DateTime getPublished() {
            return published;
        }

        public void setPublished(DateTime published) {
            this.published = published;
        }

        public DateTime getUpdated() {
            return updated;
        }

        public void setUpdated(DateTime updated) {
            this.updated = updated;
        }

        public String getCve() {
            return cve;
        }

        public void setCve(String cve) {
            this.cve = cve;
        }

        public Map<String, String> getIds() {
            return ids;
        }

        public void setIds(Map<String, String> ids) {
            this.ids = ids;
        }
    }

    private List<Vulnerability> vulnerabilities = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVulnerabilityTotal() {
        return vulnerabilityTotal;
    }

    public void setVulnerabilityTotal(int vulnerabilityTotal) {
        this.vulnerabilityTotal = vulnerabilityTotal;
    }

    public int getVulnerabilityMatches() {
        return vulnerabilityMatches;
    }

    public void setVulnerabilityMatches(int vulnerabilityMatches) {
        this.vulnerabilityMatches = vulnerabilityMatches;
    }

    public List<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    public void setVulnerabilities(List<Vulnerability> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }
}
