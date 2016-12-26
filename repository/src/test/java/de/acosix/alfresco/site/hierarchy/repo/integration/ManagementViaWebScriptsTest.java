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
package de.acosix.alfresco.site.hierarchy.repo.integration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.extensions.surf.util.URLEncoder;

import de.acosix.alfresco.site.hierarchy.repo.entities.HierarchicSite;
import de.acosix.alfresco.site.hierarchy.repo.entities.HierarchicSiteManagementRequest;
import de.acosix.alfresco.site.hierarchy.repo.entities.HierarchicSiteManagementResponse;
import de.acosix.alfresco.site.hierarchy.repo.entities.TopLevelSites;
import de.acosix.alfresco.site.hierarchy.repo.model.SiteHierarchyModel;
import de.acosix.alfresco.utility.repo.entities.LoginRequest;
import de.acosix.alfresco.utility.repo.entities.LoginTicketResponse;
import de.acosix.alfresco.utility.repo.entities.Site;
import de.acosix.alfresco.utility.repo.jaxrs.AcosixResteasyJacksonProvider;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
@RunWith(Arquillian.class)
public class ManagementViaWebScriptsTest
{

    @Deployment
    public static WebArchive create()
    {
        final PomEquippedResolveStage configureResolverViaPlugin = Maven.configureResolverViaPlugin();
        final File warFile = configureResolverViaPlugin.resolve("org.alfresco:alfresco:war:?").withoutTransitivity().asSingleFile();
        final File[] libraries = configureResolverViaPlugin
                .resolve(Arrays.asList("org.alfresco:alfresco-repository:jar:h2scripts:?", "com.h2database:h2:jar:?",
                        "de.acosix.alfresco.utility:de.acosix.alfresco.utility.common:jar:?",
                        "de.acosix.alfresco.utility:de.acosix.alfresco.utility.repo:jar:installable:?"))
                .withoutTransitivity().asFile();
        final WebArchive archive = ShrinkWrap.createFromZipFile(WebArchive.class, warFile);
        archive.addAsLibraries(libraries);

        archive.addAsLibrary("installable-de.acosix.alfresco.site.hierarchy.repo.jar");

        archive.addAsResource("configRoot/alfresco-global.properties", "alfresco-global.properties");
        archive.addAsResource("configRoot/log4j.properties", "log4j.properties");
        archive.addAsResource("configRoot/alfresco/extension/dev-log4j.properties", "alfresco/extension/dev-log4j.properties");
        return archive;
    }

    @Test
    @RunAsClient
    public void defaultSiteWithoutHierarchy(@ArquillianResteasyResource(value = "s") final WebTarget webTarget)
    {
        if (webTarget instanceof ResteasyWebTarget)
        {
            final Configuration configuration = webTarget.getConfiguration();
            if (configuration instanceof ClientConfiguration)
            {
                ((ClientConfiguration) configuration).register(AcosixResteasyJacksonProvider.class);
            }
        }

        final LoginRequest rq = new LoginRequest();
        rq.setUsername("admin");
        rq.setPassword("admin");

        final LoginTicketResponse rs = webTarget.path("/api/login").request(MediaType.APPLICATION_JSON).post(Entity.json(rq),
                LoginTicketResponse.class);
        Assert.assertNotNull("Login repsonse does not contain data", rs.getData());
        final String ticket = rs.getData().getTicket();
        Assert.assertNotNull("Ticket should have been obtained", ticket);

        final HierarchicSiteManagementRequest siteToCreate = new HierarchicSiteManagementRequest();
        siteToCreate.setShortName("defaultSiteWithoutHierarchy");
        siteToCreate.setSitePreset("site-dashboard");
        siteToCreate.setTitle("Default Site");
        siteToCreate.setDescription("Default site without hierarchy data");
        siteToCreate.setIsPublic(Boolean.TRUE);

        this.createAndCheckSite(webTarget, ticket, siteToCreate);
        checkHierarchySite(webTarget, ticket, siteToCreate, null, null, SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_NEVER,
                SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_NONE);
    }

