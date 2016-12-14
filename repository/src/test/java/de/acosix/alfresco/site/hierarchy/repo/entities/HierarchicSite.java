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

import de.acosix.alfresco.utility.repo.entities.Site;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class HierarchicSite extends Site
{

    private String hierarchyUri;

    private String nodeRef;

    private String autoMembershipMode;

    private String showInHierarchyMode;

    private HierarchicSite parent;

    private List<HierarchicSite> children;

    /**
     * @return the hierarchyUri
     */
    public String getHierarchyUri()
    {
        return this.hierarchyUri;
    }

    /**
     * @param hierarchyUri
     *            the hierarchyUri to set
     */
    public void setHierarchyUri(final String hierarchyUri)
    {
        this.hierarchyUri = hierarchyUri;
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

    /**
     * @return the autoMembershipMode
     */
    public String getAutoMembershipMode()
    {
        return this.autoMembershipMode;
    }

    /**
     * @param autoMembershipMode
     *            the autoMembershipMode to set
     */
    public void setAutoMembershipMode(final String autoMembershipMode)
    {
        this.autoMembershipMode = autoMembershipMode;
    }

    /**
     * @return the showInHierarchyMode
     */
    public String getShowInHierarchyMode()
    {
        return this.showInHierarchyMode;
    }

    /**
     * @param showInHierarchyMode
     *            the showInHierarchyMode to set
     */
    public void setShowInHierarchyMode(final String showInHierarchyMode)
    {
        this.showInHierarchyMode = showInHierarchyMode;
    }

    /**
     * @return the parent
     */
    public HierarchicSite getParent()
    {
        return this.parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(final HierarchicSite parent)
    {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    public List<HierarchicSite> getChildren()
    {
        return this.children;
    }

    /**
     * @param children
     *            the children to set
     */
    public void setChildren(final List<HierarchicSite> children)
    {
        this.children = children;
    }

    // just a remapping
    /**
     *
     * @param uri
     *            the uri to set
     */
    public void setUri(final String uri)
    {
        super.setUrl(uri);
    }

    /**
     *
     * @return the uri
     */
    public String getUri()
    {
        return super.getUrl();
    }

    /**
     *
     * @param nodeUri
     *            the nodeUri to set
     */
    public void setNodeUri(final String nodeUri)
    {
        super.setNode(nodeUri);
    }

    /**
     *
     * @return the uri
     */
    public String getNodeUri()
    {
        return super.getNode();
    }
}
