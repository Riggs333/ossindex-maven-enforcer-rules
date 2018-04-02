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

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.joda.time.Instant;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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
                .registerTypeAdapter(Instant.class, new InstantAdapter())
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

    /**
     * Joda-time {@link Instant} adapter.
     */
    private static class InstantAdapter
        implements JsonDeserializer<Instant>, JsonSerializer<Instant>
    {
        @Override
        public Instant deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
            return new Instant(element.getAsLong());
        }

        @Override
        public JsonElement serialize(final Instant value, final Type type, final JsonSerializationContext context) {
            return new JsonPrimitive(value.getMillis());
        }
    }
}
