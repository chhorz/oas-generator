<#ftl auto_esc=false>
<#--

#/components/schemas/ArticleResource -> components_schemas_ArticleResource

-->
<#macro clean_tag tag>${tag?replace(" ", "_")}</#macro>
<#--

#/components/schemas/ArticleResource -> components_schemas_ArticleResource

-->
<#macro clean_ref ref>${ref?replace("/", "_")[2..]}</#macro>
<#--

#/components/schemas/ArticleResource -> ArticleResource

-->
<#macro clean_ref_name ref>
<#compress>
<#assign r = ref?replace("/", "_")[2..]?last_index_of("_") + 3>
${ref?replace("/", "_")[r..]}
</#compress>
</#macro>
<#--

ArticleController#getArticles -> ArticleController_getArticles

-->
<#macro clean_operationid oid>${oid?replace("#", "_")}</#macro>
<#--

Map a Schema to an asciidoc string or reference

-->
<#macro asciidoc_schema schema>
<#compress>
<#if schema.$ref??>
<<<@clean_ref schema.$ref/>>>
<#else>
<#if schema.type??>
<#if schema.type.openApiValue == "array">
<#if schema.items.$ref??>
<<<@clean_ref schema.items.$ref/>,<@clean_ref_name schema.items.$ref/>[]>>
<#else>
${schema.items.type.openApiValue}[]
</#if>
<#elseif schema.type.openApiValue = "enum">
${schema.type.openApiValue} (<#list schema.enumValues as enumValue>${enumValue}<#sep>, </#list>)
<#elseif schema.type.openApiValue = "string" && schema.enumValues??>
${schema.type.openApiValue} (<#list schema.enumValues as enumValue>${enumValue}<#sep>, </#list>)
<#else>
${schema.type.openApiValue} <#if schema.format??>(${schema.format.openApiValue})</#if>
</#if>
</#if>
</#if>
</#compress>
</#macro>
<#--

Map a MediaType to an asciidoc formatted element

-->
<#macro asciidoc_mediatype mediatype>
<#compress>
<#if mediatype.schema??>
<@asciidoc_schema mediatype.schema/>
</#if>
</#compress>
</#macro>
<#--

Replace special characters within asciidoc

-->
<#macro clean content>
<#compress>
${content?replace('|', '&#124;')}
</#compress>
</#macro>
<#--

Create section title with correct intend

-->
<#macro section title level>
<#if standaloneFile>
<#list 0..<level as i>=</#list> ${title}
<#else>
<#list -1..<level as i>=</#list> ${title}
</#if>
</#macro>
