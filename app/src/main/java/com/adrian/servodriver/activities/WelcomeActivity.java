package com.adrian.servodriver.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.adrian.servodriver.R;
import com.adrian.servodriver.official_demo.J2xxHyperTerm;
import com.adrian.servodriver.theme_picker.ThemeResourceHelper;
import com.adrian.servodriver.utils.D2xxUtil;
import com.adrian.servodriver.views.LoadingPathAnimView;
import com.adrian.servodriver.views.StatusBarCompat;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class WelcomeActivity extends BaseActivity {

    private LoadingPathAnimView mWelTextView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
//                    if (D2xxUtil.getInstance().isConnected()) {
                        startActivity(MainActivity.class);
//                    }
//                    else {
//                        startActivity(DevDisconnActivity.class);
//                    }
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_welcome);
        StatusBarUtil.setTransparent(this);

        mWelTextView = (LoadingPathAnimView) findViewById(R.id.welcome);
        mWelTextView.setColorFg(ThemeResourceHelper.getInstance(this).getColorByAttr(R.attr.welcome_text_fg_color)).setColorBg(ThemeResourceHelper.getInstance(this).getColorByAttr(R.attr.welcome_text_color));

//        mHandler.sendEmptyMessageDelayed(0, 2000);
        if (NetworkUtils.isConnected()) {
            WelcomeActivityPermissionsDispatcher.needsMaxsinePermmissionWithCheck(this);
        } else {
            ToastUtils.showShortSafe(R.string.network_error);
            finish();
        }
    }

    private void startAnim() {
        mWelTextView.setAnimTime(2000).setAnimInfinite(false).startAnim();
    }

    private void stopAnim() {
        mWelTextView.stopAnim();
    }

    private void resetAnim() {
        mWelTextView.clearAnim();
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        D2xxUtil.getInstance().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        D2xxUtil.getInstance().onResume();
        startAnim();
    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needsMaxsinePermmission() {
        ToastUtils.showShortSafe("需要权限");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WelcomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//        ToastUtils.showShortSafe("申请结果");
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @OnShowRationale({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationable(final PermissionRequest request) {
        ToastUtils.showShortSafe("再次申请");
        new AlertDialog.Builder(this)
                .setMessage(R.string.request_permission_again)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //再次执行请求
                        request.proceed();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void permissionDenied() {
        ToastUtils.showShortSafe("权限被拒绝");
    }

    @OnNeverAskAgain({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void neverAskAgain() {
//        ToastUtils.showShortSafe("不再询问");
    }

    @Override
    public void notifyByThemeChanged() {

    }
}
