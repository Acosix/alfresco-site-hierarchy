<#-- 
 * Copyright 2016 Acosix GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
  -->

<#macro renderSite site><#escape x as jsonUtils.encodeJSONString(x)><#compress>
{
    "uri": "api/sites/${site.self.shortName?url("UTF-8")}",
    "hierarchyUri": "acosix/api/sites/${site.self.shortName?url("UTF-8")}/hierarchy",
    <#if site.self.node?exists>
        "nodeRef" : "${site.self.node.storeType}://${site.self.node.storeId}/${site.self.node.id}",
        "nodeUri": "api/node/${site.self.node.storeType}/${site.self.node.storeId}/${site.self.node.id}",
        "tagScope": "api/tagscopes/${site.self.node.storeType}/${site.self.node.storeId}/${site.self.node.id}",
    </#if>
    "shortName": "${site.self.shortName}",
    "sitePreset": "${site.self.sitePreset}",
    "title": "${site.self.title}",
    "description": "${site.self.description}",
    "isPublic": "${site.self.isPublic?string("true", "false")}",
    "visibility": "${site.self.visibility}",
    "autoMembershipMode": "${site.self.node.properties["aco6sh:autoMembershipMode"]!"none"}",
    "showInHierarchyMode": "${site.self.node.properties["aco6sh:showInHierarchyMode"]!"never"}"
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