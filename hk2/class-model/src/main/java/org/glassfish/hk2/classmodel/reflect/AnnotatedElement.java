/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *  Copyright 2010 Sun Microsystems, Inc. All rights reserved.
 *
 *  The contents of this file are subject to the terms of either the GNU
 *  General Public License Version 2 only ("GPL") or the Common Development
 *  and Distribution License("CDDL") (collectively, the "License").  You
 *  may not use this file except in compliance with the License. You can obtain
 *  a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 *  or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 *  language governing permissions and limitations under the License.
 *
 *  When distributing the software, include this License Header Notice in each
 *  file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 *  Sun designates this particular file as subject to the "Classpath" exception
 *  as provided by Sun in the GPL Version 2 section of the License file that
 *  accompanied this code.  If applicable, add the following below the License
 *  Header, with the fields enclosed by brackets [] replaced by your own
 *  identifying information: "Portions Copyrighted [year]
 *  [name of copyright owner]"
 *
 *  Contributor(s):
 *
 *  If you wish your version of this file to be governed by only the CDDL or
 *  only the GPL Version 2, indicate your decision by adding "[Contributor]
 *  elects to include this software in this distribution under the [CDDL or GPL
 *  Version 2] license."  If you don't indicate a single choice of license, a
 *  recipient has the option to distribute your version of this file under
 *  either the CDDL, the GPL Version 2 or to extend the choice of license to
 *  its licensees as provided above.  However, if you add GPL Version 2 code
 *  and therefore, elected the GPL Version 2 license, then the option applies
 *  only if the new code is made subject to such option by the copyright
 *  holder.
 */
package org.glassfish.hk2.classmodel.reflect;


import java.util.Collection;

/**
 * An annotated element is a java declaration that can be annotated. Such
 * declaration are usually types (like classes or interfaces), fields of
 * a class, or methods of a class.
 *
 * An annotated element is defined by its name and the set of annotations
 * present on the declaration.
 *
 * @author Jerome Dochez
 */
public interface AnnotatedElement {

    /**
     * Annotated element have a name, which vary depending on the actual
     * subclass type. For instance, a class annotated element's name is the
     * class name as obtained from {@link Class#getName()}
     *
     * @return the annotated element name
     */
    public String getName();

    /**
     * Construct and return a short description name that can be used to
     * display the instance value
     *
     * @return a short description
     */
    public String shortDesc();

    /**
     * Returns a unmodifiable set of annotations that are present on this
     * annotated element.
     *
     * @return the collection of annotations
     */
    Collection<AnnotationModel> getAnnotations();
}
