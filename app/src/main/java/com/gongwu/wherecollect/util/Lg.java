package com.gongwu.wherecollect.util;

import android.util.Log;

import com.gongwu.wherecollect.BuildConfig;


public class Lg {
    public static final boolean DEBUG = BuildConfig.LOGSHOW;

    private Lg() {}

    public static Lg getInstance() {
        return Holder.INSTANCE;
    }

    public void d(final String tag, final String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }

    }

    public void i(final String tag, final String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }

    }

    public void w(final String tag, final String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }

    }

    public void e(final String tag, final String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }

    }

    public void e(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.e(tag, msg, tr);
        }
    }

    private static class Holder {
        private static final Lg INSTANCE = new Lg();
    }
}
