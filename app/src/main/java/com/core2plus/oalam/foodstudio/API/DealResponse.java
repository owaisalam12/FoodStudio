package com.core2plus.oalam.foodstudio.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DealResponse {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("Deals")
    @Expose
    private List<Deal> deals = null;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public List<Deal> getDeals() {
        return deals;
    }

    public void setDeals(List<Deal> deals) {
        this.deals = deals;
    }
}
