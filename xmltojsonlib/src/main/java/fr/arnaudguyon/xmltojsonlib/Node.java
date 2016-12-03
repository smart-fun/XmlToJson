package fr.arnaudguyon.xmltojsonlib;

import java.util.ArrayList;

/**
 * Created by arnaud on 03/12/2016.
 */

class Node {

    class Attribute {
        String mKey;
        String mValue;
        Attribute(String key, String value) {
            mKey = key;
            mValue = value;
        }
    }

    String mName;
    String mPath;
    String mContent;
    ArrayList<Attribute> mAttributes = new ArrayList<>();
    ArrayList<Node> mChildren = new ArrayList<>();

    Node(String name, String path) {
        mName = name;
        mPath = path;
    }

    void addAttribute(String key, String value) {
        mAttributes.add(new Attribute(key, value));
    }
    void setContent(String content) {
        mContent = content;
    }

    void addChild(Node child) {
        mChildren.add(child);
    }

}
