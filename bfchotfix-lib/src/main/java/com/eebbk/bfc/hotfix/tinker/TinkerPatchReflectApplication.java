package com.eebbk.bfc.hotfix.tinker;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.eebbk.bfc.hotfix.util.HotfixLog;
import com.eebbk.bfc.hotfix.util.ReflectUtil;
import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 2016/12/31.
 */

public class TinkerPatchReflectApplication extends TinkerApplication {
    private static final String TAG = "zy.TinkerPatchReflectApplication";
    private static final String MATA_KEY_ORIGINAL_APPLICATION_NAME = "TINKER_PATCH_APPLICATION";

    private String mOriginalApplicationClassName;
    private Application mOriginalApplication;
    private boolean mIsReflectApplicationError;

    public TinkerPatchReflectApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.eebbk.bfc.hotfix.tinker.TinkerPatchApplicationLike", "com.tencent.tinker.loader.TinkerLoader", false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // 获取app本身的Application
        final String originalApplicationClassName = this.getOriginalApplicationClassName(this);
        if (originalApplicationClassName == null) {
            throw new IllegalStateException("can't get real application class name from manifest");
        }

        // 构建app原始的Application, 并调用 attachBaseContext方法, 对其初始化
        try {
            final Constructor applicationConstructor = Class.forName(originalApplicationClassName).getConstructor(new Class[0]);
            this.mOriginalApplication = (Application) applicationConstructor.newInstance(new Object[0]);
        } catch (Exception e) {
            throw new IllegalStateException("construct original application failed", e);
        }

