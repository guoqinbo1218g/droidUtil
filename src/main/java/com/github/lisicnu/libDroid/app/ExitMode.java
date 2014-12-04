package com.github.lisicnu.libDroid.app;

import android.app.Activity;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public enum ExitMode {
    /**
     * default one click will exit.
     */
    Default,
    /**
     * Continuous double click.
     */
    DoubleClick,
    /**
     * dialog alert exit or not.
     */
    Dialog;

    public void setExitMode(Activity activity, ExitMode mode) {

    }
}

