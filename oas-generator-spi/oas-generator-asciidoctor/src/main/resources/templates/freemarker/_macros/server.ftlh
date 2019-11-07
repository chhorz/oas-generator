<#ftl auto_esc=false>
<#--

-->
<#macro server server>
* ${server.url}
+
<#if server.description??>
${server.description}
</#if>
<#if server.variables??>
+
<#list server.variables as name, variable>
${name}::
Default: `${variable.defaultValue}` <#if variable.enumValue??>(Allowed: <#list variable.enumValue as value>`${value}`<#sep>, </#list>)</#if>
<#if variable.description??>
+
${variable.description}
</#if>
</#list>
</#if>
</#macro>
