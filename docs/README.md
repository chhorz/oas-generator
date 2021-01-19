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
  details: No additional overhead while runtime. Generation occurs while build phase.
footer: Apache License 2 Licensed | Copyright © 2018-2021 Christian Horz 
---

## About

Goal of the project is the generation of an [OpenAPI](https://www.openapis.org/) specification with a minimal amount of manual tasks.
It is configured within a few minutes and produces a first output file.
The configuration and documentation of a complete REST API lasts some time.

The **OAS Generator** is a java annotation processor working on annotations used to define a REST API in a Java application (Spring-Web or JaxRS).
All information that is required will be red using static code analysis and related Javadoc comments.
For more details have a look on the [reference documentation](/documentation/reference).

