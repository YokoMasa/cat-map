package com.masaworld.catmap.data.service;

import com.masaworld.catmap.data.model.Cat;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CatMapService {

    @GET("/cat/list/")
    Call<List<Cat>> getCatList(@Query("area") String... area);

    @GET("/cat/{id}")
    Call<Cat> getCat(@Path("id") int id);

    @Multipart
    @POST("/cat/create/")
    Call<ResponseBody> createCat(@Header("Authorization") String token,
                                 @Part("name") RequestBody name,
                                 @Part("latitude") RequestBody latitude,
                                 @Part("longitude") RequestBody longitude,
                                 @Part("area_code") RequestBody areaCode,
                                 @Part MultipartBody.Part catImage);

}
