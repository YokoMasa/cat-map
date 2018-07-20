package com.masaworld.catmap.data.datasource.remote;

import okhttp3.OkHttpClient;

public class MyOkHttpClient {
    private static OkHttpClient instance;

    public static OkHttpClient getInstance() {
        if (instance == null) {
            instance = new OkHttpClient.Builder()
                    .build();
        }
        return instance;
    }
}
