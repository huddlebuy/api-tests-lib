package com.perkbox.util;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonHelper {

    private DocumentContext documentContext;

    public JsonHelper(String jsonFile) {
        this(jsonFile, false); // For JSON File
    }

    public JsonHelper(String json, boolean isString) {
        if (isString) { // For JSON String
            this.documentContext = JsonPath.parse(json);
        }
        else { // For JSON File
            try {
                String file = System.getProperty("user.dir") + "/data/input/" + json + ".json";
                InputStream is = Files.newInputStream(Paths.get(file));
                this.documentContext = JsonPath.parse(is);
            }
            catch (IOException e) {
                System.err.println("JsonHelper: " + e.getMessage());
            }
        }
    }

    public String getJson() {
        return documentContext.jsonString();
    }

    public Object getParam(String jsonPath) {
        return documentContext.read(jsonPath);
    }

    public String getParamAsStr(String jsonPath) {
        return String.valueOf(getParam(jsonPath));
    }

    public String addParam(String jsonPath, Object value) {
        return documentContext.add(jsonPath,value).jsonString();
    }

    public String addParams(Map<String, Object> jsonPaths) {
        String body = "";

        for (Map.Entry<String, Object> entry : jsonPaths.entrySet()) {
            body = documentContext.add(entry.getKey(), entry.getValue()).jsonString();
        }

        return body;
    }

    public String addParams(MapBuilder jsonPaths) {
        return addParams(jsonPaths.getMap());
    }

    public String modifyParam(String jsonPath, Object value) {
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

    public static String getJson(String file) {
        return (new JsonHelper(file)).getJson();
    }

    public static Object getParam(String file, String jsonPath) {
        return (new JsonHelper(file)).getParam(jsonPath);
    }

    public static Object getParamAsStr(String file, String jsonPath) {
        return (new JsonHelper(file)).getParamAsStr(jsonPath);
    }

    public static String addParam(String file, String jsonPath, Object value) {
        return (new JsonHelper(file)).addParam(jsonPath, value);
    }

    public static String addParams(String file, Map<String, Object> jsonPaths) {
        return (new JsonHelper(file)).addParams(jsonPaths);
    }

    public static String addParams(String file, MapBuilder jsonPaths) {
        return addParams(file, jsonPaths.getMap());
    }

    public static String modifyParam(String file, String jsonPath, Object value) {
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

    public static String searchListByUniqueColumn(ExtractableResponse<Response> response, String columnValue, String columnName) {
        ArrayList filtered = JsonPath.read(response.asString(), "$.data[?(@." + columnName + " == '" + columnValue + "')]");
        return JsonPath.parse(filtered.get(0)).jsonString();
    }

    public static String searchListById(ExtractableResponse<Response> response, String id) {
        return searchListByUniqueColumn(response, id, "id");
    }

    public static String searchListByUuid(ExtractableResponse<Response> response, String uuid) {
        return searchListByUniqueColumn(response, uuid, "uuid");
    }
}