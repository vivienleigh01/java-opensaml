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

package org.opensaml.saml.saml1.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml1.core.AuthenticationStatement;
import org.opensaml.saml.saml1.core.AuthorityBinding;
import org.opensaml.saml.saml1.core.SubjectLocality;

/**
 * A Concrete implementation of the {@link org.opensaml.saml.saml1.core.AuthenticationStatement} Interface.
 */
public class AuthenticationStatementImpl extends SubjectStatementImpl implements AuthenticationStatement {

    /** Contains the AuthenticationMethod attribute contents. */
    private String authenticationMethod;

    /** Contains the AuthenticationMethod attribute contents. */
    private DateTime authenticationInstant;

    /** Contains the SubjectLocality subelement. */
    private SubjectLocality subjectLocality;

    /** Contains the AuthorityBinding subelements. */
    private final XMLObjectChildrenList<AuthorityBinding> authorityBindings;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AuthenticationStatementImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        authorityBindings = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    /** {@inheritDoc} */
    public void setAuthenticationMethod(String method) {
        authenticationMethod = prepareForAssignment(authenticationMethod, method);
    }

    /** {@inheritDoc} */
    public DateTime getAuthenticationInstant() {
        return authenticationInstant;
    }

    /** {@inheritDoc} */
    public void setAuthenticationInstant(DateTime instant) {
        authenticationInstant = prepareForAssignment(authenticationInstant, instant);
    }

    //
    // Elements
    //

    /** {@inheritDoc} */
    public SubjectLocality getSubjectLocality() {
        return subjectLocality;
    }

    /** {@inheritDoc} */
    public void setSubjectLocality(SubjectLocality locality) {
        subjectLocality = prepareForAssignment(subjectLocality, locality);
    }

    /** {@inheritDoc} */
    public List<AuthorityBinding> getAuthorityBindings() {
        return authorityBindings;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> list = new ArrayList<>(authorityBindings.size() + 2);

        if (super.getOrderedChildren() != null) {
            list.addAll(super.getOrderedChildren());
        }

        if (subjectLocality != null) {
            list.add(subjectLocality);
        }

        list.addAll(authorityBindings);

        if (list.size() == 0) {
            return null;
        }

        return Collections.unmodifiableList(list);
    }
}