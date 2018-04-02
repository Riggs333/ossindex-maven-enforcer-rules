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

import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link Marshaller}.
 */
public class MarshallerTest
    extends TestSupport
{
    private Marshaller underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new Marshaller();
    }

    @Test
    public void maven_nomatch() throws Exception {
        URL resource = getClass().getResource("maven-no-such-package-0.txt");
        try (InputStream input = resource.openStream()) {
            List<PackageReport> result = underTest.unmarshal(input);
            assertThat(result.size(), is(1));

            PackageReport report = result.get(0);
            assertThat(report, notNullValue(PackageReport.class));
            assertThat(report.getId(), is(0L));
            assertThat(report.getFormat(), is("maven"));
            assertThat(report.getName(), is("no-such-package"));
            assertThat(report.getVersion(), is("0"));
            assertThat(report.getVulnerabilityTotal(), is(0));
            assertThat(report.getVulnerabilityMatches(), is(0));
        }
    }

    @Test
    public void maven_commons_fileupload_match() throws Exception {
        URL resource = getClass().getResource("maven-commons-fileupload-1.3.txt");
        try (InputStream input = resource.openStream()) {
            List<PackageReport> result = underTest.unmarshal(input);
            assertThat(result.size(), is(1));

            PackageReport report = result.get(0);
            assertThat(report, notNullValue(PackageReport.class));
            assertThat(report.getId(), is(7015420058L));
            assertThat(report.getFormat(), is("maven"));
            assertThat(report.getGroup(), is("commons-fileupload"));
            assertThat(report.getName(), is("commons-fileupload"));
            assertThat(report.getVersion(), is("1.3"));

            List<PackageReport.Vulnerability> vulnerabilities = report.getVulnerabilities();
            assertThat(vulnerabilities, not(empty()));
            PackageReport.Vulnerability vuln1 = vulnerabilities.get(0);
            assertThat(vuln1.getIds(), hasEntry("CVE", "CVE-2014-0050"));
        }
    }

    @Test
    public void nuget_jquery_match() throws Exception {
        URL resource = getClass().getResource("nuget-jquery-1.9.0.txt");
        try (InputStream input = resource.openStream()) {
            List<PackageReport> result = underTest.unmarshal(input);
            assertThat(result.size(), is(1));

            PackageReport report = result.get(0);
            assertThat(report, notNullValue(PackageReport.class));
            assertThat(report.getId(), is(8396450687L));
            assertThat(report.getFormat(), is("nuget"));
            assertThat(report.getName(), is("jQuery"));
            assertThat(report.getVersion(), is("1.9.0"));
            assertThat(report.getVulnerabilityTotal(), is(7));
            assertThat(report.getVulnerabilityMatches(), is(2));

            List<PackageReport.Vulnerability> vulnerabilities = report.getVulnerabilities();
            assertThat(vulnerabilities.size(), is(2));

            PackageReport.Vulnerability vuln1 = vulnerabilities.get(0);
            assertThat(vuln1, notNullValue(PackageReport.Vulnerability.class));
            assertThat(vuln1.getId(), is(8399962417L));
            assertThat(vuln1.getResource(), is(new URI("https://github.com/jquery/jquery/issues/2432")));
            assertThat(vuln1.getTitle(), is("Cross Site Scripting (XSS)"));
            assertThat(vuln1.getVersions(), notNullValue());
            assertThat(vuln1.getVersions().size(), is(1));
            assertThat(vuln1.getReferences(), notNullValue());
            assertThat(vuln1.getReferences().size(), is(5));
            assertThat(vuln1.getReferences(), hasItem(new URL("https://cwe.mitre.org/data/definitions/79.html")));
            assertThat(vuln1.getPublished(), is(new Instant(1470469775500L)));
            assertThat(vuln1.getUpdated(), is(new Instant(1490153875967L)));
        }
    }

    @Test
    public void marshal_request() throws Exception {
        String result = underTest.marshal(new PackageRequest("maven", "foo", "1.0"));
        log(result);
        assertThat(result, is("{\"pm\":\"maven\",\"name\":\"foo\",\"version\":\"1.0\"}"));
    }

    @Test
    public void marshal_requestWithGroup() throws Exception {
        String result = underTest.marshal(new PackageRequest("maven", "foo", "bar", "1.0"));
        log(result);
        assertThat(result, is("{\"pm\":\"maven\",\"group\":\"foo\",\"name\":\"bar\",\"version\":\"1.0\"}"));
    }

    @Test
    public void marshal_requestWithGroupId() throws Exception {
        String result = underTest.marshal(new PackageRequest("maven", "foo", "bar", "1.0"));
        log(result);
        assertThat(result, is("{\"pm\":\"maven\",\"group\":\"foo\",\"name\":\"bar\",\"version\":\"1.0\"}"));
    }

    @Test
    public void marshall_batchRequest() throws Exception {
        List<PackageRequest> requests = new ArrayList<>();
        requests.add(new PackageRequest("maven", "foo", "bar", "1.0"));
        requests.add(new PackageRequest("nuget", "abc", "1.2.3"));
        String result = underTest.marshal(requests);
        log(result);
        assertThat(result, is("[{\"pm\":\"maven\",\"group\":\"foo\",\"name\":\"bar\",\"version\":\"1.0\"},{\"pm\":\"nuget\",\"name\":\"abc\",\"version\":\"1.2.3\"}]"));
    }
}
