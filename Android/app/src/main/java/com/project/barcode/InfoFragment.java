package com.project.barcode;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.project.barcode.mvp.AssetInfo;
import com.project.barcode.mvp.BaseView;

public class InfoFragment extends BaseView {
    private TextView mAssetCode;
    private TextView mAssetName;
    private TextView mAssetDescribe;

    public static InfoFragment newInstance(long scanCode) {
        InfoFragment newFragment = new InfoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("scancode", scanCode);
        newFragment.setArguments(bundle);
        return newFragment;
    }
    @Override
    protected int getViewId() {
        return R.layout.info_fragment;
    }

    @Override
    protected void initView(View view) {
        mAssetCode = view.findViewById(R.id.asset_code);
        mAssetName = view.findViewById(R.id.asset_name);
        mAssetDescribe = view.findViewById(R.id.asset_describe);
        long scanCode = getArguments().getLong("scancode");
        AssetInfo info = mPresenter.getAssetInfo(scanCode);
        displayAssetInfo(info);
    }

    private void displayAssetInfo(AssetInfo info) {
        mAssetCode.setText(info.getAssetCode().toString());
        mAssetName.setText(info.getAssetName());
        mAssetDescribe.setText(info.getAssetDescribe());
    }

}
