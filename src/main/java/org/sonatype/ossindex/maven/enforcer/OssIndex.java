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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

// TODO: convert to component

/**
 * Sonatype OSS Index access.
 *
 * @since ???
 */
public class OssIndex {
    private static final Logger log = LoggerFactory.getLogger(OssIndex.class);

    private static final String DEFAULT_URL = "https://ossindex.sonatype.org";

    private final URL baseUrl;

    private final PackageReportMarshaller marshaller;

    public OssIndex() {
        try {
            baseUrl = new URL(DEFAULT_URL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        log.debug("Base URL: {}", baseUrl);

        marshaller = new PackageReportMarshaller();
    }

    public PackageReport request(final Artifact artifact) throws Exception {
        log.debug("Requesting package-report for: {}", artifact);

        // TODO: consider better ways to use existing http-client infrastructure in Maven

        URL url = new URL(String.format("%s/v2.0/package/%s/%s/%s/%s", baseUrl, "maven", artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion()));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);

        connection.addRequestProperty("Accept", "application/json");

        log.debug("Connecting to: {}", url);
        connection.connect();

        // TODO: consider minimal retry logic?

        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            try (InputStream input = connection.getInputStream()) {
                List<PackageReport> results = marshaller.unmarshal(input);
                if (results.isEmpty()) {
                    throw new RuntimeException("Request returned zero results");
                }
                // for format/package/version requests only 1 entry is expected, pluck off the first entry
                return results.get(0);
            }
        }

        throw new RuntimeException("Unexpected response; status: " + status);
    }

    public URL packageUrl(final PackageReport report) {
        String url = String.format("%s/resource/package/%s", baseUrl, report.getId());
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public URL referenceUrl(final PackageReport.Vulnerability vulnerability) {
        String type;
        if (vulnerability.getCve() == null) {
            type = "vulnerability";
        }
        else {
            type = "cve";
        }
        String url = String.format("%s/resource/%s/%s", baseUrl, type, vulnerability.getId());

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
