package com.project.barcode.mvp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.project.barcode.utils.DatabaseHelper;
import com.project.barcode.MainActivity;
import com.project.barcode.R;
import com.project.barcode.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter {
    private static final String TAG = MainPresenter.class.getName();
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mStorageDb;
    private Context mContext;
    private FragmentManager mFragmentManager;

    public MainPresenter(Context context) {
        mContext = context;
        mFragmentManager = ((MainActivity) context).getSupportFragmentManager();
    }

    public void updateFragment(Fragment fragment) {
        if (mFragmentManager == null) {
            Log.e(TAG, "mFragmentManager is null");
            return;
        }
        mFragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void createDb() {
        Log.i(TAG, "database create success");
        mDatabaseHelper = new DatabaseHelper(mContext, "Storage.db", null, 1);
        mStorageDb = mDatabaseHelper.getWritableDatabase();
    }

    public long getScanResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                String scanResultStr = intentResult.getContents();
                if (Utils.isNumeric(scanResultStr)) {
                    long scanCode = Long.parseLong(intentResult.getContents());
                    return scanCode;
                }
            }
        }
        return -1;
    }

    public AssetInfo getAssetInfo(long scanCode) {
        if (mStorageDb == null) {
            Log.e(TAG, "database create failed.");
            return null;
        }
        Cursor cursor = mStorageDb.query("Asset", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long code = cursor.getLong(cursor.getColumnIndex("code"));
                if (scanCode == code) {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String describe = cursor.getString(cursor.getColumnIndex("describe"));
                    AssetInfo info = new AssetInfo();
                    info.setAssetCode(code);
                    info.setAssetName(name);
                    info.setAssetDescribe(describe);
                    cursor.close();
                    return info;
                }
            } while (cursor.moveToNext());
        }
        return null;
    }

    public void insertAssetInfo(AssetInfo info) {
        if (mStorageDb == null) {
            Log.e(TAG, "database create failed.");
            return;
        }
        ContentValues values = new ContentValues();
        values.put("code", info.getAssetCode());
        values.put("name", info.getAssetName());
        values.put("describe", info.getAssetDescribe());
        mStorageDb.insert("Asset", null, values);
        values.clear();
    }

    public List<AssetInfo> keyWordSearchInDb(String keyword) {
        if (mStorageDb == null) {
            Log.e(TAG, "database create failed.");
            return null;
        }
        List<AssetInfo> searchList = new ArrayList<>();
        if(keyword.equals("")){
            return searchList;
        }
        Cursor cursor = mStorageDb.query("Asset", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long code = cursor.getLong(cursor.getColumnIndex("code"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String describe = cursor.getString(cursor.getColumnIndex("describe"));
                if (fuzzySearch(name, keyword) || fuzzySearch(describe, keyword)) {
                    AssetInfo info = new AssetInfo();
                    info.setAssetCode(code);
                    info.setAssetName(name);
                    info.setAssetDescribe(describe);
                    searchList.add(info);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return searchList;
    }

    private boolean fuzzySearch(String keyword, String searchWord) {

        int index = keyword.indexOf(searchWord);
        return index != -1;
    }
}
