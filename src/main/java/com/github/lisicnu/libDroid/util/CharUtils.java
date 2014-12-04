package com.github.lisicnu.libDroid.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * byte , char 的相关操作都在里面
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class CharUtils {
    /**
     * 判断是否是英文字母 A-Z, a-z
     *
     * @param ch
     * @return
     */
    public static boolean isEnChar(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    public static char[] getChars(byte[] buffer, int offset, int length) {

        ByteBuffer bb = ByteBuffer.allocate(length);
        bb.put(buffer, offset, length);
        bb.flip();

        return Charset.defaultCharset().decode(bb).array();
    }

    public static byte[] getBytes(char[] buffer, int offset, int length) {

        CharBuffer cb = CharBuffer.allocate(length);
        cb.put(buffer, offset, length);
        cb.flip();

        return Charset.defaultCharset().encode(cb).array();
    }

    public static int byteToInt(byte b[], int i) {
        int a;
        a = (b[i] & 0xff) | ((b[i + 1] << 8) & 0xff00) | ((b[i + 2] << 16) & 0xff0000)
                | (b[i + 3] << 24);
        return a;
    }

    public static short byteToShort(byte b[], int i) {
        short a;
        a = (short) (b[i] & 0xff | ((b[i + 1] << 8) & 0xff00));
        return a;
    }

    public static int byteToChar(byte b) {
        return b & 0xff;
    }

    public static byte intToByte(int i) {
        if (i < Byte.MAX_VALUE && i > Byte.MIN_VALUE) {
            return (byte) i;
        }
        return (byte) i;
    }

    /**
     * 将 byte[] 数组转换成16进制字符串
     *
     * @param b
     * @return 传入参数为null, 将返回null
     */
    public static String toHexString(byte[] b) {
        if (b == null)
            return null;

        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
        }
        return sb.toString();
    }
}
