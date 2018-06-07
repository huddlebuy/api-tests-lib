package com.perkbox.testbase;

import com.perkbox.util.Config;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

public class Requests {

    private ExtractableResponse<Response> response;
    private RequestSpecification request;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;

    public Requests(String resourcePath) {
        this(resourcePath, Config.get("BASE_URI"));
    }

    public Requests(String resourcePath, String baseUri) {
        request = given()
                .config(RestAssured.config().encoderConfig(encoderConfig().
                        appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .contentType("application/json");

        request.baseUri(Config.get("BASE_URI"));
        request.basePath(resourcePath);

        headers = new HashMap<>();
        queryParams = new HashMap<>();
        body = null;
    }

    public Requests withHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public Requests withAuthorization(String authorization) {
        this.headers.put("Authorization", authorization);
        return this;
    }

    public Requests withContentType(String contentType) {
        this.headers.put("Content-Type", contentType);
        return this;
    }

    public Requests withIfMatch(String ifMatch) {
        this.headers.put("If-Match", ifMatch);
        return this;
    }

    public Requests withBody(String body) {
        this.body = body;
        return this;
    }

    public Requests withQueryParam(String key, String value) {
        this.queryParams.put(key, value);
        return this;
    }

    private RequestSpecification build() {
        //add headers
        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }

        //add query parameters
        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                request.param(entry.getKey(), entry.getValue());
            }
        }

        if (body != null && !body.isEmpty()) {
            request.body(body);
        }

        return request;
    }

    public ExtractableResponse<Response> extractResponse() {
        return this.response;
    }

    public Requests get() {
        this.response = this.build().when().get().then().extract();
        return this;
    }

    public Requests post() {
        this.response = this.build().when().post().then().extract();
        return this;
    }

    public Requests put() {
        this.response = this.build().when().put().then().extract();
        return this;
    }

    public Requests delete() {
        this.response = this.build().when().delete().then().extract();
        return this;
    }
}