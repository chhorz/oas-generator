<#ftl auto_esc=false>
<#include "utils.ftlh"/>
<#--
    Macro for the table of a single operation
-->
<#macro pathTable path method operation>
<#if operation.operationId??>[[<@clean_operationid operation.operationId/>]]</#if>
<@section title=method level=4/>

<#if operation.deprecated>
[WARNING]
This operation is deprecated.

</#if>
[cols="1,1,1,1,2"]
|===
^h| ${method}
4+h| ${path}

<#if operation.tags??>
h| Tags
4+| <#list operation.tags as tag>icon:tag[] <<${tag},${tag}>><#sep>, </#list>

</#if>
<#if operation.summary??>
h| Summary
4+|<#if operation.summary??> pass:[<@clean operation.summary/>]</#if>

</#if>
<#if operation.description??>
h| Description
4+|<#if operation.description??> pass:[<@clean operation.description/>]</#if>

</#if>
<#if operation.parameterObjects?? && operation.parameterObjects?has_content>
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
|<#if parameter.description??> pass:[<@clean parameter.description/>]</#if>

</#list>
</#if>
<#if operation.requestBodyReference??>
5+h| Request Body

e|Type
4+a| <<<@clean_ref operation.requestBodyReference.$ref/>,<@clean_ref_name operation.requestBodyReference.$ref/>>>

</#if>
<#if operation.responses??>
5+h| Responses

e| Status
4+e| Response

<#list operation.responses as status, response>
^| ${status}
4+a|<#if response.description?? && response.description?has_content> pass:[<@clean response.description/>]<#else>${response.description}</#if>
<#if response.content??>

[cols="2,1"]
!===
e! MediaType
e! Schema

<#list response.content as mediaType, mediaTypeSchema>
m! pass:[${mediaType}]
<#if mediaTypeSchema.schema??>
! <@asciidoc_schema mediaTypeSchema.schema/>
<#else>
! No schema set.
</#if>

</#list>
!===
</#if>
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
