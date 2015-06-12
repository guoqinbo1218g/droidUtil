package com.github.lisicnu.libDroid.util;

import android.util.Log;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class NumberUtils {

    /**
     * 方法位置为 [x,y]
     *
     * @param x
     * @param min
     * @param max
     * @return 在范围内则返回true
     * @throws Exception 下限应当小于上限
     */
    public static boolean isInRange(float x, float min, float max) {
        return x >= min && x <= max;
    }

    private static String[] numberCHineseChar = new String[]{"一", "二", "三",
            "四", "五", "六", "七", "八", "九", "十"};

    /**
     * 只能转换 [0,9] 对应汉字为 [一, 十]
     *
     * @param number 从0开始
     * @return
     */
    public static String getChineseChar(int number) {
        if (number < 0 || number > 9)
            return null;
        return numberCHineseChar[number];
    }

    /**
     * 可检测 正数,负数,小数 {10进制}, 16进制数字.{0x}
     *
     * @param obj
     * @return
     */
    public static boolean isNumberic(Object obj) {
        if (obj == null) {
            Log.e("isNumeric", "parameter can't be null.");
            return false;
        }

        try {
            Double.parseDouble(obj.toString());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean isInt(Object obj) {
        if (obj == null) {
            Log.e("isNumeric", "parameter can't be null.");
            return false;
        }

        String str = obj.toString().toLowerCase();

        if (str.startsWith("+")) {
            str = str.substring(1);
        }

        try {
            Integer.decode(str);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 获取多个数字最大公约数
     *
     * @param arg
     * @return
     * @throws Exception
     */
    public static int getGCD(int... arg) throws Exception {
        if (arg == null || arg.length == 0) {
            throw new Exception("parameters must more than one.");
        }
        if (arg.length == 1) {
            return Math.abs(arg[0]);
        }

        int tmp = gcd(arg[0], arg[1]);

        for (int i = 2; i < arg.length; i++) {
            tmp = gcd(tmp, arg[i]);
        }

        return tmp;
    }

    /**
     * 欧几里得 求最大公约数算法
     */
    private static int gcd(int m, int n) {
        return m % n == 0 ? n : gcd(n, m % n);
    }

    /**
     * 最小公倍数
     */
    private static int lcm(int m, int n) throws Exception {
        return m * n / getGCD(m, n);
    }

    /**
     * 获取多个数字的最小公倍数
     *
     * @param arg
     * @return
     * @throws Exception
     */
    public static int getLCM(int... arg) throws Exception {
        if (arg == null || arg.length == 0) {
            throw new Exception("parameters must more than one.");
        }
        if (arg.length == 1)
            return arg[0];

        int tmp = lcm(arg[0], arg[1]);

        for (int i = 2; i < arg.length; i++) {
            tmp = lcm(tmp, arg[i]);
        }

        return tmp;
    }

}
