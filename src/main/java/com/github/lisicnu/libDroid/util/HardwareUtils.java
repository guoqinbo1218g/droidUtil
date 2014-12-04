package com.github.lisicnu.libDroid.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.github.lisicnu.log4android.LogManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

/**
 * use these value to instead.
 * <p/>
 * <strong>
 * android.os.Build.BOARD：获取设备基板名称<br/>
 * android.os.Build.BOOTLOADER:获取设备引导程序版本号<br/>
 * android.os.Build.BRAND：获取设备品牌<br/>
 * android.os.Build.CPU_ABI：获取设备指令集名称（CPU的类型）<br/>
 * android.os.Build.CPU_ABI2：获取第二个指令集名称<br/>
 * android.os.Build.DEVICE：获取设备驱动名称<br/>
 * android.os.Build.DISPLAY：获取设备显示的版本包（在系统设置中显示为版本号）和ID一样<br/>
 * android.os.Build.FINGERPRINT：设备的唯一标识。由设备的多个信息拼接合成。<br/>
 * android.os.Build.HARDWARE：设备硬件名称,一般和基板名称一样（BOARD）<br/>
 * android.os.Build.HOST：设备主机地址<br/>
 * android.os.Build.ID:设备版本号。<br/>
 * android.os.Build.MODEL ：获取手机的型号 设备名称。<br/>
 * android.os.Build.MANUFACTURER:获取设备制造商<br/>
 * android:os.Build.PRODUCT：整个产品的名称<br/>
 * android:os.Build.RADIO：无线电固件版本号，通常是不可用的 显示unknown<br/>
 * android.os.Build.TAGS：设备标签。如release-keys 或测试的 test-keys<br/>
 * android.os.Build.TIME：时间<br/>
 * android.os.Build.TYPE:设备版本类型  主要为"user" 或"eng".<br/>
 * android.os.Build.USER:设备用户名 基本上都为android-build<br/>
 * android.os.Build.VERSION.RELEASE：获取系统版本字符串。如4.1.2 或2.2 或2.3等<br/>
 * android.os.Build.VERSION.CODENAME：设备当前的系统开发代号，一般使用REL代替<br/>
 * android.os.Build.VERSION.INCREMENTAL：系统源代码控制值，一个数字或者git hash值<br/>
 * android.os.Build.VERSION.SDK：系统的API级别 一般使用下面大的SDK_INT 来查看<br/>
 * android.os.Build.VERSION.SDK_INT：系统的API级别 数字表示<br/>
 * android.os.Build.VERSION_CODES类 中有所有的已公布的Android版本号。全部是Int常亮。可用于与SDK_INT进行比较来判断当前的系统版本<br/></strong>
 * <p/>
 * <p/>
 * Author: Eden <p/>
 * Date: 2014/11/19 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class HardwareUtils {

    private static final String TAG = "HardwareUtils";

    public static String getLocalMacAddressFromBusybox() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");

        //如果返回的result == null，则说明网络不可取
        if (result == null) {
            return "";
        }

        //对该行数据进行解析
        if (result.length() > 0 && result.contains("HWaddr")) {
            Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
            result = Mac;
        }
        return result;
    }

    private static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            //执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null && !line.contains(filter)) {
            }

            result = line;
            br.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * get mac address.
     *
     * @param context
     * @return if error, will return {@link #MAC_EMPTY}
     */
    public static String getMacAddr(Context context) {
        String mac = "";
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifi = wifiManager.getConnectionInfo();
            mac = wifi.getMacAddress();
        } catch (Exception e) {
            mac = null;
            LogManager.e(TAG, e.toString());
        }

        if (StringUtils.isNullOrEmpty(mac))
            mac = getLocalMacAddressFromBusybox();

        if (StringUtils.isNullOrEmpty(mac))
            mac = MAC_EMPTY;

        return mac;
    }

    /**
     * empty mac address value.
     */
    public final static String MAC_EMPTY = "00:00:00:00:00:00";

    /**
     * get status bar height in px.
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (context == null) return 0;
        Class<?> c = null;
        Object obj = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            LogManager.e(TAG, e1);
        }
        return statusBarHeight;
    }
}
