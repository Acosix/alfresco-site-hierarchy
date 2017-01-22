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
    var siteHierarchy, dashletResizer, response, siteProfile;

    if (page.url.templateArgs.site || args.rootSite)
    {
        response = remote.call('/api/sites/' + encodeURIComponent(page.url.templateArgs.site || args.rootSite));
        if (response.status.code === 200)
        {
            siteProfile = JSON.parse(response.text);
        }
    }

    siteHierarchy = {
        id : 'SiteHierarchy',
        name : 'Acosix.dashlet.SiteHierarchy',
        assignTo : 'siteHierarchy',
        options : {
            componentId : instance.object.id,
            siteId : siteProfile ? siteProfile.shortName : null,
            siteTitle : siteProfile ? siteProfile.title : null,
            siteNodeRef : siteProfile ? siteProfile.nodeRef : null
        }
    };

    dashletResizer = {
        id : 'DashletResizer',
        name : 'Alfresco.widget.DashletResizer',
        initArgs : [ '"' + args.htmlid + '"', '"' + instance.object.id + '"' ],
        useMessages : false
    };

    model.widgets = [ siteHierarchy, dashletResizer ];

    if (!page.url.templateArgs.site)
    {
        model.widgets.push({
            id : 'DashletTitleBarActions',
            name : 'Alfresco.widget.DashletTitleBarActions',
            useMessages : false,
            options : {
                actions : [ {
                    cssClass : 'edit',
                    eventOnClick : {
                        _alfValue : 'siteHierarchyDashletConfigEvent' + args.htmlid.replace(/-/g, '_'),
                        _alfType : 'REFERENCE'
                    },
                    tooltip : msg.get('dashlet.edit.tooltip')
                } ]
            }
        });
    }
}

main();
