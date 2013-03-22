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

package org.opensaml.saml.saml2.metadata.provider;

import java.io.File;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for {@link FileBackedHTTPMetadataProvider}.
 */
public class FileBackedHTTPMetadataProviderTest extends XMLObjectBaseTestCase {

    private String mdUrl;

    private String badMDURL;

    private String backupFilePath;

    private FileBackedHTTPMetadataProvider metadataProvider;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        mdUrl="http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/data/org/opensaml/saml/saml2/metadata/ukfederation-metadata.xml?content-type=text%2Fplain&view=co";
        badMDURL = "http://www.google.com/";
        backupFilePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") 
                + "filebacked-http-metadata.xml";
    }

    /** {@inheritDoc} */
    @AfterMethod
    protected void tearDown() {
        File backupFile = new File(backupFilePath);
        backupFile.delete();
    }
    
    /**
     * Tests the {@link HTTPMetadataProvider#getMetadata()} method.
     */
    @Test
    public void testGetMetadata() throws MetadataProviderException {
        metadataProvider = new FileBackedHTTPMetadataProvider(mdUrl, 1000 * 5, backupFilePath);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.initialize();
        
        Assert.assertNotNull(metadataProvider.getMetadata(), "Retrieved metadata was null");

        File backupFile = new File(backupFilePath);
        Assert.assertTrue(backupFile.exists(), "Backup file was not created");
        Assert.assertTrue(backupFile.length() > 0, "Backup file contains no data");
    }
    
    /**
     * Test fail-fast = true with known bad metadata URL.
     */
    @Test
    public void testFailFastBadURL() throws MetadataProviderException {
        metadataProvider = new FileBackedHTTPMetadataProvider(badMDURL, 1000 * 5, backupFilePath);
        
        metadataProvider.setFailFastInitialization(true);
        metadataProvider.setParserPool(parserPool);
        
        try {
            metadataProvider.initialize();
            Assert.fail("metadata provider claims to have parsed known invalid data");
        } catch (MetadataProviderException e) {
            //expected, do nothing
        }
    }
    
    /**
     * Test fail-fast = false with known bad metadata URL.
     */
    @Test
    public void testNoFailFastBadURL() throws MetadataProviderException {
        metadataProvider = new FileBackedHTTPMetadataProvider(badMDURL, 1000 * 5, backupFilePath);
        
        metadataProvider.setFailFastInitialization(false);
        metadataProvider.setParserPool(parserPool);
        
        try {
            metadataProvider.initialize();
        } catch (MetadataProviderException e) {
            Assert.fail("Provider failed init with fail-fast=false");
        }
    }
    
    /**
     *  Test fail-fast = true and bad backup file
     */
    @Test
    public void testFailFastBadBackupFile() {
        try {
            // Use a known existing directory as backup file path, which is an invalid argument.
            metadataProvider = new FileBackedHTTPMetadataProvider(mdUrl, 1000 * 5, System.getProperty("java.io.tmpdir"));
        } catch (MetadataProviderException e) {
            Assert.fail("Provider failed bad backup file in constructor");
            
        }
        metadataProvider.setFailFastInitialization(true);
        metadataProvider.setParserPool(parserPool);
        
        try {
            metadataProvider.initialize();
            Assert.fail("Provider passed init with bad backup file, fail-fast=true");
        } catch (MetadataProviderException e) {
            // expected do nothing
        }
    }
    
    /**
     *  Test case of fail-fast = false and bad backup file
     * @throws MetadataProviderException 
     */
    @Test
    public void testNoFailFastBadBackupFile() throws MetadataProviderException {
        try {
            // Use a known existing directory as backup file path, which is an invalid argument.
            metadataProvider = new FileBackedHTTPMetadataProvider(mdUrl, 1000 * 5, System.getProperty("java.io.tmpdir"));
        } catch (MetadataProviderException e) {
            Assert.fail("Provider failed bad backup file in constructor");
            
        }
        metadataProvider.setFailFastInitialization(false);
        metadataProvider.setParserPool(parserPool);
        
        try {
            metadataProvider.initialize();
        } catch (MetadataProviderException e) {
            Assert.fail("Provider failed init with bad backup file, fail-fast=false");
        }
        
        Assert.assertNotNull(metadataProvider.getMetadata(), "Metadata retrieved from backing file was null");
    }
    
    /**
     * Tests use of backup file on simulated restart.
     * 
     * @throws MetadataProviderException
     */
    @Test
    public void testBackupFileOnRestart() throws MetadataProviderException {
        // Do a setup here to get a good backup file
        metadataProvider = new FileBackedHTTPMetadataProvider(mdUrl, 1000 * 5, backupFilePath);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.initialize();
        
        Assert.assertNotNull(metadataProvider.getMetadata(), "Retrieved metadata was null");

        File backupFile = new File(backupFilePath);
        Assert.assertTrue(backupFile.exists(), "Backup file was not created");
        Assert.assertTrue(backupFile.length() > 0, "Backup file contains no data");
        
        // Now do a new provider to simulate a restart (have to set fail-fast=false).
        // Verify that can use the data from backing file.
        FileBackedHTTPMetadataProvider badProvider = new FileBackedHTTPMetadataProvider(badMDURL, 1000 * 5,
                backupFilePath);
        badProvider.setParserPool(parserPool);
        badProvider.setFailFastInitialization(false);
        badProvider.initialize();
        
        Assert.assertNotNull(metadataProvider.getMetadata(), "Metadata retrieved from backing file was null");
    }

}