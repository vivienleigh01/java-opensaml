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

package org.opensaml.saml.saml1.core.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.config.SAMLConfigurationSupport;
import org.opensaml.saml.saml1.core.RequestAbstractType;
import org.opensaml.saml.saml1.core.ResponseAbstractType;
import org.w3c.dom.Element;

/**
 * A thread safe Marshaller for {@link org.opensaml.saml.saml1.core.ResponseAbstractType} objects.
 */
public abstract class ResponseAbstractTypeMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject samlElement, Element domElement) throws MarshallingException {
        ResponseAbstractType response = (ResponseAbstractType) samlElement;

        if (response.getID() != null) {
            domElement.setAttributeNS(null, ResponseAbstractType.ID_ATTRIB_NAME, response.getID());
            if (response.getVersion() != SAMLVersion.VERSION_10) {
                domElement.setIdAttributeNS(null, ResponseAbstractType.ID_ATTRIB_NAME, true);
            }
        }

        if (response.getInResponseTo() != null) {
            domElement.setAttributeNS(null, ResponseAbstractType.INRESPONSETO_ATTRIB_NAME, response.getInResponseTo());
        }

        if (response.getIssueInstant() != null) {
            String date = SAMLConfigurationSupport.getSAMLDateFormatter().print(response.getIssueInstant());
            domElement.setAttributeNS(null, ResponseAbstractType.ISSUEINSTANT_ATTRIB_NAME, date);
        }

        domElement.setAttributeNS(null, RequestAbstractType.MAJORVERSION_ATTRIB_NAME,
                Integer.toString(response.getVersion().getMajorVersion()));
        domElement.setAttributeNS(null, RequestAbstractType.MINORVERSION_ATTRIB_NAME,
                Integer.toString(response.getVersion().getMinorVersion()));

        if (response.getRecipient() != null) {
            domElement.setAttributeNS(null, ResponseAbstractType.RECIPIENT_ATTRIB_NAME, response.getRecipient());
        }
    }

}
