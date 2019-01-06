package com.project.barcode.mvp;

public class AssetInfo {
    private Long assetCode;
    private String assetName;
    private String assetDescribe;

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetDescribe() {
        return assetDescribe;
    }

    public void setAssetDescribe(String assetDescribe) {
        this.assetDescribe = assetDescribe;
    }

    public Long getAssetCode() {

        return assetCode;
    }

    public void setAssetCode(Long assetCode) {
        this.assetCode = assetCode;
    }
}
