/*
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
 */

function handleSiteHierarchyData(newSite)
{
    var siteNode, autoMembershipMode, parentSiteNodeRefStr, parentSiteNode;
    
    if (json && json.has('aco6sh_autoMembershipMode'))
    {
        autoMembershipMode = json.get('aco6sh_autoMembershipMode');
        if (String(autoMembershipMode) !== '')
        {
            siteNode = model.site.node;
            siteNode.properties['aco6sh:autoMembershipMode'] = autoMembershipMode;
            siteNode.save();
        }
    }
    
    if (json && json.has('aco6sh_parentSite'))
    {
        parentSiteNodeRefStr = json.get('aco6sh_parentSite');
        if (String(parentSiteNodeRefStr) !== '')
        {
            parentSiteNode = search.findNode(parentSiteNodeRefStr);
            if (parentSiteNode)
            {
                siteHierarchies.addChildSite(parentSiteNode.name, newSite.shortName);
            }
        }
    }
}

if (model.site)
{
    handleSiteHierarchyData(model.site);
}
