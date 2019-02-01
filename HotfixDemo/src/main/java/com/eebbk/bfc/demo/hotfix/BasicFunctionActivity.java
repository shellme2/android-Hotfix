package com.eebbk.bfc.demo.hotfix;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.eebbk.bfc.hotfix.HotfixManager;

import java.io.File;

import butterknife.OnClick;

public class BasicFunctionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_function);
    }

    @OnClick(R.id.btn_load_patch)
    public void loadPatch(){
        String patchPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk";
        if (!new File(patchPath).exists()){
            Log.w("zy", "patch not found: " + patchPath);
        }
        HotfixManager.getInstance().loadPatch(patchPath);
    }

    @OnClick(R.id.btn_clear_patch)
    public void clearPatch(){
        HotfixManager.getInstance().clearPatch();
    }

}
