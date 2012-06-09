/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.jvnet.hk2.generator.internal;

import java.io.File;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * @author jwells
 *
 */
public class ClassVisitorImpl extends AbstractClassVisitorImpl {
    private final static String SERVICE_CLASS_FORM = "Lorg/jvnet/hk2/annotations/Service;";
    private final static String CONTRACTS_PROVIDED_CLASS_FORM = "Lorg/jvnet/hk2/annotations/ContractsProvided;";
    private final static String RANK_CLASS_FORM = "Lorg/glassfish/hk2/api/Rank;";
    private final static String NAME = "name";
    private final static String METADATA = "metadata";
    private final static String VALUE = "value";
    private final static String PROVIDE = "provide";
    
    private final boolean verbose;
    private final File searchHere;
    private final Utilities utilities;
    
    private String implName;
    private final LinkedHashSet<String> iFaces = new LinkedHashSet<String>();
    private LinkedHashSet<String> providedContracts;
    private String scopeClass;
    private final LinkedList<String> qualifiers = new LinkedList<String>();
    private boolean isAService = false;
    private NamedAnnotationVisitor baseName;
    private String metadataString = null;
    private Integer rank = null;
    private final Map<String, List<String>> metadata = new HashMap<String, List<String>>();
    
    private final LinkedList<DescriptorImpl> generatedDescriptors = new LinkedList<DescriptorImpl>();
    private boolean isFactory = false;
    private boolean factoryMethodFound = false;
    
