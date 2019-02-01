package com.eebbk.bfc.hotfix.util;

import android.util.Log;

/**
 * Created by Simon on 2017/1/17.
 */

public class HotfixLog {

    private static ILog sLog = new ILog() {
        @Override
        public void e(String tag, Throwable e, String message) {
            Log.e(tag, message, e);
        }

        @Override
        public void i(String tag, String msg) {
            Log.i(tag, msg);
        }
    };

    public static void setLog(ILog log) {
        sLog = log;
    }

    public static void e(String tag, Throwable e, String message) {
        if (sLog != null) {
            sLog.e(tag, e, message);
        }
    }

    public static void i(String tag, String msg) {
        if (sLog != null) {
            sLog.i(tag, msg);
        }
    }


    /**
     * hotfix 内部使用的接口
     */
    public interface ILog {
        void e(String tag, Throwable e, String message);

        void i(String tag, String msg);
    }

}

