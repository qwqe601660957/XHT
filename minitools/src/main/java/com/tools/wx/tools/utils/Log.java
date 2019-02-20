/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.tools.wx.tools.utils;


/**
 * ECDemo 日志打印工具类
 * Created by Jorstin on 2015/3/17.
 */
public class Log {
    private static final String TAG = "envelope--->";
    private static final String MSG = "log msg is null.";

    public static void v(String tag, String msg) {
        print(android.util.Log.VERBOSE, tag, msg);
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void d(String tag, String msg) {
        print(android.util.Log.DEBUG, tag, msg);
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void i(String tag, String msg) {
        print(android.util.Log.INFO, tag, msg);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void w(String tag, String msg) {
        print(android.util.Log.WARN, tag, msg);
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void e(String tag, String msg) {
        print(android.util.Log.ERROR, tag, msg);
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    private static void print(int mode, final String tag, String msg) {
        if (!Config.ISDEBUG) {
            return;
        }
        if (msg == null) {
            if (onLogListener != null) onLogListener.onLog(android.util.Log.ERROR, tag, MSG);
            android.util.Log.e(tag, MSG);
            return;
        }
        switch (mode) {
            case android.util.Log.VERBOSE:
                android.util.Log.v(tag, msg);
                break;
            case android.util.Log.DEBUG:
                android.util.Log.d(tag, msg);
                break;
            case android.util.Log.INFO:
                android.util.Log.i(tag, msg);
                break;
            case android.util.Log.WARN:
                android.util.Log.w(tag, msg);
                break;
            case android.util.Log.ERROR:
                android.util.Log.e(tag, msg);
                break;
            default:
                android.util.Log.d(tag, msg);
                break;
        }
        if (onLogListener != null) onLogListener.onLog(mode, tag, msg);
    }

    public static void printFunction() {
        printFunction(android.util.Log.INFO);
    }

    public static void printFunction(int mode) {
        Thread currentThread = Thread.currentThread();
        StackTraceElement elements[] = currentThread.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : elements) {
            sb.append("\n");
            sb.append(element);
        }
        print(mode, TAG, sb.toString());
    }

    public static void printLongString(String longhString) {
        printLongString(longhString, android.util.Log.INFO);
    }

    public static void printLongString(String longhString, int mode) {
        if (longhString == null) return;
        for (int i = 0; i < longhString.length(); i += 3000) {
            print(mode, TAG, longhString.substring(i, i + 3000 > longhString.length() ? longhString.length() : i + 3000));
        }
    }

    private static OnLogListener onLogListener;

    public interface OnLogListener {
        void onLog(int level, String tag, String message);
    }

    public static void setOnLogListener(OnLogListener onLogListener) {
        Log.onLogListener = onLogListener;
    }
}