package com.project.barcode;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.barcode.mvp.AssetInfo;
import com.project.barcode.mvp.Contract;
import com.project.barcode.mvp.MainPresenter;

public class ImportFragment extends Fragment implements Contract.View {
    private TextView mAssetCode;
    private EditText mAssetName;
    private EditText mAssetDescribe;
    private Button mImportConfirm;
    private Button mImportCancel;
    private MainPresenter mPresenter;

    public static ImportFragment newInstance(long scanCode) {
        ImportFragment newFragment = new ImportFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("scancode", scanCode);
        newFragment.setArguments(bundle);
        return newFragment;
    }


    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.mPresenter = (MainPresenter) presenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
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
                mPresenter.updateFragment(new ScanFragment());
                Toast.makeText(getContext(),"入库成功",Toast.LENGTH_SHORT).show();
            }
        });
        mImportCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.updateFragment(new ScanFragment());
                Toast.makeText(getContext(),"取消入库",Toast.LENGTH_SHORT).show();
            }
        });
    }


}
