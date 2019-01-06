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
import com.project.barcode.DatabaseHelper;
import com.project.barcode.ImportFragment;
import com.project.barcode.InfoFragment;
import com.project.barcode.MainActivity;
import com.project.barcode.R;
import com.project.barcode.ScanFragment;

public class MainPresenter implements Contract.Presenter {
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
        if(mFragmentManager==null){
            Log.e(TAG,"mFragmentManager is null");
            return;
        }
        mFragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
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
                long scanCode = Long.parseLong(intentResult.getContents());
                return scanCode;
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
}
