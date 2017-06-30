package com.adrian.servodriver.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.adrian.servodriver.R;
import com.adrian.servodriver.databinding.ActivityHelpBinding;
import com.blankj.utilcode.util.ToastUtils;
import com.jaeger.library.StatusBarUtil;

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
        StatusBarUtil.setTransparent(this);
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

        public void clickEp1cHelp(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.ep1c_help));
            bundle.putInt("type", 0);
            startActivity(ChmReaderActivity.class, bundle);
        }

        public void clickEp1cAHelp(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.ep1c_a_help));
            bundle.putInt("type", 1);
            startActivity(ChmReaderActivity.class, bundle);
        }

        public void clickEp3StdHelp(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.ep3_st_help));
            bundle.putInt("type", 2);
            startActivity(ChmReaderActivity.class, bundle);
        }

        public void clickEp3IoHelp(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.ep3_io_help));
            bundle.putInt("type", 3);
            startActivity(ChmReaderActivity.class, bundle);
        }
    }
}
