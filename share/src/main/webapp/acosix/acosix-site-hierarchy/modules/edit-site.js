(function()
{
    var dialog;

    if (Acosix && Acosix.customisation && Acosix.customisation.SiteHierarchy
            && typeof Acosix.customisation.SiteHierarchy.parentSiteFieldInitialiser === 'function'
            && Acosix.customisation.SiteHierarchy.editSiteDialogEnhanced !== true)
    {
        if (Alfresco && Alfresco.module && typeof Alfresco.module.getEditSiteInstance === 'function')
        {
            dialog = Alfresco.module.getEditSiteInstance();

            dialog._showPanel = Alfresco.util.bind(function(_showPanelFn)
            {
                var currentValue;
                if (this.showConfig && this.showConfig.parentSiteNodeRef)
                {
                    currentValue = this.showConfig.parentSiteNodeRef;
                }

                // enhance the dialog instance
                Acosix.customisation.SiteHierarchy.parentSiteFieldInitialiser.call(this, currentValue);

                // call default _showPanel
                _showPanelFn.apply(this, Array.prototype.slice.call(arguments, 1));
            }, dialog, dialog._showPanel);

            Acosix.customisation.SiteHierarchy.editSiteDialogEnhanced = true;
        }
    }
}());
