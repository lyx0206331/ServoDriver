package com.adrian.servodriver.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.adrian.servodriver.R;
import com.adrian.servodriver.official_demo.J2xxHyperTerm;
import com.adrian.servodriver.views.StatusBarCompat;
import com.jaeger.library.StatusBarUtil;

public class WelcomeActivity extends BaseActivity {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
//                    Intent intent = new Intent(WelcomeActivity.this, J2xxHyperTerm.class);
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
//        StatusBarUtil.setTransparent(this);
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_welcome;
    }
}
