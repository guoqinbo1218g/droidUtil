package com.github.lisicnu.libDroid.util;

import com.github.lisicnu.log4android.LogManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class ArrayUtils {

    public static <T> List<T> asList(T... array) {
        if (array == null)
            return new ArrayList<T>();

        List<T> result = new ArrayList<T>();
        Collections.addAll(result, array);

        return result;
    }

}
