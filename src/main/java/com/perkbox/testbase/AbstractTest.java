package com.perkbox.testbase;

import com.perkbox.util.JsonHelper;
import com.perkbox.util.SuiteListener;
import com.perkbox.util.token.Token;

public abstract class AbstractTest extends SuiteListener {

    public static final int LOG_NONE = 0;
    public static final int LOG_GET = 1;
    public static final int LOG_POST = 2;
    public static final int LOG_PUT = 3;
    public static final int LOG_PATCH = 4;
    public static final int LOG_DELETE = 5;
    public static final int LOG_ALL = 6;

    private String tokenEndpoint; // This is used for generating the token.
    private String jsonData;      // This is used for request body.
    private String path;          // This is the url constructed by prepending "/" to the endpoint.
    private int logLevel;         // This is used for specifying request/response log level.
    private boolean logAll;       // Is set to true if logLevel is LOG_ALL.

    public AbstractTest(String endpoint) {
        this(endpoint, null);
    }

    public AbstractTest(String endpoint, String jsonData) {
        this(endpoint, jsonData, LOG_NONE);
    }

    /**
     * Base Constructor
     *
     * @param endpoint This is used for url path (and token generation, if applicable). E.g. "questions".
     * @param jsonData This is used for request body. E.g. "questions/question".
     * @param logLevel This is used for specifying request/response log level. E.g. LOG_GET.
     */
    public AbstractTest(String endpoint, String jsonData, int logLevel) {
        this.jsonData = jsonData;
        this.path = "/" + endpoint;
        setTokenEndpoint(endpoint);
        setLogLevel(logLevel);
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
        this.logAll = logLevel == LOG_ALL;
    }

    // To be called in the constructor when the endpoint for generating tokens is different from the endpoint for url.
    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public Responses get(String uuid) {
        return get(uuid, Token.read(tokenEndpoint));
    }

    public Responses get(String uuid, String token) {
        Requests request = new Requests(path + "/" + uuid);
        if (token != null) {
            request.withAuthorization(token);
        }
        return logLevel == LOG_GET || logAll ? request.get(true, true) : request.get();
    }

    public String getEtag(String uuid) {
        return get(uuid).getHeader("Etag");
    }

    public String getEtag(String uuid, String token) {
        return get(uuid, token).getHeader("Etag");
    }

    public Requests list() {
        return list(Token.read(tokenEndpoint));
    }

    public Requests list(String token) {
        Requests request = new Requests(path);
        if (token != null) {
            request.withAuthorization(token);
        }
        return request;
    }

    public Responses post() {
        return post(JsonHelper.getJson(jsonData));
    }

    public Responses post(String body) {
        return post(body, Token.create(tokenEndpoint));
    }

    public Responses post(String body, String token) {
        Requests request = new Requests(path);
        if (token != null) {
            request.withAuthorization(token);
        }
        request.withBody(body);
        return logLevel == LOG_POST || logAll ? request.post(true, true) : request.post();
    }

    public Responses put(String uuid, String body) {
        return put(uuid, body, getEtag(uuid));
    }

    public Responses put(String uuid, String body, String etag) {
        return put(uuid, body, etag, Token.update(tokenEndpoint));
    }

    public Responses put(String uuid, String body, String etag, String token) {
        Requests request = new Requests(path + "/" + uuid);
        if (token != null) {
            request.withAuthorization(token);
        }
        request.withBody(body).withIfMatch(etag);
        return logLevel == LOG_PUT || logAll ? request.put(true, true) : request.put();
    }

    public Responses patch(String uuid, String body) {
        return patch(uuid, body, getEtag(uuid));
    }

    public Responses patch(String uuid, String body, String etag) {
        return patch(uuid, body, etag, Token.update(tokenEndpoint));
    }

    public Responses patch(String uuid, String body, String etag, String token) {
        Requests request = new Requests(path + "/" + uuid);
        if (token != null) {
            request.withAuthorization(token);
        }
        request.withBody(body).withIfMatch(etag);
        return logLevel == LOG_PATCH || logAll ? request.patch(true, true) : request.patch();
    }

    public Responses delete(String uuid) {
        return delete(uuid, Token.delete(tokenEndpoint));
    }

    public Responses delete(String uuid, String token) {
        Requests request = new Requests(path + "/" + uuid);
        if (token != null) {
            request.withAuthorization(token);
        }
        return logLevel == LOG_DELETE || logAll ? request.delete(true, true) : request.delete();
    }
}