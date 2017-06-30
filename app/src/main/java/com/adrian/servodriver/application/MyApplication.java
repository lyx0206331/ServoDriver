package com.adrian.servodriver.application;

import android.app.Application;

import com.adrian.servodriver.utils.Constants;
import com.adrian.servodriver.utils.FileUtil;
import com.blankj.utilcode.util.Utils;

/**
 * Created by ranqing on 2017/6/11.
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Utils.init(this);

        FileUtil.getInstance(this).copyAssetsToSD("help", Constants.ROOT_PATH + Constants.HELP);
    }
}
