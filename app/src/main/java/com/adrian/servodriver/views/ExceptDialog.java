package com.adrian.servodriver.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adrian.servodriver.R;

/**
 * Created by ranqing on 2017/6/30.
 */

public class ExceptDialog extends BaseDialog implements View.OnClickListener {

    private ImageButton mCloseBtn;
    private TextView mTitleTV;
    private TextView mContentTV;
    private Button mKnowBtn;

    public ExceptDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        mCloseBtn = (ImageButton) findViewById(R.id.ib_close);
        mTitleTV = (TextView) findViewById(R.id.tv_title);
        mContentTV = (TextView) findViewById(R.id.tv_content);
        mKnowBtn = (Button) findViewById(R.id.btn_know);

        mKnowBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.layout_except_dialog;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        ThemeResourceHelper helper = ThemeResourceHelper.getInstance(context);
//        helper.setBackgroundResourceByAttr(mKnowBtn, R.attr.btn_bg_small);
//
    }

    public void setTitle(String title) {
        mTitleTV.setText(title);
    }

    public void setContent(String content) {
        mContentTV.setText(content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
            case R.id.btn_know:
//                dismiss();
                cancel();
                break;
        }
    }
}
