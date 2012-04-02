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

package org.opensaml.core.xml;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.mock.SimpleXMLObjectBuilder;
import org.w3c.dom.Document;

/**
 * Test support for attributes from the XML Schema Instance namespace.
 */
public class XMLObjectXSIAttribsTest extends XMLObjectBaseTestCase {
    
    /** QName for SimpleXMLObject. */
    private QName simpleXMLObjectQName;

    /** Constructor. */
    public XMLObjectXSIAttribsTest() {
        super();
        
        simpleXMLObjectQName = new QName(SimpleXMLObject.NAMESPACE, SimpleXMLObject.LOCAL_NAME);
    }
    
    public void testUnmarshallNoNil() throws XMLParserException, UnmarshallingException {
        String documentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectWithAttribute.xml";
        Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        assertNull(sxObject.isNilXSBoolean());
        assertFalse("Expected isNil() false", sxObject.isNil());
    }
    
    public void testUnmarshallNil() throws XMLParserException, UnmarshallingException {
        String documentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectNil.xml";
        Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        assertNotNull(sxObject.isNilXSBoolean());
        assertTrue("Expected isNil() true", sxObject.isNil());
    }
    
    public void testMarshallNil() throws XMLParserException {
        String expectedDocumentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectNil.xml";
        Document expectedDocument = parserPool.parse(XMLObjectXSIAttribsTest.class
                .getResourceAsStream(expectedDocumentLocation));

        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) builderFactory.getBuilder(simpleXMLObjectQName);
        SimpleXMLObject sxObject = sxoBuilder.buildObject();
        sxObject.setNil(true);

        assertEquals(expectedDocument, sxObject);
    }
    
    public void testUnmarshallSchemaLocation() throws XMLParserException, UnmarshallingException {
        String expectedValue = "http://www.example.com/Test http://www.example.com/Test.xsd";
        String documentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectSchemaLocation.xml";
        Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        assertEquals("Incorrect xsi:schemaLocation value", expectedValue, sxObject.getSchemaLocation());
    }
    
    public void testMarshallSchemaLocation() throws XMLParserException {
        String expectedValue = "http://www.example.com/Test http://www.example.com/Test.xsd";
        String expectedDocumentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectSchemaLocation.xml";
        Document expectedDocument = parserPool.parse(XMLObjectXSIAttribsTest.class
                .getResourceAsStream(expectedDocumentLocation));

        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) builderFactory.getBuilder(simpleXMLObjectQName);
        SimpleXMLObject sxObject = sxoBuilder.buildObject();
        sxObject.setSchemaLocation(expectedValue);

        assertEquals(expectedDocument, sxObject);
    }
    
    public void testUnmarshallNoNamespaceSchemaLocation() throws XMLParserException, UnmarshallingException {
        String expectedValue = "http://www.example.com/Test.xsd";
        String documentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectNoNamespaceSchemaLocation.xml";
        Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        assertEquals("Incorrect xsi:noNamespaceSchemaLocation value", expectedValue, sxObject.getNoNamespaceSchemaLocation());
    }
    
    public void testMarshallNoNamespaceSchemaLocation() throws XMLParserException {
        String expectedValue = "http://www.example.com/Test.xsd";
        String expectedDocumentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectNoNamespaceSchemaLocation.xml";
        Document expectedDocument = parserPool.parse(XMLObjectXSIAttribsTest.class
                .getResourceAsStream(expectedDocumentLocation));

        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) builderFactory.getBuilder(simpleXMLObjectQName);
        SimpleXMLObject sxObject = sxoBuilder.buildObject();
        sxObject.setNoNamespaceSchemaLocation(expectedValue);

        assertEquals(expectedDocument, sxObject);
    }
    

}
