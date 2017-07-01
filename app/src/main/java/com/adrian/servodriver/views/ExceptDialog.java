package com.adrian.servodriver.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adrian.servodriver.R;

/**
 * Created by ranqing on 2017/6/30.
 */

public class ExceptDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private ImageButton mCloseBtn;
    private TextView mTitleTV;
    private TextView mContentTV;
    private Button mKnowBtn;

    public ExceptDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        this.context = context;
    }

    public ExceptDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public ExceptDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_except_dialog);

        mCloseBtn = (ImageButton) findViewById(R.id.ib_close);
        mTitleTV = (TextView) findViewById(R.id.tv_title);
        mContentTV = (TextView) findViewById(R.id.tv_content);
        mKnowBtn = (Button) findViewById(R.id.btn_know);

        mKnowBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
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
                dismiss();
                break;
        }
    }
}