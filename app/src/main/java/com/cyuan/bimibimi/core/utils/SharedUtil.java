package com.cyuan.bimibimi.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.cyuan.bimibimi.core.App;
import com.google.gson.Gson;

/**
 * SharedPreferences工具类，提供简单的封装接口，简化SharedPreferences的用法。
 *
 */
public class SharedUtil {

    private final static String SP = "bimibimi_shared_prefs";

    /**
     * 将实体对象转换为 json 字符串存储到 SharedPreferences 文件当中。
     * @param key
     *          存储的键
     * @param data
     *          存储的对象
     * @param <T>
     *          存储对象泛型
     */
    public static <T> void save(String key, T data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        save(key, json);
    }

    /**
     * 存储boolean类型的键值对到SharedPreferences文件当中。
     * @param key
     *          存储的键
     * @param value
     *          存储的值
     */
    public static void save(String key, boolean value) {
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 存储float类型的键值对到SharedPreferences文件当中。
     * @param key
     *          存储的键
     * @param value
     *          存储的值
     */
    public static void save(String key, float value) {
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE).edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * 存储int类型的键值对到SharedPreferences文件当中。
     * @param key
     *          存储的键
     * @param value
     *          存储的值
     */
    public static void save(String key, int value) {
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 存储long类型的键值对到SharedPreferences文件当中。
     * @param key
     *          存储的键
     * @param value
     *          存储的值
     */
    public static void save(String key, long value) {
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 存储String类型的键值对到SharedPreferences文件当中。
     * @param key
     *          存储的键
     * @param value
     *          存储的值
     */
    public static void save(String key, String value) {
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的 json 字符串，并将其转换为实体对象
     * @param key
     *          读取的键
     * @param <T>
     *          实体对象的泛型
     * @return 泛型对应的实体对象，如果读取不到，则返回空值
     */
    public static @Nullable <T> T read(String key, Class<T> clazz) {
        Gson gson = new Gson();
        String json = read(key, "");
        try {
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的boolean类型的值。
     * @param key
     *          读取的键
     * @param defValue
     *          如果读取不到值，返回的默认值
     * @return boolean类型的值，如果读取不到，则返回默认值
     */
    public static boolean read(String key, boolean defValue) {
        SharedPreferences prefs = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defValue);
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的float类型的值。
     * @param key
     *          读取的键
     * @param defValue
     *          如果读取不到值，返回的默认值
     * @return float类型的值，如果读取不到，则返回默认值
     */
    public static float read(String key, float defValue) {
        SharedPreferences prefs = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE);
        return prefs.getFloat(key, defValue);
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的int类型的值。
     * @param key
     *          读取的键
     * @param defValue
     *          如果读取不到值，返回的默认值
     * @return int类型的值，如果读取不到，则返回默认值
     */
    public static int read(String key, int defValue) {
        SharedPreferences prefs = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE);
        return prefs.getInt(key, defValue);
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的long类型的值。
     * @param key
     *          读取的键
     * @param defValue
     *          如果读取不到值，返回的默认值
     * @return long类型的值，如果读取不到，则返回默认值
     */
    public static long read(String key, long defValue) {
        SharedPreferences prefs = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE);
        return prefs.getLong(key, defValue);
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的String类型的值。
     * @param key
     *          读取的键
     * @param defValue
     *          如果读取不到值，返回的默认值
     * @return String类型的值，如果读取不到，则返回默认值
     */
    public static String read(String key, String defValue) {
        SharedPreferences prefs = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE);
        return prefs.getString(key, defValue);
    }

    /**
     * 判断SharedPreferences文件当中是否包含指定的键值。
     * @param key
     *          判断键是否存在
     * @return 键已存在返回true，否则返回false。
     */
    public static boolean contains(String key) {
        SharedPreferences prefs = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE);
        return prefs.contains(key);
    }

    /**
     * 清理SharedPreferences文件当中传入键所对应的值。
     * @param key
     *          想要清除的键
     */
    public static void clear(String key) {
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE).edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 将SharedPreferences文件中存储的所有值清除。
     */
    public static void clearAll() {
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(SP, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

}