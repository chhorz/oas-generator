<#include "_macros/externalDocs.ftlh"/>
<#include "_macros/server.ftlh"/>
<#include "_macros/tag.ftlh"/>
<#--

-->
= ${openapi.info.title}
<#if openapi.info.contact??>
${openapi.info.contact.name} <${openapi.info.contact.email}>
</#if>
:revnumber: ${openapi.info.version}
:toc:

<#if openapi.info.description??>

${openapi.info.description}
</#if>
<#--
    Servers
-->
<#if openapi.servers??>

== Servers
<#list openapi.servers as s>
<@server s/>
</#list>

</#if>
<#--
    Paths
-->
<#if openapi.paths??>

== Paths
<#list openapi.paths?keys as path>
=== ${path}
</#list>
</#if>
<#--
    Components
-->
<#if openapi.components??>

== Components
<#if openapi.components.securitySchemes??>

=== Security Schemes
[cols="1,1,2", options="header"]
|===
| Scheme | Type | Description
<#list openapi.components.securitySchemes?keys as scheme>
| ${scheme}
<#assign secScheme = openapi.components.securitySchemes[scheme]/>
| ${secScheme.type}
| ${secScheme.description}

</#list>
|===
</#if>
</#if>
<#--
    Tags
-->
<#if openapi.tags??>

== Tags
<#list openapi.tags as t>
<@tag t/>
</#list>
</#if>
<#--
    External Docs
-->
<#if openapi.externalDocs??>

== External documentation
<@externalDocs openapi.externalDocs/>
</#if>
<#--
    Terms of Service
-->
<#if openapi.info.termsOfService??>

== Terms of Service
${openapi.info.termsOfService}
</#if>
<#--
    License
-->
<#if openapi.info.license??>

== License
<#if openapi.info.license.url??>
The service is licensed under ${openapi.info.license.url}[${openapi.info.license.name}].
<#else>
The service is licensed under ${openapi.info.license.name}.
</#if>
</#if>