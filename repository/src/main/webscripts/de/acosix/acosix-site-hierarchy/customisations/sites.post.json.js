/*
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
 */

function handleSiteHierarchyData(newSite)
{
    var siteNode, propertiesChanged, showInHierarchyMode, autoMembershipMode, parentSite, parentSiteNode;

    if (json && json.has('aco6sh_showInHierarchyMode'))
    {
        showInHierarchyMode = json.get('aco6sh_showInHierarchyMode');
        if (String(showInHierarchyMode) !== '')
        {
            siteNode = newSite.node;
            siteNode.properties['aco6sh:showInHierarchyMode'] = showInHierarchyMode;
            propertiesChanged = true;
        }
    }

    if (json && json.has('aco6sh_autoMembershipMode'))
    {
        autoMembershipMode = json.get('aco6sh_autoMembershipMode');
        if (String(autoMembershipMode) !== '')
        {
            siteNode = siteNode || newSite.node;
            if (String(autoMembershipMode) !== 'systemDefault' || siteNode.properties['aco6sh:autoMembershipMode'] !== null)
            {
                siteNode.properties['aco6sh:autoMembershipMode'] = autoMembershipMode;
                propertiesChanged = true;
            }
        }
    }

    if (propertiesChanged === true && siteNode)
    {
        siteNode.save();
    }

    if (json)
    {
        if (json.has('aco6sh_parentSite_added'))
        {
            // nodeRef (legacy YUI object-finder)
            parentSite = json.get('aco6sh_parentSite_added');
            if (String(parentSite) !== '')
            {
                parentSiteNode = search.findNode(parentSite);
                if (parentSiteNode)
                {
                    siteHierarchies.addChildSite(parentSiteNode.name, newSite.shortName);
                }
            }
        }
        else if (json.has('aco6sh_parentSite'))
        {
            // site shortName (e.g. Aikau SitePicker)
            parentSite = json.get('aco6sh_parentSite');
            // might be a JSONArray since SitePicker (at least until 1.0.101) submitted single values as arrays
            if (parentSite.isNull !== undefined)
            {
                if (parentSite.length() === 1)
                {
                    parentSite = parentSite.get(0);
                }
                else
                {
                    parentSite = '';
                }
            }
            if (String(parentSite) !== '')
            {
                siteHierarchies.addChildSite(parentSite, newSite.shortName);
            }
        }
    }
    
    model.parentSite = siteHierarchies.getParentSite(newSite);
}

if (model.site)
{
    handleSiteHierarchyData(model.site);
}
