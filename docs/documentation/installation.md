# Installation Guide

Java annotation processing is a concept from the Java language.
There are multiple ways to integrate the **OAS Generator** into your project.

::: tip Hint
The following examples use the `oas-generator-spring-web`.
Each kind of installation also works with the `oas-generator-jaxrs` or `oas-generator-schema` module.
:::

## Apache Maven

### Include via dependency
The simpliest way to integrate the **OAS Generator** is to add the following dependency:
``` xml
<dependency>
    <groupId>com.github.chhorz</groupId>
    <artifactId>oas-generator-spring-web</artifactId>
    <version>${oas-generator.version}</version>
</dependency>
```

### Using compiler-plugin
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

It is not possible to mix this way of annotation processor integration with the dependency based one.


## Other
_Others will be added soon._
