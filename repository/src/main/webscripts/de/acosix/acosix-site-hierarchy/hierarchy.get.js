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
                children : []
            };

            parentSite = siteHierarchies.getParentSite(site);
            if (parentSite !== null)
            {
                model.site.parent = {
                    self : parentSite
                };
            }
            childSites = siteHierarchies.listChildSites(site);
            if (childSites.length > 0)
            {
                model.site.children = [];
                childSites.forEach(function(childSite)
                {
                    model.site.children.push({
                        self : childSite
                    });
                });
            }
        }
    }
}

main();
