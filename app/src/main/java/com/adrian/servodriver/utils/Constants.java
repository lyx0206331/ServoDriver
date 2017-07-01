package com.adrian.servodriver.utils;

import android.os.Environment;

/**
 * Created by ranqing on 2017/6/24.
 */

public class Constants {
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + "/Maxsine/";
    public static final String HELP = "help/";

    public static final long BAUD_RATE = 2000000;   //波特率：2000000bps
    public static final String PARITY_DIGIT = "NONE";   //奇偶校验位
    public static final int DATA_BIT = 8;   //数据位
    public static final int STOP_BIT = 1;   //停止位
}
