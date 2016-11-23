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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        TextView textView = (TextView) findViewById(R.id.jsonTextView);

//        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><books><toto>titi</toto><book id=\"007\">James Bond</book><book id=\"000\">Book for the dummies</book></books>";
//        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
//        String formatted = xmlToJson.toFormattedString("\t");
//        textView.setText(formatted);

        try {
            InputStream inputStream = getAssets().open("app_example.xml");
            XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null)
                    .forceList("/container/entry/forcedlist")
                    .build();
            String formatted = xmlToJson.toFormattedString("\t");
            textView.setText(formatted);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}