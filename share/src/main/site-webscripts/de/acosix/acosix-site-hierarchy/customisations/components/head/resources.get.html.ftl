<#-- 
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
  -->

<@markup id="hierarchialSiteResources" target="shareResources" action="after">
    <#-- available in 5.1.g at least - not sure about 5.0.d -->
    <@script type="text/javascript" src="${url.context}/res/components/common/common-component-style-filter-chain.js" group="template-common"/>
    <@script src="${url.context}/res/components/object-finder/object-finder.js" group="template-common"/>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/object-finder/object-finder.css" group="template-common" />
    
    <@script src="${url.context}/res/acosix/acosix-site-hierarchy/modules/parent-site-field.js" group="template-common"/>
    <@script src="${url.context}/res/acosix/acosix-site-hierarchy/modules/create-site.js" group="template-common"/>
</@>