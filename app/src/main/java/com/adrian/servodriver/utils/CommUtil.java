package com.adrian.servodriver.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.AbsoluteSizeSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.adrian.servodriver.application.MyApplication;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ranqing on 2017/6/2.
 */

public class CommUtil {
    public static final boolean DEBUG = true;
    private static final String TAG = CommUtil.class.getSimpleName();
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F'};

    public static void logE(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void logV(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void logI(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void logD(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void logW(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }

    /**
     * 获取屏幕信息
     *
     * @param act
     * @return
     */
    public static DisplayMetrics getScreenInfo(Activity act) {
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 判断是否静态IP
     *
     * @param context
     * @return
     */
    public static boolean isStaticIP(Context context) {
        ContentResolver cr = context.getContentResolver();
        try {
            return Settings.System.getInt(cr, Settings.System.WIFI_USE_STATIC_IP) != 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置静态IP
     *
     * @param context
     * @param ipAddr
     * @param netmask
     * @param gateway
     * @param dns1
     * @param dns2
     */
    public static void setStaticIP(Context context, String ipAddr, String netmask, String gateway, String dns1, String dns2) {
        if (isStaticIP(context)) {
            ContentResolver cr = context.getContentResolver();
            Settings.System.putString(cr, Settings.System.WIFI_STATIC_IP, ipAddr);
            Settings.System.putString(cr, Settings.System.WIFI_STATIC_GATEWAY, gateway);
            Settings.System.putString(cr, Settings.System.WIFI_STATIC_NETMASK, netmask);
            Settings.System.putString(cr, Settings.System.WIFI_STATIC_DNS1, dns1);
            Settings.System.putString(cr, Settings.System.WIFI_STATIC_DNS2, dns2);
        }
    }

    /**
     * 获取DHCP信息，内含IP地址，子网掩码，网关，DNS等信息,需要调用int2ip(int)方法转换为标准格式
     *
     * @param context
     * @return
     */
    public static DhcpInfo getDhcpInfo(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wm.getDhcpInfo();
    }

    /**
     * 转换int数据为标准IP格式
     *
     * @param paramInt
     * @return
     */
    public static String int2ip(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    /**
     * 获取网络状态
     *
     * @param ctx
     * @return -1:无网络;0:移动网络;1:wifi网络;2:以太网
     */
    public static int getNetworkStatus(Context ctx) {
        int status = -1;
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        // NetworkInfo mobileInfo =
        // manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // NetworkInfo wifiInfo =
        // manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable()) {
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                ///// WiFi网络
                status = 1;
            } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                ///// 有线网络
                status = 2;
            } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                ///////// 3g网络
                status = 0;
            }
        } else {
            status = -1;
        }
        return status;
    }

    /**
     * 获取wifi状态
     *
     * @param ctx
     * @return
     */
    public static boolean getWifiStatus(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int state = wifiInfo.getNetworkId();
        return state != -1 ? true : false;
    }

    /**
     * 获取wifi名称（SSID）
     *
     * @param ctx
     * @return
     */
    public static String getWifiName(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID", wifiInfo.getSSID());
        return wifiInfo.getSSID();
    }

    /**
     * 根据wifi获取ip地址
     *
     * @param ctx
     * @return
     */
    public static String getIp4Wifi(Context ctx) {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16) & 0xFF) + "."
                + (ipAddress >> 24 & 0xFF);
        // Log.e("Test", "wifi ip---->" + ip);
        return ip;
    }

    public static void showToast(String msg) {
        Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int msgId) {
        Toast.makeText(MyApplication.getInstance(), msgId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 生成dimens.xml文件
     *
     * @param name  文件名
     * @param start 起始位置
     * @param end   终止位置
     * @param unit  单位。px/dp/sp等等
     */
    public static void createDimensXML(String name, int start, int end, String unit) {
//        FileOutputStream fos = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + name);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
//            fos = new FileOutputStream(file);
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<resources>\n");
            for (int i = start; i <= end; i++) {
                bw.write("<dimen name=\"dp" + i + "\">" + i + ".0" + unit + "</dimen>\n");
                bw.write("<dimen name=\"dp" + i + "_5\">" + i + ".5" + unit + "</dimen>\n");
            }
            bw.write("</resources>");
            bw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 毫秒转日期
     *
     * @param pattern 日期格式。如：yyyy-MM-dd HH:mm:ss
     * @param millis
     * @return
     */
    public static String millis2date(String pattern, long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date(millis));
    }

    /**
     * 日期转毫秒
     *
     * @param pattern 日期格式。如：yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static long date2millis(String pattern, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        long millis = 0L;
        try {
            millis = sdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    /**
     * 生成需要的dimens.xml文件
     *
     * @param start
     * @param len
     * @param densityMulti 屏幕像素密度倍数，以160dpi为标准
     */
    public static void createDimensXml(int start, int len, float densityMulti) {
        FileOutputStream fos;
        File file;
        String fileName = "dimens.xml";
        try {
            if (start <= 0) {
                throw new IllegalArgumentException("start参数不合法，start值必须大于0");
            }
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                file = new File(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName);
            } else {
                file = new File(Environment.getRootDirectory().getAbsolutePath() + File.separator + fileName);
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            Log.e(TAG, "start dimens ---> " + file.getAbsolutePath());
            fos = new FileOutputStream(file);
            for (int i = start; i <= len; i++) {
                // <dimen name="dp5">5dp</dimen>
                // <dimen name="sp5">5sp</dimen>
                String tmp = "<dimen name=\"sp" + i + "\">" + i * densityMulti + "sp</dimen>\n";
                byte[] b = tmp.getBytes("UTF-8");
                fos.write(b);
            }
            fos.flush();
            fos.close();
            Log.e(TAG, "end dimens");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO: handle exception
        }
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取控件在屏幕中的坐标
     *
     * @param view
     * @return
     */
    public static int[] getLocOnScreen(View view) {
        int[] pos = new int[2];
        view.getLocationOnScreen(pos);
        return pos;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dp
     */
    public static int dp2px(Context context, int dp) {
        return Math.round(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context
     * @param pxValue
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 显示Toast
     *
     * @param ctx
     * @param resId
     */
    public static void showToast(Context ctx, int resId) {
        Toast.makeText(ctx, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示Toast
     *
     * @param ctx
     * @param string
     */
    public static void showToast(Context ctx, String string) {
        Toast.makeText(ctx, string, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示Toast
     *
     * @param ctx
     * @param string
     * @param duration
     */
    public static void showToast(Context ctx, String string, int duration) {
        Toast toast = new Toast(ctx);
        toast.setText(string);
        toast.setDuration(duration);
        toast.show();
    }

    /**
     * 显示Toast
     *
     * @param ctx
     * @param resId
     * @param duration
     */
    public static void showToast(Context ctx, int resId, int duration) {
        Toast toast = new Toast(ctx);
        toast.setText(resId);
        toast.setDuration(duration);
        toast.show();
    }

    /**
     * 获取指定长度的随机字条串
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return sb.toString();
    }

    /**
     * 根据Gprs获取ip
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreferenceIpAddress", ex.toString());
        }
        return null;
    }

    /**
     * 时间格式化
     *
     * @param millis
     * @return
     */
    public static String formatTime(long millis) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA); // "yyyy-MM-dd
        // HH:mm:ss"
        format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date date = new Date(millis);
        return format.format(date);
    }

    /**
     * 时间格式化
     *
     * @param millis
     * @param formatStr 格式化样式："yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String formatTime2(long millis, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.CHINA); // "yyyy-MM-dd
        // HH:mm:ss"
        Date date = new Date(millis);
        return format.format(date);
    }

    /**
     * 时间格式化
     *
     * @param millis
     * @param timeZone 时区。如：GMT+8
     * @return
     */
    public static String formatTime(long millis, String timeZone) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA); // "yyyy-MM-dd
        // HH:mm:ss"
        if (timeZone != null) {
            String[] zoneIDs = TimeZone.getAvailableIDs();
            for (String zone : zoneIDs) {
                if (timeZone.equals(zone)) {
                    format.setTimeZone(TimeZone.getTimeZone(timeZone));
                    break;
                }
            }
        }
        Date date = new Date(millis);
        return format.format(date);
    }

    /**
     * 时间格式化
     *
     * @param millis
     * @return
     */
    public static String formatTimeWithYMD(long millis, String style) {
        SimpleDateFormat format = new SimpleDateFormat(style, Locale.CHINA); // "yyyy-MM-dd
        // HH:mm:ss"
        Date date = new Date(millis);
        return format.format(date);
    }

    /**
     * 获得机身内存总大小, API18之前
     *
     * @param ctx
     * @return
     */
    public static String getMemTotalSize(Context ctx) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(ctx, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存, API18之前
     *
     * @param ctx
     * @return
     */
    public static String getMemFreeSize(Context ctx) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(ctx, blockSize * availableBlocks);
    }

    /**
     * 获取机身可用内存，API18之后
     *
     * @param ctx
     * @return
     */
    @SuppressLint("NewApi")
    public static String getMemFreeSize2(Context ctx) {
        StatFs fs = new StatFs(Environment.getDataDirectory().getPath());
        return Formatter.formatFileSize(ctx, (fs.getAvailableBytes()));
    }

    /**
     * 得到SD可用内存，API18之后
     *
     * @param ctx
     * @return
     */
    @SuppressLint("NewApi")
    public static String getSDFreeSize2(Context ctx) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            StatFs fs = new StatFs(Environment.getExternalStorageDirectory().getPath());
            // Android API18之前：fs.getAvailableBlocks()*fs.getBlockSize()
            return Formatter.formatFileSize(ctx, (fs.getAvailableBytes()));
        }
        return "未装载";
    }

    public static String getBase64(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        } else {
            return new String(Base64.encode(text.getBytes(), Base64.DEFAULT));
        }
    }

    /**
     * 获取列表长度
     *
     * @param list
     * @return
     */
    public static int getSize(List list) {
        return list == null ? 0 : list.size();
    }

    /**
     * 邮箱验证
     *
     * @param email
     * @return
     */
    public static boolean verifyEmail(String email) {
        if (!TextUtils.isEmpty(email.trim())) {
            Pattern pattern = Pattern.compile("^(\\w-*\\.*)+@(\\w-?)+(\\.\\w{2,})+$"); // ^\w+[-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
        return false;
    }

    /**
     * 昵称验证
     *
     * @param nickname
     * @return
     */
    public static boolean verifyNickname(String nickname) {
        if (!TextUtils.isEmpty(nickname)) {
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9\\u4e00-\\u9fa5]+$");
            Matcher matcher = pattern.matcher(nickname);
            return matcher.matches();
        }
        return false;
    }

    /**
     * 手机号验证
     *
     * @param phone_num
     * @return
     */
    public static boolean verifyPhoneNum(String phone_num) {
        if (!TextUtils.isEmpty(phone_num.trim())) {
            Pattern pattern = Pattern.compile("^1\\d{10}$");
            Matcher matcher = pattern.matcher(phone_num);
            return matcher.matches();
        }
        return false;
    }

    /**
     * 获得版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context, String pkgName) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(pkgName, 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context, String pkgName) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(pkgName, 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    public static String getPkgName(Context context) {
        return context.getPackageName();
    }

    public static boolean isEmpty(String str) {
        if (str == null || TextUtils.isEmpty(str.trim()) || "null".equals(str.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 将字符串格式化为不同大小
     *
     * @param txt
     * @param size
     * @param start
     * @param end
     * @return
     */
    public static SpannableString formatTextSize(String txt, int size, int start, int end) {
        SpannableString spanString = new SpannableString(txt);
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(size, true);
        spanString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    /**
     * base64加密
     *
     * @param msg
     * @param extra 加密冗余字段
     * @return
     */
    public static String encodeBase64(String msg, String extra) {
        if (TextUtils.isEmpty(msg)) {
            return null;
        }
        String txt = Base64.encodeToString(msg.getBytes(), Base64.DEFAULT) + extra;
        String result = Base64.encodeToString(txt.getBytes(), Base64.DEFAULT);
        return result;
    }

    /**
     * base64解密
     *
     * @param base64
     * @param extra  加密冗余字段
     * @return
     */
    public static String decodeBase64(String base64, String extra) {
        if (TextUtils.isEmpty(base64)) {
            return null;
        }
        String txt = new String(Base64.decode(base64, Base64.DEFAULT));
        String result = new String(Base64.decode(txt.substring(0, txt.length() - extra.length()), Base64.DEFAULT));
        return result;
    }

    /**
     * 根据包名启动应用
     *
     * @param context
     * @param appPackageName
     */
    public static void startApp(Context context, String appPackageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
            context.startActivity(intent);
        } catch (Exception e) {
            // Toast.makeText(context, "没有安装", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * 启动apk
     *
     * @param ctx
     * @param pkgName      包名
     * @param activityPath 启动Activity路径（含完整包名）
     */
    public static void startApp(Context ctx, String pkgName, String activityPath) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(pkgName, activityPath));
        ctx.startActivity(intent);
    }

    /**
     * 启动app
     *
     * @param packageName  com.exmaple.client/.MainActivity
     * @param activityName com.exmaple.client/com.exmaple.client.MainActivity
     */
    public static boolean startApp(String packageName, String activityName) {
        boolean isSuccess = false;
        String cmd = "am start -n " + packageName + "/" + activityName + " \n";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            int value = process.waitFor();
            return value == 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return isSuccess;
    }

    /**
     * 获取权限
     *
     * @param permission
     * @param path
     */
    public static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 安装apk
     *
     * @param ctx
     * @param apkPath
     * @throws FileNotFoundException
     */
    public static boolean installApk(Context ctx, String apkPath) {
        // chmod("777", apkPath);
        File file = new File(apkPath);
        if (!file.exists()) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.parse("file://" + apkPath), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
        return true;
    }

    /**
     * 判断手机是否有root权限
     */
    public static boolean hasRootPerssion() {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return value == 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    /**
     * 静默安装
     *
     * @param apkPath
     */
    public static String clientInstall(String apkPath) {
        chmod("777", apkPath);
        String[] args = {"pm", "install", "-r", apkPath};

        String result = "";

        ProcessBuilder processBuilder = new ProcessBuilder(args);

        Process process = null;

        InputStream errIs = null;

        InputStream inIs = null;
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int read = -1;

            process = processBuilder.start();

            errIs = process.getErrorStream();

            while ((read = errIs.read()) != -1) {

                baos.write(read);

            }

            baos.write("/n".getBytes());

            inIs = process.getInputStream();

            while ((read = inIs.read()) != -1) {

                baos.write(read);

            }

            byte[] data = baos.toByteArray();

            result = new String(data);

        } catch (IOException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {

                if (errIs != null) {

                    errIs.close();

                }

                if (inIs != null) {

                    inIs.close();

                }

            } catch (IOException e) {

                e.printStackTrace();

            }

            if (process != null) {
                process.destroy();

            }

        }

        return result;
    }

    /**
     * 静默卸载
     *
     * @param packageName
     */
    public static boolean clientUninstall(String packageName) {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            PrintWriter.println("pm uninstall " + packageName);
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return value == 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean delFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        boolean delSucc = file.delete();
        return delSucc;
    }

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean delFile(File file) {
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        boolean delSucc = file.delete();
        return delSucc;
    }

    /**
     * 判断应用程序处于前台还是后台(需要权限 "android.permission.GET_TASKS")
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isAppRunningBackground(Context context, String pkgName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(pkgName)) {
                return true;
            }
        }
        return false;

    }

    /**
     * 判断应用程序处于前台还是后台(需要权限 "android.permission.GET_TASKS")
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isAppRunningForeground(Context context, String pkgName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(pkgName)) {
                return true;
            }
        }
        return false;

    }

    /**
     * 判断应用程序处于前台还是后台
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isAppRunningBackground2(Context context, String pkgName) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(pkgName)) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.e("TAG", "background : " + appProcess.processName);
                    return true;
                } else {
                    Log.i("TAG", "foreground : " + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 判断应用程序处于前台还是后台
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isAppRunningForeground2(Context context, String pkgName) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(pkgName)) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.e("TAG", "background : " + appProcess.processName);
                    return true;
                } else {
                    Log.i("TAG", "foreground : " + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 静默安装
     *
     * @param apkAbsolutePath
     * @return
     */
    public static String defaultInstall(String apkAbsolutePath) {
        File file = new File(apkAbsolutePath);
        if (!file.exists()) {
            System.err.println("文件不存在！");
            return null;
        }
        String[] args = {"pm", "install", "-r", apkAbsolutePath};
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    /**
     * 打开设置系统页面
     *
     * @param context
     */
    public static void openSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS); // Settings.ACTION_WIFI_SETTINGS
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 判断是否能够使用DownloadManager
     *
     * @param context
     * @return
     */
    public static boolean isDownloadManagerAvailable(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.android.providers.downloads.ui",
                    "com.android.providers.downloads.ui.DownloadList");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 下载文件并在通知栏提示
     *
     * @param context
     * @param url
     * @param saveName
     */
    public static void downloadFile(Context context, String url, String saveName, String title, String description) {
        if (!isDownloadManagerAvailable(context)) {
            return;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(description);
        request.setTitle(title);
        // in order for this if to run, you must use the android 3.2 to compile
        // your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, saveName);
        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    /**
     * 获取联网Mac地址，需要权限
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     *
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getLocalMacAddress() {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }

    /**
     * 设备型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 硬件名称
     *
     * @return
     */
    public static String getHardwareName() {
        return Build.HARDWARE;
    }

    /**
     * android系统版本
     *
     * @return
     */
    public static String getSysVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 序列号
     *
     * @return
     */
    public static String getDeviceSerial() {
        return Build.SERIAL;
    }

    /**
     * 获取文件最后修改时间
     *
     * @param filePath
     * @return
     */
    public static String getLastModifiedTime(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return String.valueOf(file.lastModified());
        }
        return null;
    }

    /**
     * 获取文件MD5码,大文件会暴OOM
     *
     * @param file
     * @return
     */
    public static String getMd5(File file) {
        if (!file.exists()) {
            return null;
        }
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logE("TAG", "md5 : " + value);
        return value;
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static String md5File(String filename) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try {
            fis = new FileInputStream(filename);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
            return toHexString(md5.digest());
        } catch (Exception e) {
            System.out.println("error");
            return null;
        }
    }

    /**
     * 获取apk包名
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static String getPkgNameFromApk(Context context, String apkPath) {
        if (TextUtils.isEmpty(apkPath) || !apkPath.endsWith(".apk")) {
            return null;
        }
        String pkgName = null;
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (pi != null) {
            ApplicationInfo ai = pi.applicationInfo;
            pkgName = ai.packageName;
        }
        return pkgName;
    }

    /*
   * 采用了新的办法获取APK图标，之前的失败是因为android中存在的一个BUG,通过
   * appInfo.publicSourceDir = apkPath;来修正这个问题，详情参见:
   * http://code.google.com/p/android/issues/detail?id=9151
   */
    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                Log.e("ApkIconLoader", e.toString());
            }
        }
        return null;
    }

    /**
     * 判断apk是否安装
     *
     * @param context
     * @param packagename
     * @return
     */
    public static boolean isApkInstalled(Context context, String packagename) {
        PackageManager localPackageManager = context.getPackageManager();
        try {
            PackageInfo localPackageInfo = localPackageManager.getPackageInfo(packagename, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            return false;
        }

    }

    /**
     * 打开apk
     *
     * @param context
     * @param pkgName
     */
    public static void openAPK(Context context, String pkgName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        intent = packageManager.getLaunchIntentForPackage(pkgName);
        context.startActivity(intent);
    }

    /**
     * 获取Assets文件夹中的数据
     *
     * @param context
     * @param fileName
     * @return
     */
    public static byte[] getFromAssets(Context context, String fileName) {
        try {
            InputStream is = context.getResources().getAssets().open(fileName);
            int count = is.available();
            logE("COMMON", "file len: " + count);
            byte[] bytes = new byte[count];
            is.read(bytes);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();

        return imei;
    }

    /**
     * 获取手机IMSI号
     */
    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();

        return imsi;
    }

    /**
     * 获得SD卡总大小, API18之前
     *
     * @param ctx
     * @return
     */
    private String getSDTotalSize(Context ctx) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(ctx, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小, API18之前
     *
     * @param ctx
     * @return
     */
    private String getSDFreeSize(Context ctx) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(ctx, blockSize * availableBlocks);
    }

    /**
     * 重启系统
     */
    private void rebootSys() {
        // Intent intent = new Intent(Intent.ACTION_REBOOT);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // context.sendBroadcast(intent);
        try {
            Runtime.getRuntime().exec("su");
            Runtime.getRuntime().exec("reboot");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 重启到fastboot模式
     */
    private void rebootSys2Fastboot(Context context) {
        PowerManager pManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pManager.reboot("重启");
    }

    /**
     * 系统升级 （需要导入sdk/platforms/android-xx/data/layoutlib.jar）
     *
     * @param path
     */
    private void updateSys(Context context, String path) {
        File file = new File(path);
        // RecoverySystem.verifyPackage(file, progressListener, null);
        try {
            RecoverySystem.installPackage(context, file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 下载远程文件并保存到本地
     *
     * @param remoteFilePath 远程文件路径
     * @param savePath       本地保存文件路径
     * @param name           保存名字
     */
    public void downloadFile(String remoteFilePath, String savePath, String name) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(remoteFilePath);
            connection = (HttpURLConnection) url.openConnection();
            // System.out.println("download connected");
            connection.setConnectTimeout(30000);
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            int resCode = connection.getResponseCode();
            // System.out.println("download responsecode : " + resCode);
            if (resCode != HttpURLConnection.HTTP_OK) {
                return;
            }
            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            System.out.println("download file length : " + fileLength);
            // download the file
            input = connection.getInputStream();
            File dir = new File(savePath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    return;
                }
            }
            File file = new File(savePath + name);
            if (file.exists()) {
                System.out.println("del downloaded ---> " + file.delete());
            }
            output = new FileOutputStream(file);
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // System.out.println("download count : " + total);
                output.write(data, 0, count);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
    }
}
