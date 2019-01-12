package com.project.barcode;


import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.project.barcode.mvp.BaseView;

public class MainFragment extends BaseView implements View.OnClickListener {
    private Button mStartScanBtn;
    private Button mQueryBtn;
    private Button mAboutAuthor;

    @Override
    protected int getViewId() {
        return R.layout.main_fragment;
    }

    @Override
    protected void initView(View view) {
        mStartScanBtn = view.findViewById(R.id.start_scan_btn);
        mQueryBtn = view.findViewById(R.id.query_btn);
        mAboutAuthor = view.findViewById(R.id.about_author);
        mStartScanBtn.setOnClickListener(this);
        mQueryBtn.setOnClickListener(this);
        mAboutAuthor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_scan_btn:
                startScan();
                break;
            case R.id.query_btn:
                startQuery();
                break;
            case R.id.about_author:
                showAuthorInfo();
                break;
        }

    }

    private void startScan() {
        new IntentIntegrator(getActivity()).setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                .setPrompt("请对准二维码")
                .setCameraId(0)
                .setBeepEnabled(true)
                .initiateScan();
    }

    private void startQuery() {
        QueryFragment queryFragment = new QueryFragment();
        queryFragment.setPresenter(mPresenter);
        mPresenter.updateFragment(queryFragment);
    }
    private void showAuthorInfo(){
        AboutAuthorFragment aboutAuthorFragment = new AboutAuthorFragment();
        mPresenter.updateFragment(aboutAuthorFragment);
    }
}
