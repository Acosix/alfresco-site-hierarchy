<#macro renderSite site><#escape x as jsonUtils.encodeJSONString(x)><#compress>
{
    "uri": "api/sites/${site.self.shortName?url("UTF-8")}",
    "hierarchyUri": "acosix/api/sites/${site.self.shortName?url("UTF-8")}/hierarchy",
    <#if site.self.node?exists>
        "nodeUri": "api/node/${site.self.node.storeType}/${site.self.node.storeId}/${site.self.node.id}",
    </#if>
    "shortName": "${site.self.shortName}",
    "sitePreset": "${site.self.sitePreset}",
    "title": "${site.self.title}",
    "description": "${site.self.description}",
    "isPublic": "${site.self.isPublic?string("true", "false")}",
    "visibility": "${site.self.visibility}"
    <#if site.parent??>,
    "parent" : <@renderSite site.parent />
    </#if>
    <#if site.children??>,
        "children" : [
            <#list site.children as child>
                <@renderSite child />
                <#if child_has_next>,</#if>
            </#list>]
    </#if>
}
</#compress></#escape></#macro>