package com.perkbox.util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {

    private Map<String, String> map;

    public MapBuilder() {
        map = new HashMap<String, String>();
    }

    public MapBuilder add(String key, String value) {
        map.put(key, value);
        return this;
    }

    public Map<String, String> getMap() {
        return this.map;
    }
}
