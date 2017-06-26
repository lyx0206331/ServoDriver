package com.adrian.servodriver.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adrian.servodriver.R;
import com.jaeger.library.StatusBarUtil;

public class WarnActivity extends BaseActivity {

    private ImageButton mBackIB;

    private TextView mCodeTV;
    private TextView mContentTV;
    private TextView mReasonTV;
    private TextView mSolutionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_warn);
        StatusBarUtil.setTransparent(this);
        mBackIB = (ImageButton) findViewById(R.id.ib_back);
        mCodeTV = (TextView) findViewById(R.id.tv_code);
        mContentTV = (TextView) findViewById(R.id.tv_content);
        mReasonTV = (TextView) findViewById(R.id.tv_reason);
        mSolutionTV = (TextView) findViewById(R.id.tv_solution);

        mCodeTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        mContentTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        mReasonTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        mSolutionTV.setMovementMethod(ScrollingMovementMethod.getInstance());

        mBackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
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
