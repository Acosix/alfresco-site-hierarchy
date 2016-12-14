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
import java.util.Arrays;

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

        final Site siteToCreate = new Site();
        siteToCreate.setShortName("defaultSiteWithoutHierarchy");
        siteToCreate.setSitePreset("site-dashboard");
        siteToCreate.setTitle("Default Site");
        siteToCreate.setDescription("Default site without hierarchy data");
        siteToCreate.setIsPublic(Boolean.TRUE);

        this.createAndCheckSite(webTarget, ticket, siteToCreate);

        final HierarchicSite hierarchicSite = webTarget
                .path("/acosix/api/sites/" + URLEncoder.encode(siteToCreate.getShortName()) + "/hierarchy").queryParam("alf_ticket", ticket)
                .request(MediaType.APPLICATION_JSON).get(HierarchicSite.class);

        Assert.assertEquals("Site should have setting to never be displayed in hierarchy",
                SiteHierarchyModel.CONSTRAINT_SHOW_IN_HIERARCHY_MODES_NEVER, hierarchicSite.getShowInHierarchyMode());
        Assert.assertEquals("Site should have setting to not link members", SiteHierarchyModel.CONSTRAINT_AUTO_MEMBERSHIP_MODES_NONE,
                hierarchicSite.getAutoMembershipMode());
        Assert.assertNull("Site should not have a parent site", hierarchicSite.getParent());
        Assert.assertTrue("Site should not have any children",
                hierarchicSite.getChildren() == null || hierarchicSite.getChildren().isEmpty());
    }

    protected Site createAndCheckSite(final WebTarget webTarget, final Object ticket, final Site siteToCreate)
    {
        final Site site = webTarget.path("/api/sites").queryParam("alf_ticket", ticket).request(MediaType.APPLICATION_JSON)
                .post(Entity.json(siteToCreate), Site.class);

        Assert.assertEquals("Short name does not match", siteToCreate.getShortName(), site.getShortName());
        Assert.assertEquals("Site preset does not match", siteToCreate.getSitePreset(), site.getSitePreset());
        Assert.assertEquals("Title does not match", siteToCreate.getTitle(), site.getTitle());
        Assert.assertEquals("Description does not match", siteToCreate.getDescription(), site.getDescription());
        Assert.assertEquals("isPublic does not match", siteToCreate.getIsPublic(), site.getIsPublic());
        final String visibility = siteToCreate.getVisibility();
        Assert.assertEquals("isVisibility does not match",
                visibility != null ? visibility : (Boolean.TRUE.equals(siteToCreate.getIsPublic()) ? "PUBLIC" : "PRIVATE"),
                site.getVisibility());

        return site;
    }
}
