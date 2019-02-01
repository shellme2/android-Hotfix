package com.eebbk.bfc.demo.hotfix;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @OnClick(R.id.btn_basic_function)
    public void enterBasicFunctionAcitivty() {
        startActivity(BasicFunctionActivity.class);
    }


    @OnClick(R.id.btn_change)
    public void testPatch(Button view) {

        Toast.makeText(this, "i'm on base", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "i'm on patch", Toast.LENGTH_SHORT).show();
//        view.setText("变!!!");

    }
}
