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
