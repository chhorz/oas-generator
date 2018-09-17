<#ftl auto_esc=false>
<#--

-->
<#macro externalDocs docs>
<#if docs.description??>
${docs.description} ${docs.url}[Link]
<#else>
${docs.url}
</#if>
</#macro>