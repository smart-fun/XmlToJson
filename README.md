# XmlToJson
**XmlToJson** is an Android Studio Library which converts XML to JSON. It takes a String or InputStream as source for the XML and creates a JSONObject that can be directly manipulated or converted into a String.

It is fully configurable so that you can change for example attribute names, see the examples below.

## Examples of use

### Basic usage ###

The code to convert a XML String into JSON String is the following:

```java
public String convertXmlToJson(String xml) {
    XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
    JSONObject jsonObject = xmlToJson.toJson();
    return jsonObject.toString();
}
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<books>
    <book id="007">James Bond</book>
    <book id="000">Book for the dummies</book>
</books>
```

```json
{  
   "books":{  
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
By default, the content of a XML Tag is converted into a key called "content". This name can be changed with a custom one, using **Builder.setContentNameReplacement**(String contentPath, String replacementName). You can change as many content names as you want.

```java
public String convertXmlToJson(String xml) {
    XmlToJson xmlToJson = new XmlToJson.Builder(xml)
        .setContentNameReplacement("/books/book", "title")
        .build();
    JSONObject jsonObject = xmlToJson.toJson();
    return jsonObject.toString();
}
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<books>
    <book id="007">James Bond</book>
    <book id="000">Book for the dummies</book>
</books>
```

```json
{  
   "books":{  
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

Attributes are converted into key / values in the JSON. The attribute names may conflict with other keys. You can change the name of any attribute, by specifying the path to the attribute and the replacement name, using **Builder.setAttributeNameReplacement**(String attributePath, String replacementName).


```java
public String convertXmlToJson(String xml) {
    XmlToJson xmlToJson = new XmlToJson.Builder(xml)
        . Builder.setAttributeNameReplacement("/books/book/id", "code")
        .build();
    JSONObject jsonObject = xmlToJson.toJson();
    return jsonObject.toString();
}
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<books>
    <book id="007">James Bond</book>
    <book id="000">Book for the dummies</book>
</books>
```

```json
{  
   "books":{  
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

### Forcing a Tag to be a list ###

In a XML hierarchy, an entry can have children. For example, \<books> has 2 entries \<book>. In case there is only one book, there is no way to know that Books is a list. But you can force it using **Builder.forceListForPath**(String path).

```xml
<?xml version="1.0" encoding="utf-8"?>
<books>
    <book id="007">James Bond</book>
</books>
```

By default, the \<books> tag is not considered as a list

```json
{  
   "books":{  
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
        . Builder.forceListForPath("/books")
        .build();
    JSONObject jsonObject = xmlToJson.toJson();
    return jsonObject.toString();
}
```

Now \<books> is considered as a list:

```json
{  
   "books":{  
      "book":[  
         {  
            "code":"007",
            "content":"James Bond"
         }
      ]
   }
}
```

## Installation with gradle

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
    compile 'com.github.smart-fun:XmlToJson:1.0.0'    // add this line
}
```

## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
