package com.eebbk.bfc.hotfix.tinker.ex;

import android.content.Context;

import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.listener.PatchListener;

/**
 * Created by Simon on 2017/1/20.
 */

/**
 * 收到patch后调用的,  用于patch文件校验, 然后调用调用TinkerPatchService去解压patch文件;
 * TinkerInstaller.onReceiveUpgradePatch 最后会调用到此方法中间
 */
public class TinkerPatchListener extends DefaultPatchListener {
    public TinkerPatchListener(Context context) {
        super(context);
    }

    @Override
    public int onPatchReceived(String path) {
        // 需要调用 TinkerPatchService.runPatchService(context, path), 启动服务, 后台去解压patch文件
        return super.onPatchReceived(path);
    }

    @Override
    protected int patchCheck(String path) {
        return super.patchCheck(path);
    }
}
