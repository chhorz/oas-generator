<#ftl auto_esc=false>
<#--

    Macro for an components object

-->
<#macro components components>
<@schemas components.schemas/>
<@requestBodies components.requestBodies/>
<@securitySchemes components.securitySchemes/>
</#macro>
<#--

    Macro for schema components

-->
<#macro schemas schemas>
<#if schemas??>
<@section title="Schemas" level=3/>

<#list schemas as schemaName, schema>
[[components_schemas_${schemaName}]]
<@section title=schemaName level=4/>

<#if schema.deprecated>
[WARNING]
This schema is deprecated.

</#if>
<#if schema.description?? && schema.description?has_content>pass:[<@clean schema.description/>]</#if>

<#if schema.properties??>
[cols="1,2,3", options="header<#if schema.required??>,footer</#if>"]
|===
| Name | Type | Description

<#list schema.properties as propertyName, property>
| <#if schema.required?? && schema.required?seq_contains(propertyName)>*${propertyName}*<#else>${propertyName}</#if>
m| <@asciidoc_schema property/>
a|<#if property.description?? && property.description?has_content> pass:[<@clean property.description/>]</#if>
<#if property.minimum?? || property.maximum?? || property.pattern??>

<#if property.minimum??>* **Minimum:** ${property.minimum}</#if>
<#if property.maximum??>* **Maximum:** ${property.maximum}</#if>
<#if property.pattern??>* **Pattern:** ${property.pattern}</#if>
</#if>

</#list>
<#if schema.required??>3+| _**Bold** property names mark required fields._</#if>
|===

</#if>
</#list>

</#if>
</#macro>
<#--

    Macro for response body components

-->
<#macro requestBodies requestBodies>
<#if requestBodies??>

<@section title="Request Bodies" level=3/>

<#list requestBodies as requestBodyName, requestBody>
[[components_requestBodies_${requestBodyName}]]
<@section title=requestBodyName level=4/>

<#if requestBody.required>*REQUIRED*</#if> <#if requestBody.description?? && requestBody.description?has_content>pass:[${requestBody.description}]</#if>

<#if requestBody.content??>
[cols="2,1", options="header"]
|===
| Type | Schema

<#list requestBody.content as type, mediatype>
| ${type}
m| <@asciidoc_mediatype mediatype/>

</#list>
|===

</#if>
</#list>

</#if>
</#macro>
<#--

    Macro for security scheme components

-->
<#macro securitySchemes securitySchemes>
<#if securitySchemes??>

<@section title="Security Schemes" level=3/>

[cols="1,1,2", options="header"]
|===
| Scheme | Type | Description

<#list securitySchemes as schemeName, scheme>
| [[${schemeName}]] ${schemeName}
| ${scheme.type}
|<#if scheme.description?? && scheme.description?has_content> pass:[<@clean scheme.description/>]</#if>

</#list>
|===
</#if>
</#macro>
