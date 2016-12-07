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

<@markup id="css" >
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/acosix/acosix-site-hierarchy/components/dashlets/site-hierarchy.css" group="dashlets" />
</@>

<@markup id="js" >
    <@script type="text/javascript" src="${url.context}/res/acosix/acosix-site-hierarchy/components/dashlets/site-hierarchy.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="html">
    <@uniqueIdDiv>
        <#assign id = args.htmlid?html>
        <div class="dashlet site-hierarchy">
            <div class="title">${msg("dashlet.siteHierarchy.title")}</div>
            <div class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>>
                <div id="${id}-treeview" class="tree"></div>
            </div>
        </div>
    </@>
</@>