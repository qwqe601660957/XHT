package com.tools.wx.tools.utils;


import com.google.gson.Gson;

/**
 * Created by SensYang on 2016/3/30 0030.
 */
public class JsonUtils {
    private static Gson gson = new Gson();

    public static <T> T parserJson2Bean(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }

    /**
     * 将bean转换为json
     */
    public static String parserBean2Json(Object object) {
        return gson.toJson(object);
    }

    /**
     * 将bean转换为json并过滤null字段
     * 目前仅支持一级嵌套的bean
     */
    public static String parserBean2JsonIgnoreNull(Object object) {
        String jsonString = parserBean2Json(object);
        if (jsonString == null) return null;
        jsonString = jsonString.replaceAll("\"[^\"]+\":null,|,\"[^\"]+\":null", "");
        return jsonString;
    }
}
