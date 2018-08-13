<#include "externalDocs.ftlh"/>
<#--

-->
<#macro tag tag>
${tag.name}::
<#if tag.description??>
${tag.description}
</#if>
<#if tag.externalDocs??>
<@externalDocs tag.externalDocs/>
</#if>
+

</#macro>