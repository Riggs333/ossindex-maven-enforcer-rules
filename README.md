# Sonatype OSS Index - Maven Enforcer Rules

Adds [maven-enforcer-plugin][2] rules to integrate security information from [Sonatype OSS Index][1] into Maven builds.

## Features

* Ban vulnerable dependencies

## Usage

Configure an execution of the `maven-enforcer-plugin` and configure `ossindex-maven-enforcer-rules` dependency and rule:

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-enforcer-plugin</artifactId>
        <version>3.0.0-M1</version>
        <dependencies>
            <dependency>
                <groupId>org.sonatype.ossindex</groupId>
                <artifactId>ossindex-maven-enforcer-rules</artifactId>
                <version>1-SNAPSHOT</version>
            </dependency>
        </dependencies>
        <executions>
            <execution>
                <phase>validate</phase>
                <goals>
                    <goal>enforce</goal>
                </goals>
                <configuration>
                    <rules>
                        <banVunerableDependencies implementation="org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies"/>
                    </rules>
                </configuration>
            </execution>
        </executions>
    </plugin>

[1]: https://ossindex.sonatype.org
[2]: https://maven.apache.org/enforcer/maven-enforcer-plugin