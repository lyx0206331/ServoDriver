package com.adrian.servodriver.application;

import android.app.Application;

import com.adrian.servodriver.BuildConfig;
import com.adrian.servodriver.theme_picker.ThemeChangeObserver;
import com.adrian.servodriver.utils.Constants;
import com.adrian.servodriver.utils.D2xxUtil;
import com.adrian.servodriver.utils.FileUtil;
import com.blankj.utilcode.util.Utils;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.URLConnectionNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ranqing on 2017/6/11.
 */

public class MyApplication extends Application {

    private static MyApplication instance;
    private List<ThemeChangeObserver> mThemeChangeObserverStack; //  主题切换监听栈

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Logger.setDebug(BuildConfig.DEBUG);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
        Logger.setTag("NoHttpSample");// 设置NoHttp打印Log的tag。

        // 一般情况下你只需要这样初始化：
//        NoHttp.initialize(this);Headers.HEAD_VALUE_CONTENT_TYPE_OCTET_STREAM

        // 如果你需要自定义配置：
        NoHttp.initialize(InitializationConfig.newBuilder(this)
                // 设置全局连接超时时间，单位毫秒，默认10s。
                .connectionTimeout(30 * 1000)
                // 设置全局服务器响应超时时间，单位毫秒，默认10s。
                .readTimeout(30 * 1000)
                // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
                .cacheStore(
                        new DBCacheStore(this).setEnable(true) // 如果不使用缓存，设置setEnable(false)禁用。
                )
                // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现。
                .cookieStore(
                        new DBCookieStore(this).setEnable(true) // 如果不维护cookie，设置false禁用。
                )
                // 配置网络层，URLConnectionNetworkExecutor，如果想用OkHttp：OkHttpNetworkExecutor。
                .networkExecutor(new URLConnectionNetworkExecutor())
                .build()
        );

        D2xxUtil.init(this);

        Utils.init(this);

        FileUtil.getInstance(this).copyAssetsToSD("help", Constants.ROOT_PATH + Constants.HELP);
    }

    /**
     * 获得observer堆栈
     */
    private List<ThemeChangeObserver> obtainThemeChangeObserverStack() {
        if (mThemeChangeObserverStack == null) mThemeChangeObserverStack = new ArrayList<>();
        return mThemeChangeObserverStack;
    }

    /**
     * 向堆栈中添加observer
     */
    public void registerObserver(ThemeChangeObserver observer) {
        if (observer == null || obtainThemeChangeObserverStack().contains(observer)) return;
        obtainThemeChangeObserverStack().add(observer);
    }

    /**
     * 从堆栈中移除observer
     */
    public void unregisterObserver(ThemeChangeObserver observer) {
        if (observer == null || !(obtainThemeChangeObserverStack().contains(observer))) return;
        obtainThemeChangeObserverStack().remove(observer);
    }

    /**
     * 向堆栈中所有对象发送更新UI的指令
     */
    public void notifyByThemeChanged() {
        List<ThemeChangeObserver> observers = obtainThemeChangeObserverStack();
        for (ThemeChangeObserver observer : observers) {
            observer.loadingCurrentTheme(); //
            observer.notifyByThemeChanged(); //
        }
    }
}
