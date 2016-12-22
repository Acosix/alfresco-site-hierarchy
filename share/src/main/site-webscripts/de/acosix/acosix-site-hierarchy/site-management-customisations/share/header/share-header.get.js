/*
 * Copyright 2016 Acosix GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

function getSiteHierarchyFormWidgets()
{
    var widgets = [ {
        name : 'alfresco/forms/controls/Select',
        config : {
            fieldId: 'ACO6SH_SHOW_IN_HIERARCHY_MODE',
            name : 'aco6sh_showInHierarchyMode',
            label : 'acosix.siteHierarchy.siteDialog.extensions.autoMembershipMode.label',
            description : 'acosix.siteHierarchy.siteDialog.extensions.autoMembershipMode.help',
            optionsConfig : {
                fixed : [ {
                    label : msg.get('acosix.siteHierarchy.siteDialog.extensions.showInHierarchyMode.ifParentOrChild.label'),
                    value : 'ifParentOrChild'
                }, {
                    label : msg.get('acosix.siteHierarchy.siteDialog.extensions.showInHierarchyMode.never.label'),
                    value : 'never'
                }, {
                    label : msg.get('acosix.siteHierarchy.siteDialog.extensions.showInHierarchyMode.always.label'),
                    value : 'always'
                } ]
            }
        }
    }, {
        name : 'alfresco/forms/controls/SitePicker',
        config : {
            fieldId : 'ACO6SH_PARENT_SITE',
            name : 'aco6sh_parentSite',
            label : 'acosix.siteHierarchy.siteDialog.extensions.parentSite.label',
            // this is a way to add CSS dependencies provided by acosix-utility module (not supported by default)
            // it does not really matter where this is included as long as it is on the page
            cssRequirements : [{
                cssFile : 'aco6-aikau/forms/css/PickerEnhancements.css'
            }],
            additionalCssClasses : 'betterInlinePicker'
        }
    }, {
        name : 'alfresco/forms/controls/Select',
        config : {
            fieldId: 'ACO6SH_AUTO_MEMBERSHIP_MODE',
            name : 'aco6sh_autoMembershipMode',
            label : 'acosix.siteHierarchy.siteDialog.extensions.showInHierarchyMode.label',
            description : 'acosix.siteHierarchy.siteDialog.extensions.showInHierarchyMode.help',
            optionsConfig : {
                fixed : [ {
                    label : msg.get('acosix.siteHierarchy.siteDialog.extensions.autoMembershipMode.systemDefault.label'),
                    value : 'systemDefault'
                }, {
                    label : msg.get('acosix.siteHierarchy.siteDialog.extensions.autoMembershipMode.none.label'),
                    value : 'none'
                }, {
                    label : msg.get('acosix.siteHierarchy.siteDialog.extensions.autoMembershipMode.parentMembersAsChildConsumers.label'),
                    value : 'parentMembersAsChildConsumers'
                }, {
                    label : msg.get('acosix.siteHierarchy.siteDialog.extensions.autoMembershipMode.childMembersAsParentConsumers.label'),
                    value : 'childMembersAsParentConsumers'
                } ]
            }
        }
    } ];

    return widgets;
}

function processSiteServiceCreateDialog(siteService)
{
    var acosixGenericBlock;

    siteService.config.widgetsForCreateSiteDialogOverrides = siteService.config.widgetsForCreateSiteDialogOverrides || [];

    acosixGenericBlock = widgetUtils.findObject(siteService.config.widgetsForCreateSiteDialogOverrides, 'id',
            'ACOSIX_GENERIC_CREATE_SITE_BLOCK');
    if (!acosixGenericBlock)
    {
        acosixGenericBlock = {
            id : 'ACOSIX_GENERIC_CREATE_SITE_BLOCK',
            name : 'aco6-aikau/forms/ProcessWidgets',
            config : {
                widgets : []
            }
        };

        siteService.config.widgetsForCreateSiteDialogOverrides.push({
            id : 'ACOSIX_GENERIC_CREATE_SITE',
            targetPosition : 'END',
            name : 'alfresco/forms/CollapsibleSection',
            config : {
                // this is a way to add CSS dependencies provided by acosix-utility module (not supported by default)
                // it does not really matter where this is included as long as it is on the page
                cssRequirements : [{
                    cssFile : 'aco6-aikau/layout/css/TwisterEnhancements.css'
                }],
                label : 'acosix.utility.siteDialog.extensions.section.label',
                initiallyOpen : false,
                additionalCssClasses : 'withoutInset',
                widgets : [ acosixGenericBlock ]
            }
        });
    }
    else
    {
        acosixGenericBlock.name = 'alfresco/layout/AlfTabContainer';
    }

    acosixGenericBlock.config.widgets.push({
        id : 'ACOSIX_CREATE_SITE_HIERARCHY',
        title : 'acosix.siteHierarchy.siteDialog.extensions.title',
        selected : acosixGenericBlock.config.widgets.length === 0,
        delayProcessing : acosixGenericBlock.config.widgets.length !== 0,
        name : 'aco6-aikau/forms/ProcessWidgets',
        config : {
            additionalCssClasses : 'formRootSubstitute',
            widgets : getSiteHierarchyFormWidgets()
        }
    });
}

function processSiteServiceEditDialog(siteService)
{
    var acosixGenericBlock;

    siteService.config.widgetsForEditSiteDialogOverrides = siteService.config.widgetsForEditSiteDialogOverrides || [];

    acosixGenericBlock = widgetUtils.findObject(siteService.config.widgetsForEditSiteDialogOverrides, 'id',
            'ACOSIX_GENERIC_EDIT_SITE_BLOCK');
    if (!acosixGenericBlock)
    {
        acosixGenericBlock = {
            id : 'ACOSIX_GENERIC_EDIT_SITE_BLOCK',
            name : 'aco6-aikau/forms/ProcessWidgets',
            config : {
                widgets : []
            }
        };

        siteService.config.widgetsForEditSiteDialogOverrides.push({
            id : 'ACOSIX_GENERIC_EDIT_SITE',
            targetPosition : 'END',
            name : 'alfresco/forms/CollapsibleSection',
            config : {
                // this is a way to add CSS dependencies provided by acosix-utility module (not supported by default)
                // it does not really matter where this is included as long as it is on the page
                cssRequirements : [{
                    cssFile : 'aco6-aikau/layout/css/TwisterEnhancements.css'
                }],
                label : 'acosix.utility.siteDialog.extensions.section.label',
                initiallyOpen : false,
                additionalCssClasses : 'withoutInset',
                widgets : [ acosixGenericBlock ]
            }
        });
    }
    else
    {
        acosixGenericBlock.name = 'alfresco/layout/AlfTabContainer';
    }

    acosixGenericBlock.config.widgets.push({
        id : 'ACOSIX_CREATE_SITE_HIERARCHY',
        title : 'acosix.siteHierarchy.siteDialog.extensions.title',
        selected : acosixGenericBlock.config.widgets.length === 0,
        delayProcessing : acosixGenericBlock.config.widgets.length !== 0,
        name : 'aco6-aikau/forms/ProcessWidgets',
        config : {
            additionalCssClasses : 'formRootSubstitute',
            widgets : getSiteHierarchyFormWidgets()
        }
    });
}

function findAndProcessSiteService(services)
{
    var idx, siteService;

    for (idx = 0; idx < services.length; idx++)
    {
        if ((!services[idx].name && String(services[idx]) === 'alfresco/services/SiteService')
                || (services[idx].name && String(services[idx].name) === 'alfresco/services/SiteService'))
        {
            if (services[idx].name)
            {
                siteService = services[idx];
            }
            else
            {
                siteService = {
                    name : services[idx],
                    config : {}
                };
                services[idx] = siteService;
            }
        }
    }

    if (siteService === undefined)
    {
        siteService = {
            name : 'alfresco/services/SiteService',
            config : {}
        };
        services.push(siteService);
    }

    processSiteServiceCreateDialog(siteService);
    processSiteServiceEditDialog(siteService);
}

findAndProcessSiteService(model.jsonModel.services);
