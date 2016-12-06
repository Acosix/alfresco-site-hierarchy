<@markup id="siteHierarchyFields" target="fields" action="after">
    <#assign field = { "control": { "params" : {}}} />
    <#include "/org/alfresco/components/form/controls/common/picker.inc.ftl" />

    <#assign el=args.htmlid?html />
    <#assign parentSiteFieldId = el + "-parentSite" />
    <#assign parentSiteControlId = parentSiteFieldId + "-cntrl" />
    
    <div id="${el}-acosix-site-hierarchy" class="acosix-site-hierarchy customSiteFields hidden">
        <div class="yui-gd">
            <div class="yui-u first"><label for="${parentSiteControlId}">${msg("label.parentSite")?html}:</label></div>
            <div class="yui-u">
                <div id="${parentSiteControlId}" class="object-finder">
                    <div id="${parentSiteControlId}-currentValueDisplay" class="current-values"></div>
                    <input type="hidden" id="${parentSiteFieldId}" name="-" value="" />
                    <input type="hidden" id="${parentSiteControlId}-added" name="parentSite_added" />
                    <input type="hidden" id="${parentSiteControlId}-removed" name="parentSite_removed" />
                    <div id="${parentSiteControlId}-itemGroupActions" class="show-picker"></div>

                    <@renderPickerHTML parentSiteControlId />
                </div>
            </div>
        </div>
        
        <#-- due to https://github.com/Alfresco/Aikau/issues/1331 we have to double-check initialisation normally done in edit-site.js -->
        <script type="text/javascript">//<![CDATA[
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
        //]]></script>
        <script type="text/javascript">//<![CDATA[
            Alfresco.module.getEditSiteInstance().showConfig.parentSiteNodeRef = "${parentSiteNodeRef!""}";
        //]]></script>
    </div>
</@markup>