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
package de.acosix.alfresco.site.hierarchy.repo.model;

import org.alfresco.service.namespace.QName;

/**
 * @author Axel Faust
 */
public interface SiteHierarchyModel
{

    static final String SERVICE_NAMESPACE_URI = "http://acosix.de/service/siteHierarchy/1.0";

    static final String MODEL_NAMESPACE_URI = "http://acosix.de/model/siteHierarchy/1.0";

    static final String MODEL_NAMESPACE_PREFIX = "aco6sh";

    static final QName MODEL = QName.createQName(MODEL_NAMESPACE_URI, "model");

    static final QName ASPECT_HIERARCHY_SITE = QName.createQName(MODEL_NAMESPACE_URI, "hierarchySite");

    static final QName PROP_SHOW_IN_HIERARCHY_MODE = QName.createQName(MODEL_NAMESPACE_URI, "showInHierarchyMode");

    static final QName ASPECT_TOP_LEVEL_SITE = QName.createQName(MODEL_NAMESPACE_URI, "topLevelSite");

    static final QName ASPECT_CHILD_SITE = QName.createQName(MODEL_NAMESPACE_URI, "childSite");

    static final QName PROP_AUTO_MEMBERSHIP_MODE = QName.createQName(MODEL_NAMESPACE_URI, "autoMembershipMode");

    static final QName ASPECT_PARENT_SITE = QName.createQName(MODEL_NAMESPACE_URI, "parentSite");

    static final QName ASSOC_CHILD_SITE = QName.createQName(MODEL_NAMESPACE_URI, "childSite");

    static final String CONSTRAINT_AUTO_MEMBERSHIP_MODES_NONE = "none";

    static final String CONSTRAINT_AUTO_MEMBERSHIP_MODES_SYSTEM_DEFAULT = "systemDefault";

    static final String CONSTRAINT_AUTO_MEMBERSHIP_MODES_PARENT_MEMBERS_AS_CHILD_CONSUMERS = "parentMembersAsChildConsumers";

    static final String CONSTRAINT_AUTO_MEMBERSHIP_MODES_CHILD_MEMBERS_AS_PARENT_CONSUMERS = "childMembersAsParentConsumers";

    static final String CONSTRAINT_SHOW_IN_HIERARCHY_MODES_IF_PARENT_OR_CHILD = "ifParentOrChild";

    static final String CONSTRAINT_SHOW_IN_HIERARCHY_MODES_NEVER = "never";

    static final String CONSTRAINT_SHOW_IN_HIERARCHY_MODES_ALWAYS = "always";
}
