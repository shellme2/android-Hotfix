package com.eebbk.bfc.hotfix;

import android.content.Context;

import com.eebbk.bfc.hotfix.tinker.TinkerManager;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

/**
 * Created by Simon on 2017/1/19.
 */

public class HotfixManager {
    private static HotfixManager sInstance;
    Context mContext;

//    private static HotfixListener sHotfixListener = new DefaultHotfixListener();
    private static HotfixListener sHotfixListener;

    public static HotfixManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("you must init HotfixManger before you get");
        }
        return sInstance;
    }

    public static HotfixListener getHotfixListener(){
        return sHotfixListener;
    }

    public HotfixManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static void init(Context context) {
        if (sInstance != null) return;
        TinkerManager.init(context);
        sInstance = new HotfixManager(context);
    }


    public void loadPatch(String patchPath) {
        TinkerInstaller.onReceiveUpgradePatch(mContext, patchPath);
    }

    public boolean isPatchLoaded() {
        return Tinker.with(mContext).isTinkerLoaded();
    }

    /**
     * 清楚补丁
     */
    public void clearPatch() {
        Tinker.with(mContext).cleanPatch();
    }

    public void killProcess() {
        ShareTinkerInternals.killAllOtherProcess(mContext);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
