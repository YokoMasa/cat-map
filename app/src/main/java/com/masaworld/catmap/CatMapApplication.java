package com.masaworld.catmap;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.masaworld.catmap.data.repository.CatRepository;
import com.masaworld.catmap.data.repository.TokenRepository;
import com.twitter.sdk.android.core.Twitter;

public class CatMapApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TokenRepository.init(this);
        CatRepository.init(this);
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        Twitter.initialize(this);
    }

}
