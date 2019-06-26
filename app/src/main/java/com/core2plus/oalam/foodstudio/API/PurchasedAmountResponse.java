package com.core2plus.oalam.foodstudio.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PurchasedAmountResponse {
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("PurchasedAmount")
    @Expose
    private String purchasedAmount;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPurchasedAmount() {
        return purchasedAmount;
    }

    public void setPurchasedAmount(String purchasedAmount) {
        this.purchasedAmount = purchasedAmount;
    }
}
