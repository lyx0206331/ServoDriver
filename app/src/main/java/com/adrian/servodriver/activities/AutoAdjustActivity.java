package com.adrian.servodriver.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.adrian.servodriver.R;
import com.jaeger.library.StatusBarUtil;

public class AutoAdjustActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_auto_adjust);
        StatusBarUtil.setTransparent(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        close();
    }
}