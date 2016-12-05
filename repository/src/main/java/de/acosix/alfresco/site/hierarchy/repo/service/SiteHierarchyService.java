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
package de.acosix.alfresco.site.hierarchy.repo.service;

import java.util.List;

import org.alfresco.service.cmr.site.SiteInfo;

import de.acosix.alfresco.site.hierarchy.repo.model.SiteHierarchyModel;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public interface SiteHierarchyService
{

    /**
     * Lists all the top level {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy sites} in the system.
     *
     * @return the list of top level hierarchy sites ordered by name
     */
    List<SiteInfo> listTopLevelSites();

    /**
     * Retrieves the parent of a {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy site}.
     *
     * @param site
     *            the site for which to retrieve the parent site
     *
     * @return the parent site or {@code null} if site is not part of a hierarchy or a {@link SiteHierarchyModel#ASPECT_TOP_LEVEL_SITE top
     *         level site}
     */
    SiteInfo getParentSite(String site);

    /**
     * Lists all the child {@link SiteHierarchyModel#ASPECT_HIERARCHY_SITE hierarchy sites} of a specific site.
     *
     * @param parentSite
     *            the site of which to list the child sites
     *
     * @return the list of child sites ordered by name
     */
    List<SiteInfo> listChildSites(String parentSite);

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
    void addChildSite(String parentSite, String childSite);

    /**
     * Removes a site as a child of another.
     *
     * @param parentSite
     *            the site from which to remove the child
     * @param childSite
     *            the site to remove
     */
    void removeChildSite(String parentSite, String childSite);

}
