package com.adrian.servodriver.databinding;

import android.databinding.BindingConversion;
import android.graphics.Typeface;
import android.view.View;

import com.adrian.servodriver.activities.AboutActivity;
import com.adrian.servodriver.application.MyApplication;

/**
 * Created by ranqing on 2017/7/30.
 */

public class AboutBinding {

    private AboutActivity act;
    private String version;

    public AboutBinding(AboutActivity act) {
        this.act = act;
    }

    @BindingConversion
    public static Typeface convertStringToFace(String s) {
        try {
            return Typeface.createFromAsset(MyApplication.getInstance().getAssets(), s);
        } catch (Exception e) {
            throw e;
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void clickBack(View v) {
        act.close();
    }
}
