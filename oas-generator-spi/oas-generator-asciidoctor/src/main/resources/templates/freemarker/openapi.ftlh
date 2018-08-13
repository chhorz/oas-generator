<#include "_macros/externalDocs.ftlh"/>
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

<#if openapi.tags??>
== Tags
<#list openapi.tags as t>
<@tag t/>
</#list>
</#if>

<#if openapi.externalDocs??>
== External documentation
<@externalDocs openapi.externalDocs/>
</#if>

<#if openapi.info.termsOfService??>
== Terms of Service
${openapi.info.termsOfService}
</#if>

<#if openapi.info.license??>
== License
<#if openapi.info.license.url??>
The service is licensed under ${openapi.info.license.url}[${openapi.info.license.name}].
<#else>
The service is licensed under ${openapi.info.license.name}.
</#if>
</#if>