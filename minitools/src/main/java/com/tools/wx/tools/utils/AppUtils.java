package com.tools.wx.tools.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

/**
 * className : AppUtils
 * description :
 * author : 大圣
 * created : 2019/2/16
 * qq : 601660957
 */

public class AppUtils {
    public static String getAndroidID(Context context) {
        @SuppressLint("HardwareIds") String id = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        return id == null ? "" : id;
    }
}
