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

    public boolean assertTrue(int statusCode, Map<String, String> fields) {
        if (response.statusCode() != statusCode) return false;
        if (fields != null) {
            for (Map.Entry<String, String> entry: fields.entrySet()) {
                if (!JsonHelper.getParam(response, entry.getKey()).equals(entry.getValue()))
                    return false;
            }
        }
        return true;
    }

    public boolean assertTrue(int statusCode, MapBuilder mapBuilder) {
        return assertTrue(statusCode, mapBuilder.getMap());
    }

    public String getHeader(String headerName) {
        return response.header(headerName);
    }

    public String getParam(String jsonPath) {
        return response.body().jsonPath().getString(jsonPath);
    }
}