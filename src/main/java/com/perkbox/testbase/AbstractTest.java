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
    public static final int LOG_ALL = 5;

    private String endpoint; //e.g. "questions" used in path and token
    private String jsonData; //e.g. "questions/question" used request body
    private String path;
    private int logLevel;
    private boolean logAll;

    public AbstractTest(String endpoint, String jsonData) {
        this(endpoint, jsonData, LOG_NONE);
    }

    public AbstractTest(String endpoint, String jsonData, int logLevel) {
        this.endpoint = endpoint;
        this.jsonData = jsonData;
        this.logLevel = logLevel;
        this.logAll = logLevel == LOG_ALL;
        this.path = "/" + endpoint;
    }

    public Responses get(String uuid) {
        return get(uuid, Token.read(endpoint));
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

    public Requests list() {
        return list(Token.read(endpoint));
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
        return post(body, Token.create(endpoint));
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
        return put(uuid, body, etag, Token.update(endpoint));
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
        return patch(uuid, body, etag, Token.update(endpoint));
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
        return delete(uuid, Token.delete(endpoint));
    }

    public Responses delete(String uuid, String token) {
        Requests request = new Requests(path + "/" + uuid);
        if (token != null) {
            request.withAuthorization(token);
        }
        return logLevel == LOG_GET || logAll ? request.get(true, true) : request.delete();
    }
}