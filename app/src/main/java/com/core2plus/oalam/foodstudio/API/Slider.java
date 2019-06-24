package com.core2plus.oalam.foodstudio.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Slider {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("banners_name")
    @Expose
    private String bannersName;
    @SerializedName("img")
    @Expose
    private String img;
    @SerializedName("description")
    @Expose
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBannersName() {
        return bannersName;
    }

    public void setBannersName(String bannersName) {
        this.bannersName = bannersName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

