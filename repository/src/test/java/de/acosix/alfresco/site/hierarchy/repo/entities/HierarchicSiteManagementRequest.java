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
package de.acosix.alfresco.site.hierarchy.repo.entities;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class HierarchicSiteManagementRequest extends BaseHierarchicSiteManagementEntity
{

    private String aco6sh_parentSite;

    private String aco6sh_parentSite_added;

    private String aco6sh_parentSite_removed;

    /**
     * @return the aco6sh_parentSite
     */
    public String getAco6sh_parentSite()
    {
        return this.aco6sh_parentSite;
    }

    /**
     * @param aco6sh_parentSite
     *            the aco6sh_parentSite to set
     */
    public void setAco6sh_parentSite(final String aco6sh_parentSite)
    {
        this.aco6sh_parentSite = aco6sh_parentSite;
    }

    /**
     * @return the aco6sh_parentSite_added
     */
    public String getAco6sh_parentSite_added()
    {
        return this.aco6sh_parentSite_added;
    }

    /**
     * @param aco6sh_parentSite_added
     *            the aco6sh_parentSite_added to set
     */
    public void setAco6sh_parentSite_added(final String aco6sh_parentSite_added)
    {
        this.aco6sh_parentSite_added = aco6sh_parentSite_added;
    }

    /**
     * @return the aco6sh_parentSite_removed
     */
    public String getAco6sh_parentSite_removed()
    {
        return this.aco6sh_parentSite_removed;
    }

    /**
     * @param aco6sh_parentSite_removed
     *            the aco6sh_parentSite_removed to set
     */
    public void setAco6sh_parentSite_removed(final String aco6sh_parentSite_removed)
    {
        this.aco6sh_parentSite_removed = aco6sh_parentSite_removed;
    }

}
