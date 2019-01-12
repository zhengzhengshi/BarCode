package com.project.barcode;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.barcode.mvp.AssetInfo;
import com.project.barcode.mvp.BaseView;

public class ImportFragment extends BaseView {
    private TextView mAssetCode;
    private EditText mAssetName;
    private EditText mAssetDescribe;
    private Button mImportConfirm;
    private Button mImportCancel;

    public static ImportFragment newInstance(long scanCode) {
        ImportFragment newFragment = new ImportFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("scancode", scanCode);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    protected int getViewId() {
        return R.layout.import_fragment;
    }

    @Override
    protected void initView(View view) {
        mAssetCode = view.findViewById(R.id.asset_code);
        mAssetName = view.findViewById(R.id.asset_name);
        mAssetDescribe = view.findViewById(R.id.asset_describe);
        mImportConfirm = view.findViewById(R.id.import_confirm);
        mImportCancel = view.findViewById(R.id.import_cancel);
        final Long assetCode = getArguments().getLong("scancode");
        mAssetCode.setText(assetCode.toString());
        mImportConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssetInfo info = new AssetInfo();
                info.setAssetCode(assetCode);
                info.setAssetName(mAssetName.getText().toString());
                info.setAssetDescribe(mAssetDescribe.getText().toString());
                mPresenter.insertAssetInfo(info);
                mPresenter.updateFragment(new MainFragment());
                Toast.makeText(getContext(), "入库成功", Toast.LENGTH_SHORT).show();
            }
        });
        mImportCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.updateFragment(new MainFragment());
                Toast.makeText(getContext(), "取消入库", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_fragment, container, false);
        initView(view);
        return view;
    }
}
