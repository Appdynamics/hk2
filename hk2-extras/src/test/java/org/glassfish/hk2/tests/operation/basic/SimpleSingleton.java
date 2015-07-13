/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.hk2.tests.operation.basic;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceHandle;

import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.extras.operation.OperationManager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Singleton
public class SimpleSingleton {

  private final static BasicOperationScope BASIC_OPERATION_ANNOTATION = new BasicOperationScopeImpl();
  
  private final ServiceLocator serviceLocator;
  
  @Inject
  public SimpleSingleton(final ServiceLocator serviceLocator) {
    super();
    assertNotNull(serviceLocator);
    this.serviceLocator = serviceLocator;
  }

  public void reticulateSplines() {
    final OperationManager operationManager = this.serviceLocator.getService(OperationManager.class);
    assertNotNull(operationManager);

    final OperationHandle<BasicOperationScope> handle = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
    assertNotNull(handle);
    try {
      final ActiveDescriptor<?> fd = this.serviceLocator.getBestDescriptor(new FrobnicatorFilter());
      assertNotNull(fd);

      final ServiceHandle<?> sh = this.serviceLocator.getServiceHandle(fd);
      assert sh != null;
      
      final Frobnicator frobnicator = (Frobnicator)sh.getService();
      assertTrue(frobnicator instanceof ProxyCtl);
      frobnicator.toString();
    } finally {
      handle.closeOperation();
    }
  }

  private static final class FrobnicatorFilter implements Filter {

    private FrobnicatorFilter() {
      super();
    }

    @Override
    public final boolean matches(final Descriptor descriptor) {
      boolean returnValue = false;
      if (descriptor != null &&
          Frobnicator.class.getName().equals(descriptor.getImplementation()) &&
          BasicOperationScope.class.getName().equals(descriptor.getScope())) {
        returnValue = true;
      }
      return returnValue;
    }
  }
  
}
