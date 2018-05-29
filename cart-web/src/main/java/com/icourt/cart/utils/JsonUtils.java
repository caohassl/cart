package com.icourt.cart.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Json 工具类加入序列化注解
 *
 * @author lan
 */
public class JsonUtils {

    public static final Gson gson = new GsonBuilder()
            .serializeNulls()                       // 当需要序列化的值为空时，采用null映射，否则会把该字段省略
            .setDateFormat("yyyy-MM-dd HH:mm:ss")   // 日期格式转换
            .create();

    /**
     * 对象转成json字符串
     *
     * @param obj 参数
     * @return json string
     */
    public static String objToJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * json字符串转成对象
     *
     * @param json json串
     * @param cls  真实类
     * @return object
     */
    public static <T> T jsonToObj(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

    /**
     * 将Json串转换为Map<String,String>
     *
     * @param json json串
     * @return Map<String,String>
     */
    public static Map<String, String> jsonToMap(String json) {
        return gson.fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    /**
     * 将Json串转换为Map<String,String>
     *
     * @param json json串
     * @return Map<String,String>
     */
    public static Map<String, Object> jsonToObjectMap(String json) {
        return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    /**
     * 将json串转换成List<Map<String, String>>
     *
     * @param json json串
     * @return List<Map<String, String>>
     */
    public static List<Map<String, String>> jsonToList(String json) {
        return gson.fromJson(json, new TypeToken<List<Map<String, String>>>() {
        }.getType());
    }

    public static <T> List<T> jsonToObjectList(String json, Class<T> cls) {
        JsonParser parser = new JsonParser();
        JsonArray Jarray = parser.parse(json).getAsJsonArray();

        ArrayList<T> objectList = new ArrayList<T>();

        for(JsonElement obj : Jarray ){
            T cse = gson.fromJson( obj , cls);
            objectList.add(cse);
        }
        return objectList;
    }
}
