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

import java.util.ArrayList;

/**
 * Class used to store XML hierarchy
 *
 */
public class Tag {

    private String mPath;
    private String mName;
    private ArrayList<Tag> mChildren = new ArrayList<>();
    private String mContent;

    /* package */ Tag(String path, String name) {
        mPath = path;
        mName = name;
    }

    /* package */ void addChild(Tag tag) {
        mChildren.add(tag);
    }

    /* package */ void setContent(String content) {
        // checks that there is a relevant content (not only spaces or \n)
        boolean hasContent = false;
        if (content != null) {
            for(int i=0; i<content.length(); ++i) {
                char c = content.charAt(i);
                if ((c != ' ') && (c != '\n')) {
                    hasContent = true;
                    break;
                }
            }
        }
        if (hasContent) {
            mContent = content;
        }
    }

    /* package */ String getName() {
        return mName;
    }

    /* package */ String getContent() {
        return mContent;
    }

    /* package */ ArrayList<Tag> getChildren() {
        return mChildren;
    }

    /* package */ boolean hasChildren() {
        return (mChildren.size() > 0);
    }

    /* package */ int getChildrenCount() {
        return mChildren.size();
    }

    /* package */ Tag getChild(int index) {
        if ((index >= 0) && (index < mChildren.size())) {
            return mChildren.get(index);
        }
        return null;
    }

    /* package */ boolean isList() {
        if (mChildren.size() > 1) {
            String tagName = getChild(0).mName; // All Tags must have the same name
            for(int i=1; i<mChildren.size(); ++i) {
                if (!tagName.equals(mChildren.get(i).mName)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /* package */ String getPath() {
        return mPath;
    }

    @Override
    public String toString() {
        return "Tag: " + mName + ", " + mChildren.size() + " children, Content: " + mContent;
    }
}