        try {
            final Method attachBaseContextMethod = ContextWrapper.class.getDeclaredMethod("attachBaseContext", new Class[]{Context.class});
//            final Method attachBaseContextMethod = this.mOriginalApplication.getClass().getDeclaredMethod("attachBaseContext", new Class[]{Context.class});
            attachBaseContextMethod.setAccessible(true);
            attachBaseContextMethod.invoke(this.mOriginalApplication, base);
        } catch (Exception e) {
            throw new IllegalStateException("invoke original application attachBaseContext method failed!", e);
        }
    }

    @Override
    public void onCreate() {
        if (this.mOriginalApplication != null) {
            try {
                Class activityThreadClass = Class.forName("android.app.ActivityThread");
                Object activityThreadObject = ReflectUtil.getActivityThread(this, activityThreadClass);

                // 更换ActivityThread的 mInitialApplication, 把它换成原先的 Application
                // mInitialApplication是app对应Application, ActivityThread操作app, 需要Context对象的话, 都是使用它
                final Field initialApplicationField = activityThreadClass.getDeclaredField("mInitialApplication");
                initialApplicationField.setAccessible(true);
                Application application = (Application) initialApplicationField.get(activityThreadObject);
                if (this.mOriginalApplication != null && application == this) {
                    initialApplicationField.set(activityThreadObject, this.mOriginalApplication);
                }

                // 更换 ActivityThread中的 mAllApplications 中, 每个Application的数据
                if (this.mOriginalApplication != null) {
                    final Field allApplicationField = activityThreadClass.getDeclaredField("mAllApplications");
                    allApplicationField.setAccessible(true);
                    List allApplicationsObject = (List) allApplicationField.get(activityThreadObject);

                    final int N = allApplicationsObject.size();
                    for (int i = 0; i < N; i++) {
                        if (allApplicationsObject.get(i) == this) {
                            allApplicationsObject.set(i, this.mOriginalApplication);
                        }
                    }
                }

                // 获取兼容的 loadapk 的 Field, 用于下面替换 LoadApk对象中的 mApplication 变量
                Class loadApkClass;
                try {
                    loadApkClass = Class.forName("android.app.LoadedApk");
                } catch (ClassNotFoundException e) {
                    loadApkClass = Class.forName("android.app.ActivityThread$PackageInfo");
                }

                final Field applicatonFieldInLoadApk = loadApkClass.getDeclaredField("mApplication");
                applicatonFieldInLoadApk.setAccessible(true);

                // 获取 Application中的 mLoadedApk变量的Field, 下面用于替换原始 Application中的 mLoadApk 变量
                Field loadApkField = null;
                try {
                    loadApkField = Application.class.getDeclaredField("mLoadedApk");
                    loadApkField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                }

                // 替换 ActivityThread 中, mPackages mResourcePackages 变量中, loadApk 变量中的 mApplication 变量
                //  mPackages mResourcePackages 都是 ArrayMap<String, WeakReference<LoadedApk>> 类型
                String[] var7 = new String[]{"mPackages", "mResourcePackages"};
                for (int i = 0; i < 2; i++) {
                    final String fieldName = var7[i];
                    final Field field = activityThreadClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    // ArrayMap<String, WeakReference<LoadedApk>>
                    Map<String, WeakReference<Object>> map = (Map) field.get(activityThreadObject);
                    Iterator iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {
                        // loadApk
                        Object loadApkObject = ((WeakReference) ((Map.Entry) iterator.next()).getValue()).get();

                        // 替换每个 loadApk对象中的 mApplication 变量, 换成 this.mOriginalApplication
                        // this.mOriginalApplication 中的 mLoadedApk 变量, 替换为系统生成的
                        if (loadApkObject != null && applicatonFieldInLoadApk.get(loadApkObject) != this) {
                            if (this.mOriginalApplication != null) {
                                applicatonFieldInLoadApk.set(loadApkObject, this.mOriginalApplication);
                            }

                            if (this.mOriginalApplication != null && loadApkField != null) {
                                loadApkField.set(this.mOriginalApplication, loadApkObject);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                HotfixLog.e(TAG, e, "reflect application error");
                this.mIsReflectApplicationError = true;
            }
        }

        super.onCreate();
        if (this.mOriginalApplication != null) {
            this.mOriginalApplication.onCreate();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.onConfigurationChanged(newConfig);
        } else {
            super.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.onLowMemory();
        } else {
            super.onLowMemory();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTrimMemory(int level) {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.onTrimMemory(level);
        } else {
            super.onTrimMemory(level);
        }
    }

    @Override
    public void onTerminate() {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.onTerminate();
        } else {
            super.onTerminate();
        }
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver var1, IntentFilter var2) {
        return this.mIsReflectApplicationError && this.mOriginalApplication != null ? this.mOriginalApplication.registerReceiver(var1, var2) : super.registerReceiver(var1, var2);
    }

    public void unregisterReceiver(BroadcastReceiver var1) {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.unregisterReceiver(var1);
        } else {
            super.unregisterReceiver(var1);
        }
    }

    @Override
    public boolean bindService(Intent var1, ServiceConnection var2, int var3) {
        return this.mIsReflectApplicationError && this.mOriginalApplication != null ? this.mOriginalApplication.bindService(var1, var2, var3) : super.bindService(var1, var2, var3);
    }

    @Override
    public void unbindService(ServiceConnection var1) {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.unbindService(var1);
        } else {
            super.unbindService(var1);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void registerComponentCallbacks(ComponentCallbacks var1) {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.registerComponentCallbacks(var1);
        } else {
            super.registerComponentCallbacks(var1);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks var1) {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.unregisterComponentCallbacks(var1);
        } else {
            super.unregisterComponentCallbacks(var1);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks var1) {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.registerActivityLifecycleCallbacks(var1);
        } else {
            super.registerActivityLifecycleCallbacks(var1);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks var1) {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.unregisterActivityLifecycleCallbacks(var1);
        } else {
            super.unregisterActivityLifecycleCallbacks(var1);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void registerOnProvideAssistDataListener(OnProvideAssistDataListener var1) {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.registerOnProvideAssistDataListener(var1);
        } else {
            super.registerOnProvideAssistDataListener(var1);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener var1) {
        if (this.mIsReflectApplicationError && this.mOriginalApplication != null) {
            this.mOriginalApplication.unregisterOnProvideAssistDataListener(var1);
        } else {
            super.unregisterOnProvideAssistDataListener(var1);
        }
    }

    @Override
    public Resources getResources() {
        return this.mIsReflectApplicationError && this.mOriginalApplication != null ? this.mOriginalApplication.getResources() : super.getResources();
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.mIsReflectApplicationError && this.mOriginalApplication != null ? this.mOriginalApplication.getClassLoader() : super.getClassLoader();
    }

    @Override
    public AssetManager getAssets() {
        return this.mIsReflectApplicationError && this.mOriginalApplication != null ? this.mOriginalApplication.getAssets() : super.getAssets();
    }

    @Override
    public ContentResolver getContentResolver() {
        return this.mIsReflectApplicationError && this.mOriginalApplication != null ? this.mOriginalApplication.getContentResolver() : super.getContentResolver();
    }

    // 从Manifest中获取原先的Application名称, 在Manifest中用 meta-data保存的
    // <meta-data android:name="TINKER_PATCH_APPLICATION" android:value="com.tinkerpatch.easy_sample.SampleApplication"/>
    private String getOriginalApplicationClassName(Context context) {
        if (this.mOriginalApplicationClassName != null) {
            return this.mOriginalApplicationClassName;
        }

        try {
            Bundle mataData = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            final String originalApplication = mataData.getString(MATA_KEY_ORIGINAL_APPLICATION_NAME);

            if (!TextUtils.isEmpty(originalApplication)) {
                this.mOriginalApplicationClassName = originalApplication;
            }

        } catch (PackageManager.NameNotFoundException e) {
        }

        HotfixLog.i(TAG, "get original application class name: " + mOriginalApplication);

        return this.mOriginalApplicationClassName;
    }
}