    @Test
    @RunAsClient
    public void standaloneSite(@ArquillianResteasyResource(value = "s") final WebTarget webTarget)
    {
        if (webTarget instanceof ResteasyWebTarget)
        {
            final Configuration configuration = webTarget.getConfiguration();
            if (configuration instanceof ClientConfiguration)
            {
                ((ClientConfiguration) configuration).register(AcosixResteasyJacksonProvider.class);
            }
        }

        final LoginRequest rq = new LoginRequest();
        rq.setUsername("admin");
        rq.setPassword("admin");

        final LoginTicketResponse rs = webTarget.path("/api/login").request(MediaType.APPLICATION_JSON).post(Entity.json(rq),
                LoginTicketResponse.class);
        Assert.assertNotNull("Login repsonse does not contain data", rs.getData());
        final String ticket = rs.getData().getTicket();
        Assert.assertNotNull("Ticket should have been obtained", ticket);

        final HierarchicSiteManagementRequest siteToCreate = new HierarchicSiteManagementRequest();
        siteToCreate.setShortName("standaloneSite");
        siteToCreate.setSitePreset("site-dashboard");
        siteToCreate.setTitle("Standalone Site");
        siteToCreate.setDescription("Standalone site - never shown");
        siteToCreate.setIsPublic(Boolean.TRUE);
        siteToCreate.setAco6sh_showInHierarchyMode(SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_NEVER);

        this.createAndCheckSite(webTarget, ticket, siteToCreate);
        this.checkHierarchySite(webTarget, ticket, siteToCreate, null, null, null, null);
        this.checkSiteInTopLevelSites(webTarget, ticket, siteToCreate.getShortName(), false);

        final HierarchicSiteManagementRequest siteToUpdate = new HierarchicSiteManagementRequest();
        siteToUpdate.setShortName(siteToCreate.getShortName());
        siteToUpdate.setDescription("Standalone site - always shown");
        siteToUpdate.setAco6sh_showInHierarchyMode(SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS);
        this.updateAndCheckSite(webTarget, ticket, siteToUpdate, null, null, null);

        this.checkHierarchySite(webTarget, ticket, siteToUpdate, null, null, null, null);
        this.checkSiteInTopLevelSites(webTarget, ticket, siteToCreate.getShortName(), true);

        siteToUpdate.setDescription("Standalone site - conditionally shown");
        siteToUpdate.setAco6sh_showInHierarchyMode(SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_IF_PARENT_OR_CHILD);
        this.updateAndCheckSite(webTarget, ticket, siteToUpdate, null, null, null);

        this.checkHierarchySite(webTarget, ticket, siteToUpdate, null, null, null, null);
        this.checkSiteInTopLevelSites(webTarget, ticket, siteToCreate.getShortName(), false);
    }

