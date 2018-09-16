# Extensibility

The **OAS Generator** provides an _Service Provider Interface (SPI)_ to register other dependencies as post processors.
All of them have to implement two specific interfaces an can perform different actions on the internal domain model afterwards.

Currenty the following post processors are included in this project.
They can be found in the `oas-generator-spi` module.

## AsciidoctorPostProcessor
This post processor creates an `.adoc` file from the domain model.
After the creation the `asciidoctor-maven-plugin` can be used to render the generated file.

``` xml
<dependency>
	<groupId>com.github.chhorz</groupId>
	<artifactId>oas-generator-asciidoctor</artifactId>
	<version>${oas-generator.version}</version>
</dependency>
```
The complete documentation can be found in the corresponding section of the reference documentation ([Link](https://chhorz.github.io/oas-generator/docs/oas-generator.html#_asciidoctorpostprocessor)).

::: tip Hint
If you have created your own post processor and want it listed here, please open a Github issue.
:::
