package com.github.lisicnu.libDroid.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class EncryptUtils {

    /**
     * @param text
     * @param charset empty means default charset.
     * @return
     */
    public static String md5(String text, String charset) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();

            digest.update(StringUtils.isNullOrEmpty(charset)
                            ? text.getBytes()
                            : text.getBytes(charset)
            );

            byte[] bt = digest.digest();
            StringBuffer sb = new StringBuffer();
            String temp = "";
            for (byte b : bt) {
                temp = Integer.toHexString(0xFF & b);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                sb.append(temp);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param text
     * @param charset empty means default charset.
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String SHA1(String text, String charset) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {

        MessageDigest md = MessageDigest.getInstance("SHA-1");

        md.update(StringUtils.isNullOrEmpty(charset)
                ? text.getBytes()
                : text.getBytes(charset), 0, text.length());

        byte[] sha1hash = md.digest();

        StringBuilder buf = new StringBuilder();
        for (byte b : sha1hash) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
