package com.adrian.servodriver.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adrian.servodriver.R;
import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.jaeger.library.StatusBarUtil;

public class AutoAdjustActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ImageButton mBackIB;

    private Button mMinusBtn;
    private Button mPlusBtn;
    private TextView mLevelTV;
    private EditText mInertiaRatioET;
    private Button mCalculateGainBtn;
    private Button mAutoIllationBtn;
    private Button mReadParamBtn;
    private ImageView mIndicatorIV;
    private EditText mP017ET;
    private EditText mP009ET;
    private EditText mP005ET;
    private EditText mP006ET;
    private EditText mP019ET;
    private EditText mP017DET;
    private EditText mP009DET;
    private EditText mP005DET;
    private EditText mP006DET;
    private EditText mP019DET;
    private CheckBox mP017CB;
    private CheckBox mP009CB;
    private CheckBox mP005CB;
    private CheckBox mP006CB;
    private CheckBox mP019CB;
    private CheckBox mP017DCB;
    private CheckBox mP009DCB;
    private CheckBox mP005DCB;
    private CheckBox mP006DCB;
    private CheckBox mP019DCB;
    private ImageButton mUpIB;
    private ImageButton mDownIB;

    private int level;
    private int curPos;
    private int stepW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
        stepW = ScreenUtils.getScreenWidth() / 3;
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_auto_adjust);
        StatusBarUtil.setTransparent(this);

        mBackIB = (ImageButton) findViewById(R.id.ib_back);
        mMinusBtn = (Button) findViewById(R.id.btn_minus);
        mPlusBtn = (Button) findViewById(R.id.btn_plus);
        mLevelTV = (TextView) findViewById(R.id.tv_level);
        mInertiaRatioET = (EditText) findViewById(R.id.et_inertia_ratio);
        mCalculateGainBtn = (Button) findViewById(R.id.btn_cal_gain);
        mAutoIllationBtn = (Button) findViewById(R.id.btn_illation);
        mReadParamBtn = (Button) findViewById(R.id.btn_read_param);
        mIndicatorIV = (ImageView) findViewById(R.id.iv_indicator);
        mP017ET = (EditText) findViewById(R.id.et_p017);
        mP009ET = (EditText) findViewById(R.id.et_p009);
        mP005ET = (EditText) findViewById(R.id.et_p005);
        mP006ET = (EditText) findViewById(R.id.et_p006);
        mP019ET = (EditText) findViewById(R.id.et_p019);
        mP017DET = (EditText) findViewById(R.id.et_p017_d);
        mP009DET = (EditText) findViewById(R.id.et_p009_d);
        mP005DET = (EditText) findViewById(R.id.et_p005_d);
        mP006DET = (EditText) findViewById(R.id.et_p006_d);
        mP019DET = (EditText) findViewById(R.id.et_p019_d);
        mP017CB = (CheckBox) findViewById(R.id.cb_p017);
        mP009CB = (CheckBox) findViewById(R.id.cb_p009);
        mP005CB = (CheckBox) findViewById(R.id.cb_p005);
        mP006CB = (CheckBox) findViewById(R.id.cb_p006);
        mP019CB = (CheckBox) findViewById(R.id.cb_p019);
        mP017DCB = (CheckBox) findViewById(R.id.cb_p017_d);
        mP009DCB = (CheckBox) findViewById(R.id.cb_p009_d);
        mP005DCB = (CheckBox) findViewById(R.id.cb_p005_d);
        mP006DCB = (CheckBox) findViewById(R.id.cb_p006_d);
        mP019DCB = (CheckBox) findViewById(R.id.cb_p019_d);
        mUpIB = (ImageButton) findViewById(R.id.ib_up);
        mDownIB = (ImageButton) findViewById(R.id.ib_down);

        mBackIB.setOnClickListener(this);
        mMinusBtn.setOnClickListener(this);
        mPlusBtn.setOnClickListener(this);
        mCalculateGainBtn.setOnClickListener(this);
        mAutoIllationBtn.setOnClickListener(this);
        mReadParamBtn.setOnClickListener(this);
        mUpIB.setOnClickListener(this);
        mDownIB.setOnClickListener(this);
        mP017CB.setOnCheckedChangeListener(this);
        mP009CB.setOnCheckedChangeListener(this);
        mP005CB.setOnCheckedChangeListener(this);
        mP006CB.setOnCheckedChangeListener(this);
        mP019CB.setOnCheckedChangeListener(this);
        mP017DCB.setOnCheckedChangeListener(this);
        mP009DCB.setOnCheckedChangeListener(this);
        mP005DCB.setOnCheckedChangeListener(this);
        mP006DCB.setOnCheckedChangeListener(this);
        mP019DCB.setOnCheckedChangeListener(this);
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
            case R.id.btn_minus:
                if (level > 0) {
                    level--;
                    mLevelTV.setText(level + "");
                }
                break;
            case R.id.btn_plus:
                if (level < 10) {
                    level++;
                    mLevelTV.setText(level + "");
                }
                break;
            case R.id.btn_cal_gain:
                startAnim(-1);
                break;
            case R.id.btn_illation:
                startAnim(0);
                break;
            case R.id.btn_read_param:
                startAnim(1);
                break;
            case R.id.ib_up:
                if (!TextUtils.isEmpty(mP017DET.getText()) && mP017CB.isChecked()) {
                    mP017ET.setText(mP017DET.getText());
                }
                if (!TextUtils.isEmpty(mP009DET.getText()) && mP009CB.isChecked()) {
                    mP009ET.setText(mP009DET.getText());
                }
                if (!TextUtils.isEmpty(mP005DET.getText()) && mP005CB.isChecked()) {
                    mP005ET.setText(mP005DET.getText());
                }
                if (!TextUtils.isEmpty(mP006DET.getText()) && mP006CB.isChecked()) {
                    mP006ET.setText(mP006DET.getText());
                }
                if (!TextUtils.isEmpty(mP019DET.getText()) && mP019CB.isChecked()) {
                    mP019ET.setText(mP019DET.getText());
                }
                break;
            case R.id.ib_down:
                if (!TextUtils.isEmpty(mP017ET.getText()) && mP017CB.isChecked()) {
                    mP017DET.setText(mP017ET.getText());
                }
                if (!TextUtils.isEmpty(mP009ET.getText()) && mP009CB.isChecked()) {
                    mP009DET.setText(mP009ET.getText());
                }
                if (!TextUtils.isEmpty(mP005ET.getText()) && mP005CB.isChecked()) {
                    mP005DET.setText(mP005ET.getText());
                }
                if (!TextUtils.isEmpty(mP006ET.getText()) && mP006CB.isChecked()) {
                    mP006DET.setText(mP006ET.getText());
                }
                if (!TextUtils.isEmpty(mP019ET.getText()) && mP019CB.isChecked()) {
                    mP019DET.setText(mP019ET.getText());
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_p017:
                mP017DCB.setChecked(isChecked);
                break;
            case R.id.cb_p009:
                mP009DCB.setChecked(isChecked);
                break;
            case R.id.cb_p005:
                mP005DCB.setChecked(isChecked);
                break;
            case R.id.cb_p006:
                mP006DCB.setChecked(isChecked);
                break;
            case R.id.cb_p019:
                mP019DCB.setChecked(isChecked);
                break;
            case R.id.cb_p017_d:
                mP017CB.setChecked(isChecked);
                break;
            case R.id.cb_p009_d:
                mP009CB.setChecked(isChecked);
                break;
            case R.id.cb_p005_d:
                mP005CB.setChecked(isChecked);
                break;
            case R.id.cb_p006_d:
                mP006CB.setChecked(isChecked);
                break;
            case R.id.cb_p019_d:
                mP019CB.setChecked(isChecked);
                break;
        }
    }

    private void startAnim(int pos) {
        TranslateAnimation anim = new TranslateAnimation(curPos * stepW, pos * stepW, 0, 0);
        anim.setDuration(200);
        anim.setFillAfter(true);
        mIndicatorIV.startAnimation(anim);
        curPos = pos;
    }
}
