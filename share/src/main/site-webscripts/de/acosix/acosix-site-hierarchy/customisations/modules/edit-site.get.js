function loadSiteHierarchyData(shortName)
{
    var response, siteProfile;

    response = remote.call('/acosix/api/sites/' + encodeURIComponent(shortName) + '/hierarchy');
    if (response.status.code === 200)
    {
        siteProfile = JSON.parse(response.text).site || {};
        model.parentSiteNodeRef = siteProfile.parent ? siteProfile.parent.nodeRef : '';
    }
}

loadSiteHierarchyData(args.shortName);