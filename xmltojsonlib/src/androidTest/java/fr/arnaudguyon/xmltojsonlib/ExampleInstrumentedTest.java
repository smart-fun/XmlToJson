package fr.arnaudguyon.xmltojsonlib;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
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

    private static final String TAG = "ExampleInstrumentedTest";

    @Test
    public void numbersTest() throws Exception {

        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("numbers.xml");
        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null)
                .forceIntegerForPath("/map/a")
                .forceLongForPath("/map/b")
                .forceDoubleForPath("/map/c")
                .forceDoubleForPath("/map/d")
                .forceIntegerForPath("/map/i/id")
                .build();
        inputStream.close();

        JSONObject json = xmlToJson.toJson();

        JSONObject map = json.getJSONObject("map");
        assertTrue(map.getInt("a") == 42);
        assertTrue(map.getLong("b") == 1498094219318L);
        assertTrue(map.getDouble("c") == 0.1);
        assertTrue(map.getDouble("d") == 1099511627776.5);

        JSONObject i = map.getJSONObject("i");
        assertTrue(i.getInt("id") == 43);

        assertTrue(map.getString("j").equals("44"));

        JsonToXml jsonToXml = new JsonToXml.Builder(json).build();

        String result = jsonToXml.toString();
        assertTrue(result.contains("<a>42</a>"));
        assertTrue(result.contains("<b>1498094219318</b>"));
        assertTrue(result.contains("<c>0.1</c>"));
        assertTrue(result.contains("<d>1099511627776.5</d>"));
    }

    @Test
    public void boolAndEmptyTest() throws Exception {

        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("numbers.xml");
        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null)
                .forceBooleanForPath("/map/e")
                .forceBooleanForPath("/map/f")
                .forceBooleanForPath("/map/g")
                .build();
        inputStream.close();

        JSONObject json = xmlToJson.toJson();

        JSONObject map = json.getJSONObject("map");
        assertTrue(map.getBoolean("e") == true);
        assertTrue(map.getBoolean("f") == false);
        assertTrue(map.getBoolean("g") == false);
        assertTrue(map.getString("h").equals(""));
    }

    @Test
    public void invalidHelloWorldTest() throws Exception {
        String xml = "hello world";
        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
        String json = xmlToJson.toString();
        assertEquals("{}", json);
    }

    @Test
    public void emptyTagTest() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><books/>";
        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
        String json = xmlToJson.toString();
        assertEquals("{\"books\":\"\"}", json);
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
        for (int i = 0; i < size; ++i) {
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

    @Test
    public void attributeReplacementTest() throws Exception {

        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("common.xml");

        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null)
                .setAttributeName("/library/book/id", "attributeReplacement")
                .build();

        inputStream.close();

        JSONObject result = xmlToJson.toJson();
        JSONObject library = result.getJSONObject("library");
        JSONArray books = library.getJSONArray("book");
        assertEquals(books.length(), 2);
        for (int i = 0; i < books.length(); ++i) {
            JSONObject book = books.getJSONObject(i);
            book.getInt("attributeReplacement");
        }
    }

    @Test
    public void contentReplacementTest() throws Exception {

        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("common.xml");

        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null)
                .setContentName("/library/book", "contentReplacement")
                .build();

        inputStream.close();

        JSONObject result = xmlToJson.toJson();
        JSONObject library = result.getJSONObject("library");
        JSONArray books = library.getJSONArray("book");
        JSONObject book1 = books.getJSONObject(0);
        JSONObject book2 = books.getJSONObject(1);
        String content1 = book1.getString("contentReplacement");
        String content2 = book2.getString("contentReplacement");
        assertTrue(content1.equals("James Bond") || content1.equals("Book for the dummies"));
        assertTrue(content2.equals("James Bond") || content2.equals("Book for the dummies"));
    }

    @Test
    public void oneObjectTest() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><books><book id=\"007\">James Bond</book></books>";

        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
        JSONObject json = xmlToJson.toJson();
        JSONObject books = json.getJSONObject("books");
        books.getJSONObject("book");
    }

    @Test
    public void oneObjectAsListTest() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><books><book id=\"007\">James Bond</book><other id=\"hello\"/></books>";

        XmlToJson xmlToJson = new XmlToJson.Builder(xml)
                .forceList("/books/book")
                .build();
        JSONObject json = xmlToJson.toJson();
        JSONObject books = json.getJSONObject("books");
        books.getJSONArray("book");
    }

    @Test
    public void escapeSpecialCharsTest() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("escape_chars.xml");

        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
        inputStream.close();

        try {
            String formatted = xmlToJson.toFormattedString();
            new JSONObject(formatted);
        } catch (JSONException exception) {
            exception.printStackTrace();
            assertTrue("invalid JSON", false);
        }
    }

    // "\r" characters were not escaped
    // RSS feed taken from https://www.bola.net/feed/
    @Test
    public void rssURLescaped() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("rss.xml");

        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
        inputStream.close();

        try {
            JSONObject json = xmlToJson.toJson();
            JSONObject rss = json.getJSONObject("rss");
            JSONObject channel = rss.getJSONObject("channel");
            JSONArray items = channel.getJSONArray("item");
            for (int i = 0; i < items.length(); ++i) {
                JSONObject item = items.getJSONObject(i);
                JSONObject enclosure = item.getJSONObject("enclosure");
                String url = enclosure.getString("url");
                Log.i(TAG, "url: " + url);
            }
            json.toString();
        } catch (JSONException exception) {
            exception.printStackTrace();
            assertTrue("invalid JSON", false);
        }
    }


}