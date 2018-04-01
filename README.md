# Sonatype OSS Index - Maven Enforcer Rules

## Features

* Ban vulnerable dependencies

## Usage

To use configure an execution of the `maven-enforcer-plugin` and configure `ossindex-maven-enforcer-rules` dependency and rule:

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
                <id>checks</id>
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
