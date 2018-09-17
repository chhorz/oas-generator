<#ftl auto_esc=false>
<#include "_macros/externalDocs.ftlh"/>
<#include "_macros/server.ftlh"/>
<#include "_macros/tag.ftlh"/>
<#include "_macros/utils.ftlh"/>
<#--

-->
= ${openapi.info.title}
<#if openapi.info.contact??>
${openapi.info.contact.name} <${openapi.info.contact.email}>
</#if>
:revnumber: ${openapi.info.version}
:icons: font
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
<#if openapi.paths?size gt 0>

== Paths
<#list openapi.paths as path, pathItem>

=== ${path}
<#if pathItem.description??>${pathItem.description}</#if><#t>
<#if pathItem.get??>
<#assign get = pathItem.get/>
<#if get.operationId??>[[<@clean_operationid get.operationId/>]]</#if>
<#if get.deprecated>
[WARNING]
This operation is deprecated.

</#if>
<#if get.tags??>icon:tag[] <#list get.tags as tag><<${tag},_${tag}_>><#sep>, </#list> +</#if>
<#if get.summary??>${get.summary} +</#if>
<#if get.description??>${get.description}</#if>
</#if>

<#if get.parameterObjects??>
[cols="1,1,1,1,1", options="header"]
|===
| Type | Name | Required | Schema | Description

<#list get.parameterObjects as parameter>
| ${parameter.in.openApiValue}
| ${parameter.name}
| ${parameter.required?c}
| <@asciidoc_schema parameter.schema/>
| <#if parameter.description??>pass:[<@clean parameter.description/>]</#if>
</#list>

|===

</#if>
<#if get.security??>
<#list get.security as security>
icon:lock[] <#list security?keys as secKey><<${secKey},_${secKey}_>><#sep>, </#list>
</#list>
</#if>

</#list>

</#if>
</#if>
<#--
    Components
-->
<#if openapi.components??>

== Components
<#if openapi.components.schemas??>
=== Schemas
<#list openapi.components.schemas as schemaName, schema>
[[components_schemas_${schemaName}]]
==== ${schemaName}
<#if schema.deprecated>
[WARNING]
This schema is deprecated.

</#if>
<#if schema.description??>${schema.description}</#if>
<#if schema.properties??>
[cols="1,1,2", options="header"]
|===
| Name | Type | Description

<#list schema.properties as propertyName, property>
| ${propertyName}
| <@asciidoc_schema property/>
| <#if property.description??>pass:[<@clean property.description/>]</#if>

</#list>
|===

</#if>
</#list>

</#if>
<#if openapi.components.securitySchemes??>

=== Security Schemes
[cols="1,1,2", options="header"]
|===
| Scheme | Type | Description

<#list openapi.components.securitySchemes as schemeName, scheme>
| [[${schemeName}]] ${schemeName}
| ${scheme.type}
| pass:[<@clean scheme.description/>]

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