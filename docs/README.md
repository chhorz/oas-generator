---
home: true
heroText: OAS Generator
tagline: Annotation processor based OpenAPI spec file generation
actionText: Get Started →
actionLink: /documentation/installation/
features:
- title: Simple
  details: Use the available annotations in your project that already are present to configure your API
- title: Up-To-Date
  details: The API specification is always up-to-date because the file is generated during the build phase
- title: Performant
  details: No additional overhead while runtime. Everything is generated on build time.
footer: Apache License 2 Licensed | Copyright © 2018-2020 Christian Horz 
---

::: warning
This page is currently _work in progress_.
:::

## About

Goal of the project is the generation of an [OpenAPI](https://www.openapis.org/) specification with a minimal amount of manual tasks.

![OpenAPI](/oas-generator/OpenAPI-Logo.png)

The **OAS Generator** is a java annotation processor working on annotations used to define a REST API in a Java application (Spring-Web or JaxRS).
All information that is required will be get using static code analysis and the related Javadoc comments.
For more details have a look on the [reference documentation](/documentation/reference).

