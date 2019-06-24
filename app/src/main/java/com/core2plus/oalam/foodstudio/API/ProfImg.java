package com.core2plus.oalam.foodstudio.API;

import android.media.Image;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfImg {
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("img")
    @Expose
    private String img;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

}
