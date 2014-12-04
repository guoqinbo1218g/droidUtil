package com.github.lisicnu.libDroid.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.webkit.WebView;

import java.util.List;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class FlashPluginHelper {

    public static boolean isPluginExist(Context context) {
        if (context == null) {
            return false;
        }

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> infoList = pm
                .getInstalledPackages(PackageManager.GET_SERVICES);
        for (PackageInfo info : infoList) {
            if ("com.adobe.flashplayer".equals(info.packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check whether the flash can play or not.
     *
     * @param context
     * @param mWebView
     * @param showInstall show install page or not.
     * @return return  the plugin exist or not.
     */
    public static boolean check(Context context, WebView mWebView, boolean showInstall) {
        if (context == null) return false;

        boolean flashPluginExist = isPluginExist(context);

        if (mWebView != null) {
            if (!flashPluginExist && showInstall) {
                mWebView.addJavascriptInterface(new AndroidBridge(context), "android");
                String content = "<html>\n" +
                        "<head></head>\n" +
                        "<body>\n" +
                        "<br/><br/>\n" +
                        "\n" +
                        "<h3>Sorry, We have found that you haven't installed adobe flash player's plugin!</h3>\n" +
                        "\n" +
                        "<p>\n" +
                        "<h4>\n" +
                        "    You can click <a href=\"#\" onclick=\"window.android.goMarket()\"> <b> here</b></a> to\n" +
                        "    install it.</h4>\n" +
                        "</p>\n" +
                        "</body>\n" +
                        "</html>\n";
                mWebView.loadData(content, "text/html", "UTF-8");
            }
        }

        return flashPluginExist;
    }

    private static class AndroidBridge {
        Context mContext;
        android.os.Handler handler;

        AndroidBridge(Context context) {
            this.mContext = context;
            handler = new android.os.Handler(mContext.getMainLooper());
        }

        public void goMarket() {

            handler.post(new Runnable() {
                public void run() {
                    Intent installIntent = new Intent("android.intent.action.VIEW");
                    installIntent.setData(Uri.parse("market://details?id=com.adobe.flashplayer"));
                    mContext.startActivity(installIntent);
                }
            });
        }
    }
}
