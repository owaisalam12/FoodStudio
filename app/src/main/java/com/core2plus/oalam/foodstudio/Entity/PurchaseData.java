package com.core2plus.oalam.foodstudio.Entity;

public class PurchaseData {
    private String userid;
    private String purchaseItem;
    private String purchaseTime;
    private String blockTime;

    public PurchaseData(String userid, String purchaseItem, String purchaseTime, String blockTime) {
        this.userid = userid;
        this.purchaseItem = purchaseItem;
        this.purchaseTime = purchaseTime;
        this.blockTime = blockTime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPurchaseItem() {
        return purchaseItem;
    }

    public void setPurchaseItem(String purchaseItem) {
        this.purchaseItem = purchaseItem;
    }

    public String getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(String purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(String blockTime) {
        this.blockTime = blockTime;
    }
}
