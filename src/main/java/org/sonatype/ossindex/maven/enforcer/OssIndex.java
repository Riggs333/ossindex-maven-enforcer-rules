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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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

    private final Marshaller marshaller;

    public OssIndex() {
        baseUrl = url(DEFAULT_URL);
        log.debug("Base URL: {}", baseUrl);

        marshaller = new Marshaller();
    }

    // TODO: may need to cope with limits?

    public Map<PackageRequest,PackageReport> request(final List<PackageRequest> requests) throws Exception {
        log.debug("Requesting {} package-reports", requests.size());

        URL url = new URL(String.format("%s/v2.0/package", baseUrl));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);

        // TODO: bring back User-Agent
        // TODO: consider using Maven http-client framework and/or adapt to proxy settings, etc

        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("Accept", "application/json");

        log.debug("Connecting to: {}", url);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
            writer.write(marshaller.marshal(requests));
        }
        connection.connect();

        // TODO: consider minimal retry logic?

        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            try (InputStream input = connection.getInputStream()) {
                List<PackageReport> reports = marshaller.unmarshal(input);

                // TODO: check if the order of input parameters is directly related to output order
                // TODO: ... if so, should ensure sizes match, if not need to correlate report back to artifact

                Map<PackageRequest,PackageReport> result = new LinkedHashMap<>();
                int i = 0;
                for (PackageRequest request : requests) {
                    result.put(request, reports.get(i++));
                }
                return result;
            }
        }

        throw new RuntimeException("Unexpected response; status: " + status);
    }

    private static URL url(final String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public URL packageUrl(final PackageReport report) {
        return url(String.format("%s/resource/package/%s", baseUrl, report.getId()));
    }

    public URL referenceUrl(final PackageReport.Vulnerability vulnerability) {
        String type;
        if (vulnerability.getCve() == null) {
            type = "vulnerability";
        }
        else {
            type = "cve";
        }
        return url(String.format("%s/resource/%s/%s", baseUrl, type, vulnerability.getId()));
    }
}