    /**
     * Creates this with the config to add to if this is a service
     * 
     * @param utilities The utilities class to use for this visitor (preserves cache)
     * @param verbose true if we should print out any service we are binding
     * @param searchHere if we cannot classload something directly, search for it here
     */
    public ClassVisitorImpl(Utilities utilities, boolean verbose, File searchHere) {
        this.utilities = utilities;
        this.verbose = verbose;
        this.searchHere = searchHere;
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.ClassVisitor#visit(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    @Override
    public void visit(int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces) {
        implName = name.replace("/", ".");
        
        iFaces.addAll(utilities.getAssociatedContracts(searchHere, implName));
        
        if (iFaces.contains(Factory.class.getName())) {
            isFactory = true;
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.ClassVisitor#visitAnnotation(java.lang.String, boolean)
     */
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (!visible) return null;
        
        if (SERVICE_CLASS_FORM.equals(desc)) {
            isAService = true;
            
            return new ServiceAnnotationVisitor();
        }
        
        if (CONTRACTS_PROVIDED_CLASS_FORM.equals(desc)) {
            providedContracts = new LinkedHashSet<String>();
            
            return new ContractsProvidedAnnotationVisitor();
        }
        
        if (RANK_CLASS_FORM.equals(desc)) {
            return new RankAnnotationVisitor();
        }
        
        if (!desc.startsWith("L")) return null;
            
        String loadQualifierName = desc.substring(1, desc.length() -1).replace("/", ".");
        if (utilities.isClassAScope(searchHere, loadQualifierName)) {
            scopeClass = loadQualifierName;
            
            return new MetadataAnnotationVisitor(loadQualifierName);
        }
        
        if (utilities.isClassAQualifier(searchHere, loadQualifierName)) {
            qualifiers.add(loadQualifierName);
            
            if (Named.class.getName().equals(loadQualifierName)) {
                baseName = new NamedAnnotationVisitor(getDefaultName(), null);
                return baseName;
            }
            
            return new MetadataAnnotationVisitor(loadQualifierName);
        }
        
        return null;
    }
    
    private String getDefaultName() {
        int index = implName.lastIndexOf('.');
        if (index <= 0) return implName;
        
        return implName.substring(index + 1);
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.ClassVisitor#visitEnd()
     */
    @Override
    public void visitEnd() {
        if (!isAService) {
            if (verbose) {
                System.out.println("Class " + implName + " is not annotated with @Service");
            }
            return;
        }
        
        DescriptorImpl generatedDescriptor = new DescriptorImpl();
        generatedDescriptor.setImplementation(implName);
        if (scopeClass == null) {
            // The default for classes with Service is Singleton
            generatedDescriptor.setScope(Singleton.class.getName());
        }
        else {
            generatedDescriptor.setScope(scopeClass);
        }
        
        if (providedContracts != null) {
            for (String providedContract : providedContracts) {
                generatedDescriptor.addAdvertisedContract(providedContract);
            }
            
        }
        else {
            generatedDescriptor.addAdvertisedContract(implName);
            for (String iFace : iFaces) {
                generatedDescriptor.addAdvertisedContract(iFace);
            }
        }
        
        for (String qualifier : qualifiers) {
            generatedDescriptor.addQualifier(qualifier);
        }
        
        if (baseName != null) {
            generatedDescriptor.setName(baseName.getName());
        }
        
        if (metadataString != null) {
            StringTokenizer commaTokenizer = new StringTokenizer(metadataString, ",");
            
            while (commaTokenizer.hasMoreTokens()) {
                String keyValueString = commaTokenizer.nextToken();
                
                int equalsIndex = keyValueString.indexOf('=');
                if (equalsIndex < 0) continue;  // unknown format
                
                String key = keyValueString.substring(0, equalsIndex);
                String value = keyValueString.substring(equalsIndex + 1);
                
                if (key.length() <= 0 || value.length() <= 0) continue;  // no blanks allowed
                
                generatedDescriptor.addMetadata(key, value);
            }
        }
        
        if (rank != null) {
            generatedDescriptor.setRanking(rank.intValue());
        }
        
        if (!metadata.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : metadata.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                
                for (String value : values) {
                    generatedDescriptor.addMetadata(key, value);
                }
            }
        }
        
        if (verbose) {
            System.out.println("Generated Descriptor: " + generatedDescriptor);
        }
        
        generatedDescriptors.add(generatedDescriptor);
    }

    /* (non-Javadoc)
     * @see org.objectweb.asm.ClassVisitor#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions) {
        if (!isFactory) return null;
        if (!PROVIDE.equals(name)) return null;
        if (!desc.startsWith("()")) return null;
        if (factoryMethodFound) return null;
        factoryMethodFound = true;
        
        DescriptorImpl asAFactory = new DescriptorImpl();
        generatedDescriptors.add(asAFactory);
        
        asAFactory.setImplementation(implName);
        asAFactory.setDescriptorType(DescriptorType.FACTORY);
        
        String factoryType = desc.substring(2);
        if (factoryType.charAt(0) == '[') {
            // Array type, may not be of an object!
            asAFactory.addAdvertisedContract(factoryType);  // Just the array of whatever type
        }
        else {
            if (factoryType.charAt(0) != 'L') {
                throw new AssertionError("Unable to handle provide descriptor " + desc);
            }
            
            int endIndex = factoryType.indexOf(';');
            if (endIndex < 0) {
                throw new AssertionError("Unable to find end of class return type in descriptor " + desc);
            }
            
            String trueFactoryClass = factoryType.substring(1, endIndex);
            
            // This might be parametererized, strip of the parameters
            trueFactoryClass = trueFactoryClass.replace('/', '.');
            
            Set<String> associatedContracts = utilities.getAssociatedContracts(
                    searchHere, trueFactoryClass);
            
            for (String contract : associatedContracts) {
                asAFactory.addAdvertisedContract(contract);
            }
        }
        
        return new MethodVisitorImpl(asAFactory);
    }
    
    private class ServiceAnnotationVisitor extends AbstractAnnotationVisitorImpl {

        /* (non-Javadoc)
         * @see org.objectweb.asm.AnnotationVisitor#visit(java.lang.String, java.lang.Object)
         */
        @Override
        public void visit(String annotationName, Object value) {
            if (annotationName.equals(NAME)) {
                baseName = new NamedAnnotationVisitor(null, (String) value);
            }
            else if (annotationName.equals(METADATA)) {
                metadataString = (String) value;
            }
        }        
    }
    
    private class NamedAnnotationVisitor extends AbstractAnnotationVisitorImpl {
        private final String defaultName;
        private boolean nameSet = false;
        private String name;
        
        public NamedAnnotationVisitor(String defaultName, String name) {
            this.defaultName = defaultName;
            this.name = name;
        }

        /* (non-Javadoc)
         * @see org.objectweb.asm.AnnotationVisitor#visit(java.lang.String, java.lang.Object)
         */
        @Override
        public void visit(String annotationName, Object value) {
            if (annotationName.equals(VALUE)) {
                name = (String) value;
                nameSet = true;
            }
        }

        /* (non-Javadoc)
         * @see org.objectweb.asm.AnnotationVisitor#visitEnd()
         */
        @Override
        public void visitEnd() {
            if (nameSet) return;
            
            name = defaultName;
        }
        
        private String getName() {
            return name;
        }
        
    }
    
    private class MethodVisitorImpl extends AbstractMethodVisitorImpl {
        private final DescriptorImpl asAFactoryDI;
        private NamedAnnotationVisitor factoryName;
        
        private MethodVisitorImpl(DescriptorImpl asAFactoryDI) {
            this.asAFactoryDI = asAFactoryDI; 
        }

        /* (non-Javadoc)
         * @see org.objectweb.asm.MethodVisitor#visitAnnotation(java.lang.String, boolean)
         */
        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (!desc.startsWith("L")) return null;
            
            String loadQualifierName = desc.substring(1, desc.length() -1).replace("/", ".");
            if (utilities.isClassAScope(searchHere, loadQualifierName)) {
                asAFactoryDI.setScope(loadQualifierName);
            }
            else if (utilities.isClassAQualifier(searchHere, loadQualifierName)) {
                asAFactoryDI.addQualifier(loadQualifierName);
                
                if (Named.class.getName().equals(loadQualifierName)) {
                    factoryName = new NamedAnnotationVisitor(getDefaultName(), null);
                    return factoryName;
                }
            }
            else if (desc.equals(RANK_CLASS_FORM)) {
                return new MethodRankAnnotationVisitor(asAFactoryDI);
            }
            
            return null;
        }

        /* (non-Javadoc)
         * @see org.objectweb.asm.MethodVisitor#visitEnd()
         */
        @Override
        public void visitEnd() {
            if (factoryName != null && factoryName.getName() != null) {
                asAFactoryDI.setName(factoryName.getName());
            }
            
            if (verbose) {
                System.out.println("Adding a factory descriptor: " + asAFactoryDI);
            }
        }        
    }
    
