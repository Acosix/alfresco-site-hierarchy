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
package de.acosix.alfresco.site.hierarchy.repo.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.LocalResteasyProviderFactory;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.extensions.surf.util.URLEncoder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import de.acosix.alfresco.rest.client.api.AuthenticationV1;
import de.acosix.alfresco.rest.client.jackson.RestAPIBeanDeserializerModifier;
import de.acosix.alfresco.rest.client.model.authentication.TicketEntity;
import de.acosix.alfresco.rest.client.model.authentication.TicketRequest;
import de.acosix.alfresco.rest.client.resteasy.MultiValuedParamConverterProvider;
import de.acosix.alfresco.site.hierarchy.repo.entities.HierarchicSite;
import de.acosix.alfresco.site.hierarchy.repo.entities.HierarchicSiteManagementRequest;
import de.acosix.alfresco.site.hierarchy.repo.entities.HierarchicSiteManagementResponse;
import de.acosix.alfresco.site.hierarchy.repo.entities.TopLevelSites;
import de.acosix.alfresco.site.hierarchy.repo.model.SiteHierarchyModel;

/**
 * @author Axel Faust
 */
public class ManagementViaWebScriptsTest
{

    private static final String baseUrlServer = "http://localhost:8082/alfresco";

    private static ResteasyClient client;

    @BeforeClass
    public static void setup()
    {
        final SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new RestAPIBeanDeserializerModifier());

        final ResteasyJackson2Provider resteasyJacksonProvider = new ResteasyJackson2Provider();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.registerModule(module);
        resteasyJacksonProvider.setMapper(mapper);

        final LocalResteasyProviderFactory resteasyProviderFactory = new LocalResteasyProviderFactory(new ResteasyProviderFactoryImpl());
        resteasyProviderFactory.register(resteasyJacksonProvider);
        // will cause a warning regarding Jackson provider which is already registered
        RegisterBuiltin.register(resteasyProviderFactory);
        resteasyProviderFactory.register(new MultiValuedParamConverterProvider());

