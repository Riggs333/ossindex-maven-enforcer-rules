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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Marshalling helper.
 *
 * @since ???
 */
public class Marshaller
{
    private final Gson gson;

    public Marshaller() {
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    private static final TypeToken<List<PackageReport>> LIST_PACKAGE_REPORT = new TypeToken<List<PackageReport>>() {};

    public List<PackageReport> unmarshal(final InputStream input) {
        return gson.fromJson(new InputStreamReader(input), LIST_PACKAGE_REPORT.getType());
    }

    public String marshal(final PackageRequest request) {
        return gson.toJson(request);
    }

    private static final TypeToken<List<PackageRequest>> LIST_PACKAGE_REQUEST = new TypeToken<List<PackageRequest>>() {};

    public String marshal(final List<PackageRequest> request) {
        return gson.toJson(request);
    }
}
