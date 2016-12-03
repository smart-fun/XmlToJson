package fr.arnaudguyon.xmltojson;

/*
    Copyright 2016 Arnaud Guyon

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

import android.content.Context;
import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

import static org.junit.Assert.assertEquals;

// TODO: check between Mac & PC as the Tag order seem to vary
// TODO: add tests for Formatted String
// TODO: add Unit Tests for JSON to XML

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class JsonToXmlInstrumentedTest {

    @Test
    public void xliffFileTest() throws Exception {

        Context context = InstrumentationRegistry.getTargetContext();

        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("texts.xliff");
        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
        inputStream.close();
        String json = xmlToJson.toString();
        Log.i("Unit Test", json);

        String attended = "{\"xliff\":{\"xmlns\":\"urn:oasis:names:tc:xliff:document:1.2\",\"file\":{\"body\":{\"trans-unit\":[{\"note\":\"create address button\",\"id\":\"address_addButton\",\"target\":{\"content\":\"NEUE ADRESSE HINZUFÜGEN\",\"xml:lang\":\"de\"},\"source\":{\"content\":\"Add a new Address\",\"xml:lang\":\"en\"}},{\"note\":\"add address button\",\"id\":\"address_createButton\",\"target\":{\"content\":\"ADRESSE ERSTELLEN\",\"xml:lang\":\"de\"},\"source\":{\"content\":\"Create Address\",\"xml:lang\":\"en\"}}]},\"datatype\":\"plaintext\",\"target-language\":\"de\",\"original\":\"global\",\"source-language\":\"en\"},\"version\":1.2}}";
        assertEquals(attended, json);
    }

    @Test
    public void stringsFileTest() throws Exception {

        Context context = InstrumentationRegistry.getTargetContext();

        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("strings_en.xml");
        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
        inputStream.close();
        String json = xmlToJson.toString();
        Log.i("Unit Test", json);

        String attended = "{\"resources\":{\"string\":[{\"content\":\"XML to JSON App\",\"name\":\"app_name\",\"translatable\":false},{\"content\":\"hello %1$s\",\"name\":\"helloUser\",\"translatable\":true},{\"content\":\"Quit\",\"name\":\"quit_button\"}]}}";
        assertEquals(attended, json);
    }

    @Test
    public void stringTest() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><resources><string translatable=\"false\" name=\"app_name\">XML to JSON App</string><string translatable=\"true\" name=\"helloUser\">hello %1$s</string><string name=\"quit_button\">Quit</string></resources>";

        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
        String json = xmlToJson.toString();
        Log.i("Unit Test", json);

        String attended = "{\"resources\":{\"string\":[{\"content\":\"XML to JSON App\",\"name\":\"app_name\",\"translatable\":false},{\"content\":\"hello %1$s\",\"name\":\"helloUser\",\"translatable\":true},{\"content\":\"Quit\",\"name\":\"quit_button\"}]}}";
        assertEquals(attended, json);
    }

    @Test
    public void attributeReplacementTest() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><books><book id=\"007\">James Bond</book><book id=\"000\">Book for the dummies</book></books>";

        XmlToJson xmlToJson = new XmlToJson.Builder(xml)
                .setAttributeName("/books/book/id", "attributeReplacement")
                .build();
        String json = xmlToJson.toString();
        Log.i("Unit Test", json);

        String attended = "{\"books\":{\"book\":[{\"content\":\"James Bond\",\"attributeReplacement\":7},{\"content\":\"Book for the dummies\",\"attributeReplacement\":0}]}}";
        assertEquals(attended, json);
    }

    @Test
    public void contentReplacementTest() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><books><book id=\"007\">James Bond</book><book id=\"000\">Book for the dummies</book></books>";

        XmlToJson xmlToJson = new XmlToJson.Builder(xml)
                .setContentName("/books/book", "contentReplacement")
                .build();
        String json = xmlToJson.toString();
        Log.i("Unit Test", json);

        String attended = "{\"books\":{\"book\":[{\"contentReplacement\":\"James Bond\",\"id\":7},{\"contentReplacement\":\"Book for the dummies\",\"id\":0}]}}";
        assertEquals(attended, json);
    }

    @Test
    public void oneObjectTest() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><books><book id=\"007\">James Bond</book></books>";

        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
        String json = xmlToJson.toString();
        Log.i("Unit Test", json);

        String attended = "{\"books\":{\"book\":{\"content\":\"James Bond\",\"id\":7}}}";
        assertEquals(attended, json);
    }

    @Test
    public void oneObjectAsListTest() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><books><book id=\"007\">James Bond</book><other id=\"hello\"/></books>";

        XmlToJson xmlToJson = new XmlToJson.Builder(xml)
                .forceList("/books/book")
                .build();
        String json = xmlToJson.toString();
        Log.i("Unit Test", json);

        String attended = "{\"books\":{\"other\":{\"id\":\"hello\"},\"book\":[{\"content\":\"James Bond\",\"id\":7}]}}";
        assertEquals(attended, json);
    }

    @Test
    public void invalidHelloWorldTest() throws Exception {
        String xml = "hello world";
        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
        String json = xmlToJson.toString();
        assertEquals("{}", json);
    }
    @Test
    public void invalidUnfinishedTest() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><books>";
        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
        String json = xmlToJson.toString();
        assertEquals("{}", json);
    }
    @Test
    public void invalidInputStreamTest() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("strings_en.xml");
        inputStream.close(); // CLOSE INPUT STREAM
        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
        String json = xmlToJson.toString();
        assertEquals("{}", json);
    }
}
