<#escape x as jsonUtils.encodeJSONString(x)><#compress>
<#import "hierarchialSite.json.lib.ftl" as siteLib/>
{
    "sites" : [
        <#list sites as site>
            <@siteLib.renderSite site />
            <#if site_has_next>,</#if>
        </#list>]
}
</#compress></#escape>