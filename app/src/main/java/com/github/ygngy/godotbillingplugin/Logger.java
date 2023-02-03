/*
 * Copyright (c) 2023 Mohamadreza Amani
 * Github: https://github.com/ygngy/
 * Linkedin: https://www.linkedin.com/in/ygngy/
 * Email: amany1388@gmail.com
 */

package com.github.ygngy.godotbillingplugin;

import android.util.Log;

public abstract class Logger {
    private static final String GODOT_TAG = "godot";

    static boolean isDebugging = false;


    public static void logInfo(String msg){
        if (isDebugging)
            Log.i(GODOT_TAG, msg);
    }

    public static void logDebug(String msg){
        if (isDebugging)
            Log.d(GODOT_TAG, msg);
    }

    public static void logWarn(String warn){
        if (isDebugging)
            Log.w(GODOT_TAG, warn);
    }

    public static void logError(String error){
        if (isDebugging)
            Log.e(GODOT_TAG, error);
    }
}
