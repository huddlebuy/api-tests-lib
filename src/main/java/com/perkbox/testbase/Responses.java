package com.perkbox.testbase;

import com.perkbox.util.JsonHelper;
import com.perkbox.util.MapBuilder;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;
import java.util.regex.Pattern;

public class Responses {

    private ExtractableResponse<Response> response;

    public Responses(ExtractableResponse<Response> response) {
        this.response = response;
    }

    public void log(String filter) {
        System.out.printf("Response::%s:: %s%n", filter, this.response.asString());
        System.out.printf("StatusCode::%s:: %s%n", filter, this.response.statusCode());
    }

    public boolean assertTrue(int statusCode) {
        return (response.statusCode() == statusCode);
    }

    public boolean assertTrue(int statusCode, String textResponse) {
        return assertTrue(statusCode, textResponse, false);
    }

    public boolean assertTrue(int statusCode, String textResponse, boolean log) {
        if (response.statusCode() != statusCode || !(response.asString().contains(textResponse))) {
            if (log) {
                System.out.println("Actual: " + response.asString());
                System.out.println("Expected (for contains): " + textResponse);
            }
            return false;
        }
        return true;
    }

    public boolean assertMatch(int statusCode, String regex, boolean log) {
        if (response.statusCode() != statusCode || !(Pattern.compile(regex).matcher(response.asString()).find())) {
            if (log) {
                System.out.println("Actual: " + response.asString());
                System.out.println("Expected (regex): " + regex);
            }
            return false;
        }
        return true;
    }

    public boolean assertMatch(int statusCode, String regex) {
        return assertMatch(statusCode, regex, false);
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