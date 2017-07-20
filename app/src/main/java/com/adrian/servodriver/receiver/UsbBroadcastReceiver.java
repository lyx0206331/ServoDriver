package com.adrian.servodriver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.adrian.servodriver.utils.CommUtil;

/**
 * Created by ranqing on 2017/7/20.
 */

public class UsbBroadcastReceiver extends BroadcastReceiver {

    private final static String ACTION = "android.hardware.usb.action.USB_STATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        Toast.makeText(context, "aciton =" + action, Toast.LENGTH_SHORT).show();
        if (action.equals(ACTION)) {
            boolean connected = intent.getExtras().getBoolean("connected");
//            Toast.makeText(context, "aciton =" + connected, Toast.LENGTH_SHORT).show();
            if (connected) {
                CommUtil.showToast("USB已连接");
            } else {
                CommUtil.showToast("USB已断开");
            }
        }
    }
}
