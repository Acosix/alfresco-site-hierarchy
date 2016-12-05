<#escape x as jsonUtils.encodeJSONString(x)><#compress>
<#import "hierarchialSite.json.lib.ftl" as siteLib/>
{
    "site" : <@siteLib.renderSite site />
}
</#compress></#escape>