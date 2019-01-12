package com.project.barcode.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.barcode.ImportFragment;

public abstract class BaseView extends Fragment {
    protected MainPresenter mPresenter;

    public void setPresenter(MainPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getViewId(), container, false);
        initView(view);
        return view;
    }
    protected abstract int getViewId();
    protected abstract void initView(View view);


}
