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

    // TODO Configurable default root site
    if (page.url.templateArgs.site !== null)
    {
        response = remote.call('/api/sites/' + encodeURIComponent(page.url.templateArgs.site));
        if (response.status.code === 200)
        {
            siteProfile = JSON.parse(response.text);
        }
    }

    siteHierarchy = {
        id : 'SiteHierarchy',
        name : 'Acosix.dashlet.SiteHierarchy',
        options : {
            siteId : siteProfile ? siteProfile.shortName : null,
            siteTitle : siteProfile ? siteProfile.title : null
        }
    };

    dashletResizer = {
        id : 'DashletResizer',
        name : 'Alfresco.widget.DashletResizer',
        initArgs : [ '"' + args.htmlid + '"', '"' + instance.object.id + '"' ],
        useMessages : false
    };

    model.widgets = [ siteHierarchy, dashletResizer ];
}

main();
