[//]: # " DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER. "
[//]: # "  "
[//]: # " Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved. "
[//]: # "  "
[//]: # " The contents of this file are subject to the terms of either the GNU "
[//]: # " General Public License Version 2 only (''GPL'') or the Common Development "
[//]: # " and Distribution License(''CDDL'') (collectively, the ''License'').  You "
[//]: # " may not use this file except in compliance with the License.  You can "
[//]: # " obtain a copy of the License at "
[//]: # " https://oss.oracle.com/licenses/CDDL+GPL-1.1 "
[//]: # " or LICENSE.txt.  See the License for the specific "
[//]: # " language governing permissions and limitations under the License. "
[//]: # "  "
[//]: # " When distributing the software, include this License Header Notice in each "
[//]: # " file and include the License file at LICENSE.txt. "
[//]: # "  "
[//]: # " GPL Classpath Exception: "
[//]: # " Oracle designates this particular file as subject to the ''Classpath'' "
[//]: # " exception as provided by Oracle in the GPL Version 2 section of the License "
[//]: # " file that accompanied this code. "
[//]: # "  "
[//]: # " Modifications: "
[//]: # " If applicable, add the following below the License Header, with the fields "
[//]: # " enclosed by brackets [] replaced by your own identifying information: "
[//]: # " ''Portions Copyright [year] [name of copyright owner]'' "
[//]: # "  "
[//]: # " Contributor(s): "
[//]: # " If you wish your version of this file to be governed by only the CDDL or "
[//]: # " only the GPL Version 2, indicate your decision by adding ''[Contributor] "
[//]: # " elects to include this software in this distribution under the [CDDL or GPL "
[//]: # " Version 2] license.''  If you don't indicate a single choice of license, a "
[//]: # " recipient has the option to distribute your version of this file under "
[//]: # " either the CDDL, the GPL Version 2 or to extend the choice of license to "
[//]: # " its licensees as provided above.  However, if you add GPL Version 2 code "
[//]: # " and therefore, elected the GPL Version 2 license, then the option applies "
[//]: # " only if the new code is made subject to such option by the copyright "
[//]: # " holder. "

<h2> Getting started</h2>

- [Maven build](getting-started.html#Maven_Build)
- [Automatic Service Population](getting-started.html#Automatic_Service_Population)

<h3>Maven Build</h3>

The best way to use HK2 in your builds is to add the following dependency in your maven build:

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.acme</groupId>
    <artifactId>myModule</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
      <dependency>
        <groupId>org.glassfish.hk2</groupId>
        <artifactId>hk2</artifactId>
        <version>2.5.0-b36</version>
      </dependency>
    </dependencies>
</project>
```xml

The org.glassfish.hk2:hk2 dependency has a dependency on all of the HK2 jars.
However, this may be more than you want, since it includes configuration, 
run-level services and some osgi support that your application may not need.
If instead you wanted the absolute minimum working profile for hk2 you would instead have your project look like this:

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.acme</groupId>
    <artifactId>myModule</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
      <dependency>
        <groupId>org.glassfish.hk2</groupId>
        <artifactId>hk2-locator</artifactId>
        <version>2.5.0-b36</version>
      </dependency>
    </dependencies>
</project>
```xml

The hk2-locator project contains the implementation of the hk2 API, with
no other bells and whistles.  In particular, the ability to automatically
detect services is not available, and so all HK2 objects must be added
programmatically and gotten with the HK2 API.  However, the above is perfect
for small projects that want to play with the HK2 API to see how it works.

<h3>Automatic Service Population</h3>

In order for HK2 to automatically find services at runtime it can read files called inhabitant files.
 These are usually placed in your JAR file at location META-INF/hk2-locator.
 Normally the file is named default.
 (You can however use a different file name or location(s) by using more specific API).
 HK2 has a tool for automatically creating these files based on class files annotated with [@Service][service].
 There is also a simple API for creating and populating a [ServiceLocator][servicelocator] with services found in these files.

In order to have your Maven build generate the META-INF files that hk2 reads in order to populate a [ServiceLocator][servicelocator] 
use the [hk2-inhabitant-generator][inhabitant-generator].
This tool can be used from the command line, or it can be put into your maven or ant builds.

In order to have your program automatically load the files generated with the [hk2-inhabitant-generator][inhabitant-generator] you can
use the [createAndPopulateServiceLocator][createandpopulateservicelocator] method near the start of your main method, like this:
  
```java
  public static void main(String argv[]) {
      ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
      
      MyService myService = locator.getService(MyService.class);
      
      ...
  }
```java

[servicelocator]: apidocs/org/glassfish/hk2/api/ServiceLocator.html
[inhabitant-generator]: inhabitant-generator.html
[createandpopulateservicelocator]: apidocs/org/glassfish/hk2/utilities/ServiceLocatorUtilities.html#createAndPopulateServiceLocator()
[service]: apidocs/org/jvnet/hk2/annotations/Service.html
