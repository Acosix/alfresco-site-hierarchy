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
package de.acosix.alfresco.site.hierarchy.repo.script;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.site.script.ScriptSiteService;
import org.alfresco.repo.site.script.Site;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.PropertyCheck;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.beans.factory.InitializingBean;

import de.acosix.alfresco.site.hierarchy.repo.model.SiteHierarchyModel;
import de.acosix.alfresco.site.hierarchy.repo.service.SiteHierarchyService;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class ScriptSiteHierarchyService extends BaseScopableProcessorExtension implements InitializingBean
{

    protected SiteHierarchyService siteHierarchyService;

    // due to stupid default visibility on the Site constructor we need this script service
    protected ScriptSiteService scriptSiteService;

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet()
    {
        PropertyCheck.mandatory(this, "siteHierarchyService", this.siteHierarchyService);
        PropertyCheck.mandatory(this, "scriptSiteService", this.scriptSiteService);
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
     * @param scriptSiteService
     *            the scriptSiteService to set
     */
    public void setScriptSiteService(final ScriptSiteService scriptSiteService)
    {
        this.scriptSiteService = scriptSiteService;
    }

    /**
     * Lists all the top level {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy sites} in the system.
     *
     * @return the list of top level hierarchy sites ordered by name
     *
     * @see SiteHierarchyService#listTopLevelSites()
     */
    public Scriptable listTopLevelSites()
    {
        final List<SiteInfo> topLevelSites = this.siteHierarchyService.listTopLevelSites();
        final Scriptable topLevelSitesArray = this.makeSitesArray(topLevelSites);
        return topLevelSitesArray;
    }

    /**
     * Retrieves the parent of a {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy site}.
     *
     * @param site
     *            the site for which to retrieve the parent site
     *
     * @return the parent site or {@code null} if site is not part of a hierarchy or a {@link SiteHierarchyModel#ASPECT_TOP_LEVEL_SITE top
     *         level site}
     *
     * @see SiteHierarchyService#getParentSite(String)
     */
    public Site getParentSite(final String site)
    {
        final SiteInfo parentSite = this.siteHierarchyService.getParentSite(site);
        Site parentScriptSite;
        if (parentSite != null)
        {
            parentScriptSite = this.scriptSiteService.getSite(parentSite.getShortName());
        }
        else
        {
            parentScriptSite = null;
        }

        return parentScriptSite;
    }

    /**
     * Retrieves the parent of a {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy site}.
     *
     * @param site
     *            the site for which to retrieve the parent site
     *
     * @return the parent site or {@code null} if site is not part of a hierarchy or a {@link SiteHierarchyModel#ASPECT_TOP_LEVEL_SITE top
     *         level site}
     *
     * @see SiteHierarchyService#getParentSite(String)
     */
    public Site getParentSite(final Site site)
    {
        ParameterCheck.mandatory("site", site);
        final SiteInfo parentSite = this.siteHierarchyService.getParentSite(site.getShortName());
        Site parentScriptSite;
        if (parentSite != null)
        {
            parentScriptSite = this.scriptSiteService.getSite(parentSite.getShortName());
        }
        else
        {
            parentScriptSite = null;
        }

        return parentScriptSite;
    }

    /**
     * Lists all the child {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy sites} of a specific site.
     *
     * @param parentSite
     *            the site of which to list the child sites
     *
     * @return the list of child sites ordered by name
     *
     * @see SiteHierarchyService#listChildSites(String)
     */
    public Scriptable listChildSites(final String parentSite)
    {
        final List<SiteInfo> childSites = this.siteHierarchyService.listChildSites(parentSite);
        final Scriptable childSitesArray = this.makeSitesArray(childSites);
        return childSitesArray;
    }

    /**
     * Lists all the child {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy sites} of a specific site.
     *
     * @param parentSite
     *            the site of which to list the child sites
     *
     * @return the list of child sites ordered by name
     *
     * @see SiteHierarchyService#listChildSites(String)
     */
    public Scriptable listChildSites(final Site parentSite)
    {
        ParameterCheck.mandatory("parentSite", parentSite);

        final List<SiteInfo> childSites = this.siteHierarchyService.listChildSites(parentSite.getShortName());
        final Scriptable childSitesArray = this.makeSitesArray(childSites);
        return childSitesArray;
    }

    /**
     * Adds a stand-alone / top level site as a child of another site. This operation will promote either site to a
     * {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy site} if they have not yet been marked as such. Sites that already have
     * been added to a parent site cannot be added to another.
     *
     * @param parentSite
     *            the site to which to add the child
     * @param childSite
     *            the site to add as the new child
     *
     * @see SiteHierarchyService#addChildSite(String, String)
     */
    public void addChildSite(final String parentSite, final String childSite)
    {
        this.siteHierarchyService.addChildSite(parentSite, childSite);
    }

    /**
     * Adds a stand-alone / top level site as a child of another site. This operation will promote either site to a
     * {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy site} if they have not yet been marked as such. Sites that already have
     * been added to a parent site cannot be added to another.
     *
     * @param parentSite
     *            the site to which to add the child
     * @param childSite
     *            the site to add as the new child
     *
     * @see SiteHierarchyService#addChildSite(String, String)
     */
    public void addChildSite(final Site parentSite, final Site childSite)
    {
        ParameterCheck.mandatory("parentSite", parentSite);
        ParameterCheck.mandatory("childSite", childSite);

        this.siteHierarchyService.addChildSite(parentSite.getShortName(), childSite.getShortName());
    }

    /**
     * Removes a site as a child of another.
     *
     * @param parentSite
     *            the site from which to remove the child
     * @param childSite
     *            the site to remove
     *
     * @see SiteHierarchyService#removeChildSite(String, String)
     */
    public void removeChildSite(final String parentSite, final String childSite)
    {
        this.siteHierarchyService.removeChildSite(parentSite, childSite);
    }

    /**
     * Removes a site as a child of another.
     *
     * @param parentSite
     *            the site from which to remove the child
     * @param childSite
     *            the site to remove
     *
     * @see SiteHierarchyService#removeChildSite(String, String)
     */
    public void removeChildSite(final Site parentSite, final Site childSite)
    {
        ParameterCheck.mandatory("parentSite", parentSite);
        ParameterCheck.mandatory("childSite", childSite);

        this.siteHierarchyService.removeChildSite(parentSite.getShortName(), childSite.getShortName());
    }

    protected Scriptable makeSitesArray(final List<SiteInfo> siteInfos)
    {
        final List<Site> sites = new ArrayList<>(siteInfos.size());
        for (final SiteInfo siteInfo : siteInfos)
        {
            final Site site = this.scriptSiteService.getSite(siteInfo.getShortName());
            sites.add(site);
        }

        final Scriptable sitesArray = Context.getCurrentContext().newArray(this.getScope(), sites.toArray(new Object[0]));
        return sitesArray;
    }
}
