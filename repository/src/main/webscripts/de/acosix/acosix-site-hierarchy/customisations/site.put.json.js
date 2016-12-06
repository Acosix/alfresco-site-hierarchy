function handleSiteHierarchyData(newSite)
{
    var parentSiteNodeRefStr, parentSiteNode;

    if (json && json.has('parentSite_removed'))
    {
        parentSiteNodeRefStr = json.get('parentSite_removed');
        if (String(parentSiteNodeRefStr) !== '')
        {
            parentSiteNode = search.findNode(parentSiteNodeRefStr);
            if (parentSiteNode)
            {
                siteHierarchies.removeChildSite(parentSiteNode.name, newSite.shortName);
            }
        }
    }

    if (json && json.has('parentSite_added'))
    {
        parentSiteNodeRefStr = json.get('parentSite_added');
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
