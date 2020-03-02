# Extensibility

The **OAS Generator** provides an _Service Provider Interface (SPI)_ to register other dependencies as post processors.
These *post processors* will be executed after the generation of the `openapi.json` file.
All of them have to implement two specific java interfaces that perform different actions on the internal domain model afterwards.

Currently the following post processors are included in this project.
They can be found in the `oas-generator-spi` module.

## AsciidoctorPostProcessor
This post processor creates an AsciiDoctor file _(`.adoc`)_ from the OpenAPI domain model.
After the creation the `asciidoctor-maven-plugin` needs to be added to render the generated file into the requested output format.

``` xml
<dependency>
    <groupId>com.github.chhorz</groupId>
    <artifactId>oas-generator-asciidoctor</artifactId>
    <version>${oas-generator.version}</version>
    <scope>provided</scope>
</dependency>
```
The complete documentation can be found in the corresponding section of the reference documentation ([Link](https://chhorz.github.io/oas-generator/docs/oas-generator.html#_asciidoctorpostprocessor)).

::: tip Hint
If you have created your own post processor and want it listed here, please open a Github issue.
:::
