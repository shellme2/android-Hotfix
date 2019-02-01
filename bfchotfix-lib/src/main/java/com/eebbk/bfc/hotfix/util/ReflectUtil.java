package com.eebbk.bfc.hotfix.util;

import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Simon on 2017/1/16.
 */

public class ReflectUtil {

    /**
     * 获取ActivityThread
     *
     * @param context
     * @param clazz   ActivityThread.class
     */
    public static Object getActivityThread(Context context, Class<?> clazz) {
        try {
            if (clazz == null) {
                clazz = Class.forName("android.app.ActivityThread");
            }

            final Method currentActivityThreadMethod;
            (currentActivityThreadMethod = clazz.getMethod("currentActivityThread", new Class[0])).setAccessible(true);
            Object activityThread = currentActivityThreadMethod.invoke((Object) null, new Object[0]);

            // 如果为null, 从contextImpl, 或者Application 的 mLoadApk 中去获取,
            if (activityThread == null && context != null) {
                // 理论还要 context.getBase, 获取contextImpl的
                // mLoadedApk 只在 Application 和 ContextImpl 中有这个变量
                final Field loadApkField = context.getClass().getField("mLoadedApk");
                loadApkField.setAccessible(true);

                // loadApk
                Object loadApkObject = loadApkField.get(context);
                final Field loadApkActivityThreadField = loadApkObject.getClass().getDeclaredField("mActivityThread");
                loadApkActivityThreadField.setAccessible(true);
                activityThread = loadApkActivityThreadField.get(loadApkObject);
            }

            return activityThread;
        } catch (Throwable e) {
            return null;
        }
    }
}
