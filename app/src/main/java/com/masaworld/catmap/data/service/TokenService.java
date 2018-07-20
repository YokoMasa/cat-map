package com.masaworld.catmap.data.service;

import com.masaworld.catmap.data.model.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TokenService {

    @FormUrlEncoded
    @POST("/user/twitter_login/")
    Call<Token> login(@Field("access_token_key") String tokenKey, @Field("access_token_secret") String tokenSecret);

}
