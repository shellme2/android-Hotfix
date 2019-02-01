package com.eebbk.bfc.hotfix.tinker;

import android.content.Context;

import com.eebbk.bfc.hotfix.tinker.ex.TinkerLoadReporter;
import com.eebbk.bfc.hotfix.tinker.ex.TinkerPatchListener;
import com.eebbk.bfc.hotfix.tinker.ex.TinkerPatchReporter;
import com.eebbk.bfc.hotfix.tinker.ex.TinkerPatchResultService;
import com.eebbk.bfc.hotfix.tinker.ex.TinkerPatchUpgradeProcessor;
import com.tencent.tinker.lib.listener.PatchListener;
import com.tencent.tinker.lib.patch.AbstractPatch;
import com.tencent.tinker.lib.reporter.LoadReporter;
import com.tencent.tinker.lib.reporter.PatchReporter;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.ApplicationLike;

/**
 * Created by Simon on 2017/1/19.
 */

public class TinkerManager {
    public static void init(Context context) {
        ApplicationLike applicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();

        LoadReporter loadReporter = new TinkerLoadReporter( context );

        PatchReporter patchReporter = new TinkerPatchReporter(context);

        PatchListener patchListener = new TinkerPatchListener(context);

        AbstractPatch abstractPatch = new TinkerPatchUpgradeProcessor();

        TinkerInstaller.install(applicationLike, loadReporter, patchReporter, patchListener, TinkerPatchResultService.class, abstractPatch);
    }
}
