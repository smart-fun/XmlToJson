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
package fr.arnaudguyon.xmltojsonlib;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by arnaud on 03/12/2016.
 */

public class FileReader {

    public static String readFileFromAsset(@NonNull Context context, @NonNull String fileName) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            String result = readFileFromInputStream(inputStream);
            inputStream.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();    // TODO
        }
        return null;
    }

    public static String readFileFromInputStream(@NonNull InputStream inputStream) {

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder result = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException exception) {
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e2) {
            }
            try {
                inputStreamReader.close();
            } catch (IOException e2) {
            }
        }
        return null;
    }
}
