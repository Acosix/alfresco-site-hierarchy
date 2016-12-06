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

import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.namespace.QName;

import de.acosix.alfresco.site.hierarchy.repo.model.SiteHierarchyModel;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public interface SiteHierarchyServicePolicies
{

    interface BeforeAddChildSitePolicy extends ClassPolicy
    {

        static final QName QNAME = QName.createQName(SiteHierarchyModel.SERVICE_NAMESPACE_URI, "beforeAddChildSite");

        void beforeAddChildSite(SiteInfo parentSiteInfo, SiteInfo childSiteInfo);
    }

    interface OnAddChildSitePolicy extends ClassPolicy
    {

        static final QName QNAME = QName.createQName(SiteHierarchyModel.SERVICE_NAMESPACE_URI, "onAddChildSite");

        void onAddChildSite(SiteInfo parentSiteInfo, SiteInfo childSiteInfo);
    }

    interface BeforeRemoveChildSitePolicy extends ClassPolicy
    {

        static final QName QNAME = QName.createQName(SiteHierarchyModel.SERVICE_NAMESPACE_URI, "beforeRemoveChildSite");

        void beforeRemoveChildSite(SiteInfo parentSiteInfo, SiteInfo childSiteInfo);
    }

    interface OnRemoveChildSitePolicy extends ClassPolicy
    {

        static final QName QNAME = QName.createQName(SiteHierarchyModel.SERVICE_NAMESPACE_URI, "onRemoveChildSite");

        void onRemoveChildSite(SiteInfo parentSiteInfo, SiteInfo childSiteInfo);
    }
}