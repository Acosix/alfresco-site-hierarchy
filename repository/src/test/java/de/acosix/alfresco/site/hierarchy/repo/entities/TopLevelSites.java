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

import java.util.List;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class TopLevelSites
{

    private List<HierarchicSite> sites;

    /**
     * @return the sites
     */
    public List<HierarchicSite> getSites()
    {
        return this.sites;
    }

    /**
     * @param sites
     *            the sites to set
     */
    public void setSites(final List<HierarchicSite> sites)
    {
        this.sites = sites;
    }

}
