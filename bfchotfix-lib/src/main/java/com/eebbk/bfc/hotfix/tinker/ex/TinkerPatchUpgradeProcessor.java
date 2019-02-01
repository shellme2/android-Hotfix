package com.eebbk.bfc.hotfix.tinker.ex;

import android.content.Context;

import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.service.PatchResult;

/**
 * Created by Simon on 2017/1/20.
 */

/**
 * 对patch文件, 具体处理的地方, 由 TinkerPatchService 调用
 */
public class TinkerPatchUpgradeProcessor extends UpgradePatch {

    /**
     * @param tempPatchPath  patch 文件路径
     * @param patchResult  补丁结果保存地方
     * @return
     */
    @Override
    public boolean tryPatch(Context context, String tempPatchPath, PatchResult patchResult) {
        return super.tryPatch(context, tempPatchPath, patchResult);
    }

}
