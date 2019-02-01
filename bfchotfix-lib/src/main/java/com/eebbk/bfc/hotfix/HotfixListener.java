package com.eebbk.bfc.hotfix;

/**
 * Created by Simon on 2017/1/20.
 */

import android.content.Intent;

/**
 * 分2个过程, 一个是 获取补丁过程, 一个是加载补丁过程, 到时候看需不需要分开吧
 */
public interface HotfixListener {
    /**
     * 打补丁的服务启动时调用
     */
    void onPatchServiceStart(Intent intent);

    /**
     * 打补丁成功
     */
    void onPatchSuccess();



    //------------------- 下面是加载过程的回调 ----------------------

    /**
     * 加载补丁成功
     */
    void onLoadSuccess();

}
