package com.gdou.longPoll.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ningle
 * @version : Config.java, v 0.1 2023/11/01 15:25 ningle
 **/
public class Config {
    public static final Map<String, String> CONFIG = new HashMap<>() {
        {
            put("host", "127.0.0.1");
            put("port", "8080");
        }
    };

    public static void updateConfig(String key, String value) {
        if (!CONFIG.containsKey(key)) return;
        CONFIG.put(key, value);
    }
}
