package com.core2plus.oalam.foodstudio.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Purchase {
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("raw_url")
    @Expose
    private String raw_url;
    @SerializedName("deal_name")
    @Expose
    private String deal_name;
    @SerializedName("img_url")
    @Expose
    private String imgUrl;
    @SerializedName("suggest_prices")
    @Expose
    private String suggest_prices;

    public String getRaw_url() {
        return raw_url;
    }

    public void setRaw_url(String raw_url) {
        this.raw_url = raw_url;
    }

    public String getDeal_name() {
        return deal_name;
    }

    public void setDeal_name(String deal_name) {
        this.deal_name = deal_name;
    }

    @SerializedName("purchaseTime")
    @Expose
    private String purchaseTime;

    public String getUserid() {
        return userid;
    }

    public String getSuggest_prices() {
        return suggest_prices;
    }

    public void setSuggest_prices(String suggest_prices) {
        this.suggest_prices = suggest_prices;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(String purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getURl() {

        String text = raw_url;
        String[] split = text.split(";");
        String[] arr = new String[3];
        for (int i = 0; i < split.length; i++) {
            arr[i] = split[i];
        }
        if (arr[0] != null) {
            //System.out.println(arr[1]);
            return arr[0];
        } else {
            return "Error occured!";
        }
    }

    public String getText() {

        String text = raw_url;
        String[] split = text.split(";");
        String[] arr = new String[3];
        for (int i = 0; i < split.length; i++) {
            arr[i] = split[i];
        }
        if (arr[1] != null) {
            //System.out.println(arr[1]);
            return arr[1];
        } else {
            return "Error occured!";
        }

    }

    public String getSuggestedPrice() {

        String text = raw_url;
        String[] split = text.split(";");
        String[] arr = new String[3];
        for (int i = 0; i < split.length; i++) {
            arr[i] = split[i];
        }
        if (arr[2] != null) {
            //System.out.println(arr[1]);
            return arr[2];
        } else {
            return "0";
        }

    }
}
