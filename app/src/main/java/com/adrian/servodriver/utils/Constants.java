package com.adrian.servodriver.utils;

import android.os.Environment;

/**
 * Created by ranqing on 2017/6/24.
 */

public class Constants {
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + "/Maxsine/";
    public static final String HELP = "help/";
    public static final String KEY_THEME_CACHE = "theme_cache";
    public static final String SHERED_PREF_NAME = "ThemeCache";

    public static final int BAUD_RATE = 2000000;   //波特率：2000000bps
    public static final int PARITY_DIGIT = 0;   //奇偶校验位
    public static final int DATA_BIT = 8;   //数据位
    public static final int STOP_BIT = 1;   //停止位
}
