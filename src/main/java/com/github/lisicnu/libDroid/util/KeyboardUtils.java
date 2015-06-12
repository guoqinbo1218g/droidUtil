package com.github.lisicnu.libDroid.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2015/5/21 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class KeyboardUtils {
    /**
     *
     * @param mEditText
     * @param mContext
     */
    public static void openKeyboard(EditText mEditText, Context mContext) {
        if (mContext == null || mEditText == null) return;
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     *
     * @param mEditText
     * @param mContext
     */
    public static void closeKeyboard(EditText mEditText, Context mContext) {
        if (mContext == null || mEditText == null) return;
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
}
