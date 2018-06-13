package com.perkbox.testbase;

import com.perkbox.util.JsonHelper;
import com.perkbox.util.MapBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

public class Responses {

    private ExtractableResponse<Response> response;

    public Responses(Requests req) {
        this.response = req.extractResponse();
    }

    public boolean assertTrue(int statusCode) {
        return (response.statusCode() == statusCode);
    }

    public boolean assertTrue(int statusCode, String textResponse) {
        if (response.statusCode() != statusCode) return false;
        return response.asString().contains(textResponse);
    }

    public boolean assertTrue(int statusCode, Map<String, Object> fields) {
        return assertTrue(statusCode, fields, false);
    }

    public boolean assertTrue(int statusCode, Map<String, Object> fields, boolean log) {
        if (response.statusCode() != statusCode) return false;
        if (fields != null) {
            for (Map.Entry<String, Object> entry: fields.entrySet()) {
                if (!JsonHelper.getParam(response, entry.getKey()).equals(entry.getValue())) {
                    if (log) {
                        System.out.println("Actual: " + JsonHelper.getParam(response, entry.getKey()));
                        System.out.println("Expected: " + entry.getValue());
                    }

                    return false;
                }
            }
        }
        return true;
    }

    public boolean assertTrue(int statusCode, MapBuilder mapBuilder, boolean log) {
        return assertTrue(statusCode, mapBuilder.getMap(), log);
    }

    public boolean assertTrue(int statusCode, MapBuilder mapBuilder) {
        return assertTrue(statusCode, mapBuilder, false);
    }

    public String getHeader(String headerName) {
        return response.header(headerName);
    }

    public String getParam(String jsonPath) {
        return response.body().jsonPath().getString(jsonPath);
    }

    public ExtractableResponse<Response> getResponse() {
        return this.response;
    }
}