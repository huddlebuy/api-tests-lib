package com.perkbox.util;

import com.google.gson.JsonParser;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonHelper {

    private String file;

    private DocumentContext documentContext;

    public JsonHelper(String file) {
        this.file = file;
        this.documentContext = JsonPath.parse(readFile());
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

    public String getJson() {
        return documentContext.jsonString();
    }

    public Object getParam(String jsonPath) {
        return documentContext.read(jsonPath);
    }

    public String getParamAsStr(String jsonPath) {
        return getParam(jsonPath).toString();
    }

    public String modifyParam(String jsonPath, String value) {
        return documentContext.set(jsonPath,value).jsonString();
    }

    public String modifyParams(Map<String, Object> jsonPaths) {
        String body = "";

        for (Map.Entry<String, Object> entry : jsonPaths.entrySet()) {
            body = documentContext.set(entry.getKey(), entry.getValue()).jsonString();
        }

        return body;
    }

    public String modifyParams(MapBuilder jsonPaths) {
        return modifyParams(jsonPaths.getMap());
    }

    public String removeParam(String jsonPath) {
        return documentContext.delete(jsonPath).jsonString();
    }

    public String removeParams(List<String> jsonPaths) {
        String body = "";

        for(String path: jsonPaths){
            body = documentContext.delete(path).jsonString();
        }

        return body;
    }

    //Static methods

    public static String readFile(String file) {
        return (new JsonHelper(file)).readFile();
    }

    public static String getJson(String file) {
        return (new JsonHelper(file)).getJson();
    }

    public static Object getParam(String file, String jsonPath) {
        return (new JsonHelper(file)).getParam(jsonPath);
    }

    public static Object getParamAsStr(String file, String jsonPath) {
        return (new JsonHelper(file)).getParamAsStr(jsonPath);
    }

    public static String modifyParam(String file, String jsonPath, String value) {
        return (new JsonHelper(file)).modifyParam(jsonPath, value);
    }

    public static String modifyParams(String file, Map<String, Object> jsonPaths) {
        return (new JsonHelper(file)).modifyParams(jsonPaths);
    }

    public static String modifyParams(String file, MapBuilder jsonPaths) {
        return modifyParams(file, jsonPaths.getMap());
    }

    public static String removeParam(String file, String jsonPath) {
        return (new JsonHelper(file)).removeParam(jsonPath);
    }

    public static String removeParams(String file, List<String> jsonPaths) {
        return (new JsonHelper(file)).removeParams(jsonPaths);
    }


    /** Static ONLY methods: These do not have instance equivalent. **/

    public static Object getParam(ExtractableResponse<Response> response, String jsonPath) {
        return JsonPath.parse(response.asString()).read(jsonPath);
    }

    public static String getParamAsStr(ExtractableResponse<Response> response, String jsonPath) {
        return getParam(response, jsonPath).toString();
    }

    // Search List By a unique column: This returns json of the item found in the list

    public static String searchListByUniqueColumn(ExtractableResponse<Response> response, String id, String column) {
        ArrayList filtered = JsonPath.read(response.asString(), "$.data[?(@." + column + " == '" + id + "')]");
        return JsonPath.parse(filtered.get(0)).jsonString();
    }

    public static String searchListById(ExtractableResponse<Response> response, String id) {
        return searchListByUniqueColumn(response, id, "id");
    }

    public static String searchListByUuid(ExtractableResponse<Response> response, String uuid) {
        return searchListByUniqueColumn(response, uuid, "uuid");
    }
}