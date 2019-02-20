package com.tools.wx.tools.utils;

import android.content.Context;
import android.content.SharedPreferences;




public class EasyKVStore {

    public EasyKVStore() {
    }

    public static final SharedPreferences getPreferences() {
        return EasyKVStore.SingletonHolder.sprefs;
    }

    public static void setIntPrefs(String key, int value) {
        SharedPreferences sprefs = getPreferences();
        SharedPreferences.Editor editor = sprefs.edit();
        editor.putInt(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static int getIntPrefs(String key) {
        return getIntPrefs(key, 0);
    }

    public static int getIntPrefs(String key, int defaultValue) {
        SharedPreferences sprefs = getPreferences();
        return sprefs.getInt(key, defaultValue);
    }

    public static void setLongPrefs(String key, long value) {
        SharedPreferences sprefs = getPreferences();
        SharedPreferences.Editor editor = sprefs.edit();
        editor.putLong(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static long getLongPrefs(String key) {
        return getLongPrefs(key, 0L);
    }

    public static long getLongPrefs(String key, long defaultValue) {
        SharedPreferences sprefs = getPreferences();
        return sprefs.getLong(key, defaultValue);
    }

    public static void setBooleanPrefs(String key, boolean value) {
        SharedPreferences sprefs = getPreferences();
        SharedPreferences.Editor editor = sprefs.edit();
        editor.putBoolean(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static boolean getBooleanPrefs(String key) {
        return getBooleanPrefs(key, false);
    }

    public static boolean getBooleanPrefs(String key, boolean defaultValue) {
        SharedPreferences sprefs = getPreferences();
        return sprefs.getBoolean(key, defaultValue);
    }

    public static void setStringPrefs(String key, String value) {
        SharedPreferences sprefs = getPreferences();
        SharedPreferences.Editor editor = sprefs.edit();
        editor.putString(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static String getStringPrefs(String key) {
        return getStringPrefs(key, "");
    }

    public static String getStringPrefs(String key, String defaultValue) {
        SharedPreferences sprefs = getPreferences();
        return sprefs.getString(key, defaultValue);
    }

    public static int getIntPrefs(Context context, String key, int defaultValue) {
        SharedPreferences sprefs = context.getSharedPreferences("sp_cache", 0);
        return sprefs.getInt(key, defaultValue);
    }

    public static void removeKey(String key){
        SharedPreferences sprefs = getPreferences();
        SharedPreferences.Editor editor = sprefs.edit();
        editor.remove(key);
        editor.commit();
    }
    private static class SingletonHolder {
        private static final SharedPreferences sprefs;

        private SingletonHolder() {
        }

        static {
            sprefs = Utils.getContext().getSharedPreferences("sp_cache", 0);
        }
    }
}
