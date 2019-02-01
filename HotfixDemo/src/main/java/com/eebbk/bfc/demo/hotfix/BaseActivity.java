package com.eebbk.bfc.demo.hotfix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by Simon on 2017/1/20.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    void startActivity(Class clazz){
        startActivity(new Intent(this, clazz));
    }
}
