package com.masaworld.catmap.data.datasource.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.masaworld.catmap.Config;
import com.masaworld.catmap.data.FailableData;
import com.masaworld.catmap.data.model.Token;
import com.masaworld.catmap.data.service.TokenService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenRemoteDataSource {

    private TokenService userService;

    public TokenRemoteDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(MyOkHttpClient.getInstance())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(TokenService.class);
    }

    public LiveData<FailableData<Token>> twitterLogin(String tokenKey, String tokenSecret) {
        MutableLiveData<FailableData<Token>> liveData = new MutableLiveData<>();
        FailableData<Token> failableData = new FailableData<>();
        userService.login(tokenKey, tokenSecret).enqueue(new ResponseHandler<>(failableData, liveData));
        return liveData;
    }

}
