<#ftl auto_esc=false>
<#--

-->
<#macro externalDocs docs>
<#if docs.description??>
${docs.description} (icon:external-link[] link:${docs.url}[_Link_])
<#else>
${docs.url}
</#if>
</#macro>
