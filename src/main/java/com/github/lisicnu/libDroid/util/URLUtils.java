package com.github.lisicnu.libDroid.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class URLUtils {
//    public final static String ACCEPT_IMAGE = "image/gif, image/jpeg, image/pjpeg, image/pjpeg";

    public static String analysisFileName(String url) {
        if (StringUtils.isNullOrEmpty(url))
            return url;

        String result = url;
        int idx = url.lastIndexOf("/");
        int idx2 = url.lastIndexOf("?");
        if (idx != -1) {
            if (idx2 != -1) {
                result = url.substring(idx + 1, idx2);
            } else {
                result = url.substring(idx + 1);
            }
        } else {
            if (idx2 != -1) {
                result = url.substring(0, idx2);
            }
        }
        return result;
    }

    /**
     * 根据传入的字符串确定是否需要进行转码编译
     */
    public static String formatUrl(String url) {

        if (MiscUtils.findChinese(url)) {

            @SuppressWarnings("deprecation")
            String tmp = URLDecoder.decode(url);
            tmp = encodeGB(tmp);

            return tmp;
        } else {
            return url;
        }
    }

    private static String encodeGB(String string) {
        // 转换中文编码
        try {
            URI uri = new URI(string);

            return uri.toASCIIString();
        } catch (URISyntaxException e1) {

        }

        if (StringUtils.isNullOrEmpty(string)) {
            return string;
        }

        String prefix = "";
        int idx = string.indexOf("//");
        if (idx > 0) {
            prefix = string.substring(0, idx + 2);
            string = string.substring(idx + 2);
        }
        String split[] = string.split("/");
        String tmp = "";
        for (int i = 0; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "gb2312");
            } catch (Exception e) {
                // TODO: handle exception
            }
            tmp = tmp + "/" + split[i];
        }
        tmp = prefix + tmp;
        tmp = tmp.replaceAll("\\+", "%20").replace("///", "//");// 处理空格
        return tmp;
    }

//
//    /**
//     * 获取默认的http连接信息
//     */
//    public static HttpURLConnection getNormalCon(String url) throws MalformedURLException,
//            IOException {
//
//        return getNormalCon(url, false);
//    }
//
//    public static HttpURLConnection getNormalCon(String url, boolean usePost)
//            throws MalformedURLException, IOException {
//
//        HttpURLConnection conn = null;
//
//        conn = (HttpURLConnection) (new URL(url)).openConnection();
//        conn.setConnectTimeout(10 * 1000);
//        conn.setReadTimeout(30 * 1000);
//        conn.setRequestMethod(usePost ? "POST" : "GET");
//        conn.setUseCaches(false);
//        conn.setRequestProperty("Accept", "*/*");
//        // conn.setRequestProperty(
//        // "Accept",
//        // "image/gif, image/jpeg, image/pjpeg, image/pjpeg,
//        // application/x-shockwave-flash, application/xaml+xml,
//        // application/vnd.ms-xpsdocument,
//        // application/x-ms-xbap, application/x-ms-application,
//        // application/vnd.ms-excel,
//        // application/vnd.ms-powerpoint, application/msword, */*");
//        // conn.setRequestProperty("Accept-Language", "zh-CN,en-US");
////        conn.setRequestProperty("Referer", url);
//        conn.setRequestProperty("Charset", "UTF-8");
//        conn.setRequestProperty(
//                "User-Agent",
//                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
//
//        return conn;
//    }
//
//    /**
//     * 获取当前 URL 地址是否能够联通
//     *
//     * @param url
//     * @return
//     */
//    public static synchronized boolean canConnect(String url) {
//        boolean result = false;
//        HttpURLConnection conn = null;
//        try {
//            conn = getNormalCon(formatUrl(url));
//            conn.connect();
//
//            int resultCode = conn.getResponseCode();
//            result = (200 == resultCode);
//
//                /*
//            if (!result) {
//                Log.d("canConnect", resultCode + "|" + url);
//            }
//            // */
//
//        } catch (IOException e) {
//            result = false;
////            Log.e("canConnect", url + "|error:" + e.toString());
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//
//        return result;
//    }

}
