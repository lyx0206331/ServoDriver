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
import com.adrian.servodriver.adapter.StateMonitorAdapter;
import com.adrian.servodriver.pojo.StateBean;
import com.blankj.utilcode.util.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class StateMonitorActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton mBackIV;
    private Button mStopBtn;
    private ListView mStateLV;
    private EditText mPeriodET;

    private StateMonitorAdapter mAdapter;

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
        StatusBarUtil.setTransparent(this);
        mBackIV = (ImageButton) findViewById(R.id.ib_back);
        mStopBtn = (Button) findViewById(R.id.btn_stop_monitor);
        mStateLV = (ListView) findViewById(R.id.lv_state);
        mPeriodET = (EditText) findViewById(R.id.et_period);

        mAdapter = new StateMonitorAdapter(this);
        mStateLV.setAdapter(mAdapter);

        mBackIV.setOnClickListener(this);
        mStopBtn.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        List<StateBean> data = new ArrayList<>();
        data.add(new StateBean("电机速度", 0, "r/min"));
        data.add(new StateBean("原始位置指令(输入脉冲)", 1, "%"));
        data.add(new StateBean("电机转距", 1, "%"));
        data.add(new StateBean("位置指令脉冲率", 3, "kHz"));
        data.add(new StateBean("速度模拟指令电压", 1, "mV"));
        data.add(new StateBean("转子绝对位置(脉冲）", 1, "-"));
        data.add(new StateBean("母线电压", 1, "V"));
        data.add(new StateBean("报警代码", 1, "-"));
        data.add(new StateBean("电机电流", 1, "%"));
        mAdapter.setData(data);
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
//                ToastUtils.showShortSafe("停止监视");
                close();
                break;
        }
    }

    @Override
    public void notifyByThemeChanged() {

    }
}
