package com.core2plus.oalam.foodstudio.API.APICall;

import com.core2plus.oalam.foodstudio.API.DealResponse;
import com.core2plus.oalam.foodstudio.API.InsertResponse;
import com.core2plus.oalam.foodstudio.API.ProfImgResponse;
import com.core2plus.oalam.foodstudio.API.PurchaseResponse;
import com.core2plus.oalam.foodstudio.API.SliderResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {


    @FormUrlEncoded
    @POST("?fx=insertuser")
    Call<InsertResponse> insertdata(@Field("userid") String user_id, @Field("name") String name, @Field("email") String email, @Field("password") String password,
                                    @Field("phone") String phone, @Field("createdDtm") String createdDtm);

    @FormUrlEncoded
    @POST("?fx=insertpurchase")
    Call<InsertResponse> insertpurchase(@Field("userid") String user_id, @Field("img_url") String img_url, @Field("purchaseTime") String purchaseTime,
                                        @Field("blockTime") String blockTime);

    @GET("?fx=getdeals")
    Call<DealResponse> getDeals();

    @GET("?fx=getdealsUpcoming")
    Call<DealResponse> getdealsUpcoming();

    @GET("?fx=getPurchasebyUserId")
    Call<PurchaseResponse> getPurchasebyUserId(@Query("userid") String user_id);

    @GET("?fx=getslider")
    Call<SliderResponse> getSlider();

    @FormUrlEncoded
    @POST("?fx=updateProfile")
    Call<InsertResponse> updateUser(@Field("userid") String user_id, @Field("name") String name, @Field("email") String email,
                                    @Field("phone") String phone);

    @FormUrlEncoded
    @POST("?fx=uploadImage")
    Call<InsertResponse> uploadImage(@Field("userid") String user_id, @Field("image") String image);

    @FormUrlEncoded
    @POST("?fx=getUserImg")
    Call<ProfImgResponse> getUserImg(@Field("userid") String user_id);
}
