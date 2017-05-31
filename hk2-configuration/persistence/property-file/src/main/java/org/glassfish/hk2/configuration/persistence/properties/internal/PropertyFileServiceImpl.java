/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.hk2.configuration.persistence.properties.internal;

import java.beans.PropertyChangeEvent;

import javax.inject.Inject;

import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase;
import org.glassfish.hk2.configuration.hub.api.WriteableType;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileBean;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileHandle;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileService;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
public class PropertyFileServiceImpl implements PropertyFileService {
    private final static int MAX_TRIES = 10000;
    
    @Inject
    private Hub hub;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.persistence.properties.PropertyFileService#createPropertyHandleOfSpecificType(java.lang.String, java.lang.String)
     */
    @Override
    public PropertyFileHandle createPropertyHandleOfSpecificType(
            String specificTypeName, String defaultInstanceName) {
        return new PropertyFileHandleImpl(specificTypeName, null, defaultInstanceName, hub);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.persistence.properties.PropertyFileService#createPropertyHandleOfSpecificType(java.lang.String)
     */
    @Override
    public PropertyFileHandle createPropertyHandleOfSpecificType(
            String specificTypeName) {
        return new PropertyFileHandleImpl(specificTypeName, null, null, hub);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.persistence.properties.PropertyFileService#createPropertyHandleOfAnyType(java.lang.String, java.lang.String)
     */
    @Override
    public PropertyFileHandle createPropertyHandleOfAnyType(
            String defaultTypeName, String defaultInstanceName) {
        return new PropertyFileHandleImpl(null, defaultTypeName, defaultInstanceName, hub);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.persistence.properties.PropertyFileService#createPropertyHandleOfAnyType()
     */
    @Override
    public PropertyFileHandle createPropertyHandleOfAnyType() {
        return new PropertyFileHandleImpl(null, null, null, hub);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.persistence.properties.PropertyFileService#addPropertyFileBean(org.glassfish.hk2.configuration.persistence.properties.PropertyFileBean)
     */
    @Override
    public void addPropertyFileBean(PropertyFileBean propertyFileBean) {
        boolean success = false;
        for (int lcv = 0; lcv < MAX_TRIES; lcv++) {
            WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
            
            WriteableType wt = wbd.findOrAddWriteableType(PropertyFileBean.TYPE_NAME);
            
            Instance oldInstance = wt.getInstance(PropertyFileBean.INSTANCE_NAME);
            if (oldInstance != null) {
                PropertyFileBean oldBean = (PropertyFileBean) oldInstance.getBean();
                wt.modifyInstance(PropertyFileBean.INSTANCE_NAME, propertyFileBean,
                        new PropertyChangeEvent(propertyFileBean,
                                "typeMapping",
                                oldBean.getTypeMapping(),
                                propertyFileBean.getTypeMapping()));
            }
            else {
                wt.addInstance(PropertyFileBean.INSTANCE_NAME, propertyFileBean);
            }
            
            try {
                wbd.commit();
                success = true;
                break;
            }
            catch (IllegalStateException ise) {
                // try again
            }
        }
        
        if (!success) {
            throw new IllegalStateException("Could not update hub with propertyFileBean " + propertyFileBean);
        }
    }
    
    @Override
    public void removePropertyFileBean() {
        boolean success = false;
        for (int lcv = 0; lcv < MAX_TRIES; lcv++) {
            WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
            
            wbd.removeType(PropertyFileBean.TYPE_NAME);
            
            try {
                wbd.commit();
                success = true;
                break;
            }
            catch (IllegalStateException ise) {
                // try again
            }
        }
        
        if (!success) {
            throw new IllegalStateException("Could not update hub to remove the propertyFileBean");
        }
    }

}
