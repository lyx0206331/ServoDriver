package com.adrian.servodriver.activities;

import android.os.Bundle;

import com.adrian.servodriver.R;
import com.jaeger.library.StatusBarUtil;

public class FFTActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_fft);
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
