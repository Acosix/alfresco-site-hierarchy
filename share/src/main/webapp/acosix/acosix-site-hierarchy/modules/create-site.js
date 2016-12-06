(function()
{
    var dialog;

    if (Acosix && Acosix.customisation && Acosix.customisation.SiteHierarchy
            && typeof Acosix.customisation.SiteHierarchy.parentSiteFieldInitialiser === 'function'
            && Acosix.customisation.SiteHierarchy.createSiteDialogEnhanced !== true)
    {
        if (Alfresco && Alfresco.module && typeof Alfresco.module.getCreateSiteInstance === 'function')
        {
            dialog = Alfresco.module.getCreateSiteInstance();

            dialog._showPanel = Alfresco.util.bind(function(_showPanelFn)
            {
                // enhance the dialog instance
                Acosix.customisation.SiteHierarchy.parentSiteFieldInitialiser.call(this);

                // call default _showPanel
                _showPanelFn.apply(this, Array.prototype.slice.call(arguments, 1));
            }, dialog, dialog._showPanel);

            Acosix.customisation.SiteHierarchy.createSiteDialogEnhanced = true;
        }
    }
}());
