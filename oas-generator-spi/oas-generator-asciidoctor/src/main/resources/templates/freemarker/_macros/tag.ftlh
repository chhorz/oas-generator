<#include "externalDocs.ftlh"/>
<#--

-->
<#macro tag tag>
_${tag.name}_::
<#if tag.description??>
[[${tag.name}]] ${tag.description}
<#else>
[[${tag.name}]]
</#if>
<#if tag.externalDocs??>
<@externalDocs tag.externalDocs/>
</#if>

</#macro>