    @Test
    @RunAsClient
    public void relateExistingSitesAsParentAndChild(@ArquillianResteasyResource(value = "s") final WebTarget webTarget)
    {
        if (webTarget instanceof ResteasyWebTarget)
        {
            final Configuration configuration = webTarget.getConfiguration();
            if (configuration instanceof ClientConfiguration)
            {
                ((ClientConfiguration) configuration).register(AcosixResteasyJacksonProvider.class);
            }
        }

        final LoginRequest rq = new LoginRequest();
        rq.setUsername("admin");
        rq.setPassword("admin");

        final LoginTicketResponse rs = webTarget.path("/api/login").request(MediaType.APPLICATION_JSON).post(Entity.json(rq),
                LoginTicketResponse.class);
        Assert.assertNotNull("Login repsonse does not contain data", rs.getData());
        final String ticket = rs.getData().getTicket();
        Assert.assertNotNull("Ticket should have been obtained", ticket);

        final HierarchicSiteManagementRequest parentSiteToCreate = new HierarchicSiteManagementRequest();
        parentSiteToCreate.setShortName("existingParentSite");
        parentSiteToCreate.setSitePreset("site-dashboard");
        parentSiteToCreate.setTitle("Parent Site");
        parentSiteToCreate.setDescription("Parent site - always shown");
        parentSiteToCreate.setIsPublic(Boolean.TRUE);
        parentSiteToCreate.setAco6sh_showInHierarchyMode(SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS);

        this.createAndCheckSite(webTarget, ticket, parentSiteToCreate);
        final HierarchicSite parentSite = this.checkHierarchySite(webTarget, ticket, parentSiteToCreate, null, null, null, null);
        this.checkSiteInTopLevelSites(webTarget, ticket, parentSiteToCreate.getShortName(), true);

        final HierarchicSiteManagementRequest childSiteToCreate = new HierarchicSiteManagementRequest();
        childSiteToCreate.setShortName("existingChildSite");
        childSiteToCreate.setSitePreset("site-dashboard");
        childSiteToCreate.setTitle("Child Site");
        childSiteToCreate.setDescription("Child site - always shown");
        childSiteToCreate.setIsPublic(Boolean.TRUE);
        childSiteToCreate.setAco6sh_showInHierarchyMode(SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS);

        this.createAndCheckSite(webTarget, ticket, childSiteToCreate);
        this.checkHierarchySite(webTarget, ticket, childSiteToCreate, null, null, null, null);
        this.checkSiteInTopLevelSites(webTarget, ticket, childSiteToCreate.getShortName(), true);

        final HierarchicSiteManagementRequest childSiteToUpdate = new HierarchicSiteManagementRequest();
        childSiteToUpdate.setShortName(childSiteToCreate.getShortName());
        childSiteToUpdate.setAco6sh_parentSite_added(parentSite.getNodeRef());
        this.updateAndCheckSite(webTarget, ticket, childSiteToUpdate, null, SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS,
                SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_SYSTEM_DEFAULT);

        this.checkSiteInTopLevelSites(webTarget, ticket, parentSiteToCreate.getShortName(), true);
        this.checkSiteInTopLevelSites(webTarget, ticket, childSiteToCreate.getShortName(), false);

        this.checkHierarchySite(webTarget, ticket, parentSiteToCreate, null, Collections.singletonList(childSiteToCreate.getShortName()),
                null, null);
        this.checkHierarchySite(webTarget, ticket, childSiteToCreate, parentSiteToCreate.getShortName(), null, null,
                SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_SYSTEM_DEFAULT);

        childSiteToUpdate.setAco6sh_parentSite_added(null);
        childSiteToUpdate.setAco6sh_parentSite_removed(parentSite.getNodeRef());
        this.updateAndCheckSite(webTarget, ticket, childSiteToUpdate, null, SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS,
                null);

        this.checkSiteInTopLevelSites(webTarget, ticket, parentSiteToCreate.getShortName(), true);
        this.checkSiteInTopLevelSites(webTarget, ticket, childSiteToCreate.getShortName(), true);

        this.checkHierarchySite(webTarget, ticket, parentSiteToCreate, null, null, null, null);
        this.checkHierarchySite(webTarget, ticket, childSiteToCreate, null, null, null, null);
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

        String parentSiteShortName = siteToCreate.getAco6sh_parentSite();
        String parentSiteNodeRef = siteToCreate.getAco6sh_parentSite_added();
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

        String autoMembershipMode = siteToCreate.getAco6sh_autoMembershipMode();
        String showInHierarchyMode = siteToCreate.getAco6sh_showInHierarchyMode();
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

        String parentSiteShortName = siteToUpdate.getAco6sh_parentSite();
        String parentSiteNodeRef = siteToUpdate.getAco6sh_parentSite_added();
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

        String autoMembershipMode = siteToUpdate.getAco6sh_autoMembershipMode();
        if (autoMembershipMode != null || expectedAutoMembershipMode != null)
        {
            Assert.assertEquals("AutoMembershipMode does not match expectation",
                    autoMembershipMode != null ? autoMembershipMode : expectedAutoMembershipMode, site.getAco6sh_autoMembershipMode());
        }
        else
        {
            Assert.assertNull("AutoMembershipMode should have been null", site.getAco6sh_autoMembershipMode());
        }

        String showInHierarchyMode = siteToUpdate.getAco6sh_showInHierarchyMode();
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
