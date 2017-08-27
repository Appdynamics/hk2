/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.hk2.xml.test1.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.json.api.JsonUtilities;
import org.glassfish.hk2.pbuf.api.PBufUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.xml.api.XmlServiceUtilities;
import org.jvnet.hk2.external.generator.ServiceLocatorGeneratorImpl;

/**
 * @author jwells
 *
 */
public class Utilities {
    private final static ServiceLocatorGenerator GENERATOR = new ServiceLocatorGeneratorImpl();

    /**
     * Creates a fresh service locator with the XmlService added
     * 
     * @return A service locator with the XmlService added
     */
    public static ServiceLocator createLocator(Class<?>... classes) {
        ServiceLocator retVal = ServiceLocatorFactory.getInstance().create(null, null, GENERATOR);
        
        ServiceLocatorUtilities.addClasses(retVal, classes);
        
        XmlServiceUtilities.enableXmlService(retVal);
        
        return retVal;
    }
    
    /**
     * Creates a fresh service locator with the XmlService added
     * 
     * @return A service locator with the XmlService added
     */
    public static ServiceLocator createDomLocator(Class<?>... classes) {
        ServiceLocator retVal = ServiceLocatorFactory.getInstance().create(null, null, GENERATOR);
        
        ServiceLocatorUtilities.addClasses(retVal, classes);
        
        XmlServiceUtilities.enableDomXmlService(retVal);
        
        return retVal;
    }
    
    public static ServiceLocator createInteropLocator(Class<?>...classes) {
        ServiceLocator retVal = ServiceLocatorFactory.getInstance().create(null, null, GENERATOR);
        
        ServiceLocatorUtilities.addClasses(retVal, classes);
        
        XmlServiceUtilities.enableDomXmlService(retVal);
        XmlServiceUtilities.enableXmlService(retVal);
        JsonUtilities.enableJsonService(retVal);
        PBufUtilities.enablePBufService(retVal);
        
        return retVal;
    }
    
    public static void writeBytesToFile(String fileName, byte writeMe[]) throws IOException {
        File f = new File(fileName);
        
        FileOutputStream fos = new FileOutputStream(f);
        try {
            fos.write(writeMe);
        }
        finally {
            fos.close();
        }
        
    }

}
