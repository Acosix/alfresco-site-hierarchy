function handleSiteHierarchyData(newSite)
{
    var parentSiteNodeRefStr, parentSiteNode;
    if (json && json.has('parentSite'))
    {
        parentSiteNodeRefStr = json.get('parentSite');
        parentSiteNode = search.findNode(parentSiteNodeRefStr);
        if (parentSiteNode)
        {
            siteHierarchies.addChildSite(parentSiteNode.name, newSite.shortName);
        }
    }
}

if (model.site)
{
    handleSiteHierarchyData(model.site);
}
