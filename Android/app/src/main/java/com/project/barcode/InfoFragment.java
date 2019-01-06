package com.project.barcode;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.barcode.mvp.AssetInfo;
import com.project.barcode.mvp.Contract;
import com.project.barcode.mvp.MainPresenter;

public class InfoFragment extends Fragment implements Contract.View {
    private TextView mAssetCode;
    private TextView mAssetName;
    private TextView mAssetDescribe;
    private MainPresenter mPresenter;

    @Override
    public void setPresenter(Contract.Presenter presenter) {
            this.mPresenter = (MainPresenter)presenter;
    }
    public static InfoFragment newInstance(long scanCode) {
        InfoFragment newFragment = new InfoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("scancode", scanCode);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view =inflater.inflate(R.layout.info_fragment,container,false);
        initView(view);
        return view;
    }
    private void initView(View view){
        mAssetCode = view.findViewById(R.id.asset_code);
        mAssetName = view.findViewById(R.id.asset_name);
        mAssetDescribe = view.findViewById(R.id.asset_describe);
        long scanCode=getArguments().getLong("scancode");
        AssetInfo info = mPresenter.getAssetInfo(scanCode);
        displayAssetInfo(info);
    }
    private void displayAssetInfo(AssetInfo info){
        mAssetCode.setText(info.getAssetCode().toString());
        mAssetName.setText(info.getAssetName());
        mAssetDescribe.setText(info.getAssetDescribe());
    }

}
