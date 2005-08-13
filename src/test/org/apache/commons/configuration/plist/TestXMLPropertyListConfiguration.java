/*
 * Copyright 2005 The Apache Software Foundation.
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

package org.apache.commons.configuration.plist;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;
import junitx.framework.ObjectAssert;
import junitx.framework.ArrayAssert;
import junitx.framework.ListAssert;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TestXMLPropertyListConfiguration extends TestCase
{
    private FileConfiguration config;

    protected void setUp() throws Exception
    {
        config = new XMLPropertyListConfiguration();
        config.setFileName("conf/test.plist.xml");
        config.load();
    }

    public void testString() throws Exception
    {
        assertEquals("'string' property", "value1", config.getString("string"));
    }

    public void testInteger() throws Exception
    {
        assertEquals("'integer' property", 12345, config.getInt("integer"));
    }

    public void testReal() throws Exception
    {
        assertEquals("'real' property", -12.345, config.getDouble("real"), 0);
    }

    public void testBoolean() throws Exception
    {
        assertEquals("'boolean1' property", true, config.getBoolean("boolean1"));
        assertEquals("'boolean2' property", false, config.getBoolean("boolean2"));
    }

    public void testDictionary()
    {
        assertEquals("1st element", "value1", config.getProperty("dictionary.key1"));
        assertEquals("2nd element", "value2", config.getProperty("dictionary.key2"));
        assertEquals("3rd element", "value3", config.getProperty("dictionary.key3"));
    }

    public void testSubset()
    {
        Configuration subset = config.subset("dictionary");
        Iterator keys = subset.getKeys();

        String key = (String) keys.next();
        assertEquals("1st key", "key1", key);
        assertEquals("1st value", "value1", subset.getString(key));

        key = (String) keys.next();
        assertEquals("2nd key", "key2", key);
        assertEquals("2nd value", "value2", subset.getString(key));

        key = (String) keys.next();
        assertEquals("3rd key", "key3", key);
        assertEquals("3rd value", "value3", subset.getString(key));

        assertFalse("more than 3 properties founds", keys.hasNext());
    }

    public void testArray()
    {
        Object array = config.getProperty("array");

        assertNotNull("array not found", array);
        ObjectAssert.assertInstanceOf("the array element is not parsed as a List", List.class, array);
        List list = config.getList("array");

        assertFalse("empty array", list.isEmpty());
        assertEquals("size", 3, list.size());
        assertEquals("1st element", "value1", list.get(0));
        assertEquals("2nd element", "value2", list.get(1));
        assertEquals("3rd element", "value3", list.get(2));
    }

    public void testNestedArray()
    {
        String key = "nested-array";

        Object array = config.getProperty(key);

        // root array
        assertNotNull("array not found", array);
        ObjectAssert.assertInstanceOf("the array element is not parsed as a List", List.class, array);
        List list = config.getList(key);

        assertFalse("empty array", list.isEmpty());
        assertEquals("size", 2, list.size());

        // 1st array
        ObjectAssert.assertInstanceOf("the array element is not parsed as a List", List.class, list.get(0));
        List list1 = (List) list.get(0);
        assertFalse("nested array 1 is empty", list1.isEmpty());
        assertEquals("size", 2, list1.size());
        assertEquals("1st element", "a", list1.get(0));
        assertEquals("2nd element", "b", list1.get(1));

        // 2nd array
        ObjectAssert.assertInstanceOf("the array element is not parsed as a List", List.class, list.get(1));
        List list2 = (List) list.get(1);
        assertFalse("nested array 2 is empty", list2.isEmpty());
        assertEquals("size", 2, list2.size());
        assertEquals("1st element", "c", list2.get(0));
        assertEquals("2nd element", "d", list2.get(1));
    }

    public void testDictionaryArray()
    {
        String key = "dictionary-array";

        Object array = config.getProperty(key);

        // root array
        assertNotNull("array not found", array);
        ObjectAssert.assertInstanceOf("the array element is not parsed as a List", List.class, array);
        List list = config.getList(key);

        assertFalse("empty array", list.isEmpty());
        assertEquals("size", 2, list.size());

        // 1st dictionary
        ObjectAssert.assertInstanceOf("the dict element is not parsed as a Configuration", Configuration.class, list.get(0));
        Configuration conf1 = (Configuration) list.get(0);
        assertFalse("configuration 1 is empty", conf1.isEmpty());
        assertEquals("configuration element", "bar", conf1.getProperty("foo"));

        // 2nd dictionary
        ObjectAssert.assertInstanceOf("the dict element is not parsed as a Configuration", Configuration.class, list.get(1));
        Configuration conf2 = (Configuration) list.get(1);
        assertFalse("configuration 2 is empty", conf2.isEmpty());
        assertEquals("configuration element", "value", conf2.getProperty("key"));
    }

    public void testNested()
    {
        assertEquals("nested property", "value", config.getString("nested.node1.node2.node3"));
    }

    public void invalidtestSave() throws Exception
    {
        File savedFile = new File("target/testsave.plist.xml");

        // remove the file previously saved if necessary
        if (savedFile.exists())
        {
            assertTrue(savedFile.delete());
        }

        // add an array of strings to the configuration
        /*
        config.addProperty("string", "value1");
        List list = new ArrayList();
        for (int i = 1; i < 5; i++)
        {
            list.add("value" + i);
        }
        config.addProperty("newarray", list);*/
        // todo : investigate why the array structure of 'newarray' is lost in the saved file

        // add a map of strings
        /*
        Map map = new HashMap();
        map.put("foo", "bar");
        map.put("int", new Integer(123));
        config.addProperty("newmap", map);
        */
        // todo : a Map added to a HierarchicalConfiguration should be decomposed as list of nodes

        // save the configuration
        String filename = savedFile.getAbsolutePath();
        config.save(filename);

        assertTrue("The saved file doesn't exist", savedFile.exists());

        // read the configuration and compare the properties
        Configuration checkConfig = new XMLPropertyListConfiguration(filename);
        for (Iterator i = config.getKeys(); i.hasNext();)
        {
            String key = (String) i.next();
            assertTrue("The saved configuration doesn't contain the key '" + key + "'", checkConfig.containsKey(key));

            Object value = checkConfig.getProperty(key);
            if (value instanceof byte[])
            {
                byte[] array = (byte[]) value;
                ArrayAssert.assertEquals("Value of the '" + key + "' property", (byte[]) config.getProperty(key), array);
            }
            else if (value instanceof List)
            {
                List list1 = (List) value;
                ListAssert.assertEquals("Value of the '" + key + "' property", (List) config.getProperty(key), list1);
            }
            else
            {
                assertEquals("Value of the '" + key + "' property", config.getProperty(key), checkConfig.getProperty(key));
            }

        }
    }
}
