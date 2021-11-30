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
package de.acosix.alfresco.site.hierarchy.repo.entities;

/**
 * @author Axel Faust
 */
public class HierarchicSiteManagementResponse extends BaseHierarchicSiteManagementEntity
{

    public static class ParenSite
    {

        private String shortName;

        private String title;

        private String description;

        private String nodeRef;

        /**
         * @return the shortName
         */
        public String getShortName()
        {
            return shortName;
        }

        /**
         * @param shortName
         *            the shortName to set
         */
        public void setShortName(String shortName)
        {
            this.shortName = shortName;
        }

        /**
         * @return the title
         */
        public String getTitle()
        {
            return title;
        }

        /**
         * @param title
         *            the title to set
         */
        public void setTitle(String title)
        {
            this.title = title;
        }

        /**
         * @return the description
         */
        public String getDescription()
        {
            return description;
        }

        /**
         * @param description
         *            the description to set
         */
        public void setDescription(String description)
        {
            this.description = description;
        }

        /**
         * @return the nodeRef
         */
        public String getNodeRef()
        {
            return nodeRef;
        }

        /**
         * @param nodeRef
         *            the nodeRef to set
         */
        public void setNodeRef(String nodeRef)
        {
            this.nodeRef = nodeRef;
        }

    }

    private ParenSite aco6sh_parentSite;

    /**
     * @return the aco6sh_parentSite
     */
    public ParenSite getAco6sh_parentSite()
    {
        return aco6sh_parentSite;
    }

    /**
     * @param aco6sh_parentSite
     *            the aco6sh_parentSite to set
     */
    public void setAco6sh_parentSite(ParenSite aco6sh_parentSite)
    {
        this.aco6sh_parentSite = aco6sh_parentSite;
    }

}
