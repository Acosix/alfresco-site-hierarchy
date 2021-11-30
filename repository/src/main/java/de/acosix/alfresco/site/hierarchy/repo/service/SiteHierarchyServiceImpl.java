/*
 * Copyright 2016 - 2021 Acosix GmbH
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
package de.acosix.alfresco.site.hierarchy.repo.service;

import java.io.Serializable;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.BeforeRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteChildAssociationPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.QueryConsistency;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.surf.util.I18NUtil;

import de.acosix.alfresco.site.hierarchy.repo.model.SiteHierarchyModel;
import de.acosix.alfresco.site.hierarchy.repo.service.SiteHierarchyServicePolicies.BeforeAddChildSitePolicy;
import de.acosix.alfresco.site.hierarchy.repo.service.SiteHierarchyServicePolicies.BeforeRemoveChildSitePolicy;
import de.acosix.alfresco.site.hierarchy.repo.service.SiteHierarchyServicePolicies.OnAddChildSitePolicy;
import de.acosix.alfresco.site.hierarchy.repo.service.SiteHierarchyServicePolicies.OnRemoveChildSitePolicy;

/**
 * @author Axel Faust
 */
public class SiteHierarchyServiceImpl implements SiteHierarchyService, InitializingBean, OnCreateChildAssociationPolicy,
        OnDeleteChildAssociationPolicy, OnAddAspectPolicy, BeforeRemoveAspectPolicy
{

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteHierarchyServiceImpl.class);

    protected Set<String> storesToIgnorePolicies = Collections.emptySet();

    protected NamespaceService namespaceService;

    protected DictionaryService dictionaryService;

    protected NodeService nodeService;

    protected SiteService siteService;

    protected TenantService tenantService;

    protected SearchService searchService;

    protected PolicyComponent policyComponent;

    protected ClassPolicyDelegate<BeforeAddChildSitePolicy> beforeAddChildSiteDelegate;

    protected ClassPolicyDelegate<OnAddChildSitePolicy> onAddChildSiteDelegate;

    protected ClassPolicyDelegate<BeforeRemoveChildSitePolicy> beforeRemoveChildSiteDelegate;

    protected ClassPolicyDelegate<OnRemoveChildSitePolicy> onRemoveChildSiteDelegate;

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet()
    {
        PropertyCheck.mandatory(this, "storesToIgnorePolicies", this.storesToIgnorePolicies);
        PropertyCheck.mandatory(this, "namespaceService", this.namespaceService);
        PropertyCheck.mandatory(this, "dictionaryService", this.dictionaryService);
        PropertyCheck.mandatory(this, "nodeService", this.nodeService);
        PropertyCheck.mandatory(this, "siteService", this.siteService);
        PropertyCheck.mandatory(this, "searchService", this.searchService);
        PropertyCheck.mandatory(this, "tenantService", this.tenantService);

        PropertyCheck.mandatory(this, "policyComponent", this.policyComponent);

        this.policyComponent.bindAssociationBehaviour(OnCreateChildAssociationPolicy.QNAME, SiteHierarchyModel.ASPECT_HIERARCHY_SITE,
                SiteHierarchyModel.ASSOC_CHILD_SITE,
                new JavaBehaviour(this, "onCreateChildAssociation", NotificationFrequency.EVERY_EVENT));
        this.policyComponent.bindAssociationBehaviour(OnDeleteChildAssociationPolicy.QNAME, SiteHierarchyModel.ASPECT_HIERARCHY_SITE,
                SiteHierarchyModel.ASSOC_CHILD_SITE,
                new JavaBehaviour(this, "onDeleteChildAssociation", NotificationFrequency.EVERY_EVENT));

        this.policyComponent.bindClassBehaviour(OnAddAspectPolicy.QNAME, SiteHierarchyModel.ASPECT_HIERARCHY_SITE,
                new JavaBehaviour(this, "onAddAspect", NotificationFrequency.EVERY_EVENT));
        this.policyComponent.bindClassBehaviour(BeforeRemoveAspectPolicy.QNAME, SiteHierarchyModel.ASPECT_HIERARCHY_SITE,
                new JavaBehaviour(this, "beforeRemoveAspect", NotificationFrequency.EVERY_EVENT));

        this.beforeAddChildSiteDelegate = this.policyComponent
                .registerClassPolicy(SiteHierarchyServicePolicies.BeforeAddChildSitePolicy.class);
        this.onAddChildSiteDelegate = this.policyComponent.registerClassPolicy(SiteHierarchyServicePolicies.OnAddChildSitePolicy.class);
        this.beforeRemoveChildSiteDelegate = this.policyComponent
                .registerClassPolicy(SiteHierarchyServicePolicies.BeforeRemoveChildSitePolicy.class);
        this.onRemoveChildSiteDelegate = this.policyComponent
                .registerClassPolicy(SiteHierarchyServicePolicies.OnRemoveChildSitePolicy.class);
    }

    /**
     * @param storesToIgnorePolicies
     *            the storesToIgnorePolicies to set
     */
    public void setStoresToIgnorePolicies(final Set<String> storesToIgnorePolicies)
    {
        this.storesToIgnorePolicies = storesToIgnorePolicies;
    }

    /**
     * @param namespaceService
     *            the namespaceService to set
     */
    public void setNamespaceService(final NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * @param dictionaryService
     *            the dictionaryService to set
     */
    public void setDictionaryService(final DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * @param nodeService
     *            the nodeService to set
     */
    public void setNodeService(final NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * @param siteService
     *            the siteService to set
     */
    public void setSiteService(final SiteService siteService)
    {
        this.siteService = siteService;
    }

    /**
     * @param tenantService
     *            the tenantService to set
     */
    public void setTenantService(final TenantService tenantService)
    {
        this.tenantService = tenantService;
    }

    /**
     * @param searchService
     *            the searchService to set
     */
    public void setSearchService(final SearchService searchService)
    {
        this.searchService = searchService;
    }

    /**
     * @param policyComponent
     *            the policyComponent to set
     */
    public void setPolicyComponent(final PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public List<SiteInfo> listTopLevelSites()
    {
        return this.listTopLevelSites(true);
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public List<SiteInfo> listTopLevelSites(final boolean respectShowInHierarchyMode)
    {
        LOGGER.debug("Listing top level sites");

        final SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setQueryConsistency(QueryConsistency.TRANSACTIONAL_IF_POSSIBLE);
        sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

        final MessageFormat queryFormat = new MessageFormat("TYPE:\"{0}\" AND ASPECT:\"{1}\"", Locale.ENGLISH);
        String query = queryFormat.format(new Object[] { SiteModel.TYPE_SITE.toPrefixString(this.namespaceService),
                SiteHierarchyModel.ASPECT_TOP_LEVEL_SITE.toPrefixString(this.namespaceService) });

        if (respectShowInHierarchyMode)
        {
            // this will cause db-fts not to be used
            final MessageFormat queryFilterFormat = new MessageFormat("={0}:{1} OR (={0}:{2} AND ASPECT:\"{3}\")", Locale.ENGLISH);
            final String queryFilter = queryFilterFormat
                    .format(new Object[] { SiteHierarchyModel.PROP_SHOW_IN_HIERARCHY_MODE.toPrefixString(this.namespaceService),
                            SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS,
                            SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_IF_PARENT_OR_CHILD,
                            SiteHierarchyModel.ASPECT_PARENT_SITE.toPrefixString(this.namespaceService) });
            // if we did not want to support 5.0.d we could use filter query support
            // TODO find a way to dynamically use filter query support WITHOUT reflection
            query = query + " AND (" + queryFilter + ")";
        }

        sp.setQuery(query);

        LOGGER.debug("Using FTS query \"{}\" to list top level sites", query);

        // we use short name sort since that is properly handled in either db-fts or fts
        // and might be specific enough so that in-memory post sort does not have to do that much
        final String shortNameSort = "@" + ContentModel.PROP_NAME.toPrefixString(this.namespaceService);
        sp.addSort(shortNameSort, true);

        List<SiteInfo> sites;
        final ResultSet resultSet = this.searchService.query(sp);
        try
        {
            sites = new ArrayList<>(resultSet.length());
            resultSet.forEach(resultRow -> {
                final NodeRef nodeRef = resultRow.getNodeRef();
                final SiteInfo site = this.siteService.getSite(nodeRef);
                if (site != null)
                {
                    sites.add(site);
                }
            });
        }
        finally
        {
            resultSet.close();
        }

        final Collator collator = Collator.getInstance(I18NUtil.getLocale());
        Collections.sort(sites, (siteA, siteB) -> {
            final int titleCompareResult = collator.compare(siteA.getTitle(), siteB.getTitle());
            return titleCompareResult;
        });

        LOGGER.debug("Listed top level sites {}", sites);

        return sites;
    }

    @Override
    public SiteInfo getParentSite(final String site)
    {
        ParameterCheck.mandatoryString("site", site);

        final SiteInfo siteInfo = this.siteService.getSite(site);
        if (siteInfo == null)
        {
            throw new SiteDoesNotExistException(site);
        }

        final List<ChildAssociationRef> parentSiteAssocs = this.nodeService.getParentAssocs(siteInfo.getNodeRef(),
                SiteHierarchyModel.ASSOC_CHILD_SITE, RegexQNamePattern.MATCH_ALL);
        SiteInfo parentSite;

        if (parentSiteAssocs.size() == 1)
        {
            parentSite = this.siteService.getSite(parentSiteAssocs.get(0).getParentRef());
        }
        else
        {
            parentSite = null;
        }

        return parentSite;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public List<SiteInfo> listChildSites(final String parentSite)
    {
        return this.listChildSites(parentSite, true);
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public List<SiteInfo> listChildSites(final String parentSite, final boolean respectShowInHierarchyMode)
    {
        ParameterCheck.mandatoryString("parentSite", parentSite);
        final SiteInfo parentSiteInfo = this.siteService.getSite(parentSite);

        if (parentSiteInfo == null)
        {
            throw new SiteDoesNotExistException(parentSite);
        }

        LOGGER.debug("Listing child sites for parent {}", parentSite);

        final List<ChildAssociationRef> childSiteAssocs = this.nodeService.getChildAssocs(parentSiteInfo.getNodeRef(),
                SiteHierarchyModel.ASSOC_CHILD_SITE, RegexQNamePattern.MATCH_ALL);
        if (respectShowInHierarchyMode)
        {
            final List<ChildAssociationRef> childSiteAssocsNeverToShow = this.nodeService.getChildAssocsByPropertyValue(
                    parentSiteInfo.getNodeRef(), SiteHierarchyModel.PROP_SHOW_IN_HIERARCHY_MODE,
                    SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_NEVER);
            childSiteAssocs.removeAll(childSiteAssocsNeverToShow);
        }

        final List<SiteInfo> childSites = new ArrayList<>(childSiteAssocs.size());
        childSiteAssocs.forEach(childSiteAssoc -> {
            final SiteInfo site = this.siteService.getSite(childSiteAssoc.getChildRef());
            if (site != null)
            {
                childSites.add(site);
            }
        });

        final Collator collator = Collator.getInstance(I18NUtil.getLocale());
        Collections.sort(childSites, (siteA, siteB) -> {
            final int titleCompareResult = collator.compare(siteA.getTitle(), siteB.getTitle());
            return titleCompareResult;
        });

        LOGGER.debug("Listed child sites {} for parent {}", childSites, parentSite);

        return childSites;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChildSite(final String parentSite, final String childSite)
    {
        ParameterCheck.mandatoryString("parentSite", parentSite);
        ParameterCheck.mandatoryString("childSite", childSite);

        final SiteInfo parentSiteInfo = this.siteService.getSite(parentSite);
        final SiteInfo childSiteInfo = this.siteService.getSite(childSite);

        if (parentSiteInfo == null)
        {
            throw new SiteDoesNotExistException(parentSite);
        }

        if (childSiteInfo == null)
        {
            throw new SiteDoesNotExistException(childSite);
        }

        final NodeRef parentSiteNodeRef = parentSiteInfo.getNodeRef();
        final NodeRef childSiteNodeRef = childSiteInfo.getNodeRef();

        final boolean childIsChildSite = this.nodeService.hasAspect(childSiteNodeRef, SiteHierarchyModel.ASPECT_CHILD_SITE);
        if (childIsChildSite)
        {
            final List<ChildAssociationRef> parentSiteAssocs = this.nodeService.getParentAssocs(childSiteNodeRef,
                    SiteHierarchyModel.ASSOC_CHILD_SITE, RegexQNamePattern.MATCH_ALL);
            if (!parentSiteAssocs.isEmpty())
            {
                throw new AlfrescoRuntimeException("acosix-site-hierarchy.error.childSiteHasParent",
                        new Object[] { parentSite, childSite });
            }
        }

        LOGGER.debug("Adding child site {} to parent {}", childSite, parentSite);

        this.invokeBeforeAddChildSite(parentSiteInfo, childSiteInfo);

        if (!childIsChildSite)
        {
            LOGGER.debug("Adding childSite aspect to child site {}", childSite);
            this.nodeService.addAspect(childSiteNodeRef, SiteHierarchyModel.ASPECT_CHILD_SITE,
                    Collections.<QName, Serializable> emptyMap());
        }

        if (!this.nodeService.hasAspect(parentSiteNodeRef, SiteHierarchyModel.ASPECT_PARENT_SITE))
        {
            LOGGER.debug("Adding parentSite aspect to parent site {}", parentSite);
            this.nodeService.addAspect(parentSiteNodeRef, SiteHierarchyModel.ASPECT_PARENT_SITE,
                    Collections.<QName, Serializable> emptyMap());
        }

        final ChildAssociationRef primaryParent = this.nodeService.getPrimaryParent(childSiteNodeRef);
        this.nodeService.addChild(parentSiteNodeRef, childSiteNodeRef, SiteHierarchyModel.ASSOC_CHILD_SITE, primaryParent.getQName());

        this.invokeOnAddChildSite(parentSiteInfo, childSiteInfo);

        LOGGER.debug("Added child site {} to parent {}", childSite, parentSite);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChildSite(final String parentSite, final String childSite)
    {
        ParameterCheck.mandatoryString("parentSite", parentSite);
        ParameterCheck.mandatoryString("childSite", childSite);

        final SiteInfo parentSiteInfo = this.siteService.getSite(parentSite);
        final SiteInfo childSiteInfo = this.siteService.getSite(childSite);

        if (parentSiteInfo == null)
        {
            throw new SiteDoesNotExistException(parentSite);
        }

        if (childSiteInfo == null)
        {
            throw new SiteDoesNotExistException(childSite);
        }

        final NodeRef parentSiteNodeRef = parentSiteInfo.getNodeRef();
        final NodeRef childSiteNodeRef = childSiteInfo.getNodeRef();

        final List<ChildAssociationRef> parentSiteAssocs = this.nodeService.getParentAssocs(childSiteNodeRef,
                SiteHierarchyModel.ASSOC_CHILD_SITE, RegexQNamePattern.MATCH_ALL);
        if (parentSiteAssocs.isEmpty() || !EqualsHelper.nullSafeEquals(parentSiteNodeRef, parentSiteAssocs.get(0).getParentRef()))
        {
            throw new AlfrescoRuntimeException("acosix-site-hierarchy.error.childSiteHasDifferentParent",
                    new Object[] { parentSite, childSite });
        }

        LOGGER.debug("Removing child site {} from parent {}", childSite, parentSite);

        this.invokeBeforeRemoveChildSite(parentSiteInfo, childSiteInfo);

        // removeSecondaryChildAssociation would be the correct operation but due to ACE-5361 we cannot use it
        this.nodeService.removeChildAssociation(parentSiteAssocs.get(0));

        this.invokeOnRemoveChildSite(parentSiteInfo, childSiteInfo);

        LOGGER.debug("Removed child site {} from parent {}", childSite, parentSite);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateChildAssociation(final ChildAssociationRef childAssocRef, final boolean isNewNode)
    {
        final NodeRef parentRef = childAssocRef.getParentRef();
        if (StoreRef.STORE_REF_WORKSPACE_SPACESSTORE.equals(parentRef.getStoreRef()) && !childAssocRef.isPrimary()
                && SiteHierarchyModel.ASSOC_CHILD_SITE.equals(childAssocRef.getTypeQName()))
        {
            final NodeRef childRef = childAssocRef.getChildRef();
            LOGGER.debug("onCreateChildAssociation - {} added as child of {}", childRef, parentRef);
            if (this.nodeService.hasAspect(childRef, SiteHierarchyModel.ASPECT_TOP_LEVEL_SITE))
            {
                LOGGER.debug("Removing topLevelSite from {}", childRef);
                this.nodeService.removeAspect(childRef, SiteHierarchyModel.ASPECT_TOP_LEVEL_SITE);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeleteChildAssociation(final ChildAssociationRef childAssocRef)
    {
        final NodeRef parentRef = childAssocRef.getParentRef();
        if (StoreRef.STORE_REF_WORKSPACE_SPACESSTORE.equals(parentRef.getStoreRef()) && !childAssocRef.isPrimary()
                && SiteHierarchyModel.ASSOC_CHILD_SITE.equals(childAssocRef.getTypeQName()))
        {
            final NodeRef childRef = childAssocRef.getChildRef();
            LOGGER.debug("onDeleteChildAssociation - {} removed as child of {}", childRef, parentRef);

            final List<ChildAssociationRef> remainingParentChildSiteAssocs = this.nodeService.getChildAssocs(parentRef,
                    SiteHierarchyModel.ASSOC_CHILD_SITE, RegexQNamePattern.MATCH_ALL);
            if (remainingParentChildSiteAssocs.isEmpty())
            {
                LOGGER.debug("Ex-parent {} has no other child sites - removing parentSite aspect", parentRef);

                this.withAspectInheritedPropertyRemovalGuard(parentRef, SiteHierarchyModel.ASPECT_HIERARCHY_SITE, () -> {
                    this.nodeService.removeAspect(parentRef, SiteHierarchyModel.ASPECT_PARENT_SITE);
                    return null;
                });
            }

            LOGGER.debug("Removing childSite aspect from ex-child {}", childRef);
            this.withAspectInheritedPropertyRemovalGuard(childRef, SiteHierarchyModel.ASPECT_HIERARCHY_SITE, () -> {
                this.nodeService.removeAspect(childRef, SiteHierarchyModel.ASPECT_CHILD_SITE);
                return null;
            });

            LOGGER.debug("Adding topLevelSite aspect to ex-child {}", childRef);
            this.nodeService.addAspect(childRef, SiteHierarchyModel.ASPECT_TOP_LEVEL_SITE, Collections.<QName, Serializable> emptyMap());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAddAspect(final NodeRef nodeRef, final QName aspectTypeQName)
    {
        if (StoreRef.STORE_REF_WORKSPACE_SPACESSTORE.equals(nodeRef.getStoreRef()))
        {
            LOGGER.debug("onAddAspect - {} added to {}", aspectTypeQName, nodeRef);
            if (this.dictionaryService.isSubClass(aspectTypeQName, SiteHierarchyModel.ASPECT_HIERARCHY_SITE))
            {
                final QName type = this.nodeService.getType(nodeRef);
                if (!this.dictionaryService.isSubClass(type, SiteModel.TYPE_SITE))
                {
                    throw new IllegalStateException("Aspects for a hierarchy sites may only be applied to actual site nodes");
                }

                if (!this.nodeService.hasAspect(nodeRef, SiteHierarchyModel.ASPECT_TOP_LEVEL_SITE))
                {
                    final List<ChildAssociationRef> parentSiteAssocs = this.nodeService.getParentAssocs(nodeRef,
                            SiteHierarchyModel.ASSOC_CHILD_SITE, RegexQNamePattern.MATCH_ALL);
                    if (parentSiteAssocs.isEmpty())
                    {
                        // expected case during site creation
                        LOGGER.debug("Adding topLevelSite aspect to site {}",
                                this.nodeService.getProperty(nodeRef, ContentModel.PROP_NAME));
                        this.nodeService.addAspect(nodeRef, SiteHierarchyModel.ASPECT_TOP_LEVEL_SITE,
                                Collections.<QName, Serializable> emptyMap());
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeRemoveAspect(final NodeRef nodeRef, final QName aspectTypeQName)
    {
        if (StoreRef.STORE_REF_WORKSPACE_SPACESSTORE.equals(nodeRef.getStoreRef()))
        {
            LOGGER.debug("beforeRemoveAspect - {} to be removed from {}", aspectTypeQName, nodeRef);
            // topLevelSite has hierarchySite as mandatory-aspect which would make removing hierarchySite impossible
            // since topLevelSite is just a technical marker we automatically remove it when hierarchySite is about to be removed
            if (SiteHierarchyModel.ASPECT_HIERARCHY_SITE.equals(aspectTypeQName)
                    && this.nodeService.hasAspect(nodeRef, SiteHierarchyModel.ASPECT_TOP_LEVEL_SITE))
            {
                LOGGER.debug("Removing topLevelSite from {}", nodeRef);
                this.nodeService.removeAspect(nodeRef, SiteHierarchyModel.ASPECT_TOP_LEVEL_SITE);
            }
        }
    }

    protected void invokeBeforeAddChildSite(final SiteInfo parentSiteInfo, final SiteInfo childSiteInfo)
    {
        this.invokePolicyViaInvoker(parentSiteInfo, qnames -> {
            LOGGER.trace("Invoking beforeAddChildSite for parent {} (qnames: {}), child {}", parentSiteInfo, qnames, childSiteInfo);
            final BeforeAddChildSitePolicy beforeAddChildSitePolicy = this.beforeAddChildSiteDelegate.get(parentSiteInfo.getNodeRef(),
                    qnames);
            beforeAddChildSitePolicy.beforeAddChildSite(parentSiteInfo, childSiteInfo);
            return null;
        });
    }

    protected void invokeOnAddChildSite(final SiteInfo parentSiteInfo, final SiteInfo childSiteInfo)
    {
        this.invokePolicyViaInvoker(parentSiteInfo, qnames -> {
            LOGGER.trace("Invoking onAddChildSite for parent {} (qnames: {}), child {}", parentSiteInfo, qnames, childSiteInfo);
            final OnAddChildSitePolicy onAddChildSitePolicy = this.onAddChildSiteDelegate.get(parentSiteInfo.getNodeRef(), qnames);
            onAddChildSitePolicy.onAddChildSite(parentSiteInfo, childSiteInfo);
            return null;
        });
    }

    protected void invokeBeforeRemoveChildSite(final SiteInfo parentSiteInfo, final SiteInfo childSiteInfo)
    {
        this.invokePolicyViaInvoker(parentSiteInfo, qnames -> {
            LOGGER.trace("Invoking beforeRemoveChildSite for parent {} (qnames: {}), child {}", parentSiteInfo, qnames, childSiteInfo);
            final BeforeRemoveChildSitePolicy beforeRemoveChildSitePolicy = this.beforeRemoveChildSiteDelegate
                    .get(parentSiteInfo.getNodeRef(), qnames);
            beforeRemoveChildSitePolicy.beforeRemoveChildSite(parentSiteInfo, childSiteInfo);
            return null;
        });
    }

    protected void invokeOnRemoveChildSite(final SiteInfo parentSiteInfo, final SiteInfo childSiteInfo)
    {
        this.invokePolicyViaInvoker(parentSiteInfo, qnames -> {
            LOGGER.trace("Invoking onRemoveChildSite for parent {} (qnames: {}), child {}", parentSiteInfo, qnames, childSiteInfo);
            final OnRemoveChildSitePolicy onRemoveChildSitePolicy = this.onRemoveChildSiteDelegate.get(parentSiteInfo.getNodeRef(), qnames);
            onRemoveChildSitePolicy.onRemoveChildSite(parentSiteInfo, childSiteInfo);
            return null;
        });
    }

    protected void invokePolicyViaInvoker(final SiteInfo parentSiteInfo, final Function<Set<QName>, Void> policyInvoker)
    {
        final StoreRef storeRef = parentSiteInfo.getNodeRef().getStoreRef();
        if (this.storesToIgnorePolicies.contains(this.tenantService.getBaseName(storeRef).toString()))
        {
            return;
        }

        final QName type = this.nodeService.getType(parentSiteInfo.getNodeRef());
        final Set<QName> aspects = this.nodeService.getAspects(parentSiteInfo.getNodeRef());

        final Set<QName> qnames = new HashSet<>();
        qnames.add(type);
        qnames.addAll(aspects);

        policyInvoker.apply(qnames);
    }

    protected void withAspectInheritedPropertyRemovalGuard(final NodeRef node, final QName baseAspect, final Supplier<?> fn)
    {
        // there is a bug when aspects that inherit from another are removed but the base aspect remains
        // all properties of the base aspect are deleted as well
        final AspectDefinition aspect = this.dictionaryService.getAspect(baseAspect);
        final Set<QName> propertiesToPreserve = aspect.getProperties().keySet();
        final Map<QName, Serializable> properties = this.nodeService.getProperties(node);
        final Map<QName, Serializable> propertiesBackup = new HashMap<>();
        propertiesToPreserve.forEach(propertyQName -> {
            if (properties.containsKey(propertyQName))
            {
                propertiesBackup.put(propertyQName, properties.get(propertyQName));
            }
        });

        fn.get();

        if (!propertiesBackup.isEmpty())
        {
            this.nodeService.addProperties(node, propertiesBackup);
        }
    }
}