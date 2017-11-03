package com.adrian.servodriver.utils;

import android.os.Environment;

/**
 * Created by ranqing on 2017/6/24.
 */

public class Constants {

    public static final String HOST_IP = "221.232.144.14";
    public static final int HOST_PORT = 3050;
    public static final String TEST_DOWNLOAD = "http://oss.ucdl.pp.uc.cn/fs01/union_pack/Wandoujia_110644_web_direct_binded.apk?x-oss-process=udf/pp-udf,Jjc3LiMnJ3FxdnJ1fnE=";

    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + "/Maxsine/";
    public static final String HELP = "help/";
    public static final String KEY_THEME_CACHE = "theme_cache";
    public static final String SHERED_PREF_NAME = "ThemeCache";
    public static final String DOWNLOAD_FIRMWARE = "firmware/";

    public static final int BAUD_RATE = 2000000;   //波特率：2000000bps
    public static final int PARITY_DIGIT = 0;   //奇偶校验位
    public static final int DATA_BIT = 8;   //数据位
    public static final int STOP_BIT = 1;   //停止位
}
