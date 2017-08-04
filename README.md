[![Build Status](https://travis-ci.org/Acosix/alfresco-site-hierarchy.svg?branch=master)](https://travis-ci.org/Acosix/alfresco-site-hierarchy)

# About

This addon provides the ability to add simple hierarchies to Alfresco Share Sites. Sites will still be maintained in a flat list by Alfresco core but Alfresco administrators / site managers have the ability to manage secondary parent-child associations between sites to form tree-like hierarchies. These hierarchies can be used to automatically assign access to sites for either parent or child sites, and may be reacted upon in policies to perform custom logic.

## Compatbility

This module is built to be compatible with Alfresco 5.0d and above. It may be used on either Community or Enterprise Edition. The Acosix [alfresco-utility](https://github.com/Acosix/alfresco-utility) module is a mandatory dependency for this module to work.

## Features

This addon adds a custom data model (namespace "http://acosix.de/model/siteHierarchy/1.0" and using prefix "aco6sh") to manage site hierarchies, and a dashlet for site and user dashboards to display these hierarchies.

### Create / Edit Site Dialog Customisation

By default the YUI based "Create Site" and "Edit Site" dialogs are customised to include the following new fields:

 - Show in Hierarchy
 - Parent Site
 - Membership Link Mode

These allow a site to be defined as a "child site" of another. The membership link mode defines how members of the parent / child site should be linked and the "Show in Hierarchy" setting is used to specify if/when the site should be included in the site hierarchy dashlet.

![Create Site Dialog](/src/images/site hierarchy - create site.png)

This customisation is defined by a Surf extension module and can be disabled by either undeploying the module or setting the *acosix-site-hierarchy.siteManagementExtension.enabled* property to *false* in *&lt;configRoot&gt;/share-global.properties* (support for *share-global.properties* is added by the dependency module *de.acosix.alfresco.utility.share* to provide a means of configuring Share customisations via a central properties file similar to Repository-tier *alfresco-global.properties*).

### Site Hierarchy Dashlet

The dashlet "Site Hierarchy" can be added to any dashboard to show the hierarchy of sites. On a user dashboard it will initially list all top-level sites (sites without a parent site) to which the user has access, while on a site dashboard it will display the current site and all its immediate children. The user can drill down on the hierarchy to any level of detail as long as access is granted to the specific sites. A click on any specific site will navigate to that site's dashboard.

![Site Hierarchy Dashlet](/src/images/site hierarchy - dashlet.png)

### Site Hierarchy Policies

This module adds a new set of policies that can be used to react to changes in site hierarchies similar to the core policies for node changes. These policies are defined in the [SiteHierarchyServicePolicies interface](https://github.com/Acosix/alfresco-site-hierarchy/blob/master/repository/src/main/java/de/acosix/alfresco/site/hierarchy/repo/service/SiteHierarchyServicePolicies.java) and can be registered just like any other policy. When any event relating to these policies is fired, the type and aspects of the site will be used to select which registered policy implementations are actually called. Using the type st:site to register a policy will ensure it is called for all kinds of sites even if extensions or modules like Records Management add custom site types.

* *beforeAddChildSite* is called before a aco6sh:childSite association is created between two sites and either site are enhanced by applying any required or implicit aspects
* *onAddChildSite* is called after a site has been added as a child of another and all processing inherent to the module is complete
* *beforeRemoveChildSite* is called before a site is removed as a child from another
* *onRemoveChildSite* is called after a site has been removed as a child from another and any required or implicit aspects have been removed when they are no longer applicable

### Auto Membership Mode

THis module includes a default policy for handling parent and child site relations in such that basic access to either a parent or child site can be automatically provided when creating or modifying a site. This policy relates to the "Membership Link Mode" setting in the site dialog.

* *None* - no processing is applied to link members of the parent or child sites
* *System default* - the setting defined via *alfresco-global.properties* will be used as the mode of linking
* *Parent site members as consumers* - all members of the parent site will be included as Site Consumer in the child site allowing read-access to its contents unless individual permissions restrict viewing of specific nodes
* *Members as parent site consumers* - all members of the child site will be included as Site Consumer on the parent site

Unfortunately the restrictions Alfresco imposes on group memberships (no cycles allowed), it is impossible to provide a mode that links both parent members into the child site and child members into the parent site.
The system default setting can be configured via the *alfresco-global.properties* file using the property *acosix-site-hierarchy.autoMembership.systemDefaultMode*. Allowed values are *none*, *parentMembersAsChildConsumers* and *childMembersAsParentConsumers*.

# Maven usage

This addon is being built using the [Acosix Alfresco Maven framework](https://github.com/Acosix/alfresco-maven) and produces both AMP and installable JAR artifacts. Depending on the setup of a project that wants to include the addon, different approaches can be used to include it in the build.

## Build

This project can be build simply by executing the standard Maven build lifecycles for package, install or deploy depending on the intent for further processing. A Java Development Kit (JDK) version 8 or higher is required for the build.

## Dependency in Alfresco SDK

The simplest option to include the addon in an All-in-One project is by declaring a dependency to the installable JAR artifact. Alternatively, the AMP package may be included which typically requires additional configuration in addition to the dependency.

### Using SNAPSHOT builds

In order to use a pre-built SNAPSHOT artifact published to the Open Source Sonatype Repository Hosting site, the artifact repository may need to be added to the POM, global settings.xml or an artifact repository proxy server. The following is the XML snippet for inclusion in a POM file.

```xml
<repositories>
    <repository>
        <id>ossrh</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

### Repository

```xml
<!-- JAR packaging -->
<dependency>
    <groupId>de.acosix.alfresco.utility</groupId>
    <artifactId>de.acosix.alfresco.utility.common</artifactId>
    <version>1.0.1.0</version>
    <type>jar</type>
</dependency>

<dependency>
    <groupId>de.acosix.alfresco.utility</groupId>
    <artifactId>de.acosix.alfresco.utility.repo</artifactId>
    <version>1.0.1.0</version>
    <type>jar</type>
    <classifier>installable</classifier>
</dependency>

<dependency>
    <groupId>de.acosix.alfresco.site.hierarchy</groupId>
    <artifactId>de.acosix.alfresco.site.hierarchy.repo</artifactId>
    <version>1.0.0.1</version>
    <type>jar</type>
    <classifier>installable</classifier>
</dependency>

<!-- OR -->

<!-- AMP packaging -->
<dependency>
    <groupId>de.acosix.alfresco.utility</groupId>
    <artifactId>de.acosix.alfresco.utility.repo</artifactId>
    <version>1.0.1.0</version>
    <type>amp</type>
</dependency>

<dependency>
    <groupId>de.acosix.alfresco.site.hierarchy</groupId>
    <artifactId>de.acosix.alfresco.site.hierarchy.repo</artifactId>
    <version>1.0.0.1</version>
    <type>amp</type>
</dependency>

<plugin>
    <artifactId>maven-war-plugin</artifactId>
    <configuration>
        <overlays>
            <overlay />
            <overlay>
                <groupId>${alfresco.groupId}</groupId>
                <artifactId>${alfresco.repo.artifactId}</artifactId>
                <type>war</type>
                <excludes />
            </overlay>
            <!-- other AMPs -->
            <overlay>
                <groupId>de.acosix.alfresco.utility</groupId>
                <artifactId>de.acosix.alfresco.utility.repo</artifactId>
                <type>amp</type>
            </overlay>
            <overlay>
                <groupId>de.acosix.alfresco.site.hierarchy</groupId>
                <artifactId>de.acosix.alfresco.site.hierarchy.repo</artifactId>
                <type>amp</type>
            </overlay>
        </overlays>
    </configuration>
</plugin>
```

For Alfresco SDK 3 beta users:

```xml
<platformModules>
    <moduleDependency>
        <groupId>de.acosix.alfresco.utility</groupId>
        <artifactId>de.acosix.alfresco.utility.repo</artifactId>
        <version>1.0.1.0</version>
        <type>amp</type>
    </moduleDependency>
    <moduleDependency>
        <groupId>de.acosix.alfresco.site.hierarchy</groupId>
        <artifactId>de.acosix.alfresco.site.hierarchy.repo</artifactId>
        <version>1.0.0.1</version>
        <type>amp</type>
    </moduleDependency>
</platformModules>
```

### Share

```xml
<!-- JAR packaging -->
<dependency>
    <groupId>de.acosix.alfresco.utility</groupId>
    <artifactId>de.acosix.alfresco.utility.common</artifactId>
    <version>1.0.1.0</version>
    <type>jar</type>
</dependency>

<dependency>
    <groupId>de.acosix.alfresco.utility</groupId>
    <artifactId>de.acosix.alfresco.utility.share</artifactId>
    <version>1.0.1.0</version>
    <type>jar</type>
    <classifier>installable</classifier>
</dependency>

<dependency>
    <groupId>de.acosix.alfresco.site.hierarchy</groupId>
    <artifactId>de.acosix.alfresco.site.hierarchy.share</artifactId>
    <version>1.0.0.1</version>
    <type>jar</type>
    <classifier>installable</classifier>
</dependency>

<!-- OR -->

<!-- AMP packaging -->
<dependency>
    <groupId>de.acosix.alfresco.utility</groupId>
    <artifactId>de.acosix.alfresco.utility.share</artifactId>
    <version>1.0.1.0</version>
    <type>amp</type>
</dependency>

<dependency>
    <groupId>de.acosix.alfresco.site.hierarchy</groupId>
    <artifactId>de.acosix.alfresco.site.hierarchy.share</artifactId>
    <version>1.0.0.1</version>
    <type>amp</type>
</dependency>

<plugin>
    <artifactId>maven-war-plugin</artifactId>
    <configuration>
        <overlays>
            <overlay />
            <overlay>
                <groupId>${alfresco.groupId}</groupId>
                <artifactId>${alfresco.share.artifactId}</artifactId>
                <type>war</type>
                <excludes />
            </overlay>
            <!-- other AMPs -->
            <overlay>
                <groupId>de.acosix.alfresco.utility</groupId>
                <artifactId>de.acosix.alfresco.utility.share</artifactId>
                <type>amp</type>
            </overlay>
            <overlay>
                <groupId>de.acosix.alfresco.site.hierarchy</groupId>
                <artifactId>de.acosix.alfresco.site.hierarchy.share</artifactId>
                <type>amp</type>
            </overlay>
        </overlays>
    </configuration>
</plugin>
```

For Alfresco SDK 3 beta users:

```xml
<shareModules>
    <moduleDependency>
        <groupId>de.acosix.alfresco.utility</groupId>
        <artifactId>de.acosix.alfresco.utility.share</artifactId>
        <version>1.0.1.0</version>
        <type>amp</type>
    </moduleDependency>
    <moduleDependency>
        <groupId>de.acosix.alfresco.site.hierarchy</groupId>
        <artifactId>de.acosix.alfresco.site.hierarchy.share</artifactId>
        <version>1.0.0.1</version>
        <type>amp</type>
    </moduleDependency>
</shareModules>
```

# Other installation methods

Using Maven to build the Alfresco WAR is the **recommended** approach to install this module. As an alternative it can be installed manually.

## alfresco-mmt.jar / apply_amps

The default Alfresco installer creates folders *amps* and *amps_share* where you can place AMP files for modules which Alfresco will install when you use the apply_amps script. Place the AMPs for the *de.acosix.alfresco.utility.repo* and *de.acosix.alfresco.site.hierarchy.repo* modules in the *amps* directory, *de.acosix.alfresco.utility.share* and *de.acosix.alfresco.site.hierarchy.share* modules in the *amps_share* directory, and execute the script to install them. You must restart Alfresco for the installation to take effect.

Alternatively you can use the alfresco-mmt.jar to install the modules as [described in the documentation](http://docs.alfresco.com/5.1/concepts/dev-extensions-modules-management-tool.html).

## Manual "installation" using JAR files

Some addons and some other sources on the net suggest that you can install **any** addon by putting their JARs in a path like &lt;tomcat&gt;/lib, &lt;tomcat&gt;/shared or &lt;tomcat&gt;/shared/lib. This is **not** correct. Only the most trivial addons / extensions can be installed that way - "trivial" in this case means that these addons have no Java class-level dependencies on any component that Alfresco ships, e.g. addons that only consist of static resources, configuration files or web scripts using pure JavaScript / Freemarker.

The only way to manually install an addon using JARs that is **guaranteed** not to cause Java classpath issues is by dropping the JAR files directly into the &lt;tomcat&gt;/webapps/alfresco/WEB-INF/lib (Repository-tier) or &lt;tomcat&gt;/webapps/share/WEB-INF/lib (Share-tier) folders.

For this addon the following JARs need to be dropped into &lt;tomcat&gt;/webapps/alfresco/WEB-INF/lib:

 - de.acosix.alfresco.utility.common-&lt;version&gt;.jar
 - de.acosix.alfresco.utility.repo-&lt;version&gt;-installable.jar
 - de.acosix.alfresco.site.hierarchy.repo-&lt;version&gt;-installable.jar
 
For this addon the following JARs need to be dropped into &lt;tomcat&gt;/webapps/share/WEB-INF/lib:

 - de.acosix.alfresco.utility.common-&lt;version&gt;.jar
 - de.acosix.alfresco.utility.share-&lt;version&gt;-installable.jar
 - de.acosix.alfresco.site.hierarchy.share-&lt;version&gt;-installable.jar

If Alfresco has been setup by using the official installer, another, **explicitly recommended** way to install the module manually would be by dropping the JAR(s) into the &lt;alfresco&gt;/modules/platform (Repository-tier) or &lt;alfresco&gt;/modules/share (Share-tier) folders.