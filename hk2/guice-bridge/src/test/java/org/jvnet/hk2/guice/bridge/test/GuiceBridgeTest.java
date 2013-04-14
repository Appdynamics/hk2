/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.jvnet.hk2.guice.bridge.test;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.testing.junit.HK2Runner;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceBridgeTest extends HK2Runner {
    @Before
    public void before() {
        LinkedList<String> packages = new LinkedList<String>();
        packages.add("org.jvnet.hk2.guice.bridge.internal");
        
        initialize("GuiceBridgeTest", packages, null);
    }
    
    /**
     * Tests a service from Guice being injected into an HK2 service
     */
    @Test @Ignore
    public void testGuiceServiceInHk2Service() {
        Injector injector = Guice.createInjector(new GuiceBridgeModule());
        Assert.assertNotNull(injector);
        
        GuiceBridge guiceBridge = testLocator.getService(GuiceBridge.class);
        Assert.assertNotNull(guiceBridge);
        
        guiceBridge.bridgeGuiceInjector(injector);
        
        HK2Service1 hk2Service = testLocator.getService(HK2Service1.class);
        Assert.assertNotNull(hk2Service);
        
        hk2Service.verifyGuiceService();
    }
}
