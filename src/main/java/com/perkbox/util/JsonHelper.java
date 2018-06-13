package com.perkbox.util;

import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class JsonHelper {

    private String file;

    public JsonHelper(String file) {
        this.file = file;
    }

    public String readFile() {
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

    public String removeParam(String jsonPath) {
        return JsonPath.parse(readFile()).delete(jsonPath).jsonString();
    }

    public String removeParams(List<String> jsonPaths) {
        String body = readFile();

        for(String path: jsonPaths){
            JsonPath.parse(body).delete(path);
        }

        return body;
    }

    public String modifyParam(String jsonPath, String value) {
        return JsonPath.parse(readFile()).set(jsonPath,value).jsonString();
    }

    public String modifyParams(Map<String, String> jsonPaths) {
        String body = readFile();

        for (Map.Entry<String, String> entry : jsonPaths.entrySet()) {
            JsonPath.parse(body).set(entry.getKey(), entry.getValue());
        }

        return body;
    }

    public Object getParam(String jsonPath) {
        return JsonPath.parse(readFile()).read(jsonPath);
    }

    public String getParamAsStr(String jsonPath) {
        return getParam(jsonPath).toString();
    }

    //Static methods

    public static String readFile(String file) {
        return (new JsonHelper(file)).readFile();
    }

    public static String removeParam(String file, String jsonPath) {
        return (new JsonHelper(file)).removeParam(jsonPath);
    }

    public static String removeParams(String file, List<String> jsonPaths) {
        return (new JsonHelper(file)).removeParams(jsonPaths);
    }

    public static String modifyParam(String file, String jsonPath, String value) {
        return (new JsonHelper(file)).modifyParam(jsonPath, value);
    }

    public static String modifyParams(String file, Map<String, String> jsonPaths) {
        return (new JsonHelper(file)).modifyParams(jsonPaths);
    }

    public static Object getParam(String file, String jsonPath) {
        return (new JsonHelper(file)).getParam(jsonPath);
    }

    public static Object getParamAsStr(String file, String jsonPath) {
        return (new JsonHelper(file)).getParamAsStr(jsonPath);
    }

    // These methods do not have instance equivalent
    public static Object getParam(ExtractableResponse<Response> response, String jsonPath) {
        return JsonPath.parse(response.asString()).read(jsonPath);
    }

    public static String getParamAsStr(ExtractableResponse<Response> response, String jsonPath) {
        return getParam(response, jsonPath).toString();
    }
}