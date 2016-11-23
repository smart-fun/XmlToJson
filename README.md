# XML to JSON for Android #

**XML to JSON** is an Android Studio Library which converts XML to JSON. It takes a **String or InputStream** as the source for the XML and creates a **JSONObject** that can be directly manipulated or converted into a String.

It is fully **configurable** so that you can change for example attribute names.

It is easy to integrate with **gradle**.

## Examples of use ##

### Basic usage ###

The code to convert a XML String into a JSON String is the following:

```java
String xml = ...;  // some xml String

XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();

// convert to a JSONObject
JSONObject jsonObject = xmlToJson.toJson();

// OR convert to a Json String
String jsonString = xmlToJson.toString();

// OR convert to a formatted Json String (with indent & line breaks)
String formatted = xmlToJson.toFormattedString();

```

```xml
<?xml version="1.0" encoding="utf-8"?>
<library>
    <owner>John Doe</owner>
    <book id="007">James Bond</book>
    <book id="000">Book for the dummies</book>
</library>
```

```json
{  
   "library":{
      "owner": "John Doe",
      "book":[  
         {  
            "id":7,
            "content":"James Bond"
         },
         {  
            "id":0,
            "content":"Book for the dummies"
         }
      ]
   }
}
```

### Use with an InputStream ###

Instead of converting a XML String, you can convert a InputStream, coming from a File for example.

```java
    AssetManager assetManager = context.getAssets();
    InputStream inputStream = assetManager.open("myFile.xml");
    XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
    String json = xmlToJson.toString();
    inputStream.close();
```

### Custom Content names ###

By default, the content of a XML Tag is converted into a key called "content". This name can be changed with a custom one, using **Builder.setContentName**(String contentPath, String replacementName). You can change as many content names as you want.

```java
public String convertXmlToJson(String xml) {
    XmlToJson xmlToJson = new XmlToJson.Builder(xml)
        .setContentName("/library/book", "title")
        .build();
    return xmlToJson.toString();
}
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<library>
    <book id="007">James Bond</book>
    <book id="000">Book for the dummies</book>
</library>
```

```json
{  
   "library":{  
      "book":[  
         {  
            "id":7,
            "title":"James Bond"
         },
         {  
            "id":0,
            "title":"Book for the dummies"
         }
      ]
   }
}
```

### Custom Attributes names ###

Attributes are converted into key / values in the JSON. The attribute names may conflict with other keys. You can change the name of any attribute, by specifying the path to the attribute and the replacement name, using **Builder.setAttributeName**(String attributePath, String replacementName).


```java
public String convertXmlToJson(String xml) {
    XmlToJson xmlToJson = new XmlToJson.Builder(xml)
        .setAttributeName("/library/book/id", "code")
        .build();
    return xmlToJson.toString();
}
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<library>
    <book id="007">James Bond</book>
    <book id="000">Book for the dummies</book>
</library>
```

```json
{  
   "library":{  
      "book":[  
         {  
            "code":7,
            "content":"James Bond"
         },
         {  
            "code":0,
            "content":"Book for the dummies"
         }
      ]
   }
}
```

### Forcing a Tag to be a list ###

In a XML hierarchy, an entry can have children. For example, \<library> has 2 entries \<book>. In case there is only one book, there is no way to know that Book is a list. But you can force it using **Builder.forceList**(String path).

```xml
<?xml version="1.0" encoding="utf-8"?>
<library>
    <book id="007">James Bond</book>
</library>
```

By default, the \<book> tag is NOT considered as a list

```json
{  
   "library":{  
      "book":{  
         "id":7,
         "content":"James Bond"
      }
   }
}
```

```java
public String convertXmlToJson(String xml) {
    XmlToJson xmlToJson = new XmlToJson.Builder(xml)
        .forceList("/library/book")
        .build();
    return xmlToJson.toString();
}
```

Now \<book> is considered as a list:

```json
{  
   "library":{  
      "book":[  
         {  
            "id":7,
            "content":"James Bond"
         }
      ]
   }
}
```

## Installation with gradle ##

Add the following maven{} line to your **PROJECT** build.gradle file

```
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }		// add this line
    }
}
```

Add the libary dependency to your **APP** build.gradle file

```
dependencies {
    compile 'com.github.smart-fun:XmlToJson:1.1.1'    // add this line
}
```

## License ##

Copyright 2016 Arnaud Guyon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
