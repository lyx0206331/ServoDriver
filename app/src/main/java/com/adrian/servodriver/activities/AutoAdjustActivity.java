package com.adrian.servodriver.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.adrian.servodriver.R;
import com.jaeger.library.StatusBarUtil;

public class AutoAdjustActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton mBackIB;

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

        mBackIB = (ImageButton) findViewById(R.id.ib_back);

        mBackIB.setOnClickListener(this);
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
        }
    }
}
