package com.adrian.servodriver.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.annotation.StyleRes;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adrian.servodriver.R;

/**
 * Created by ranqing on 2017/7/18.
 */

public class WarningDialog extends Dialog implements View.OnClickListener {

    private ImageButton mBackIB;
    private TextView mCodeTV;
    private TextView mContentTV;
    private TextView mReasonTV;
    private TextView mSolutionTV;
    private Button mKnowBtn;

    public WarningDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
    }

    public WarningDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected WarningDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_warning_dialog);

        mBackIB = (ImageButton) findViewById(R.id.ib_close);
        mCodeTV = (TextView) findViewById(R.id.tv_code);
        mContentTV = (TextView) findViewById(R.id.tv_content);
        mReasonTV = (TextView) findViewById(R.id.tv_reason);
        mSolutionTV = (TextView) findViewById(R.id.tv_solution);
        mKnowBtn = (Button) findViewById(R.id.btn_know);

        mCodeTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        mContentTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        mReasonTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        mSolutionTV.setMovementMethod(ScrollingMovementMethod.getInstance());

        mBackIB.setOnClickListener(this);
        mKnowBtn.setOnClickListener(this);
    }

    public WarningDialog setErrCode(String code) {
        mCodeTV.setText(code);
        return this;
    }

    public WarningDialog setErrContent(String content) {
        mContentTV.setText(content);
        return this;
    }

    public WarningDialog setErrReason(String reason) {
        mReasonTV.setText(reason);
        return this;
    }

    public WarningDialog setSolution(String solution) {
        mSolutionTV.setText(solution);
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
                dismiss();
                break;
            case R.id.btn_know:
                dismiss();
                break;
        }
    }
}
