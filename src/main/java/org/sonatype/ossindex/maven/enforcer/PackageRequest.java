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

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Package request coordinates.
 *
 * @since ???
 *
 * @see PackageReport
 * @see Marshaller
 */
public class PackageRequest
{
    public PackageRequest(final String format, @Nullable final String group, final String name, final String version) {
        this.format = format;
        this.group = group;
        this.name = name;
        this.version = version;
    }

    public PackageRequest(final String format, final String name, final String version) {
        this(format, null, name, version);
    }

    public PackageRequest() {
        // empty
    }

    @SerializedName("pm")
    private String format;

    @Nullable
    private String group;

    private String name;

    private String version;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Nullable
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageRequest that = (PackageRequest) o;
        return Objects.equals(format, that.format) &&
                Objects.equals(group, that.group) &&
                Objects.equals(name, that.name) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(format, group, name, version);
    }

    @Override
    public String toString() {
        return "PackageRequest{" +
                "format='" + format + '\'' +
                ", group='" + group + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
