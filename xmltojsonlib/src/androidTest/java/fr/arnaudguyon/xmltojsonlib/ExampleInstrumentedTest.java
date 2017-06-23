package fr.arnaudguyon.xmltojsonlib;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void numbersTest() throws Exception {

        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("numbers.xml");
        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
        inputStream.close();

        JSONObject json = xmlToJson.toJson();

        JsonToXml jsonToXml = new JsonToXml.Builder(json).build();

        String result = jsonToXml.toString();
        assertTrue(result.contains("<value>1498094219318</value>"));
        assertTrue(result.contains("<value>0.1</value>"));
        assertTrue(result.contains("<value>1099511627776.5</value>"));
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
        InputStream inputStream = assetManager.open("common.xml");
        inputStream.close(); // CLOSE INPUT STREAM
        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
        String json = xmlToJson.toString();
        assertEquals("{}", json);
    }

    @Test
    public void issueGitHub3_Test() throws Exception {

        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("bug_3_github.xml");

        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null)
                .forceList("/biblio/auteur")
                .forceList("/biblio/auteur/ouvrages/livre")
                .build();

//        String jsonStr = xmlToJson.toString();
        JSONObject json = xmlToJson.toJson();
        JSONObject biblio = json.getJSONObject("biblio");
        JSONArray auteurs = biblio.getJSONArray("auteur");
        int nbAuteurs = auteurs.length();
        assertEquals(nbAuteurs, 2);
        for (int i = 0; i < nbAuteurs; ++i) {
            JSONObject auteur = auteurs.getJSONObject(i);
            String id = auteur.optString("id");
            String nom = auteur.optString("nom");
            assertTrue("id not found", !TextUtils.isEmpty(id));
            assertTrue("nom not found", !TextUtils.isEmpty(nom));
            JSONObject ouvrages = auteur.getJSONObject("ouvrages");
            String quantite = ouvrages.getString("quantite");
            assertTrue("quantite not found", !TextUtils.isEmpty(quantite));
            Object livres = ouvrages.opt("livre");
            assertTrue("livre is not an array", livres instanceof JSONArray);
        }

        inputStream.close();
    }

    @Test
    public void skipAttributeTest() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("common.xml");

        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null)
                .skipAttribute("/library/book/id")
                .build();

        inputStream.close();

        JSONObject result = xmlToJson.toJson();
        assertTrue(result.has("library"));
        JSONObject library = result.getJSONObject("library");
        assertTrue(library.has("book"));
        JSONArray books = library.getJSONArray("book");
        int size = books.length();
        assertTrue(size == 2);
        for(int i=0; i<size; ++i) {
            JSONObject book = books.getJSONObject(i);
            assertFalse(book.has("id"));
        }

    }

    @Test
    public void skipTagTest() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("common.xml");

        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null)
                .skipTag("/library/owner")
                .build();

        inputStream.close();

        JSONObject result = xmlToJson.toJson();
        assertTrue(result.has("library"));
        JSONObject library = result.getJSONObject("library");
        assertTrue(library.has("book"));
        assertFalse(library.has("owner"));

    }

}