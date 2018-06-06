package com.perkbox.util;

import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class JsonHelper {

    public static String readFile(String file) {
        try {
            String dataFolder = System.getProperty("user.dir") + "/data/input/";
            FileReader fileReader = new FileReader(dataFolder + file + ".json");
            return new JsonParser().parse(fileReader).toString();
        }
        catch (FileNotFoundException e) {
            System.err.println("JsonHelper/readFile: " + e.getMessage());
            return null;
        }
    }

    public static String removeField(String file, String jsonPath) {
        return JsonPath.parse(readFile(file)).delete(jsonPath).jsonString();
    }

    public static String removeFields(String file, List<String> jsonPaths) {
        String body = readFile(file);

        for(String path: jsonPaths){
            JsonPath.parse(body).delete(path);
        }

        return body;
    }

    public static String modifyField(String file, String jsonPath, String value) {
        return JsonPath.parse(readFile(file)).set(jsonPath,value).jsonString();
    }

    public static String modifyFields(String file, Map<String, String> jsonPaths) {
        String body = readFile(file);

        for (Map.Entry<String, String> entry : jsonPaths.entrySet()) {
            JsonPath.parse(body).set(entry.getKey(), entry.getValue());
        }

        return body;
    }

    public static String getField(String response, String jsonPath) {
        return JsonPath.parse(response).read(jsonPath);
    }
}