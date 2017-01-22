<#assign el=args.htmlid?html>
<div id="${el}-configDialog">
    <div class="hd">${msg("acosix.siteHierarchy.config.dialog.label")}</div>
    <div class="bd">
        <form id="${el}-form" action="" method="POST">
            <#assign field = { "control": { "params" : {}}} />
            <#include "/org/alfresco/components/form/controls/common/picker.inc.ftl" />
            <#assign rootSiteFieldId = el + "-rootSite" />
            <#assign rootSiteControlId = rootSiteFieldId + "-cntrl" />
            <script type="text/javascript">//<![CDATA[
                (function()
                {
                    Alfresco.util.addMessages(${messages},'Alfresco.ObjectFinder');
                    Alfresco.util.addMessages(${messages},'Alfresco.ObjectRenderer');
                }());
            //]]></script>
            
            <div class="yui-gd">
                <div class="yui-u first"><label for="${rootSiteControlId}">${msg("acosix.siteHierarchy.config.rootSite.label")?html}:</label></div>
                <div class="yui-u">
                    <div id="${rootSiteControlId}" class="object-finder">
                        <div id="${rootSiteControlId}-currentValueDisplay" class="current-values"></div>
                        <input type="hidden" id="${rootSiteFieldId}" name="-" value="<#if args.currentSiteNodeRef??>${args.currentSiteNodeRef}</#if>" />
                        <input type="hidden" id="${rootSiteControlId}-added" name="rootSite_added" />
                        <input type="hidden" id="${rootSiteControlId}-removed" name="rootSite_removed" />
                        <div id="${rootSiteControlId}-itemGroupActions" class="show-picker"></div>
    
                        <@renderPickerHTML rootSiteControlId />
                    </div>
                </div>
            </div>
            <div class="bdft">
                <input type="submit" id="${el}-ok" value="${msg("button.ok")}" />
                <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" />
            </div>
        </form>
    </div>
</div>