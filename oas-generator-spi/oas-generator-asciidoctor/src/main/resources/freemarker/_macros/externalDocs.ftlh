<#ftl auto_esc=false>
<#--
    Macro for an external documentation object
-->
<#macro externalDocs docs>
<#if docs.description??>
${docs.description} (icon:external-link[] link:${docs.url}[_Link_])
<#else>
${docs.url}
</#if>
</#macro>
