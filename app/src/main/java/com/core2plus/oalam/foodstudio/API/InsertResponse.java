package com.core2plus.oalam.foodstudio.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InsertResponse {


    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
//    @SerializedName("LastID")
//    @Expose
//    private int lastID;
    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
//    public int getLastID() {
//        return lastID;
//    }
//
//    public void setLastID(int lastID) {
//        this.lastID = lastID;
//    }
}