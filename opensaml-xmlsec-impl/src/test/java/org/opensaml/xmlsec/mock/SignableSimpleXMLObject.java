/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */
package org.opensaml.xmlsec.mock;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.ElementExtensibleXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.signature.AbstractSignableXMLObject;

/**
 * Simple XMLObject that can be used for testing
 */
public class SignableSimpleXMLObject extends AbstractSignableXMLObject implements ElementExtensibleXMLObject, AttributeExtensibleXMLObject {
    //TODO these and other supporting classes need to be refactored
    
    /** Default namespace */
    public final static String NAMESPACE = "http://www.example.org/testObjects";
    
    /** Default namespace prefix */
    public final static String NAMESPACE_PREFIX = "test";
    
    /** Element local name */
    public final static String LOCAL_NAME = "SignableSimpleElement";
    
    /** Default element name */
    public final static QName ELEMENT_NAME = new QName(NAMESPACE, LOCAL_NAME, NAMESPACE_PREFIX);
    
    /** Local name of encrypted element */
    public final static String ENCRYPTED_NAME = "Encrypted" + LOCAL_NAME;
    
    /** Name attribute name */
    public final static String ID_ATTRIB_NAME = "Id";
    
    /** Name attribute */
    private String id;
    
    /** Value of the object stored as text content in the element */
    private String value;
    
    /** Child SimpleXMLObjects */
    private XMLObjectChildrenList<SignableSimpleXMLObject> simpleXMLObjects;
    
    /** Other children */
    private IndexedXMLObjectChildrenList<XMLObject> unknownXMLObjects;
    
    /** EncryptedData child */
    private EncryptedData encryptedData;
    
    /** anyAttribute wildcard attributes. */
    private AttributeMap unknownAttributes;
    
    /**
     * Constructor
     */
    public SignableSimpleXMLObject(String namspaceURI, String localName, String namespacePrefix) {
        super(namspaceURI, localName, namespacePrefix);
        
        simpleXMLObjects = new XMLObjectChildrenList<>(this);
        unknownXMLObjects = new IndexedXMLObjectChildrenList<>(this);
        unknownAttributes = new AttributeMap(this);
    }
    
    /**
     * Gets the name attribute.
     * 
     * @return the name attribute
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the name attribute.
     * 
     * @param newId the name attribute
     */
    public void setId(String newId) {
        registerOwnID(id, newId);
        id = newId;
    }
    
    /**
     * Gets the value of this object.
     * 
     * @return the value of this object
     */
    public String getValue(){
        return value;
    }
    
    /**
     * Sets the value of this object.
     * 
     * @param newValue the value of this object
     */
    public void setValue(String newValue){
        value = prepareForAssignment(value, newValue);
    }
    
    /**
     * Get the EncryptedData child element.
     * 
     * @return the EncryptedData child element
     */
    public EncryptedData getEncryptedData() {
       return this.encryptedData;
    }
    
    /**
     * Set the EncryptedData child element.
     * 
     * @param newEncryptedData the new EncryptedData child element
     */
    public void setEncryptedData(EncryptedData newEncryptedData) {
        this.encryptedData = prepareForAssignment(this.encryptedData, newEncryptedData);
    }
    
    /**
     * Gets the list of child SimpleXMLObjects.
     * 
     * @return the list of child SimpleXMLObjects
     */
    public List<SignableSimpleXMLObject> getSimpleXMLObjects(){
        return simpleXMLObjects;
    }
    
    /** {@inheritDoc} */
    public List<XMLObject> getUnknownXMLObjects() {
        return unknownXMLObjects;
    }
    
    /** {@inheritDoc} */
    public List<XMLObject> getUnknownXMLObjects(QName typeOrName) {
        return (List<XMLObject>) unknownXMLObjects.subList(typeOrName);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> children = new LinkedList<>();
        
        children.addAll(simpleXMLObjects);
        if (encryptedData != null) {
            children.add(encryptedData);
        }
        children.addAll(unknownXMLObjects);
        children.add(getSignature());
        
        return Collections.unmodifiableList(children);
    }

    /** {@inheritDoc} */
    public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }
}