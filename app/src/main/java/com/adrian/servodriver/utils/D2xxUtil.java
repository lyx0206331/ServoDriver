package com.adrian.servodriver.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;

import com.ftdi.j2xx.D2xxManager;

/**
 * Created by ranqing on 2017/5/4.
 */

public class D2xxUtil {
    private static D2xxUtil ourInstance;

    private D2xxManager ftD2xx;

    private D2xxUtil(Context context) {
        try {
            ftD2xx = D2xxManager.getInstance(context);
        } catch (D2xxManager.D2xxException ex) {
            ex.printStackTrace();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
    }

    public static D2xxUtil getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new D2xxUtil(context);
        }
        return ourInstance;
    }
}
