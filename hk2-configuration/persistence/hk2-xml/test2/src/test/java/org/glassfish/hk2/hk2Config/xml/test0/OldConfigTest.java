/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.hk2.hk2Config.xml.test0;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.hk2Config.xml.test.utilities.LocatorUtilities;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.hk2Config.test.beans.KingdomConfig;
import org.glassfish.hk2.xml.hk2Config.test.beans.Phyla;
import org.glassfish.hk2.xml.hk2Config.test.beans.Phylum;
import org.glassfish.hk2.xml.hk2Config.test.beans.PropertyValue;
import org.glassfish.hk2.xml.hk2Config.test.beans.pv.NamedPropertyValue;
import org.glassfish.hk2.xml.hk2Config.test.customizers.KingdomCustomizer;
import org.glassfish.hk2.xml.hk2Config.test.customizers.PhylaCustomizer;
import org.glassfish.hk2.xml.hk2Config.test.customizers.PhylumCustomizer;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hk2.config.types.PropertyBagCustomizerImpl;

public class OldConfigTest {
    private static final String KINGDOM_FILE = "kingdom1.xml";
    
    private static final String ALICE_NAME = "Alice";
    private static final String BOB_NAME = "Bob";
    
    private static final String USERNAME_PROP_KEY = "username";
    private static final String USERNAME_PROP_VALUE = "sa";
    private static final String PASSWORD_PROP_KEY = "password";
    private static final String PASSWORD_PROP_VALUE = "sp";
    
    private static final String P1 = "P1";
    private static final String P2 = "P2";
    private static final String P3 = "P3";
    private static final String P4 = "P4";
    
    private static final String V1 = "V1";
    private static final String V2 = "V2";
    private static final String V3 = "V3";
    private static final String V4 = "V4";
    
    /**
     * See that the simple names and methods work
     * 
     * @throws Exception
     */
    @Test
    public void testSimplePropertiesAndNamesAreCallable() throws Exception {
        ServiceLocator locator = LocatorUtilities.createLocator(PhylumCustomizer.class,
                PropertyBagCustomizerImpl.class,
                KingdomCustomizer.class,
                PhylaCustomizer.class);
        
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(KINGDOM_FILE);
        
        XmlRootHandle<KingdomConfig> rootHandle = xmlService.unmarshall(url.toURI(), KingdomConfig.class, true, false);
        KingdomConfig kingdom1 = rootHandle.getRoot();
        
        assertOriginalStateKingdom1(kingdom1);
    }
    
    @Test
    public void testCustomizedMethodsAreCallable() throws Exception {
        ServiceLocator locator = LocatorUtilities.createLocator(PhylumCustomizer.class,
                PropertyBagCustomizerImpl.class,
                KingdomCustomizer.class,
                PhylaCustomizer.class);
        
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(KINGDOM_FILE);
        
        XmlRootHandle<KingdomConfig> rootHandle = xmlService.unmarshall(url.toURI(), KingdomConfig.class, true, false);
        KingdomConfig kingdom1 = rootHandle.getRoot();
        
        Phyla phyla = kingdom1.getPhyla();
        Phylum alice = phyla.getPhylumByName(ALICE_NAME);
        Assert.assertNotNull(alice);
        
        Assert.assertEquals(USERNAME_PROP_VALUE, alice.getPropertyValue(USERNAME_PROP_KEY));
        Assert.assertEquals(PASSWORD_PROP_VALUE, alice.getPropertyValue(PASSWORD_PROP_KEY));
        
        List<Phylum> all = phyla.getPhylumByType(Phylum.class);
        Assert.assertEquals(1, all.size());
        
        for (Phylum phylum : all) {
            Assert.assertEquals(ALICE_NAME, phylum.getName());
            
            Assert.assertEquals(USERNAME_PROP_VALUE, phylum.getPropertyValue(USERNAME_PROP_KEY));
            Assert.assertEquals(PASSWORD_PROP_VALUE, phylum.getPropertyValue(PASSWORD_PROP_KEY));
        }
        
        Map<String, PropertyValue> props = new HashMap<String, PropertyValue>();
        props.put("name", new NamedPropertyValue(BOB_NAME));
        
        Phylum bob = phyla.createPhylum(props);
        Assert.assertNotNull(bob);
        Assert.assertEquals(BOB_NAME, bob.getName());
        
        all = phyla.getPhylum();
        Assert.assertEquals(2, all.size());
        
        int lcv = 0;
        for (Phylum phylum : all) {
            if (lcv == 0) {
                Assert.assertEquals(ALICE_NAME, phylum.getName());
                
                Assert.assertEquals(USERNAME_PROP_VALUE, phylum.getPropertyValue(USERNAME_PROP_KEY));
                Assert.assertEquals(PASSWORD_PROP_VALUE, phylum.getPropertyValue(PASSWORD_PROP_KEY));
            }
            else if (lcv == 1) {
                Assert.assertEquals(BOB_NAME, phylum.getName());
                
                Assert.assertNull(phylum.getPropertyValue(USERNAME_PROP_KEY));
                Assert.assertNull(phylum.getPropertyValue(PASSWORD_PROP_KEY));
            }
            lcv++;
        }
    }
    
    private static void assertOriginalStateKingdom1(KingdomConfig kingdom1) {
        Assert.assertNotNull(kingdom1);
        
        Phyla phyla = kingdom1.getPhyla();
        Assert.assertNotNull(phyla);
        
        List<Phylum> phylums = phyla.getPhylum();
        Assert.assertEquals(1, phylums.size());
        
        for (Phylum phylum : phylums) {
            Assert.assertEquals(ALICE_NAME, phylum.getName());
            
            Assert.assertEquals(USERNAME_PROP_VALUE, phylum.getPropertyValue(USERNAME_PROP_KEY));
            Assert.assertEquals(PASSWORD_PROP_VALUE, phylum.getPropertyValue(PASSWORD_PROP_KEY));
        }
        
        Assert.assertEquals(P1, kingdom1.getProperty().get(0).getName());
        Assert.assertEquals(V1, kingdom1.getProperty().get(0).getValue());
        
        Assert.assertEquals(P2, kingdom1.getProperty().get(1).getName());
        Assert.assertEquals(V2, kingdom1.getProperty().get(1).getValue());
        
        Assert.assertEquals(P3, kingdom1.getProperty().get(2).getName());
        Assert.assertEquals(V3, kingdom1.getProperty().get(2).getValue());
        
        Assert.assertEquals(V1, kingdom1.getPropertyValue(P1));
        Assert.assertEquals(V2, kingdom1.getPropertyValue(P2));
        Assert.assertEquals(V3, kingdom1.getPropertyValue(P3));
        
        // Check that "defaulting" works
        Assert.assertEquals(V4, kingdom1.getPropertyValue(P4, V4));
    }
}
