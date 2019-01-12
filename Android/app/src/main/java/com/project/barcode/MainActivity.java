/*
 * Copyright (c) 2019. ZhengZhengShi QiuJiaJun All rights reserved.
 */

package com.project.barcode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.project.barcode.mvp.AssetInfo;
import com.project.barcode.mvp.MainPresenter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainPresenter = new MainPresenter(this);
        mMainPresenter.createDb();
        MainFragment mainFragment = new MainFragment();
        mainFragment.setPresenter(mMainPresenter);
        mMainPresenter.updateFragment(mainFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        long scanCode = mMainPresenter.getScanResult(requestCode, resultCode, data);
        if (scanCode < 0) {
            Toast.makeText(this, "条形码错误,请重新扫描", Toast.LENGTH_LONG).show();
            return;
        }
        AssetInfo info = mMainPresenter.getAssetInfo(scanCode);
        if (info == null) {
            Log.i(TAG, "The asset don't exist in database");
            ImportFragment importFragment = ImportFragment.newInstance(scanCode);
            importFragment.setPresenter(mMainPresenter);
            mMainPresenter.updateFragment(importFragment);
        } else {
            Log.i(TAG, "The asset exists in database");
            InfoFragment infoFragment = InfoFragment.newInstance(scanCode);
            infoFragment.setPresenter(mMainPresenter);
            mMainPresenter.updateFragment(infoFragment);
        }
    }
}
