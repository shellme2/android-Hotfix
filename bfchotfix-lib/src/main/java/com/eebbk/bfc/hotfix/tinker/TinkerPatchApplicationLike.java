package com.eebbk.bfc.hotfix.tinker;

import android.app.Application;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.tencent.tinker.loader.app.ApplicationLike;
import com.tencent.tinker.loader.app.DefaultApplicationLike;

/**
 * Created by Simon on 2017/1/16.
 */

public class TinkerPatchApplicationLike extends DefaultApplicationLike {
    private static ApplicationLike tinkerPatchApplicationLike;

    public TinkerPatchApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent, Resources[] resources, ClassLoader[] classLoader, AssetManager[] assetManager) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent, resources, classLoader, assetManager);
        setTinkerPatchApplicationLike(this);
    }

    private static void setTinkerPatchApplicationLike(ApplicationLike var0) {
        tinkerPatchApplicationLike = var0;
    }

    public static ApplicationLike getTinkerPatchApplicationLike() {
        return tinkerPatchApplicationLike;
    }
}