    private class ContractsProvidedAnnotationVisitor extends AbstractAnnotationVisitorImpl {
        /* (non-Javadoc)
         * @see org.objectweb.asm.AnnotationVisitor#visitAnnotation(java.lang.String, java.lang.String)
         */
        @Override
        public void visit(String name, Object value) {
            if (value == null) return;
            if (!(value instanceof Type)) return;
            
            providedContracts.add(((Type) value).getClassName());
        }

        /* (non-Javadoc)
         * @see org.objectweb.asm.AnnotationVisitor#visit(java.lang.String, java.lang.Object)
         */
        @Override
        public AnnotationVisitor visitArray(String name) {
            if (!VALUE.equals(name)) {
                return null;
            }
            
            return this;
        }        
    }
    
    private class RankAnnotationVisitor extends AbstractAnnotationVisitorImpl {
        /* (non-Javadoc)
         * @see org.objectweb.asm.AnnotationVisitor#visitAnnotation(java.lang.String, java.lang.String)
         */
        @Override
        public void visit(String name, Object value) {
            rank = (Integer) value;
        }      
    }
    
    private class MethodRankAnnotationVisitor extends AbstractAnnotationVisitorImpl {
        private final DescriptorImpl di;
        
        private MethodRankAnnotationVisitor(DescriptorImpl di) {
            this.di = di;
        }
        
        /* (non-Javadoc)
         * @see org.objectweb.asm.AnnotationVisitor#visitAnnotation(java.lang.String, java.lang.String)
         */
        @Override
        public void visit(String name, Object value) {
            di.setRanking(((Integer) value).intValue());
        }      
    }
    
    private class MetadataAnnotationVisitor extends AbstractAnnotationVisitorImpl {
        private final String scopeOrQualifierName;
        private final String arrayName;
        
        private MetadataAnnotationVisitor(String scopeOrQualifierName) {
            this(scopeOrQualifierName, null);
        }
        
        private MetadataAnnotationVisitor(String scopeOrQualifierName, String arrayName) {
            this.scopeOrQualifierName = scopeOrQualifierName;
            this.arrayName = arrayName;
        }
        
        @Override
        public void visit(String name, Object value) {
            if (name == null) name = arrayName;
            String metadataKey = utilities.getMetadataKey(scopeOrQualifierName, name);
            
            if (metadataKey != null) {
                String valueString;
                if (value instanceof Type) {
                    Type type = (Type) value;
                    
                    valueString = type.getClassName();
                }
                else {
                    String valueClassName = value.getClass().getName();
                    if (valueClassName.startsWith("[")) {
                        int length = Array.getLength(value);
                        for (int lcv = 0; lcv < length; lcv++) {
                            Object member = Array.get(value, lcv);
                            
                            ReflectionHelper.addMetadata(metadata, metadataKey, member.toString());
                        }
                        
                        return;
                    }
                    
                    valueString = value.toString();
                }
                
                ReflectionHelper.addMetadata(metadata, metadataKey, valueString);
            }
        }
        
        /* (non-Javadoc)
         * @see org.objectweb.asm.AnnotationVisitor#visitArray(java.lang.String)
         */
        @Override
        public AnnotationVisitor visitArray(String name) {
            return new MetadataAnnotationVisitor(scopeOrQualifierName, name);
        }
    }
    
    /**
     * Gets the generated descriptor created by this visitor
     * 
     * @return The descriptor generated by this visitor, or null if the
     * class was not annotated with &#64;Service
     */
    public LinkedList<DescriptorImpl> getGeneratedDescriptor() {
        return generatedDescriptors;
    }

}
