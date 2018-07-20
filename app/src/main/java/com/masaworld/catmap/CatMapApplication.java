package com.masaworld.catmap;

import android.app.Application;

import com.masaworld.catmap.data.repository.CatRepository;
import com.masaworld.catmap.data.repository.TokenRepository;
import com.twitter.sdk.android.core.Twitter;

public class CatMapApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TokenRepository.init(this);
        CatRepository.init(this);
        Twitter.initialize(this);
    }

}
