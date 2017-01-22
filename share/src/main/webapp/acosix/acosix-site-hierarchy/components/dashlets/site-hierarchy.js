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

/* jshint -W003 */
if (typeof Acosix === 'undefined' || !Acosix)
{
    var Acosix = {};
}
/* jshint +W003 */

(function()
{
    var Event = YAHOO.util.Event;

    Acosix.dashlet = Acosix.dashlet || {};

    Acosix.dashlet.SiteHierarchy = function Acosix_SiteHierarchy__constructor(htmlId)
    {
        return Acosix.dashlet.SiteHierarchy.superclass.constructor.call(this, 'Acosix.dashlet.SiteHierarchyNavigation', htmlId, [
                'treeview', 'json' ]);
    };

    YAHOO.extend(Acosix.dashlet.SiteHierarchy, Alfresco.component.Base, {

        onReady : function Acosix_SiteHierarchy__onReady()
        {
            this._buildTree();
        },

        onConfigClick : function Acosix_SiteHierarchy_onConfigClick(e)
        {
            var configDialog;

            Event.stopEvent(e);

            configDialog = new Alfresco.module.SimpleDialog(this.id + '-configDialog');
            configDialog.setOptions({
                destroyOnHide : true,
                doBeforeDialogShow : {
                    fn : function Acosix_SiteHierarchy_ConfigDialog_doBeforeDialogShow(form, dialog)
                    {
                        dialog.widgets.parentSite = new Alfresco.ObjectFinder(dialog.id + '-rootSite-cntrl', dialog.id + '-rootSite');
                        dialog.widgets.parentSite.setOptions({
                            field : 'rootSite',
                            compactMode : false,
                            mandatory : false,
                            currentValue : this.options.siteNodeRef || '',
                            selectActionLabel : Alfresco.util.message('button.select'),
                            itemFamily : 'node',
                            itemType : 'st:site',
                            parentNodeRef : 'alfresco://sites/home',
                            multipleSelectMode : false,
                            displayMode : 'items'
                        });

                        // need to call this explicitly as both default load events (page load and dialog template load) have already been
                        // passed and object-finder dependencies may still be in the queue if they weren't already available
                        Alfresco.util.YUILoaderHelper.loadComponents(false);
                    },
                    scope : this
                },
                doBeforeAjaxRequest : {
                    fn : function Acosix_SiteHierarchy_ConfigDialog_doBeforeAjaxRequest(config)
                    {
                        var dataObj;

                        dataObj = config.dataObj;

                        if (dataObj.rootSite_removed !== '')
                        {
                            Alfresco.util.Ajax.jsonPost({
                                url : Alfresco.constants.URL_SERVICECONTEXT + 'modules/dashlet/config/' + this.options.componentId,
                                dataObj : {
                                    rootSite : ''
                                },
                                successCallback : {
                                    fn : function Acosix_SiteHierarchy_ConfigDialog_onConfigCleared()
                                    {
                                        window.location.reload();
                                    },
                                    scope : this
                                }
                            });
                        }
                        else if (dataObj.rootSite_added !== '')
                        {
                            Alfresco.util.Ajax.jsonGet({
                                url : Alfresco.constants.PROXY_URI + 'slingshot/doclib2/node/'
                                        + dataObj.rootSite_added.replace(/:\/\//, '/'),
                                successCallback : {
                                    fn : function Acosix_SiteHierarchy_ConfigDialog_onSiteLoadedForUpdate(response)
                                    {
                                        var shortName = response.json.item.node.properties['cm:name'];

                                        Alfresco.util.Ajax.jsonPost({
                                            url : Alfresco.constants.URL_SERVICECONTEXT + 'modules/dashlet/config/'
                                                    + this.options.componentId,
                                            dataObj : {
                                                rootSite : shortName
                                            },
                                            successCallback : {
                                                fn : function Acosix_SiteHierarchy_ConfigDialog_onConfigUpdated()
                                                {
                                                    window.location.reload();
                                                },
                                                scope : this
                                            }
                                        });
                                    },
                                    scope : this
                                }
                            });
                        }

                        return false;
                    },
                    scope : this
                }
            });

            if (this.options.siteNodeRef)
            {
                configDialog.setOptions({
                    templateUrl : Alfresco.constants.URL_SERVICECONTEXT
                            + 'acosix/modules/dashlet/site-hierarchy/config?currentSiteNodeRef='
                            + encodeURIComponent(this.options.siteNodeRef)
                });
            }
            else
            {
                configDialog.setOptions({
                    templateUrl : Alfresco.constants.URL_SERVICECONTEXT + 'acosix/modules/dashlet/site-hierarchy/config'
                });
            }

            configDialog.show();
        },

        onNodeClicked : function Acosix_SiteHierarchy__onNodeClicked(args)
        {
            var node, shortName;

            node = args.node;
            shortName = node.data.shortName;

            if (shortName)
            {
                // TODO Incorporate site-specific landing pages
                // In what Alfresco version was/is this added?
                window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'site/' + shortName + '/dashboard';

                // prevent expansion of tree
                Event.stopEvent(args.event);
                return false;
            }
            else
            {
                return true;
            }
        },

        _buildTree : function Acosix_SiteHierarchy__buildTree()
        {
            var tree, root, rootNode;

            tree = new YAHOO.widget.TreeView(this.id + '-treeview');
            tree.setDynamicLoad(this.bind(this._loadTreeData));
            tree.subscribe('clickEvent', this.onNodeClicked, this, true);

            this.widgets.treeView = tree;

            if (this.options.siteId && this.options.siteTitle)
            {
                root = tree.getRoot();
                rootNode = new YAHOO.widget.TextNode({
                    label : this.options.siteTitle,
                    shortName : this.options.siteId,
                    labelStyle : 'site ygtvlabel',
                    expanded : false
                }, root);

                tree.render();
                rootNode.expand();
            }
            else
            {
                Alfresco.util.Ajax.jsonGet({
                    url : Alfresco.constants.PROXY_URI + 'acosix/api/sites/topLevelSites',
                    successCallback : {
                        fn : function(response)
                        {
                            var idx, site, root, rootNode;
                            if (response.json.sites)
                            {
                                root = this.widgets.treeView.getRoot();
                                for (idx = 0; idx < response.json.sites.length; idx++)
                                {
                                    site = response.json.sites[idx];
                                    rootNode = new YAHOO.widget.TextNode({
                                        label : site.title || site.shortName,
                                        shortName : site.shortName,
                                        hierarchyUri : site.hierarchyUri,
                                        labelStyle : 'site ygtvlabel',
                                        isLeaf : site.hasChildSites !== true && site.hasChildSites !== 'true',
                                        expanded : false
                                    }, root);
                                }

                                this.widgets.treeView.render();

                                // only one root site found - auto-expand as a convenience
                                if (response.json.sites.length === 1 && rootNode)
                                {
                                    rootNode.expand();
                                }
                            }
                        },
                        scope : this
                    },
                    failureCallback : {
                        fn : function(oResponse)
                        {
                            var response, rootNode, docNode;
                            if (oResponse.status === 401)
                            {
                                // session likely timed-out so refresh
                                window.location.reload();
                            }
                            else
                            {
                                try
                                {
                                    response = YAHOO.lang.JSON.parse(oResponse.responseText);

                                    rootNode = this.widgets.treeView.getRoot();
                                    docNode = rootNode.children[0];
                                    docNode.isLoading = false;
                                    docNode.isLeaf = true;
                                    docNode.label = response.message;
                                    docNode.labelStyle = 'ygtverror';
                                    rootNode.refresh();
                                }
                                catch (e)
                                {
                                    Alfresco.logger.error('Acosix.dashlet.SiteHierarchy._buildTree() - server response', oResponse, e);
                                }
                            }
                        },
                        scope : this
                    }
                });
            }
        },

        _loadTreeData : function Acosix_SiteHierarchy__loadTreeData(node, fnLoadComplete)
        {
            var uri, callback;

            if (node.data.hierarchyUri)
            {
                uri = node.data.hierarchyUri;
            }
            else if (node.data.shortName)
            {
                uri = 'acosix/api/sites/' + encodeURIComponent(node.data.shortName) + '/hierarchy';
            }

            callback = {
                success : function Acosix_SiteHierarchy__loadTreeData_success(oResponse)
                {
                    var result, sites, site, idx, newNode;

                    result = YAHOO.lang.JSON.parse(oResponse.responseText);
                    sites = result.children || result.sites || [];

                    if (sites.length > 0)
                    {
                        for (idx = 0; idx < sites.length; idx++)
                        {
                            site = sites[idx];
                            newNode = new YAHOO.widget.TextNode({
                                label : site.title || site.shortName,
                                shortName : site.shortName,
                                hierarchyUri : site.hierarchyUri,
                                labelStyle : 'site ygtvlabel',
                                isLeaf : site.hasChildSites !== true && site.hasChildSites !== 'true',
                                expanded : false
                            }, node);
                        }
                    }
                    else
                    {
                        node.isLeaf = true;
                    }

                    oResponse.argument.fnLoadComplete();
                },
                failure : function Acosix_SiteHierarchy__loadTreeData_failure(oResponse)
                {
                    var response, rootNode, docNode;
                    if (oResponse.status === 401)
                    {
                        // session likely timed-out so refresh
                        window.location.reload();
                    }
                    else
                    {
                        try
                        {
                            response = YAHOO.lang.JSON.parse(oResponse.responseText);

                            rootNode = this.widgets.treeView.getRoot();
                            docNode = rootNode.children[0];
                            docNode.isLoading = false;
                            docNode.isLeaf = true;
                            docNode.label = response.message;
                            docNode.labelStyle = 'ygtverror';
                            rootNode.refresh();
                        }
                        catch (e)
                        {
                            Alfresco.logger.error('Acosix.dashlet.SiteHierarchy._loadTreeData() - server response', oResponse, e);
                        }
                    }
                },
                scope : this,
                argument : {
                    node : node,
                    fnLoadComplete : fnLoadComplete
                }
            };

            if (uri)
            {
                YAHOO.util.Connect.asyncRequest('GET', Alfresco.constants.PROXY_URI + uri, callback);
            }
        }

    });

}());
