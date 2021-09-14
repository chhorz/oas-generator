# Installation Guide
[[toc]]

Java annotation processing is a concept from the Java language.
There are multiple ways to integrate the **OAS Generator** into your project.
The complete installation guide can be found in the [reference documentation](https://chhorz.github.io/oas-generator/docs/latest/oas-generator.html#_installation).

## Dependency 

::: tip Hint
The following examples use the `oas-generator-spring-web`.
The installation of the `oas-generator-jaxrs` or `oas-generator-schema` module follows the same steps.
:::

### Apache Maven

#### Include via dependency
The simplest way to integrate the **OAS Generator** is to add the following dependency:
``` xml
<dependency>
    <groupId>com.github.chhorz</groupId>
    <artifactId>oas-generator-spring-web</artifactId>
    <version>${oas-generator.version}</version>
    <scope>provided</scope>
</dependency>
```

#### Maven Compiler Plugin
Another way to include an annotation processor is the [maven-compiler-plugin](https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html):

``` xml{5,13}
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths> <!--1-->
            <path>
                <groupId>com.github.chhorz</groupId>
                <artifactId>oas-generator-spring-web</artifactId>
                <version>${oas-generator.version}</version>
            </path>
            <!-- ... more ... -->
        </annotationProcessorPaths>
        <annotationProcessors> <!--2-->
            <annotationProcessor>com.github.chhorz.openapi.spring.SpringWebOpenApiProcessor</annotationProcessor>
            <!-- ... more ... -->
        </annotationProcessors>
    </configuration>
</plugin>
```

1. Plugin documentation: [annotationProcessorPaths](https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html#annotationProcessorPaths)
2. Plugin documentation: [annotationProcessors](https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html#annotationProcessors)

It is not possible to mix this way of annotation processor integration with the dependency based one shown above.

#### Kotlin Maven Plugin
The plugin documentation for Kotlin Maven projects and annotation processors using `kapt` can be found [here](https://kotlinlang.org/docs/kapt.html#using-in-maven).
For the following use case the `oas-generator-*` was added as dependency.

::: warning
Resolution of the `oas-generator.yml` property file is broken for versions <= 0.2.2.
:::

```xml
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>kapt</id>
            <goals>
                <goal>kapt</goal> <!--1-->
            </goals>
        </execution>
    </executions>
    <configuration>
        <!-- ... -->
        <annotationProcessorArgs>
            <arg>propertiesPath=${project.basedir}/src/main/resources/oas-generator.yml</arg> <!--2-->
            <arg>version=${project.version}</arg>
        </annotationProcessorArgs>
    </configuration>
    <!-- ... -->
</plugin>
```

1. Annotation processing for Kotlin is provided by `kapt`
2. Definition of the default properties file `src/main/resources/oas-generator.yml` does not work for Kotlin projects.
The full path to the properties file *must* be specified.

### Gradle
_Gradle installation steps may be added soon._

### Others
_Others options may be added soon._

## Configuration
Beside the configuration of the dependencies mentioned above, some additional configuration need to be done.
The complete list of possible properties can be found at the corresponding section of the [reference documentation](https://chhorz.github.io/oas-generator/docs/latest/oas-generator.html#_configuration).
The project serves a [custom _JSON Schema_](https://chhorz.github.io/oas-generator/schema/oas-generator.schema.json) for the configuration file, that can be included in an IDE to gain auto-completion.
