<#ftl auto_esc=false>
<#include "externalDocs.ftlh"/>
<#include "utils.ftlh"/>
<#--
    Macro for a single OpenAPI tag in Asciidoctor format
-->
<#macro tag tag>
_${tag.name}_::
<#if tag.description?? && tag.description?has_content>
[[<@clean_tag tag.name/>]] pass:[${tag.description}]
<#else>
[[<@clean_tag tag.name/>]]
</#if>
<#if tag.externalDocs??>
+
<@externalDocs tag.externalDocs/>
</#if>

</#macro>
