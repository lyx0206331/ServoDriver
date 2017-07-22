package com.adrian.servodriver.theme_picker;

/**
 * Created by ranqing on 2017/7/23.
 */

public interface ThemeChangeObserver {

    void loadingCurrentTheme();

    void notifyByThemeChanged();
}
