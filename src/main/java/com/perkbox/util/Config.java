package com.perkbox.util;

import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static Properties load() {
        String file = "config.properties";
        Properties properties = new Properties();

        try {
            InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(file);
            properties.load(inputStream);
        }
        catch (Exception e) {
            System.out.println("Error opening config: " + e.getMessage());
            System.exit(0);
        }

        return properties;
    }

    public static String get(String key) {
        return load().getProperty(key);
    }
}