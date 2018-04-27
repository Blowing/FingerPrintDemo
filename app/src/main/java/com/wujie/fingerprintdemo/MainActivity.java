package com.wujie.fingerprintdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView resultTv;
    private Button cancelBtn;
    private Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        resultTv = (TextView) findViewById(R.id.tv_result);
        okBtn = (Button) findViewById(R.id.btn_ok);
        cancelBtn = (Button) findViewById(R.id.btn_cancel);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                cancelBtn.setEnabled(true);
                okBtn.setEnabled(false);
                break;
            case R.id.btn_cancel:
                okBtn.setEnabled(true);
                cancelBtn.setEnabled(false);
                break;
            default:
                break;
        }
    }
}
