package com.core2plus.oalam.foodstudio.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfImgResponse {
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("ProfImgs")
    @Expose
    private List<ProfImg> profImgs = null;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public List<ProfImg> getProfImgs() {
        return profImgs;
    }

    public void setProfImgs(List<ProfImg> profImgs) {
        this.profImgs = profImgs;
    }
}
