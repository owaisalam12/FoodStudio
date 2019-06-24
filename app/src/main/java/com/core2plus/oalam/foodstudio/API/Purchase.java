package com.core2plus.oalam.foodstudio.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Purchase {
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("img_url")
    @Expose
    private String imgUrl;
    @SerializedName("purchaseTime")
    @Expose
    private String purchaseTime;

    public String getUserid() {
        return userid;
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
    public String getURl(){

        String text=imgUrl;
        String[] split=text.split(";");
        String[]arr=new String[3];
        for(int i=0;i<split.length;i++){
            arr[i]=split[i];
        }
        if(arr[0]!=null){
            //System.out.println(arr[1]);
            return arr[0];
        }else {
            return "Error occured!";
        }
    }

    public String getText(){

        String text=imgUrl;
        String[] split=text.split(";");
        String[]arr=new String[3];
        for(int i=0;i<split.length;i++){
            arr[i]=split[i];
        }
        if(arr[1]!=null){
            //System.out.println(arr[1]);
            return arr[1];
        }else {
            return "Error occured!";
        }

    }

    public String getPrice(){

        String text=imgUrl;
        String[] split=text.split(";");
        String[]arr=new String[3];
        for(int i=0;i<split.length;i++){
            arr[i]=split[i];
        }
        if(arr[2]!=null){
            //System.out.println(arr[1]);
            return arr[2];
        }else {
            return "Error occured!";
        }

    }
}
