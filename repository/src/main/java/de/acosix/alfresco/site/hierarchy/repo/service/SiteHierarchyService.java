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

import java.util.List;

import org.alfresco.service.Auditable;
import org.alfresco.service.cmr.site.SiteInfo;

import de.acosix.alfresco.site.hierarchy.repo.model.SiteHierarchyModel;

/**
 * @author Axel Faust
 */
public interface SiteHierarchyService
{

    /**
     * Lists all the top level {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy sites} in the system. This operation respects the
     * {@link SiteHierarchyModel#PROP_SHOW_IN_HIERARCHY_MODE showInHierarchyMode} of sites.
     *
     * @return the list of top level hierarchy sites ordered by name
     */
    @Auditable
    List<SiteInfo> listTopLevelSites();

    /**
     * Lists all the top level {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy sites} in the system.
     *
     * @return the list of top level hierarchy sites ordered by name
     * @param respectShowInHierarchy
     *            {@code true} if the {@link SiteHierarchyModel#PROP_SHOW_IN_HIERARCHY_MODE showInHierarchyMode} should be respected during
     *            retrieval (e.g. sites set to never show should be filtered} or {@code false} if all top level sites should be retrieved
     */
    @Auditable(parameters = { "respectShowInHierarchy" })
    List<SiteInfo> listTopLevelSites(boolean respectShowInHierarchy);

    /**
     * Retrieves the parent of a {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy site}.
     *
     * @param site
     *            the site for which to retrieve the parent site
     *
     * @return the parent site or {@code null} if site is not part of a hierarchy or a {@link SiteHierarchyModel#ASPECT_TOP_LEVEL_SITE top
     *         level site}
     */
    @Auditable(parameters = { "site" })
    SiteInfo getParentSite(String site);

    /**
     * Lists all the child {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy sites} of a specific site. This operation respects the
     * {@link SiteHierarchyModel#PROP_SHOW_IN_HIERARCHY_MODE showInHierarchyMode} of sites.
     *
     * @param parentSite
     *            the site of which to list the child sites
     *
     * @return the list of child sites ordered by name
     */
    @Auditable(parameters = { "parentSite" })
    List<SiteInfo> listChildSites(String parentSite);

    /**
     * Lists all the child {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy sites} of a specific site.
     *
     * @param parentSite
     *            the site of which to list the child sites
     * @param respectShowInHierarchy
     *            {@code true} if the {@link SiteHierarchyModel#PROP_SHOW_IN_HIERARCHY_MODE showInHierarchyMode} should be respected during
     *            retrieval (e.g. sites set to never show should be filtered} or {@code false} if all child sites should be retrieved
     *
     * @return the list of child sites ordered by name
     */
    @Auditable(parameters = { "parentSite", "respectShowInHierarchy" })
    List<SiteInfo> listChildSites(String parentSite, boolean respectShowInHierarchy);

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
     *
     */
    @Auditable(parameters = { "parentSite", "childSite" })
    void addChildSite(String parentSite, String childSite);

    /**
     * Removes a site as a child of another.
     *
     * @param parentSite
     *            the site from which to remove the child
     * @param childSite
     *            the site to remove
     */
    @Auditable(parameters = { "parentSite", "childSite" })
    void removeChildSite(String parentSite, String childSite);

}
