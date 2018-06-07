package com.perkbox.util;

public class Env {

    public static String get(String key) {
        String env = System.getenv(key);
        return (env != null ? env : Config.get(key));
    }
}