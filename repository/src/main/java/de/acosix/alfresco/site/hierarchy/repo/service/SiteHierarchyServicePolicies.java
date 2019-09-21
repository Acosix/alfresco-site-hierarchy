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
package de.acosix.alfresco.site.hierarchy.repo.service;

import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.namespace.QName;

import de.acosix.alfresco.site.hierarchy.repo.model.SiteHierarchyModel;

/**
 * Collection of interfaces for policies allowing reacting to events of the {@link SiteHierarchyService}.
 *
 * @author Axel Faust
 */
public interface SiteHierarchyServicePolicies
{

    /**
     * Policy interface to react to the event triggered before a site is added as a child of another.
     *
     * @author Axel Faust
     */
    interface BeforeAddChildSitePolicy extends ClassPolicy
    {

        static final String NAMESPACE = SiteHierarchyModel.SERVICE_NAMESPACE_URI;

        static final QName QNAME = QName.createQName(NAMESPACE, "beforeAddChildSite");

        /**
         * Handles the pending addition of a site as the child of another.
         *
         * @param parentSiteInfo
         *            the intended parent site
         * @param childSiteInfo
         *            the child site
         */
        void beforeAddChildSite(SiteInfo parentSiteInfo, SiteInfo childSiteInfo);
    }

    /**
     * Policy interface to react to the event triggered after a site has been added as a child of another.
     *
     * @author Axel Faust
     */
    interface OnAddChildSitePolicy extends ClassPolicy
    {

        static final String NAMESPACE = SiteHierarchyModel.SERVICE_NAMESPACE_URI;

        static final QName QNAME = QName.createQName(NAMESPACE, "onAddChildSite");

        /**
         * Handles the addition of a site as the child of another.
         *
         * @param parentSiteInfo
         *            the parent site
         * @param childSiteInfo
         *            the child site
         */
        void onAddChildSite(SiteInfo parentSiteInfo, SiteInfo childSiteInfo);
    }

    /**
     * Policy interface to react to the event triggered before a site is removed as a child of another.
     *
     * @author Axel Faust
     */
    interface BeforeRemoveChildSitePolicy extends ClassPolicy
    {

        static final String NAMESPACE = SiteHierarchyModel.SERVICE_NAMESPACE_URI;

        static final QName QNAME = QName.createQName(NAMESPACE, "beforeRemoveChildSite");

        /**
         * Handles the pending removal of a site as the child of another.
         *
         * @param parentSiteInfo
         *            the intended parent site
         * @param childSiteInfo
         *            the child site
         */
        void beforeRemoveChildSite(SiteInfo parentSiteInfo, SiteInfo childSiteInfo);
    }

    /**
     * Policy interface to react to the event triggered after a site has been removed as a child to another.
     *
     * @author Axel Faust
     */
    interface OnRemoveChildSitePolicy extends ClassPolicy
    {

        static final String NAMESPACE = SiteHierarchyModel.SERVICE_NAMESPACE_URI;

        static final QName QNAME = QName.createQName(NAMESPACE, "onRemoveChildSite");

        /**
         * Handles the removal of a site as the child of another.
         *
         * @param parentSiteInfo
         *            the previous parent site
         * @param childSiteInfo
         *            the child site
         */
        void onRemoveChildSite(SiteInfo parentSiteInfo, SiteInfo childSiteInfo);
    }
}
