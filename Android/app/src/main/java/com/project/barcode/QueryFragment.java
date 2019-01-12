package com.project.barcode;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.barcode.mvp.AssetInfo;
import com.project.barcode.mvp.BaseView;

import java.util.ArrayList;
import java.util.List;

public class QueryFragment extends BaseView {
    private SearchView mSearchView;
    private ListView mSearchResultListView;
    private List<AssetInfo> mSearchList = new ArrayList<>();
    private SearchAdapter mSearcAdapter;

    public class SearchAdapter extends ArrayAdapter<AssetInfo> {
        private int mLayoutId;

        public SearchAdapter(Context context, int layoutId, List<AssetInfo> assetInfos) {
            super(context, layoutId, assetInfos);
            mLayoutId = layoutId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AssetInfo info = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(mLayoutId, parent, false);
            TextView codeText = view.findViewById(R.id.search_code);
            TextView nameText = view.findViewById(R.id.search_name);
            TextView describe = view.findViewById(R.id.search_describe);
            codeText.setText(info.getAssetCode().toString());
            nameText.setText(info.getAssetName());
            describe.setText(info.getAssetDescribe());
            return view;
        }
    }

    @Override
    protected int getViewId() {
        return R.layout.query_fragment;
    }

    @Override
    protected void initView(View view) {
        mSearchResultListView = view.findViewById(R.id.search_result_list);
        mSearcAdapter = new SearchAdapter(getContext(), R.layout.query_list_item, mSearchList);
        mSearchResultListView.setAdapter(mSearcAdapter);

        mSearchView = view.findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    Toast.makeText(getContext(), "查询内容不能为空！", Toast.LENGTH_SHORT).show();
                    return true;
                }
                mSearchList.clear();
                mSearchList = searchFromDb(query);
                if (mSearchList.size() == 0) {
                    Toast.makeText(getContext(), "查找的商品不在仓库中", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "查找成功", Toast.LENGTH_SHORT).show();
                }
                mSearcAdapter = new SearchAdapter(getContext(), R.layout.query_list_item, mSearchList);
                mSearcAdapter.notifyDataSetChanged();
                mSearchResultListView.setAdapter(mSearcAdapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mSearchList.clear();
                mSearchList = searchFromDb(query);
                mSearcAdapter = new SearchAdapter(getContext(), R.layout.query_list_item, mSearchList);
                mSearcAdapter.notifyDataSetChanged();
                mSearchResultListView.setAdapter(mSearcAdapter);
                return true;
            }
        });
    }

    private List<AssetInfo> searchFromDb(String keyword) {
        return mPresenter.keyWordSearchInDb(keyword);
    }
}
