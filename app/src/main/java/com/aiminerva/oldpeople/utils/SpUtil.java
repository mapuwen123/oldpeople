package com.aiminerva.oldpeople.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {
    private static SharedPreferences sharedPreferences;

    //存储字符串
    public static void putString(Context context, String key, String value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key, value).commit();
    }

    //读取
    public static String getString(Context context, String key, String defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, defValue);
    }

    //存储boolean
    public static void putBoolean(Context context, String key, boolean value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    //读取
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(key, defValue);
    }

    //存储int
    public static void putInt(Context context, String key, int value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putInt(key, value).commit();
    }

    //读取
    public static int getInt(Context context, String key, int defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sharedPreferences.getInt(key, defValue);
    }
}	
