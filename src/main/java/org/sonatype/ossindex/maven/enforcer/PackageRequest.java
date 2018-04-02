package org.sonatype.ossindex.maven.enforcer;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Package request coordinates.
 *
 * @since ???
 */
public class PackageRequest
{
    public PackageRequest(final String format, final String name, final String version) {
        this.format = format;
        this.name = name;
        this.version = version;
    }

    public PackageRequest(final String format, final String group, final String name, final String version) {
        this.format = format;
        this.group = group;
        this.name = name;
        this.version = version;
    }

    public PackageRequest() {
        // empty
    }

    @SerializedName("pm")
    private String format;

    private String group;

    private String name;

    private String version;

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
