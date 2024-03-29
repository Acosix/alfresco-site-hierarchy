<#-- 
 * Copyright 2016 - 2021 Acosix GmbH
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
<@markup id="hierarchySiteData" target="siteCustomJSONProperties" action="after"><#escape x as jsonUtils.encodeJSONString(x)><#compress>
    <#-- we use the namespace prefix to avoid conflicts with other extensions / data -->
    <#if parentSite??>
    "aco6sh_parentSite" : {
        "shortName" : "${parentSite.shortName}",
        "title" : "${parentSite.title}",
        "description" : "${parentSite.description}"
        <#if parentSite.node?exists>,
            "nodeRef" : "${parentSite.node.storeType}://${parentSite.node.storeId}/${parentSite.node.id}"
        </#if>
    },
    </#if>
    <#if site.node.properties["aco6sh:autoMembershipMode"]??>
        "aco6sh_autoMembershipMode": "${site.node.properties["aco6sh:autoMembershipMode"]}",
    </#if>
    <#if site.node.properties["aco6sh:showInHierarchyMode"]??>
        "aco6sh_showInHierarchyMode": "${site.node.properties["aco6sh:showInHierarchyMode"]}",
    </#if>
</#compress></#escape></@>