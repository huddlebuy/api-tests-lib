package com.perkbox.testbase;

import com.perkbox.util.JsonHelper;
import com.perkbox.util.MapBuilder;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.testng.Assert;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Responses {

    private ExtractableResponse<Response> response;

    public Responses(ExtractableResponse<Response> response) {
        this.response = response;
    }

    public void assertTrue(int statusCode) {
        Assert.assertTrue((response.statusCode() == statusCode));
    }

    public void assertTrue(int statusCode, String textResponse) {
        assertTrue(statusCode, textResponse, false);
    }

    public void assertTrue(int statusCode, String textResponse, boolean log) {
        boolean result = true;
        if (response.statusCode() != statusCode || !(response.asString().contains(textResponse))) {
            if (log) {
                System.out.println("Actual: " + response.asString());
                System.out.println("Expected (for contains): " + textResponse);
            }
            result = false;
        }
        Assert.assertTrue(result);
    }

    public void assertTrue(int statusCode, Map<String, Object> fields) {
        assertTrue(statusCode, fields, false);
    }

    public void assertTrue(int statusCode, Map<String, Object> fields, boolean log) {
        boolean result = true;
        if (response.statusCode() != statusCode) {
            result = false;
        }
        else if (fields != null) {
            for (Map.Entry<String, Object> entry: fields.entrySet()) {
                if (!JsonHelper.getParam(response, entry.getKey()).equals(entry.getValue())) {
                    if (log) {
                        System.out.println("Actual: " + JsonHelper.getParam(response, entry.getKey()));
                        System.out.println("Expected: " + entry.getValue());
                    }
                    result = false;
                }
            }
        }
        Assert.assertTrue(result);
    }

    public void assertTrue(int statusCode, MapBuilder mapBuilder) {
        assertTrue(statusCode, mapBuilder, false);
    }

    public void assertTrue(int statusCode, MapBuilder mapBuilder, boolean log) {
        assertTrue(statusCode, mapBuilder.getMap(), log);
    }

    public void assertMatch(int statusCode, String expect, String ... regexParams) {
        boolean result = true;
        int placeholders = expect.split("%REGEX", -1).length - 1;
        if (regexParams.length != placeholders) {
            System.out.println("Error: %REGEX count does equal to regexParams count.");
            result = false;
        } else {
            String[] arr = expect.split("%REGEX");
            StringBuilder regex = new StringBuilder();
            int count = 0;

            for (String str : arr) {
                regex.append(Pattern.quote(str));

                if (count < regexParams.length) {
                    regex.append(regexParams[count]);
                }

                count++;
            }

            result = (response.statusCode() == statusCode && Pattern.compile(regex.toString()).matcher(response.asString()).find());
        }

        Assert.assertTrue(result);
    }

    public void assertSchema(String file) {
        try {
            String dataFolder = System.getProperty("user.dir") + "/data/input/";
            FileReader fileReader = new FileReader(dataFolder + file + ".json");
            response.response().then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(fileReader));
        }
        catch (FileNotFoundException e) {
            System.err.println("JsonHelper/readFile: " + e.getMessage());
        }
    }

    public String getHeader(String headerName) {
        return response.header(headerName);
    }

    public String getParam(String path) {
        return response.body().jsonPath().getString(path);
    }

    public List<Object> getList(String path) {
        return response.body().jsonPath().getList(path);
    }

    public String getUuid() {
        String link = response.body().jsonPath().get("links.self");
        return getUuid(link);
    }

    public String getUuid(String link) {
        return link.substring(link.lastIndexOf('/') + 1);
    }

    public ExtractableResponse<Response> getResponse() {
        return this.response;
    }

    public void log(String filter) {
        System.out.printf("Response::%s:: %s%n", filter, this.response.asString());
        System.out.printf("StatusCode::%s:: %s%n", filter, this.response.statusCode());
    }
}