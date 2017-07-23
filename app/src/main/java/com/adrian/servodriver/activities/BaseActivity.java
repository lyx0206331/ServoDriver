package com.adrian.servodriver.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.adrian.servodriver.R;
import com.adrian.servodriver.application.MyApplication;
import com.adrian.servodriver.theme_picker.ThemeChangeObserver;
import com.adrian.servodriver.utils.Constants;
import com.adrian.servodriver.views.StatusBarCompat;
import com.jaeger.library.StatusBarUtil;

public abstract class BaseActivity extends AppCompatActivity implements ThemeChangeObserver {

    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setupActivityBeforeCreate();
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        StatusBarCompat.compat(this, Color.TRANSPARENT);
//        setContentView(getLayoutResId());
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.picton_blue));
//        setTheme(R.style.Theme1);
        initVariables();
        initViews();
//        StatusBarUtil.setTransparent(this);
        loadData();
    }

    protected void startActivity(Class<? extends Activity> dstAct) {
        Intent intent = new Intent(this, dstAct);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    protected void startActivity(Class<? extends Activity> dstAct, Bundle bundle) {
        Intent intent = new Intent(this, dstAct);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void close() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    /**
     * 初始化变量
     */
    protected abstract void initVariables();

    /**
     * 初始化UI
     */
    protected abstract void initViews();

    /**
     * 数据加载
     */
    protected abstract void loadData();

    /**
     * */
    private void setupActivityBeforeCreate() {
        MyApplication.getInstance().registerObserver(this);
        loadingCurrentTheme();
    }

    @Override
    public void loadingCurrentTheme() {
        switch (getThemeTag()) {
            case 1:
                setTheme(R.style.AppTheme1);
                break;
            case -1:
                setTheme(R.style.AppTheme2);
                break;
        }
    }

    /**
     * */
    protected int getThemeTag() {
        SharedPreferences preferences = getSharedPreferences(Constants.SHERED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(Constants.KEY_THEME_CACHE, 1);
    }

    /**
     * */
    protected void setThemeTag(int tag) {
        SharedPreferences preferences = getSharedPreferences(Constants.SHERED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(Constants.KEY_THEME_CACHE, tag);
        edit.commit();
    }

    /**
     * */
    public void switchCurrentThemeTag() {
        setThemeTag(0 - getThemeTag());
        loadingCurrentTheme();
    }

    /**
     * 返回布局ID
     *
     * @return
     */
//    protected abstract int getLayoutResId();

    public void showProgress(String msg) {
        if (mPd == null) {
            mPd = new ProgressDialog(this);
            mPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPd.setIndeterminate(false);
            mPd.setCancelable(true);
            mPd.setCanceledOnTouchOutside(false);
        }
        mPd.setMessage(msg);
        if (!mPd.isShowing()) {
            mPd.show();
        }
    }

    public void hideProgress() {
        if (mPd == null) return;
        if (mPd.isShowing()) {
            mPd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().unregisterObserver(this);
        super.onDestroy();
    }
}
