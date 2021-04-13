<#ftl auto_esc=false>
<#--
    Macro for an external documentation object
-->
<#macro externalDocs docs>
<#if docs.description??>
${docs.description} (<#if attributeIcons?? && attributeIcons?matches("font")>icon:external-link[]</#if> link:${docs.url}[_Link_])
<#else>
${docs.url}
</#if>
</#macro>
