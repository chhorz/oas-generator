<#ftl auto_esc=false>
<#include "externalDocs.ftlh"/>
<#--
    Macro for a single OpenAPI tag in Asciidoctor format
-->
<#macro tag tag>
_${tag.name}_::
<#if tag.description?? && tag.description?has_content>
[[${tag.name}]] pass:[${tag.description}]
<#else>
[[${tag.name}]]
</#if>
<#if tag.externalDocs??>
+
<@externalDocs tag.externalDocs/>
</#if>

</#macro>
