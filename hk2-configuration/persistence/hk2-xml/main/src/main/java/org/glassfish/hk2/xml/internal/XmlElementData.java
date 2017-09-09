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
package org.glassfish.hk2.xml.internal;

/**
 * Information about the XmlElement annotation
 * 
 * @author jwells
 *
 */
public class XmlElementData {
    private final String namespace;
    private final String name;
    private final String alias;
    private final String defaultValue;
    private final Format format;
    private final String type;
    private final boolean isTypeInterface;
    private final String xmlWrapperTag;
    private final boolean required;
    private final String originalMethodName;
    
    XmlElementData(String namespace,
            String name,
            String alias,
            String defaultValue,
            Format format,
            String type,
            boolean isTypeInterface,
            String xmlWrapperTag,
            boolean required,
            String originalMethodName) {
        this.namespace = namespace;
        this.name = name;
        this.alias = alias;
        this.defaultValue = defaultValue;
        this.format = format;
        this.type = type;
        this.isTypeInterface = isTypeInterface;
        this.xmlWrapperTag = xmlWrapperTag;
        this.required = required;
        this.originalMethodName = originalMethodName;
    }
    
    public String getNamespace() { return namespace; }
    public String getName() { return name; }
    public String getAlias() { return alias; }
    public String getDefaultValue() { return defaultValue; }
    public Format getFormat() { return format; }
    public String getType() { return type; }
    public boolean isTypeInterface() { return isTypeInterface; }
    public String getXmlWrapperTag() { return xmlWrapperTag; }
    public boolean isRequired() { return required; }
    public String getOriginalMethodName() { return originalMethodName; }
    
    @Override
    public String toString() {
        return "XmlElementData(" + namespace +
                "," + name +
                "," + alias +
                "," + Utilities.safeString(defaultValue) +
                "," + format +
                "," + type +
                "," + isTypeInterface +
                "," + xmlWrapperTag +
                "," + required +
                "," + originalMethodName +
                "," + System.identityHashCode(this) + ")";
    }
}