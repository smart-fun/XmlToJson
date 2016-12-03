package fr.arnaudguyon.xmltojsonlib;

import android.support.annotation.NonNull;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * Created by Arnaud Guyon on 03/12/2016.
 */

public class JsonToXml {

    public static class Builder {

        private JSONObject mJson;

        public Builder(@NonNull JSONObject jsonObject) {
            mJson = jsonObject;
        }

        public JsonToXml build() {
            return new JsonToXml(mJson);
        }
    }

    private JSONObject mJson;
    private Node mNode;

    private JsonToXml(@NonNull JSONObject jsonObject) {
        mJson = jsonObject;
    }

    @Override
    public String toString() {
        //return serialize(mJson);
        mNode = new Node(null, "");
        prepareObject(mNode, mJson);
        return nodeToXML(mNode);
    }

    private String nodeToXML(Node node) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);

            nodeToXml(serializer, node);

            serializer.endDocument();
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);  // TODO: do my own
        }
    }

    private void nodeToXml(XmlSerializer serializer, Node node) throws IOException {
        if (node.mName != null) {
            serializer.startTag("", node.mName);

            for (Node.Attribute attribute : node.mAttributes) {
                serializer.attribute("", attribute.mKey, attribute.mValue);
            }
            if (node.mContent != null) {
                serializer.text(node.mContent);
            }
        }

        for (Node subNode : node.mChildren) {
            nodeToXml(serializer, subNode);
        }

        if (node.mName != null) {
            serializer.endTag("", node.mName);
        }
    }

    private void prepareObject(Node node, JSONObject json) {
        Iterator<String> keyterator = json.keys();
        while (keyterator.hasNext()) {
            String key = keyterator.next();
            Object object = json.opt(key);
            if (object != null) {
                if (object instanceof JSONObject) {
                    JSONObject subObject = (JSONObject) object;
                    String path = node.mPath + "/" + key;
                    Node subNode = new Node(key, path);
                    node.addChild(subNode);
                    prepareObject(subNode, subObject);
                } else if (object instanceof JSONArray) {
                    JSONArray array = (JSONArray) object;
                    prepareArray(node, key, array);
                } else {
                    String path = node.mPath + "/" + key;
                    String value = object.toString();
                    if (isAttribute(path, key)) {
                        node.addAttribute(key, value);
                    } else if (isContent(path, key) ) {
                        node.setContent(value);
                    } else {
                        Node subNode = new Node(key, node.mPath);
                        subNode.mContent = value;
                        node.addChild(subNode);
                    }
                }
            }
        }
    }

    private void prepareArray(Node node, String key, JSONArray array) {
        int count = array.length();
        String path = node.mPath + "/" + key;
        for (int i = 0; i < count; ++i) {
            Node subNode = new Node(key, path);
            Object object = array.opt(i);
            if (object != null) {
                if (object instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) object;
                    prepareObject(subNode, jsonObject);
                } else if (object instanceof JSONArray) {
                    JSONArray subArray = (JSONArray) object;
                    prepareArray(subNode, key, subArray);
                } else {
                    String value = object.toString();
                    subNode.mName = key;
                    subNode.mContent = value;
                }
            }
            node.addChild(subNode);
        }
    }

    private boolean isAttribute(String path, String key) {
        return false;    // TODO: exception with content
    }

    private boolean isContent(String path, String key) {
        return false;    // TODO: exception with content
    }
}
