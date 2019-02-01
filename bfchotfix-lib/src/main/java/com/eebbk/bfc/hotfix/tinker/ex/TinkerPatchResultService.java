package com.eebbk.bfc.hotfix.tinker.ex;

import com.eebbk.bfc.hotfix.HotfixListener;
import com.eebbk.bfc.hotfix.HotfixManager;
import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;

/**
 * Created by Simon on 2017/1/20.
 */

/**
 * IntentService
 * 补丁完成后的回调, 一般需要重启进程, 方便补丁生效
 * 由 TinkerPatchService 调用
 */
public class TinkerPatchResultService extends DefaultTinkerResultService {

    @Override
    public void onPatchResult(PatchResult result) {
        if (result == null){
            return;
        }

        if (result.isSuccess){
            HotfixManager.getHotfixListener().onPatchSuccess();
        }
        super.onPatchResult(result);
    }
}
