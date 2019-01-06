/*
 * Copyright (c) 2019. ZhengZhengShi QiuJiaJun All rights reserved.
 */

package com.project.barcode;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
        mMainPresenter.updateFragment(new ScanFragment());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        long scanCode = mMainPresenter.getScanResult(requestCode, resultCode, data);
        if (scanCode < 0) {
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
