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
