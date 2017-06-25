package com.adrian.servodriver.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.adrian.servodriver.R;
import com.blankj.utilcode.util.ToastUtils;

public class StateMonitorActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton mBackIV;
    private Button mStopBtn;
    private ListView mStateLV;
    private EditText mPeriodET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_state_monitor);
        mBackIV = (ImageButton) findViewById(R.id.ib_back);
        mStopBtn = (Button) findViewById(R.id.btn_stop_monitor);
        mStateLV = (ListView) findViewById(R.id.lv_state);
        mPeriodET = (EditText) findViewById(R.id.et_period);

        mBackIV.setOnClickListener(this);
        mStopBtn.setOnClickListener(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
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
            case R.id.btn_stop_monitor:
                ToastUtils.showShortSafe("停止监视");
                break;
        }
    }
}
