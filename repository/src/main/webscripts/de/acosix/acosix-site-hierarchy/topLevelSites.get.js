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
    var topLevelSites, siteModels;

    topLevelSites = siteHierarchies.listTopLevelSites();
    siteModels = [];

    topLevelSites.forEach(function(site)
    {
        var immediateChildSites, siteData = {
            self : site,
            hasChildSites : false
        };

        if (site.node.hasAspect('aco6sh:parentSite'))
        {
            immediateChildSites = siteHierarchies.listChildSites(site);
            siteData.hasChildSites = immediateChildSites !== null && immediateChildSites.length > 0;
        }

        siteModels.push(siteData);
    });

    model.sites = siteModels;
}

main();
