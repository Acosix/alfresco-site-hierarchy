<@markup id="siteHierarchyFields" target="fields" action="after">
    <#assign field = { "control": { "params" : {}}} />
    <#include "/org/alfresco/components/form/controls/common/picker.inc.ftl" />

    <#assign el=args.htmlid?html />
    <#assign parentSiteFieldId = el + "-parentSite" />
    <#assign parentSiteControlId = parentSiteFieldId + "-cntrl" />
    
    <div id="${el}-acosix-site-hierarchy" class="acosix-site-hierarchy customSiteFields hidden">
        <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-showInHierarchyMode">${msg("acosix.siteHierarchy.siteDialog.extensions.showInHierarchyMode.label")?html}:</label></div>
            <div class="yui-u">
                <select id="${el}-showInHierarchyMode" name="aco6sh_showInHierarchyMode" tabindex="0">
                    <#list ["ifParentOrChild", "never", "always"] as mode>
                        <option value="${mode}"<#if profile.aco6sh_showInHierarchyMode?? && mode == profile.aco6sh_showInHierarchyMode> selected="selected"</#if>>${msg("acosix.siteHierarchy.siteDialog.extensions.showInHierarchyMode." + mode + ".label")?html}</option>
                    </#list>
                </select>
                <div>
                    <span class="help">${msg("acosix.siteHierarchy.siteDialog.extensions.showInHierarchyMode.help")?html}</span>
                </div>
            </div>
        </div>
        <div class="yui-gd">
            <div class="yui-u first"><label for="${parentSiteControlId}">${msg("acosix.siteHierarchy.siteDialog.extensions.parentSite.label")?html}:</label></div>
            <div class="yui-u">
                <div id="${parentSiteControlId}" class="object-finder">
                    <div id="${parentSiteControlId}-currentValueDisplay" class="current-values"></div>
                    <input type="hidden" id="${parentSiteFieldId}" name="-" value="<#if profile.aco6sh_parentSite??>${profile.aco6sh_parentSite.nodeRef}</#if>" />
                    <input type="hidden" id="${parentSiteControlId}-added" name="aco6sh_parentSite_added" />
                    <input type="hidden" id="${parentSiteControlId}-removed" name="aco6sh_parentSite_removed" />
                    <div id="${parentSiteControlId}-itemGroupActions" class="show-picker"></div>

                    <@renderPickerHTML parentSiteControlId />
                </div>
            </div>
        </div>
        <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-autoMembershipMode">${msg("acosix.siteHierarchy.siteDialog.extensions.autoMembershipMode.label")?html}:</label></div>
            <div class="yui-u">
                <select id="${el}-autoMembershipMode" name="aco6sh_autoMembershipMode" tabindex="0">
                    <#list ["systemDefault", "none", "parentMembersAsChildConsumers", "childMembersAsParentConsumers"] as mode>
                        <option value="${mode}"<#if profile.aco6sh_autoMembershipMode?? && mode == profile.aco6sh_autoMembershipMode> selected="selected"</#if>>${msg("acosix.siteHierarchy.siteDialog.extensions.autoMembershipMode." + mode + ".label")?html}</option>
                    </#list>
                </select>
                <div>
                    <span class="help">${msg("acosix.siteHierarchy.siteDialog.extensions.autoMembershipMode.help")?html}</span>
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
            Alfresco.module.getEditSiteInstance().showConfig.parentSiteNodeRef = "<#if profile.aco6sh_parentSite??>${profile.aco6sh_parentSite.nodeRef}</#if>";
        //]]></script>
    </div>
</@markup>