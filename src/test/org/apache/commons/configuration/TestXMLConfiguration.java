/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

/**
 * test for loading and saving xml properties files
 *
 * @version $Id: TestXMLConfiguration.java,v 1.8 2004/08/16 22:16:31 henning Exp $
 */
public class TestXMLConfiguration extends TestCase
{
    /** The File that we test with */
    private String testProperties = new File("conf/test.xml").getAbsolutePath();
    private String testBasePath = new File("conf").getAbsolutePath();
    private File testSaveConf = new File("target/testsave.xml");

    private XMLConfiguration conf;

    protected void setUp() throws Exception
    {
        conf = new XMLConfiguration(new File(testProperties));
    }

    public void testGetProperty() throws Exception
    {
        assertEquals("value", conf.getProperty("element"));
    }

    public void testGetCommentedProperty()
    {
        assertEquals(null, conf.getProperty("test.comment"));
    }

    public void testGetPropertyWithXMLEntity()
    {
        assertEquals("1<2", conf.getProperty("test.entity"));
    }

    public void testClearProperty() throws ConfigurationException, IOException
    {
        // test non-existent element
        String key = "clearly";
        conf.clearProperty(key);
        assertNull(key, conf.getProperty(key));
        assertNull(key, conf.getXmlProperty(key));
        
        // test single element
        conf.load();
        key = "clear.element";
        conf.clearProperty(key);
        assertNull(key, conf.getProperty(key));
        assertNull(key, conf.getXmlProperty(key));

        // test single element with attribute
        conf.load();
        key = "clear.element2";
        conf.clearProperty(key);
        assertNull(key, conf.getProperty(key));
        assertNull(key, conf.getXmlProperty(key));
        key = "clear.element2[@id]";
        assertNotNull(key, conf.getProperty(key));
        assertNotNull(key, conf.getXmlProperty(key));
        
        // test non-text/cdata element
        conf.load();
        key = "clear.comment";
        conf.clearProperty(key);
        assertNull(key, conf.getProperty(key));
        assertNull(key, conf.getXmlProperty(key));
        
        // test cdata element
        conf.load();
        key = "clear.cdata";
        conf.clearProperty(key);
        assertNull(key, conf.getProperty(key));
        assertNull(key, conf.getXmlProperty(key));
        
        // test multiple sibling elements
        conf.load();
        key = "clear.list.item";
        conf.clearProperty(key);
        assertNull(key, conf.getProperty(key));
        assertNull(key, conf.getXmlProperty(key));
        key = "clear.list.item[@id]";
        assertNotNull(key, conf.getProperty(key));
        assertNotNull(key, conf.getXmlProperty(key));
        
        // test multiple, disjoined elements
        conf.load();
        key = "list.item";
        conf.clearProperty(key);
        assertNull(key, conf.getProperty(key));
        assertNull(key, conf.getXmlProperty(key));
    }

