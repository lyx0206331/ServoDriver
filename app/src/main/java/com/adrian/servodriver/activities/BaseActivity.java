package com.adrian.servodriver.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.adrian.servodriver.R;
import com.adrian.servodriver.views.StatusBarCompat;
import com.jaeger.library.StatusBarUtil;

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        StatusBarCompat.compat(this, Color.TRANSPARENT);
//        setContentView(getLayoutResId());
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.picton_blue));
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
}
