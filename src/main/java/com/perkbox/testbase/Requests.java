package com.perkbox.testbase;

import com.perkbox.util.Env;
import com.perkbox.util.MapBuilder;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

public class Requests {

    private ExtractableResponse<Response> response;
    private RequestSpecification request;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;
    private String url;

    public Requests() {
        RestAssured.useRelaxedHTTPSValidation();

        request = given()
                .config(RestAssured.config().encoderConfig(encoderConfig().
                        appendDefaultContentCharsetToContentTypeIfUndefined(false)));

        headers = new HashMap<>();
        queryParams = new HashMap<>();
        body = null;
    }

    public Requests(String resourcePath) {
        this(resourcePath, Env.get("BASE_URL"));
    }

    public Requests(String resourcePath, String baseUri) {
        this();
        url = baseUri + resourcePath;
    }

    public Requests disableUrlEncoding() {
        RestAssured.urlEncodingEnabled = false;
        return this;
    }

    public Requests withUrl(String url) {
        this.url = url;
        return this;
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

    public Requests withQueryParams(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.queryParams.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Requests withQueryParams(MapBuilder map) {
        return withQueryParams(map.getMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (String)e.getValue())));
    }

    private RequestSpecification build(boolean logRequest, boolean logResponse) {
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

        String logMessage = "";

        if (logRequest) {
            logMessage += "Request";
            request.filter(new RequestLoggingFilter());
        }

        if (logResponse) {
            logMessage += logMessage.length() > 0 ? " and Response" : "Response";
            request.filter(new ResponseLoggingFilter());
        }

        if (logRequest || logResponse) {
            System.out.println("\n::Logging for " + logMessage + ".\n");
        }

        return request;
    }

    public Responses get(boolean ... logs) {
        boolean logRequest = logs.length > 0 && logs[0];
        boolean logResponse = logs.length > 1 && logs[1];
        response = build(logRequest, logResponse).when().get(url).then().extract();
        return new Responses(response);
    }

    public Responses post(boolean ... logs) {
        boolean logRequest = logs.length > 0 && logs[0];
        boolean logResponse = logs.length > 1 && logs[1];
        if (headers.get("Content-Type") == null) request.contentType("application/json");
        response = build(logRequest, logResponse).when().post(url).then().extract();
        return new Responses(response);
    }

    public Responses put(boolean ... logs) {
        boolean logRequest = logs.length > 0 && logs[0];
        boolean logResponse = logs.length > 1 && logs[1];
        if (headers.get("Content-Type") == null) request.contentType("application/json");
        response = build(logRequest, logResponse).when().put(url).then().extract();
        return new Responses(response);
    }

    public Responses delete(boolean ... logs) {
        boolean logRequest = logs.length > 0 && logs[0];
        boolean logResponse = logs.length > 1 && logs[1];
        response = build(logRequest, logResponse).when().delete(url).then().extract();
        return new Responses(response);
    }
}