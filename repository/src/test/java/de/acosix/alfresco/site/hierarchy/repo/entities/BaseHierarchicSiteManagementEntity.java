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
package de.acosix.alfresco.site.hierarchy.repo.entities;

import de.acosix.alfresco.utility.repo.entities.Site;

/**
 * @author Axel Faust
 */
public class BaseHierarchicSiteManagementEntity extends Site
{

    private String aco6sh_showInHierarchyMode;

    private String aco6sh_autoMembershipMode;

    // added by enhancement in Acosix Utility
    private String nodeRef;

    /**
     * @return the aco6sh_showInHierarchyMode
     */
    public String getAco6sh_showInHierarchyMode()
    {
        return this.aco6sh_showInHierarchyMode;
    }

    /**
     * @param aco6sh_showInHierarchyMode
     *            the aco6sh_showInHierarchyMode to set
     */
    public void setAco6sh_showInHierarchyMode(final String aco6sh_showInHierarchyMode)
    {
        this.aco6sh_showInHierarchyMode = aco6sh_showInHierarchyMode;
    }

    /**
     * @return the aco6sh_autoMembershipMode
     */
    public String getAco6sh_autoMembershipMode()
    {
        return this.aco6sh_autoMembershipMode;
    }

    /**
     * @param aco6sh_autoMembershipMode
     *            the aco6sh_autoMembershipMode to set
     */
    public void setAco6sh_autoMembershipMode(final String aco6sh_autoMembershipMode)
    {
        this.aco6sh_autoMembershipMode = aco6sh_autoMembershipMode;
    }

    /**
     * @return the nodeRef
     */
    public String getNodeRef()
    {
        return this.nodeRef;
    }

    /**
     * @param nodeRef
     *            the nodeRef to set
     */
    public void setNodeRef(final String nodeRef)
    {
        this.nodeRef = nodeRef;
    }

}
