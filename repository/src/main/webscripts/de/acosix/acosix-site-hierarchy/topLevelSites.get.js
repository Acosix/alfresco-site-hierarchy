function main()
{
    var topLevelSites, siteModels;

    topLevelSites = siteHierarchies.listTopLevelSites();
    siteModels = [];

    topLevelSites.forEach(function(site)
    {
        siteModels.push({
            self : site
        });
    });

    model.sites = siteModels;
}

main();
