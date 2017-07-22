package com.adrian.servodriver.application;

import android.app.Application;

import com.adrian.servodriver.theme_picker.ThemeChangeObserver;
import com.adrian.servodriver.utils.Constants;
import com.adrian.servodriver.utils.D2xxUtil;
import com.adrian.servodriver.utils.FileUtil;
import com.blankj.utilcode.util.Utils;

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
