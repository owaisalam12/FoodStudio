package com.core2plus.oalam.foodstudio.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Deal {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("deals_name")
    @Expose
    private String dealsName;
    @SerializedName("no_of_items")
    @Expose
    private String noOfItems;
    @SerializedName("img")
    @Expose
    private String img;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDealsName() {
        return dealsName;
    }

    public void setDealsName(String dealsName) {
        this.dealsName = dealsName;
    }

    public String getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(String noOfItems) {
        this.noOfItems = noOfItems;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

}
