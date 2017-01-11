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

function main()
{
    var shortName, site, parentSite, childSites;

    shortName = url.templateArgs.shortName;

    if (shortName === null)
    {
        status.setCode(400, "Site shortName needs to be provided");
    }
    else
    {
        site = siteService.getSite(shortName);
        if (site === null)
        {
            status.setCode(404, "Site " + shortName + " does not exist");
        }
        else
        {
            model.site = {
                self : site,
                hasChildSites : true,
                children : []
            };

            parentSite = siteHierarchies.getParentSite(site);
            if (parentSite !== null)
            {
                model.site.parent = {
                    self : parentSite,
                    hasChildSites : true
                };
            }
            childSites = siteHierarchies.listChildSites(site);
            if (childSites.length > 0)
            {
                model.site.children = [];
                childSites.forEach(function(childSite)
                {
                    var immediateChildSites, siteData = {
                        self : childSite,
                        hasChildSites : false
                    };
                    
                    if (childSite.node.hasAspect('aco6sh:parentSite'))
                    {
                        immediateChildSites = siteHierarchies.listChildSites(site);
                        siteData.hasChildSites = immediateChildSites !== null && immediateChildSites.length > 0;
                    }
                    
                    model.site.children.push(siteData);
                });
            }
        }
    }
}

main();
