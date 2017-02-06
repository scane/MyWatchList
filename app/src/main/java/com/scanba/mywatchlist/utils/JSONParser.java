package com.scanba.mywatchlist.utils;

import org.json.JSONObject;

public class JSONParser {

    public static boolean hasKey(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }
}
