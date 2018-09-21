<#ftl auto_esc=false>
<#--

-->
<#macro pathTable operation>
<#if operation.operationId??>[[<@clean_operationid operation.operationId/>]]</#if>
<#if operation.deprecated>
[WARNING]
This operation is deprecated.

</#if>

[cols="1,1,1,1,1"]
|===
<#if operation.tags??>
h| Tags
4+| <#list operation.tags as tag>icon:tag[] <<${tag},${tag}>><#sep>, </#list>

</#if>
<#if operation.summary??>
h| Summary
4+| ${operation.summary}

</#if>
<#if operation.description??>
h| Description
4+| ${operation.description}

</#if>
<#if operation.parameterObjects??>
5+h| Parameters

e| Type
e| Name
e| Required
e| Schema
e| Description

<#list operation.parameterObjects as parameter>
| ${parameter.in.openApiValue}
| ${parameter.name}
| ${parameter.required?c}
m| <@asciidoc_schema parameter.schema/>
| <#if parameter.description??>pass:[<@clean parameter.description/>]</#if>

</#list>
</#if>
<#if operation.responses??>
5+h| Responses

e| Status
4+e| Response

<#list operation.responses as status, response>
^| ${status}
4+| ${response.description}

<#else>
5+e| No responses.
</#list>

</#if>
<#if operation.security??>
.${operation.security?size}+h| Security
<#list operation.security as security>
4+| <#list security?keys as secKey>icon:lock[] <<${secKey},_${secKey}_>><#sep>, </#list>

</#list>

</#if>
|===

</#macro>