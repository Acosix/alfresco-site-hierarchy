<@markup id="hierarchialSiteResources" target="shareResources" action="after">
    <#-- available in 5.1.g at least - not sure about 5.0.d -->
    <@script type="text/javascript" src="${url.context}/res/components/common/common-component-style-filter-chain.js" group="template-common"/>
    <@script src="${url.context}/res/components/object-finder/object-finder.js" group="template-common"/>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/object-finder/object-finder.css" group="template-common" />
    
    <@script src="${url.context}/res/acosix/acosix-site-hierarchy/modules/parent-site-field.js" group="template-common"/>
    <@script src="${url.context}/res/acosix/acosix-site-hierarchy/modules/create-site.js" group="template-common"/>
</@>