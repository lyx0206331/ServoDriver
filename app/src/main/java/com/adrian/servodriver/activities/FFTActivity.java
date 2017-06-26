package com.adrian.servodriver.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.adrian.servodriver.R;
import com.blankj.utilcode.util.ToastUtils;
import com.jaeger.library.StatusBarUtil;

public class FFTActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton mBackIB;
    private Button mCheckBtn;

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

        mBackIB = (ImageButton) findViewById(R.id.ib_back);
        mCheckBtn = (Button) findViewById(R.id.btn_check);

        mBackIB.setOnClickListener(this);
        mCheckBtn.setOnClickListener(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                close();
                break;
            case R.id.btn_check:
                ToastUtils.showShortSafe("检测");
                break;
        }
    }
}
