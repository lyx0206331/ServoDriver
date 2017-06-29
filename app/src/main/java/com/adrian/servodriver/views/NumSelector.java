package com.adrian.servodriver.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adrian.servodriver.R;

/**
 * Created by qing on 2017/6/29 0029.
 */

public class NumSelector extends LinearLayout implements View.OnClickListener {

    private Context context;
    private Button mMinusBtn;
    private Button mPlusBtn;
    private TextView mNumTV;
    private TextView mUnitTV;

    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;
    private int curValue = 0;

    private String unit = "--";

    public NumSelector(Context context) {
        super(context);
    }

    public NumSelector(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public NumSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_num_selector, null, false);
        addView(view);

        mMinusBtn = (Button) view.findViewById(R.id.btn_minus);
        mPlusBtn = (Button) view.findViewById(R.id.btn_plus);
        mNumTV = (TextView) view.findViewById(R.id.tv_num);
        mUnitTV = (TextView) findViewById(R.id.tv_unit);

        mMinusBtn.setOnClickListener(this);
        mPlusBtn.setOnClickListener(this);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        mUnitTV.setText(unit);
    }

    public int getCurValue() {
        return curValue;
    }

    public void setDefaultValue(int curValue) {
        this.curValue = curValue;
        mNumTV.setText(curValue + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_minus:
                mNumTV.setText((curValue <= min ? curValue : --curValue) + "");
                break;
            case R.id.btn_plus:
                mNumTV.setText((curValue >= max ? curValue : ++curValue) + "");
                break;
        }
    }
}
