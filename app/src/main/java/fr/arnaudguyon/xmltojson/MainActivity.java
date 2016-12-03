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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        try {
            InputStream inputStream = getAssets().open("app_example.xml");
            XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null)
                    .forceList("/container/entry/forcedlist")
                    .build();
            String formatted = xmlToJson.toFormattedString("\t");
            TextView jsonTextView = (TextView) findViewById(R.id.jsonTextView);
            jsonTextView.setText(formatted);
            inputStream.close();

            try {
                JSONObject jsonObject = new JSONObject(formatted);
                JsonToXml jsonToXml = new JsonToXml.Builder(jsonObject)
                        .forceAttribute("/container/entry/bool")
                        .forceAttribute("/container/entry/name")
                        .forceAttribute("/container/entry/number")
                        .forceAttribute("/container/entry/forcedlist/id")
                        .forceContent("/container/entry/content")
                        .build();
//                String result = jsonToXml.toString();
                String result = jsonToXml.toFormattedString(4);
                TextView xmlTextView = (TextView) findViewById(R.id.xmlTextView);
                xmlTextView.setText(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}