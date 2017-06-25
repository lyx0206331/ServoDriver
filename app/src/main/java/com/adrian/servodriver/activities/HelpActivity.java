package com.adrian.servodriver.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.adrian.servodriver.R;
import com.adrian.servodriver.databinding.ActivityHelpBinding;
import com.blankj.utilcode.util.ToastUtils;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
        ActivityHelpBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_help);
        Help help = new Help();
        binding.setHelp(help);
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        close();
    }

    public class Help {
        public Help() {
        }

        public void clickBack(View view) {
            close();
        }

        public void clickDisconnect(View view) {
            ToastUtils.showShortSafe("断开连接");
        }
    }
}
