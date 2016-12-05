<@markup id="css" >
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/acosix/acosix-site-hierarchy/components/dashlets/site-hierarchy.css" group="dashlets" />
</@>

<@markup id="js" >
    <@script type="text/javascript" src="${url.context}/res/acosix/acosix-site-hierarchy/components/dashlets/site-hierarchy.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="html">
    <@uniqueIdDiv>
        <#assign id = args.htmlid?html>
        <div class="dashlet site-hierarchy">
            <div class="title">${msg("dashlet.siteHierarchy.title")}</div>
            <div class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>>
                <div id="${id}-treeview" class="tree"></div>
            </div>
        </div>
    </@>
</@>