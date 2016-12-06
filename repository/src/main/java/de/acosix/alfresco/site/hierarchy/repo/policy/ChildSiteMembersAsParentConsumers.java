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
package de.acosix.alfresco.site.hierarchy.repo.policy;

import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import de.acosix.alfresco.site.hierarchy.repo.service.SiteHierarchyServicePolicies.BeforeRemoveChildSitePolicy;
import de.acosix.alfresco.site.hierarchy.repo.service.SiteHierarchyServicePolicies.OnAddChildSitePolicy;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class ChildSiteMembersAsParentConsumers implements InitializingBean, OnAddChildSitePolicy, BeforeRemoveChildSitePolicy
{

    private static final Logger LOGGER = LoggerFactory.getLogger(ChildSiteMembersAsParentConsumers.class);

    protected PolicyComponent policyComponent;

    protected AuthorityService authorityService;

    protected SiteService siteService;

    protected boolean enabled;

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "policyComponent", this.policyComponent);
        PropertyCheck.mandatory(this, "authorityService", this.authorityService);
        PropertyCheck.mandatory(this, "siteService", this.siteService);

        if (this.enabled)
        {
            this.policyComponent.bindClassBehaviour(OnAddChildSitePolicy.QNAME, SiteModel.TYPE_SITE,
                    new JavaBehaviour(this, "onAddChildSite", NotificationFrequency.EVERY_EVENT));
            this.policyComponent.bindClassBehaviour(BeforeRemoveChildSitePolicy.QNAME, SiteModel.TYPE_SITE,
                    new JavaBehaviour(this, "beforeRemoveChildSite", NotificationFrequency.EVERY_EVENT));
        }
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
     * @param siteService
     *            the siteService to set
     */
    public void setSiteService(final SiteService siteService)
    {
        this.siteService = siteService;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAddChildSite(final SiteInfo parentSiteInfo, final SiteInfo childSiteInfo)
    {
        AuthenticationUtil.runAsSystem(() -> {
            final String childSiteGroup = this.siteService.getSiteGroup(childSiteInfo.getShortName());
            final String parentConsumerGroup = this.siteService.getSiteRoleGroup(parentSiteInfo.getShortName(), SiteModel.SITE_CONSUMER);

            if (this.authorityService.authorityExists(parentConsumerGroup))
            {
                LOGGER.debug("Hooking {} into {}", childSiteGroup, parentConsumerGroup);
                this.authorityService.addAuthority(parentConsumerGroup, childSiteGroup);
            }
            else
            {
                LOGGER.warn("Site {} does not contain the expected consumer role - unable to hook child site members as consumers",
                        parentSiteInfo.getShortName());
            }

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
            final String childSiteGroup = this.siteService.getSiteGroup(childSiteInfo.getShortName());
            final String parentConsumerGroup = this.siteService.getSiteRoleGroup(parentSiteInfo.getShortName(), SiteModel.SITE_CONSUMER);

            if (this.authorityService.authorityExists(parentConsumerGroup))
            {
                LOGGER.debug("Removing {} from {}", childSiteGroup, parentConsumerGroup);
                this.authorityService.removeAuthority(parentConsumerGroup, childSiteGroup);
            }
            else
            {
                LOGGER.warn("Site {} does not contain the expected consumer role - unable to remove child site members as consumers",
                        parentSiteInfo.getShortName());
            }

            return null;
        });
    }

}
