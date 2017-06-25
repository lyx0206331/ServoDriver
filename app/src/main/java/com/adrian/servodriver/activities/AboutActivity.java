package com.adrian.servodriver.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.adrian.servodriver.R;
import com.adrian.servodriver.databinding.ActivityAboutBinding;
import com.blankj.utilcode.util.AppUtils;
import com.jaeger.library.StatusBarUtil;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        close();
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
        ActivityAboutBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_about);

        About about = new About();
        about.setVersion("v" + AppUtils.getAppVersionName());
        binding.setAbout(about);
//        StatusBarUtil.setTransparent(this);
    }

    @Override
    protected void loadData() {

    }

    public class About {
        String version;

        public About() {
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public void clickBack(View v) {
            close();
        }
    }
}
