/*
 * Copyright (c) 2019. ZhengZhengShi QiuJiaJun All rights reserved.
 */

package com.project.barcode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class InfoActivity extends AppCompatActivity {
    private TextView mInfoTextView;
    private Button mStartScanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mStartScanBtn = findViewById(R.id.start_scan_btn);
        mInfoTextView = findViewById(R.id.scan_result_text);
        mStartScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(InfoActivity.this).setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                        .setPrompt("请对准二维码")
                        .setCameraId(0)
                        .setBeepEnabled(true)
                        .initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                String result = intentResult.getContents();//返回值
                mInfoTextView.setText("扫码结果：" + result);
            }
        }
    }
}
