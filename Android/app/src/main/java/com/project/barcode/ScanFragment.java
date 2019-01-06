package com.project.barcode;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;

public class ScanFragment extends Fragment {
    private Button mStartScanBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scan_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mStartScanBtn = view.findViewById(R.id.start_scan_btn);
        mStartScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(getActivity()).setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                        .setPrompt("请对准二维码")
                        .setCameraId(0)
                        .setBeepEnabled(true)
                        .initiateScan();
            }
        });
    }
}
