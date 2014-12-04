package com.github.lisicnu.libDroid.util;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class SqliteUtils {
    /**
     * 转换sqlite中特殊的的查询字符[特殊字符]
     *
     * @param para
     * @return
     */
    public static synchronized String paramParse(String para) {
        if (para == null || para.isEmpty()) {
            return para;
        }

        para = para.replace("/", "//");
        para = para.replace("'", "''");
        para = para.replace("[", "/[");
        para = para.replace("]", "/]");
        para = para.replace("%", "/%");
        para = para.replace("&", "/&");
        para = para.replace("_", "/_");
        para = para.replace("(", "/(");
        para = para.replace(")", "/)");

        return para;
    }

}