    public void testGetXmlProperty() {
        // test non-leaf element
        Object property = conf.getXmlProperty("clear");
        assertNull(property);
        
        // test non-existent element
        property = conf.getXmlProperty("e");
        assertNull(property);

        // test non-existent element
        property = conf.getXmlProperty("element3[@n]");
        assertNull(property);
        
        // test single element
        property = conf.getXmlProperty("element");
        assertNotNull(property);
        assertTrue(property instanceof String);
        assertEquals("value", property);
        
        // test single attribute
        property = conf.getXmlProperty("element3[@name]");
        assertNotNull(property);
        assertTrue(property instanceof String);
        assertEquals("foo", property);
        
        // test non-text/cdata element
        property = conf.getXmlProperty("test.comment");
        assertNull(property);
        
        // test cdata element
        property = conf.getXmlProperty("test.cdata");
        assertNotNull(property);
        assertTrue(property instanceof String);
        assertEquals("<cdata value>", property);
        
        // test multiple sibling elements
        property = conf.getXmlProperty("list.sublist.item");
        assertNotNull(property);
        assertTrue(property instanceof List);
        List list = (List)property;
        assertEquals(2, list.size());
        assertEquals("five", list.get(0));
        assertEquals("six", list.get(1));
        
        // test multiple, disjoined elements
        property = conf.getXmlProperty("list.item");
        assertNotNull(property);
        assertTrue(property instanceof List);
        list = (List)property;
        assertEquals(4, list.size());
        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));
        assertEquals("three", list.get(2));
        assertEquals("four", list.get(3));

        // test multiple, disjoined attributes
        property = conf.getXmlProperty("list.item[@name]");
        assertNotNull(property);
        assertTrue(property instanceof List);
        list = (List)property;
        assertEquals(2, list.size());
        assertEquals("one", list.get(0));
        assertEquals("three", list.get(1));
    }
    
    public void testGetAttribute() throws Exception
    {
        assertEquals("element3[@name]", "foo", conf.getProperty("element3[@name]"));
    }

    public void testClearAttribute() throws Exception
    {
        // test non-existent attribute
        String key = "clear[@id]";
        conf.clearProperty(key);
        assertNull(key, conf.getProperty(key));
        assertNull(key, conf.getXmlProperty(key));

        // test single attribute
        conf.load();
        key = "clear.element2[@id]";
        Object p = conf.getXmlProperty(key);
        p = conf.getXmlProperty("clear.element2");
        conf.clearProperty(key);
        assertNull(key, conf.getProperty(key));
        assertNull(key, conf.getXmlProperty(key));
        key = "clear.element2";
        assertNotNull(key, conf.getProperty(key));
        assertNotNull(key, conf.getXmlProperty(key));
        
        // test multiple, disjoined attributes
        conf.load();
        key = "clear.list.item[@id]";
        conf.clearProperty(key);
        assertNull(key, conf.getProperty(key));
        assertNull(key, conf.getXmlProperty(key));
        key = "clear.list.item";
        assertNotNull(key, conf.getProperty(key));
        assertNotNull(key, conf.getXmlProperty(key));
    }

    public void testSetAttribute() throws Exception
    {
        // replace an existing attribute
        conf.setProperty("element3[@name]", "bar");
        assertEquals("element3[@name]", "bar", conf.getProperty("element3[@name]"));

        // set a new attribute
        conf.setProperty("foo[@bar]", "value");
        assertEquals("foo[@bar]", "value", conf.getProperty("foo[@bar]"));
    }

    public void testAddAttribute() throws Exception
    {
        conf.addProperty("element3[@name]", "bar");

        List list = conf.getList("element3[@name]");
        assertNotNull("null list", list);
        assertTrue("'foo' element missing", list.contains("foo"));
        assertTrue("'bar' element missing", list.contains("bar"));
        assertEquals("list size", 2, list.size());
    }

    public void testAddVectorAttribute() throws Exception
    {
        conf.addProperty("element3[@name]", "bar");

        Vector vector = conf.getVector("element3[@name]");
        assertNotNull("null vector", vector);
        assertTrue("'foo' element missing", vector.contains("foo"));
        assertTrue("'bar' element missing", vector.contains("bar"));
        assertEquals("vector size", 2, vector.size());
    }

    public void testAddList() throws Exception
    {
        conf.addProperty("test.array", "value1");
        conf.addProperty("test.array", "value2");

        List list = conf.getList("test.array");
        assertNotNull("null list", list);
        assertTrue("'value1' element missing", list.contains("value1"));
        assertTrue("'value2' element missing", list.contains("value2"));
        assertEquals("list size", 2, list.size());
    }

    public void testAddVector() throws Exception
    {
        conf.addProperty("test.array", "value1");
        conf.addProperty("test.array", "value2");

        Vector vector = conf.getVector("test.array");
        assertNotNull("null vector", vector);
        assertTrue("'value1' element missing", vector.contains("value1"));
        assertTrue("'value2' element missing", vector.contains("value2"));
        assertEquals("vector size", 2, vector.size());
    }

    public void testGetComplexProperty() throws Exception
    {
        assertEquals("I'm complex!", conf.getProperty("element2.subelement.subsubelement"));
    }

    public void testSettingFileNames() throws Exception
    {
        conf = new XMLConfiguration();
        conf.setFileName(testProperties);
        assertEquals(testProperties.toString(), conf.getFileName());

        conf.setBasePath(testBasePath);
        conf.setFileName("hello.xml");
        assertEquals("hello.xml", conf.getFileName());
        assertEquals(testBasePath.toString(), conf.getBasePath());
        assertEquals(new File(testBasePath, "hello.xml"), conf.getFile());

        conf.setBasePath(testBasePath);
        conf.setFileName("/subdir/hello.xml");
        assertEquals("/subdir/hello.xml", conf.getFileName());
        assertEquals(testBasePath.toString(), conf.getBasePath());
        assertEquals(new File(testBasePath, "/subdir/hello.xml"), conf.getFile());
    }

    public void testLoad() throws Exception
    {
        conf = new XMLConfiguration();
        conf.setFileName(testProperties);
        conf.load();

        assertEquals("I'm complex!", conf.getProperty("element2.subelement.subsubelement"));
    }

    public void testLoadWithBasePath() throws Exception
    {
        conf = new XMLConfiguration();

        conf.setFileName("test.xml");
        conf.setBasePath(testBasePath);
        conf.load();

        assertEquals("I'm complex!", conf.getProperty("element2.subelement.subsubelement"));
    }

    public void testAddProperty()
    {
        // add a property to a non initialized xml configuration
        XMLConfiguration config = new XMLConfiguration();
        config.addProperty("test.string", "hello");

        assertEquals("'test.string'", "hello", config.getString("test.string"));
    }

    public void testSave() throws Exception
    {
    	// remove the file previously saved if necessary
        if(testSaveConf.exists()){
    		assertTrue(testSaveConf.delete());
    	}

        // add an array of strings to the configuration
    	conf.addProperty("string", "value1");
        for (int i = 1; i < 5; i++)
        {
            conf.addProperty("test.array", "value" + i);
        }

        // add an array of strings in an attribute
        for (int i = 1; i < 5; i++)
        {
           conf.addProperty("test.attribute[@array]", "value" + i);
        }

        // save the configuration
        conf.save(testSaveConf.getAbsolutePath());

        // read the configuration and compare the properties
        XMLConfiguration checkConfig = new XMLConfiguration();
        checkConfig.setFileName(testSaveConf.getAbsolutePath());
        checkConfig.load();

        for (Iterator i = conf.getKeys(); i.hasNext();)
        {
        	String key = (String) i.next();
        	assertTrue("The saved configuration doesn't contain the key '" + key + "'", checkConfig.containsKey(key));
        	assertEquals("Value of the '" + key + "' property", conf.getProperty(key), checkConfig.getProperty(key));
        }
    }

    public void testParseElementsNames()
    {
        // without attribute
        String key = "x.y.z";
        String[] array = new String[] {"x", "y", "z"};
        ArrayAssert.assertEquals("key without attribute", array, XMLConfiguration.parseElementNames(key));

        // with attribute
        key = "x.y.z[@name]";
        ArrayAssert.assertEquals("key with attribute", array, XMLConfiguration.parseElementNames(key));

        // null key
        ArrayAssert.assertEquals("null key", new String[] {}, XMLConfiguration.parseElementNames(null));
    }

    public void testParseAttributeName()
    {
        // no attribute
        String key = "x.y.z";
        assertEquals("no attribute", null, XMLConfiguration.parseAttributeName(key));

        // simple attribute
        key = "x.y.z[@name]";
        assertEquals("simple attribute", "name", XMLConfiguration.parseAttributeName(key));

        // missing end marker
        key = "x.y.z[@name";
        assertEquals("missing end marker", "name", XMLConfiguration.parseAttributeName(key));

        // null key
        assertEquals("null key", null, XMLConfiguration.parseAttributeName(null));
    }
}
