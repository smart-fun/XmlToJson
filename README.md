# XML to JSON for Android #

XML to JSON is an Android Studio Library which converts easily **XML to JSON** and **JSON to XML**.

It is fully **configurable** so that you can change for example attribute names.

It is easy to integrate with **gradle**.

## XML to JSON ##

### Basic usage ###

There are 2 ways to create a **XmlToJson** object: from a **String** or from an **InputStream**.

```java
    String xmlString;  // some XML String previously created
    XmlToJson xmlToJson = new XmlToJson.Builder(xmlString).build();
```

OR

```java
    AssetManager assetManager = context.getAssets();
    InputStream inputStream = assetManager.open("myFile.xml");
    XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
    inputStream.close();
```

Then you can convert it to a **JSONObject**, a **String**, or a **Formatted String** (with indentation and line breaks).

```java
    // convert to a JSONObject
    JSONObject jsonObject = xmlToJson.toJson();

    // convert to a Json String
    String jsonString = xmlToJson.toString();

    // convert to a formatted Json String
    String formatted = xmlToJson.toFormattedString();
```

Thats' it. Here is an example of XML...

```xml
<?xml version="1.0" encoding="utf-8"?>
<library>
    <owner>John Doe</owner>
    <book id="007">James Bond</book>
    <book id="000">Book for the dummies</book>
</library>
```

... converted into JSON

```json
{  
   "library":{
      "owner": "John Doe",
      "book":[  
         {  
            "id":"007",
            "content":"James Bond"
         },
         {  
            "id":"000",
            "content":"Book for the dummies"
         }
      ]
   }
}
```

### Custom Content names ###

By default, the content of a XML Tag is converted into a key called "content". This name can be changed with a custom one, using **Builder.setContentName**(String contentPath, String replacementName). You can change as many content names as you want.

```xml
<?xml version="1.0" encoding="utf-8"?>
<library>
    <book id="007">James Bond</book>
    <book id="000">Book for the dummies</book>
</library>
```

```java
public String convertXmlToJson(String xml) {
    XmlToJson xmlToJson = new XmlToJson.Builder(xml)
        .setContentName("/library/book", "title")
        .build();
    return xmlToJson.toString();
}
```

```json
{  
   "library":{  
      "book":[  
         {  
            "id":"007",
            "title":"James Bond"
         },
         {  
            "id":"000",
            "title":"Book for the dummies"
         }
      ]
   }
}
```

### Custom Attributes names ###

Attributes are converted into key / values in the JSON. The attribute names may conflict with other keys. You can change the name of any attribute, by specifying the path to the attribute and the replacement name, using **Builder.setAttributeName**(String attributePath, String replacementName).

```xml
<?xml version="1.0" encoding="utf-8"?>
<library>
    <book id="007">James Bond</book>
    <book id="000">Book for the dummies</book>
</library>
```

```java
public String convertXmlToJson(String xml) {
    XmlToJson xmlToJson = new XmlToJson.Builder(xml)
        .setAttributeName("/library/book/id", "code")
        .build();
    return xmlToJson.toString();
}
```

```json
{  
   "library":{  
      "book":[  
         {  
            "code":"007",
            "content":"James Bond"
         },
         {  
            "code":"000",
            "content":"Book for the dummies"
         }
      ]
   }
}
```

### Force a Tag to be a list ###

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
         "id":"007",
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
            "id":"007",
            "content":"James Bond"
         }
      ]
   }
}
```

### Force a Tag or Attribute to be an Integer / Long / Double / Boolean ###

By default the XML attributes or content are processed as Strings. If you want to force them to be another type (like Integer), then use on of these methods **Builder.forceIntegerForPath**(String path), **Builder.forceLongForPath**(String path), **Builder.forceDoubleForPath**(String path) or **Builder.forceBooleanForPath**(String path).

```xml
<?xml version="1.0" encoding="utf-8"?>
<library>
    <owner>John Doe</owner>
    <book id="007">James Bond</book>
    <book id="000">Book for the dummies</book>
</library>
```

```java
public String convertXmlToJson(String xml) {
    XmlToJson xmlToJson = new XmlToJson.Builder(xml)
        .Builder.forceIntegerForPath("/library/book/id")
        .build();
    return xmlToJson.toString();
}
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
Here "007" and "000" are converted to 7 and 0.

Note that you can use forcexxxForPath methods AND change the attribute or content name for the same path; the methods in the Builder can be combined. The path used in forcexxxForPath methods is the path in the xml before eventually changing its name.

### Skip a Tag or an Attribute ###

If you are not interrested in getting all the content of the XML, you can skip some Tags or some Attributes. Like for other methods you have to provide the path for the element to skip. You can use **skipTag** and **skipAttribute** as many times as you need.

```xml
<?xml version="1.0" encoding="utf-8"?>
<library>
    <owner>John Doe</owner>
    <book id="007">James Bond</book>
    <book id="000">Book for the dummies</book>
</library>
```

```java
XmlToJson xmlToJson = new XmlToJson.Builder(xml)
    .skipTag("/library/owner")
    .skipAttribute("/library/book/id")
    .build();    
```

```json
{  
   "library":{  
      "book":[  
         {  
            "content":"James Bond"
         },
         {  
            "content":"Book for the dummies"
         }
      ]
   }
}
```


## JSON to XML ##

### Basic usage ###

There are several ways to create a **JsonToXml** object: from a Json **String**, a **JSONObject** or from an **InputStream**.

```java
    JSONObject jsonObject; // some JSONObject previously created
    JsonToXml jsonToXml = new JsonToXml.Builder(jsonObject).build();
```

OR

```java
    String jsonString; // some JSON String previously created
    JsonToXml jsonToXml = new JsonToXml.Builder(jsonString).build();
```

OR

```java
    AssetManager assetManager = context.getAssets();
    InputStream inputStream = assetManager.open("myFile.json");
    JsonToXml jsonToXml = new JsonToXml.Builder(inputStream).build();
    inputStream.close();
```

Then you can convert it to a XML String or a XML Formatted String (with indentation and line breaks)

```java

    // Converts to a simple XML String
    String xmlString = jsonToXml.toString();

    // Converts to a formatted XML String
    int indentationSize = 3;
    String formattedXml = jsonToXml.toFormattedString(indentationSize);
```

Here is a JSON example

```json
{
    "owner": {
        "id": 124,
        "name": "John Doe"
    }
}
```

which is converted into XML

```xml
<?xml version="1.0" encoding="UTF-8"?>
<owner>
    <id>124</id>
    <name>John Doe</name>
</owner>
```


### Force a TAG to be an parent Attribute ###

You may want to use XML Attributes instead of TAG content. You can do this by using the **forceAttribute** method. You need to specify the Path to the TAG.

```java
    JsonToXml jsonToXml = new JsonToXml.Builder(jsonObject)
            .forceAttribute("/owner/id")
            .build();
```

The result becomes

```xml
<?xml version="1.0" encoding="UTF-8"?>
<owner id="124">
    <name>John Doe</name>
</owner>
```

### Force a TAG to be a parent Content ###

When a Tag has only one child, you may want that child to be the Content for its parent. You can use the **forceContent** method to achieve this.

```java
    JsonToXml jsonToXml = new JsonToXml.Builder(jsonObject)
            .forceAttribute("/owner/id")
            .forceContent("/owner/name")
            .build();
```

The result becomes

```xml
<?xml version="1.0" encoding="UTF-8"?>
<owner id="124">John Doe</owner>
```

which is very compact :)

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
    compile 'com.github.smart-fun:XmlToJson:1.4.4'    // add this line
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