        client = new ResteasyClientBuilderImpl().providerFactory(resteasyProviderFactory).build();
    }

    @Test
    public void defaultSiteWithoutHierarchy()
    {
        final ResteasyWebTarget targetServer = client.target(UriBuilder.fromPath(baseUrlServer));

        final TicketRequest rq = new TicketRequest();
        rq.setUserId("admin");
        rq.setPassword("admin");

        final AuthenticationV1 authenticationAPI = targetServer.proxy(AuthenticationV1.class);
        final TicketEntity ticket = authenticationAPI.createTicket(rq);

        Assert.assertNotNull("Ticket should have been obtained", ticket);

        final WebTarget webTarget = client.target(UriBuilder.fromPath(baseUrlServer + "/s"));

        final HierarchicSiteManagementRequest siteToCreate = new HierarchicSiteManagementRequest();
        siteToCreate.setShortName("defaultSiteWithoutHierarchy");
        siteToCreate.setSitePreset("site-dashboard");
        siteToCreate.setTitle("Default Site");
        siteToCreate.setDescription("Default site without hierarchy data");
        siteToCreate.setIsPublic(Boolean.TRUE);

        this.createAndCheckSite(webTarget, ticket.getId(), siteToCreate);
        this.checkHierarchySite(webTarget, ticket.getId(), siteToCreate, null, null,
                SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_NEVER,
                SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_NONE);
    }

    @Test
    public void standaloneSite()
    {
        final ResteasyWebTarget targetServer = client.target(UriBuilder.fromPath(baseUrlServer));

        final TicketRequest rq = new TicketRequest();
        rq.setUserId("admin");
        rq.setPassword("admin");

        final AuthenticationV1 authenticationAPI = targetServer.proxy(AuthenticationV1.class);
        final TicketEntity ticket = authenticationAPI.createTicket(rq);

        Assert.assertNotNull("Ticket should have been obtained", ticket);

        final WebTarget webTarget = client.target(UriBuilder.fromPath(baseUrlServer + "/s"));

        final HierarchicSiteManagementRequest siteToCreate = new HierarchicSiteManagementRequest();
        siteToCreate.setShortName("standaloneSite");
        siteToCreate.setSitePreset("site-dashboard");
        siteToCreate.setTitle("Standalone Site");
        siteToCreate.setDescription("Standalone site - never shown");
        siteToCreate.setIsPublic(Boolean.TRUE);
        siteToCreate.setAco6sh_showInHierarchyMode(SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_NEVER);

        this.createAndCheckSite(webTarget, ticket.getId(), siteToCreate);
        this.checkHierarchySite(webTarget, ticket.getId(), siteToCreate, null, null, null, null);
        this.checkSiteInTopLevelSites(webTarget, ticket.getId(), siteToCreate.getShortName(), false);

        final HierarchicSiteManagementRequest siteToUpdate = new HierarchicSiteManagementRequest();
        siteToUpdate.setShortName(siteToCreate.getShortName());
        siteToUpdate.setDescription("Standalone site - always shown");
        siteToUpdate.setAco6sh_showInHierarchyMode(SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS);
        this.updateAndCheckSite(webTarget, ticket.getId(), siteToUpdate, null, null, null);

        this.checkHierarchySite(webTarget, ticket.getId(), siteToUpdate, null, null, null, null);
        this.checkSiteInTopLevelSites(webTarget, ticket.getId(), siteToCreate.getShortName(), true);

        siteToUpdate.setDescription("Standalone site - conditionally shown");
        siteToUpdate.setAco6sh_showInHierarchyMode(SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_IF_PARENT_OR_CHILD);
        this.updateAndCheckSite(webTarget, ticket.getId(), siteToUpdate, null, null, null);

        this.checkHierarchySite(webTarget, ticket.getId(), siteToUpdate, null, null, null, null);
        this.checkSiteInTopLevelSites(webTarget, ticket.getId(), siteToCreate.getShortName(), false);
    }

    @Test
    public void relateExistingSitesAsParentAndChild()
    {
        final ResteasyWebTarget targetServer = client.target(UriBuilder.fromPath(baseUrlServer));

        final TicketRequest rq = new TicketRequest();
        rq.setUserId("admin");
        rq.setPassword("admin");

        final AuthenticationV1 authenticationAPI = targetServer.proxy(AuthenticationV1.class);
        final TicketEntity ticket = authenticationAPI.createTicket(rq);

        Assert.assertNotNull("Ticket should have been obtained", ticket);

        final WebTarget webTarget = client.target(UriBuilder.fromPath(baseUrlServer + "/s"));

        final HierarchicSiteManagementRequest parentSiteToCreate = new HierarchicSiteManagementRequest();
        parentSiteToCreate.setShortName("existingParentSite");
        parentSiteToCreate.setSitePreset("site-dashboard");
        parentSiteToCreate.setTitle("Parent Site");
        parentSiteToCreate.setDescription("Parent site - always shown");
        parentSiteToCreate.setIsPublic(Boolean.TRUE);
        parentSiteToCreate.setAco6sh_showInHierarchyMode(SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS);

        this.createAndCheckSite(webTarget, ticket.getId(), parentSiteToCreate);
        final HierarchicSite parentSite = this.checkHierarchySite(webTarget, ticket.getId(), parentSiteToCreate, null, null, null, null);
        this.checkSiteInTopLevelSites(webTarget, ticket.getId(), parentSiteToCreate.getShortName(), true);

        final HierarchicSiteManagementRequest childSiteToCreate = new HierarchicSiteManagementRequest();
        childSiteToCreate.setShortName("existingChildSite");
        childSiteToCreate.setSitePreset("site-dashboard");
        childSiteToCreate.setTitle("Child Site");
        childSiteToCreate.setDescription("Child site - always shown");
        childSiteToCreate.setIsPublic(Boolean.TRUE);
        childSiteToCreate.setAco6sh_showInHierarchyMode(SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS);

        this.createAndCheckSite(webTarget, ticket.getId(), childSiteToCreate);
        this.checkHierarchySite(webTarget, ticket.getId(), childSiteToCreate, null, null, null, null);
        this.checkSiteInTopLevelSites(webTarget, ticket.getId(), childSiteToCreate.getShortName(), true);

        final HierarchicSiteManagementRequest childSiteToUpdate = new HierarchicSiteManagementRequest();
        childSiteToUpdate.setShortName(childSiteToCreate.getShortName());
        childSiteToUpdate.setAco6sh_parentSite_added(parentSite.getNodeRef());
        this.updateAndCheckSite(webTarget, ticket.getId(), childSiteToUpdate, null,
                SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS,
                SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_SYSTEM_DEFAULT);

        this.checkSiteInTopLevelSites(webTarget, ticket.getId(), parentSiteToCreate.getShortName(), true);
        this.checkSiteInTopLevelSites(webTarget, ticket.getId(), childSiteToCreate.getShortName(), false);

        this.checkHierarchySite(webTarget, ticket.getId(), parentSiteToCreate, null, Collections.singletonList(childSiteToCreate.getShortName()),
                null, null);
        this.checkHierarchySite(webTarget, ticket.getId(), childSiteToCreate, parentSiteToCreate.getShortName(), null, null,
                SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_SYSTEM_DEFAULT);

        childSiteToUpdate.setAco6sh_parentSite_added(null);
        childSiteToUpdate.setAco6sh_parentSite_removed(parentSite.getNodeRef());
        this.updateAndCheckSite(webTarget, ticket.getId(), childSiteToUpdate, null, SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS,
                null);

        this.checkSiteInTopLevelSites(webTarget, ticket.getId(), parentSiteToCreate.getShortName(), true);
        this.checkSiteInTopLevelSites(webTarget, ticket.getId(), childSiteToCreate.getShortName(), true);

        this.checkHierarchySite(webTarget, ticket.getId(), parentSiteToCreate, null, null, null, null);
        this.checkHierarchySite(webTarget, ticket.getId(), childSiteToCreate, null, null, null, null);
    }

    protected HierarchicSite checkHierarchySite(final WebTarget webTarget, final String ticket, final HierarchicSiteManagementRequest site,
            final String parentSite, final List<String> childSites, final String showInHierarchyMode, final String autoMembershipMode)
    {
        final HierarchicSite hierarchicSite = webTarget.path("/acosix/api/sites/" + URLEncoder.encode(site.getShortName()) + "/hierarchy")
                .queryParam("alf_ticket", ticket).request(MediaType.APPLICATION_JSON).get(HierarchicSite.class);

        if (parentSite == null)
        {
            Assert.assertNull("Site should not have a parent site", hierarchicSite.getParent());
        }
        else
        {
            Assert.assertEquals("Parent site does not match", parentSite, hierarchicSite.getParent().getShortName());
        }

        if (childSites == null || childSites.isEmpty())
        {
            Assert.assertTrue("Site should not have any children",
                    hierarchicSite.getChildren() == null || hierarchicSite.getChildren().isEmpty());
        }
        else
        {
            Assert.assertTrue("Site should have children", hierarchicSite.getChildren() != null && !hierarchicSite.getChildren().isEmpty());
            Assert.assertEquals("Number of child sites does not match", childSites.size(), hierarchicSite.getChildren().size());

            final List<String> foundChildSites = new ArrayList<>();
            hierarchicSite.getChildren().forEach(childSite -> {
                foundChildSites.add(childSite.getShortName());
            });

            Assert.assertTrue("Site should have expected site(s) as children", foundChildSites.containsAll(childSites));
        }

        Assert.assertEquals("ShowInHierarchyMode does not match expectation",
                site.getAco6sh_showInHierarchyMode() != null ? site.getAco6sh_showInHierarchyMode()
                        : (showInHierarchyMode != null ? showInHierarchyMode : SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_NEVER),
                hierarchicSite.getShowInHierarchyMode());
        Assert.assertEquals("AutoMembershipMode does not match expectation",
                site.getAco6sh_autoMembershipMode() != null ? site.getAco6sh_autoMembershipMode()
                        : (autoMembershipMode != null ? autoMembershipMode : SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_NONE),
                hierarchicSite.getAutoMembershipMode());

        return hierarchicSite;
    }

    protected void checkSiteInTopLevelSites(final WebTarget webTarget, final Object ticket, final String siteShortName,
            final boolean expectation)
    {
        final TopLevelSites topLevelSites = webTarget.path("/acosix/api/sites/topLevelSites").queryParam("alf_ticket", ticket)
                .request(MediaType.APPLICATION_JSON).get(TopLevelSites.class);

        Assert.assertNotNull("List of top-level sites should never be null", topLevelSites.getSites());
        if (expectation)
        {
            Assert.assertFalse("List of top-level sites should not be empty", topLevelSites.getSites().isEmpty());
        }

        final AtomicBoolean containsSite = new AtomicBoolean(false);
        topLevelSites.getSites().forEach(site -> {
            if (site.getShortName().equals(siteShortName))
            {
                containsSite.set(true);
            }
        });
        Assert.assertEquals("Site top-level status listing does not match expectation", expectation, containsSite.get());
    }

    protected HierarchicSiteManagementResponse createAndCheckSite(final WebTarget webTarget, final Object ticket,
            final HierarchicSiteManagementRequest siteToCreate)
    {
        final HierarchicSiteManagementResponse site = webTarget.path("/api/sites").queryParam("alf_ticket", ticket)
                .request(MediaType.APPLICATION_JSON).post(Entity.json(siteToCreate), HierarchicSiteManagementResponse.class);

        Assert.assertEquals("Short name does not match", siteToCreate.getShortName(), site.getShortName());
        Assert.assertEquals("Site preset does not match", siteToCreate.getSitePreset(), site.getSitePreset());
        Assert.assertEquals("Title does not match", siteToCreate.getTitle(), site.getTitle());
        if (siteToCreate.getDescription() != null)
        {
            Assert.assertEquals("Description does not match", siteToCreate.getDescription(), site.getDescription());
        }
        if (siteToCreate.getIsPublic() != null)
        {
            Assert.assertEquals("isPublic does not match", siteToCreate.getIsPublic(), site.getIsPublic());
        }

        final String visibility = siteToCreate.getVisibility();
        Assert.assertEquals("Visibility does not match expectation",
                visibility != null ? visibility : (Boolean.TRUE.equals(siteToCreate.getIsPublic()) ? "PUBLIC" : "PRIVATE"),
                site.getVisibility());

        final String parentSiteShortName = siteToCreate.getAco6sh_parentSite();
        final String parentSiteNodeRef = siteToCreate.getAco6sh_parentSite_added();
        if (parentSiteShortName != null)
        {
            Assert.assertNotNull("Parent site should have been set", site.getAco6sh_parentSite());
            Assert.assertEquals("Parent site does not match expectation", parentSiteShortName, site.getAco6sh_parentSite().getShortName());
        }
        else if (parentSiteNodeRef != null)
        {
            Assert.assertNotNull("Parent site should have been set", site.getAco6sh_parentSite());
            Assert.assertEquals("Parent site does not match expectation", parentSiteNodeRef, site.getAco6sh_parentSite().getNodeRef());
        }
        else
        {
            Assert.assertNull("Parent site should have been null", site.getAco6sh_parentSite());
        }

        final String autoMembershipMode = siteToCreate.getAco6sh_autoMembershipMode();
        final String showInHierarchyMode = siteToCreate.getAco6sh_showInHierarchyMode();
        if (autoMembershipMode != null)
        {
            Assert.assertEquals("AutoMembershipMode does not match expectation", autoMembershipMode, site.getAco6sh_autoMembershipMode());
        }
        else if (parentSiteShortName != null || parentSiteNodeRef != null)
        {
            // check model default value
            Assert.assertEquals("AutoMembershipMode does not match model default value",
                    SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_SYSTEM_DEFAULT, site.getAco6sh_autoMembershipMode());
        }
        else
        {
            Assert.assertNull("AutoMembershipMode should have been null", site.getAco6sh_autoMembershipMode());
        }

        if (showInHierarchyMode != null)
        {
            Assert.assertEquals("ShowInHierarchyMode does not match expectation", showInHierarchyMode,
                    site.getAco6sh_showInHierarchyMode());
        }
        else if (parentSiteShortName != null || parentSiteNodeRef != null || autoMembershipMode != null)
        {
            // check model default value
            Assert.assertEquals("ShowInHierarchyMode does not match model default value",
                    SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_IF_PARENT_OR_CHILD, site.getAco6sh_showInHierarchyMode());
        }
        else
        {
            Assert.assertNull("ShowInHierarchyMode should have been null", site.getAco6sh_showInHierarchyMode());
        }

        return site;
    }

    protected HierarchicSiteManagementResponse updateAndCheckSite(final WebTarget webTarget, final Object ticket,
            final HierarchicSiteManagementRequest siteToUpdate, final String expectedParentSiteShortName,
            final String expectedShowInHierarchyMode, final String expectedAutoMembershipMode)
    {
        final HierarchicSiteManagementResponse site = webTarget.path("/api/sites").path(siteToUpdate.getShortName())
                .queryParam("alf_ticket", ticket).request(MediaType.APPLICATION_JSON)
                .put(Entity.json(siteToUpdate), HierarchicSiteManagementResponse.class);

        if (siteToUpdate.getTitle() != null)
        {
            Assert.assertEquals("Title does not match", siteToUpdate.getTitle(), site.getTitle());
        }
        if (siteToUpdate.getDescription() != null)
        {
            Assert.assertEquals("Description does not match", siteToUpdate.getDescription(), site.getDescription());
        }

        final Boolean isPublic = siteToUpdate.getIsPublic();
        if (isPublic != null)
        {
            Assert.assertEquals("isPublic does not match", isPublic, site.getIsPublic());
        }
        final String visibility = siteToUpdate.getVisibility();
        if (visibility != null || isPublic != null)
        {
            Assert.assertEquals("Visibility does not match expectation",
                    visibility != null ? visibility : (Boolean.TRUE.equals(isPublic) ? "PUBLIC" : "PRIVATE"), site.getVisibility());
        }

        final String parentSiteShortName = siteToUpdate.getAco6sh_parentSite();
        final String parentSiteNodeRef = siteToUpdate.getAco6sh_parentSite_added();
        if (parentSiteShortName != null || (parentSiteNodeRef == null && expectedParentSiteShortName != null))
        {
            Assert.assertNotNull("Parent site should have been set", site.getAco6sh_parentSite());
            Assert.assertEquals("Parent site does not match expectation",
                    parentSiteShortName != null ? parentSiteShortName : expectedParentSiteShortName,
                    site.getAco6sh_parentSite().getShortName());
        }
        else if (parentSiteNodeRef != null)
        {
            Assert.assertNotNull("Parent site should have been set", site.getAco6sh_parentSite());
            Assert.assertEquals("Parent site does not match expectation", parentSiteNodeRef, site.getAco6sh_parentSite().getNodeRef());
        }
        else
        {
            Assert.assertNull("Parent site should have been null", site.getAco6sh_parentSite());
        }

        final String autoMembershipMode = siteToUpdate.getAco6sh_autoMembershipMode();
        if (autoMembershipMode != null || expectedAutoMembershipMode != null)
        {
            Assert.assertEquals("AutoMembershipMode does not match expectation",
                    autoMembershipMode != null ? autoMembershipMode : expectedAutoMembershipMode, site.getAco6sh_autoMembershipMode());
        }
        else
        {
            Assert.assertNull("AutoMembershipMode should have been null", site.getAco6sh_autoMembershipMode());
        }

        final String showInHierarchyMode = siteToUpdate.getAco6sh_showInHierarchyMode();
        if (showInHierarchyMode != null || expectedShowInHierarchyMode != null)
        {
            Assert.assertEquals("ShowInHierarchyMode does not match expectation",
                    showInHierarchyMode != null ? showInHierarchyMode : expectedShowInHierarchyMode, site.getAco6sh_showInHierarchyMode());
        }
        else
        {
            Assert.assertNull("ShowInHierarchyMode should have been null", site.getAco6sh_showInHierarchyMode());
        }

        return site;
    }
}
