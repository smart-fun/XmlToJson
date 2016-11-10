package fr.arnaudguyon.xmltojsonlib;

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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Arnaud Guyon on 08.11.16.
 * Converts XML to JSON
 */

public class XmlToJson {

    private static final String TAG = "XmlToJson";
    private static final String DEFAULT_CONTENT_NAME = "content";
    private static final String DEFAULT_ENCODING = "utf-8";

    /**
     * Builder class to create a XmlToJson object
     */
    public static class Builder {

        private StringReader mStringSource;
        private InputStream mInputStreamSource;
        private String mInputEncoding = DEFAULT_ENCODING;
        private HashSet<String> mForceListPaths = new HashSet<>();
        private HashMap<String, String> mAttributeNameReplacements = new HashMap<>();
        private HashMap<String, String> mContentNameReplacements = new HashMap<>();

        /**
         * Constructor
         * @param xmlSource XML source
         */
        public Builder(@NonNull String xmlSource) {
            mStringSource = new StringReader(xmlSource);
        }

        /**
         * Constructor
         * @param inputStreamSource XML source
         * @param inputEncoding XML encoding format, can be null (uses UTF-8 if null).
         */
        public Builder(@NonNull InputStream inputStreamSource, @Nullable String inputEncoding) {
            mInputStreamSource = inputStreamSource;
            mInputEncoding = (inputEncoding != null) ? inputEncoding : DEFAULT_ENCODING;
        }

        /**
         * Force a XML Tag to be interpreted as a list
         * @param path Path for the tag, with format like "/parentTag/childTag/tagAsAList"
         * @return the Builder
         */
        public Builder forceListForPath(@NonNull String path) {
            mForceListPaths.add(path);
            return this;
        }

        /**
         * Change the name of an attribute
         * @param attributePath Path for the attribute, using format like "/parentTag/childTag/childTagAttribute"
         * @param replacementName Name used for replacement (childTagAttribute becomes replacementName)
         * @return the Builder
         */
        public Builder setAttributeNameReplacement(@NonNull String attributePath, @NonNull String replacementName) {
            mAttributeNameReplacements.put(attributePath, replacementName);
            return this;
        }

        /**
         * Change the name of the key for a XML content
         * In XML there is no extra key name for a tag content. So a default name "content" is used.
         * This "content" name can be replaced with a custom name.
         * @param contentPath Path for the Tag that holds the content, using format like "/parentTag/childTag"
         * @param replacementName Name used in place of the default "content" key
         * @return
         */
        public Builder setContentNameReplacement(@NonNull String contentPath, @NonNull String replacementName) {
            mContentNameReplacements.put(contentPath, replacementName);
            return this;
        }

        /**
         * Creates the XmlToJson object
         * @return a XmlToJson object
         */
        public XmlToJson build() {
            return new XmlToJson(this);
        }
    }

    private StringReader mStringSource;
    private InputStream mInputStreamSource;
    private String mInputEncoding;
    private HashSet<String> mForceListPaths;
    private HashMap<String, String> mAttributeNameReplacements;
    private HashMap<String, String> mContentNameReplacements;
    private JSONObject mJsonObject; // Used for caching the result

    private XmlToJson(Builder builder) {
        mStringSource = builder.mStringSource;
        mInputStreamSource = builder.mInputStreamSource;
        mInputEncoding = builder.mInputEncoding;
        mForceListPaths = builder.mForceListPaths;
        mAttributeNameReplacements = builder.mAttributeNameReplacements;
        mContentNameReplacements = builder.mContentNameReplacements;
    }

    /**
     * Creates a JSONObject. To convert this object to a json String call object.toString();
     * @return a JSONObject, or null. Can be empty if the XML is invalid.
     */
    public @Nullable JSONObject toJson() {
        if (mJsonObject == null) {
            try {
                Tag parentTag = new Tag("", "xml");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);   // tags with namespace are taken as-is ("namespace:tagname")
                XmlPullParser xpp = factory.newPullParser();

                setInput(xpp);

                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.START_DOCUMENT) {
                    eventType = xpp.next();
                }
                readTags(parentTag, xpp);

                unsetInput();

                mJsonObject = convertTagToJson(parentTag);  // cache result for future use
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return mJsonObject;
    }

    private void setInput(XmlPullParser xpp) {
        if (mStringSource != null) {
            try {
                xpp.setInput(mStringSource);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        } else {
            try {
                xpp.setInput(mInputStreamSource, mInputEncoding);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
    }

    private void unsetInput() {
        if (mStringSource != null) {
            mStringSource.close();
        }
        // else the InputStream has been given by the user, it is not our role to close it
    }

    private void readTags(Tag parent, XmlPullParser xpp) {
        try {
            int eventType;
            do {
                eventType = xpp.next();
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xpp.getName();
                    String path = parent.getPath() + "/" + tagName;
                    Tag child = new Tag(path, tagName);
                    parent.addChild(child);

                    // Attributes are taken into account as key/values in the child
                    int attrCount = xpp.getAttributeCount();
                    for (int i = 0; i < attrCount; ++i) {
                        String attrName = xpp.getAttributeName(i);
                        String attrValue = xpp.getAttributeValue(i);
                        String attrPath = parent.getPath() + "/" + child.getName() + "/" + attrName;
                        attrName = getAttributeNameReplacement(attrPath, attrName);
                        Tag attribute = new Tag(attrPath, attrName);
                        attribute.setContent(attrValue);
                        child.addChild(attribute);
                    }

                    readTags(child, xpp);
                } else if (eventType == XmlPullParser.TEXT) {
                    parent.setContent(xpp.getText());
                } else if (eventType == XmlPullParser.END_TAG) {
                    return;
                } else {
                    Log.i(TAG, "unknown xml eventType " + eventType);
                }
            } while (eventType != XmlPullParser.END_DOCUMENT);
        } catch (XmlPullParserException | IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private JSONObject convertTagToJson(Tag tag) {
        JSONObject json = new JSONObject();

        // Content is injected as a key/value
        if (tag.getContent() != null) {
            try {
                String path = tag.getPath();
                String name = getContentNameReplacement(path, DEFAULT_CONTENT_NAME);
                json.put(name, tag.getContent());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            if (tag.isList() || isForcedList(tag)) {
                JSONArray list = new JSONArray();
                ArrayList<Tag> children = tag.getChildren();
                for (Tag child : children) {
                    list.put(convertTagToJson(child));
                }
                String childrenNames = tag.getChild(0).getName();
                json.put(childrenNames, list);
                return json;
            } else {
                ArrayList<Tag> children = tag.getChildren();
                if (children.size() == 0) {
                    json.put(tag.getName(), tag.getContent());
                } else {
                    for (Tag child : children) {
                        if (child.hasChildren()) {
                            JSONObject jsonChild = convertTagToJson(child);
                            json.put(child.getName(), jsonChild);
                        } else {
                            json.put(child.getName(), child.getContent());
                        }
                    }
                }
                return json;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isForcedList(Tag tag) {
        String path = tag.getPath();
        return mForceListPaths.contains(path);
    }

    private String getAttributeNameReplacement(String path, String defaultValue) {
        String result = mAttributeNameReplacements.get(path);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

    private String getContentNameReplacement(String path, String defaultValue) {
        String result = mContentNameReplacements.get(path);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

}
