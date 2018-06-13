package com.perkbox.util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {

    private Map<String, Object> map;

    public MapBuilder() {
        map = new HashMap<String, Object>();
    }

    public MapBuilder add(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public Map<String, Object> getMap() {
        return this.map;
    }
}
