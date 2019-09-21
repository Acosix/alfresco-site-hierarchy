/*
 * Copyright 2016 - 2019 Acosix GmbH
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
package de.acosix.alfresco.site.hierarchy.repo.policy;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import de.acosix.alfresco.site.hierarchy.repo.model.SiteHierarchyModel;
import de.acosix.alfresco.site.hierarchy.repo.service.SiteHierarchyService;
import de.acosix.alfresco.site.hierarchy.repo.service.SiteHierarchyServicePolicies.BeforeRemoveChildSitePolicy;
import de.acosix.alfresco.site.hierarchy.repo.service.SiteHierarchyServicePolicies.OnAddChildSitePolicy;

/**
 * @author Axel Faust
 */
public class AutoMembershipMode implements InitializingBean, OnAddChildSitePolicy, BeforeRemoveChildSitePolicy, OnUpdatePropertiesPolicy
{

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoMembershipMode.class);

    protected PolicyComponent policyComponent;

    protected AuthorityService authorityService;

    protected NodeService nodeService;

    protected SiteService siteService;

    protected SiteHierarchyService siteHierarchyService;

    protected String systemDefaultMode = SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_NONE;

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "policyComponent", this.policyComponent);
        PropertyCheck.mandatory(this, "authorityService", this.authorityService);
        PropertyCheck.mandatory(this, "nodeService", this.nodeService);
        PropertyCheck.mandatory(this, "siteService", this.siteService);
        PropertyCheck.mandatory(this, "siteHierarchyService", this.siteHierarchyService);
        PropertyCheck.mandatory(this, "systemDefaultMode", this.systemDefaultMode);

        this.policyComponent.bindClassBehaviour(OnAddChildSitePolicy.QNAME, SiteModel.TYPE_SITE,
                new JavaBehaviour(this, "onAddChildSite", NotificationFrequency.EVERY_EVENT));
        this.policyComponent.bindClassBehaviour(BeforeRemoveChildSitePolicy.QNAME, SiteModel.TYPE_SITE,
                new JavaBehaviour(this, "beforeRemoveChildSite", NotificationFrequency.EVERY_EVENT));
        this.policyComponent.bindClassBehaviour(OnUpdatePropertiesPolicy.QNAME, SiteHierarchyModel.ASPECT_HIERARCHY_SITE,
                new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.EVERY_EVENT));
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
     * @param authorityService
     *            the authorityService to set
     */
    public void setAuthorityService(final AuthorityService authorityService)
    {
        this.authorityService = authorityService;
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
     * @param siteHierarchyService
     *            the siteHierarchyService to set
     */
    public void setSiteHierarchyService(final SiteHierarchyService siteHierarchyService)
    {
        this.siteHierarchyService = siteHierarchyService;
    }

    /**
     * @param systemDefaultMode
     *            the systemDefaultMode to set
     */
    public void setSystemDefaultMode(final String systemDefaultMode)
    {
        if (SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_SYSTEM_DEFAULT.equals(systemDefaultMode))
        {
            throw new IllegalArgumentException(
                    SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_SYSTEM_DEFAULT + " cannot be used as the system default mode");
        }
        this.systemDefaultMode = systemDefaultMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAddChildSite(final SiteInfo parentSiteInfo, final SiteInfo childSiteInfo)
    {
        final Map<QName, Serializable> properties = this.nodeService.getProperties(childSiteInfo.getNodeRef());
        final Serializable mode = properties.get(SiteHierarchyModel.PROP_AUTO_MEMBERSHIP_MODE);
        AuthenticationUtil.runAsSystem(() -> {
            this.applyAutoRelation(childSiteInfo, parentSiteInfo, mode);
            return null;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeRemoveChildSite(final SiteInfo parentSiteInfo, final SiteInfo childSiteInfo)
    {
        AuthenticationUtil.runAsSystem(() -> {
            this.removeAnyAutoRelations(childSiteInfo, parentSiteInfo);
            return null;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after)
    {
        if (StoreRef.STORE_REF_WORKSPACE_SPACESSTORE.equals(nodeRef.getStoreRef()))
        {
            final Serializable modeBefore = before.get(SiteHierarchyModel.PROP_AUTO_MEMBERSHIP_MODE);
            final Serializable modeAfter = after.get(SiteHierarchyModel.PROP_AUTO_MEMBERSHIP_MODE);

            if (!EqualsHelper.nullSafeEquals(modeBefore, modeAfter))
            {
                LOGGER.debug("{} changed on {} from {} to {}", SiteHierarchyModel.PROP_AUTO_MEMBERSHIP_MODE, nodeRef, modeBefore,
                        modeAfter);

                final SiteInfo childSiteInfo = this.siteService.getSite(nodeRef);
                if (childSiteInfo != null && EqualsHelper.nullSafeEquals(nodeRef, childSiteInfo.getNodeRef()))
                {
                    AuthenticationUtil.runAsSystem(() -> {
                        final SiteInfo parentSiteInfo = this.siteHierarchyService.getParentSite(childSiteInfo.getShortName());
                        if (parentSiteInfo != null)
                        {
                            this.removeAnyAutoRelations(childSiteInfo, parentSiteInfo);
                            this.applyAutoRelation(childSiteInfo, parentSiteInfo, modeAfter);
                        }
                        else
                        {
                            LOGGER.debug("Site {} has no parent - no need to update anything", childSiteInfo.getShortName());
                        }

                        return null;
                    });
                }
                else
                {
                    LOGGER.warn("{} aspect applied to a non-site", SiteHierarchyModel.ASPECT_HIERARCHY_SITE);
                }
            }
        }
    }

    protected void applyAutoRelation(final SiteInfo childSiteInfo, final SiteInfo parentSiteInfo, final Serializable mode)
    {
        if (mode instanceof String)
        {
            if (SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_NONE.equals(mode))
            {
                LOGGER.debug("Site {} explicitly set to not use auto-relation", childSiteInfo.getShortName());
            }
            else
            {
                String effectiveMode;
                if (SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_SYSTEM_DEFAULT.equals(mode))
                {
                    effectiveMode = this.systemDefaultMode;
                }
                else
                {
                    effectiveMode = (String) mode;
                }

                switch (effectiveMode)
                {
                    case SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_NONE:
                        LOGGER.debug("Site {} set to not use auto-relation", childSiteInfo.getShortName());
                        break;
                    case SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_CHILD_MEMBERS_AS_PARENT_CONSUMERS:
                        this.addMembersAsCustomers(childSiteInfo, parentSiteInfo);
                        break;
                    case SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_PARENT_MEMBERS_AS_CHILD_CONSUMERS:
                        this.addMembersAsCustomers(parentSiteInfo, childSiteInfo);
                        break;
                    default:
                        LOGGER.warn("{} mode {} is an unsupported value - not performing any auto relation of members",
                                SiteHierarchyModel.PROP_AUTO_MEMBERSHIP_MODE, mode);
                }
            }
        }
        else
        {
            LOGGER.warn("{} mode value {} is of an unexpected type - not performing any auto relation of members",
                    SiteHierarchyModel.PROP_AUTO_MEMBERSHIP_MODE, mode);
        }
    }

    protected void addMembersAsCustomers(final SiteInfo membersSourceSite, final SiteInfo consumerTargetSite)
    {
        final String siteGroup = this.siteService.getSiteGroup(membersSourceSite.getShortName());
        final String consumerGroup = this.siteService.getSiteRoleGroup(consumerTargetSite.getShortName(), SiteModel.SITE_CONSUMER);

        if (this.authorityService.authorityExists(consumerGroup))
        {
            LOGGER.debug("Hooking {} into {}", siteGroup, consumerGroup);
            this.authorityService.addAuthority(consumerGroup, siteGroup);
        }
        else
        {
            LOGGER.warn("Site {} does not contain the expected consumer role - unable to hook {} members as consumers",
                    consumerTargetSite.getShortName(), membersSourceSite.getShortName());
        }
    }

    protected void removeAnyAutoRelations(final SiteInfo childSiteInfo, final SiteInfo parentSiteInfo)
    {
        final String parentSiteGroup = this.siteService.getSiteGroup(parentSiteInfo.getShortName());
        final String childSiteGroup = this.siteService.getSiteGroup(childSiteInfo.getShortName());
        final String parentConsumerGroup = this.siteService.getSiteRoleGroup(parentSiteInfo.getShortName(), SiteModel.SITE_CONSUMER);
        final String childConsumerGroup = this.siteService.getSiteRoleGroup(childSiteInfo.getShortName(), SiteModel.SITE_CONSUMER);

        if (this.authorityService.authorityExists(childConsumerGroup))
        {
            LOGGER.debug("Removing potential member group {} from {}", parentSiteGroup, childConsumerGroup);
            this.authorityService.removeAuthority(childConsumerGroup, parentSiteGroup);
        }
        else
        {
            LOGGER.warn("Site {} does not contain the expected consumer role - unable to remove potential parent site members as consumers",
                    childSiteInfo.getShortName());
        }

        if (this.authorityService.authorityExists(parentConsumerGroup))
        {
            LOGGER.debug("Removing potential member group {} from {}", childSiteGroup, parentConsumerGroup);
            this.authorityService.removeAuthority(parentConsumerGroup, childSiteGroup);
        }
        else
        {
            LOGGER.warn("Site {} does not contain the expected consumer role - unable to remove potential child site members as consumers",
                    parentSiteInfo.getShortName());
        }
    }
}
