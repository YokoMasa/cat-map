package com.masaworld.catmap.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;

import com.masaworld.catmap.data.FailableData;
import com.masaworld.catmap.data.datasource.local.TokenLocalDataSource;
import com.masaworld.catmap.data.datasource.remote.TokenRemoteDataSource;
import com.masaworld.catmap.data.model.Token;

public abstract class TokenRepository {

    private static TokenRepository instance;

    public static TokenRepository getInstance() {
        if (instance == null) {
            throw new RuntimeException("init() must be called before getting the instance.");
        }
        return instance;
    }

    public static void init(Context appContext) {
        instance = new UserRepositoryImpl(appContext);
    }

    public abstract void deleteToken();

    public abstract String getToken();

    public abstract boolean hasToken();

    public abstract LiveData<Boolean> twitterLogin(String tokenKey, String tokenSecret);

    public abstract LiveData<Boolean> googleLogin(String authCode);

    private static class UserRepositoryImpl extends TokenRepository {

        private TokenLocalDataSource tokenLocalDataSource;
        private TokenRemoteDataSource tokenRemoteDataSource;

        @Override
        public void deleteToken() {
            tokenLocalDataSource.deleteToken();
        }

        @Override
        public String getToken() {
            String token = tokenLocalDataSource.getToken();
            //Log.i("catdebug", "token: " + token);
            return token;
        }

        @Override
        public boolean hasToken() {
            return tokenLocalDataSource.hasToken();
        }

        @Override
        public LiveData<Boolean> twitterLogin(String tokenKey, String tokenSecret) {
            MutableLiveData<Boolean> liveData = new MutableLiveData<>();
            tokenRemoteDataSource.twitterLogin(tokenKey, tokenSecret).observeForever(new TokenResultObserver(liveData));
            return liveData;
        }

        @Override
        public LiveData<Boolean> googleLogin(String authCode) {
            MutableLiveData<Boolean> liveData = new MutableLiveData<>();
            tokenRemoteDataSource.googleLogin(authCode).observeForever(new TokenResultObserver(liveData));
            return liveData;
        }

        UserRepositoryImpl(Context appContext) {
            this.tokenLocalDataSource = new TokenLocalDataSource(appContext);
            this.tokenRemoteDataSource = new TokenRemoteDataSource();
        }

        private class TokenResultObserver implements Observer<FailableData<Token>> {

            private MutableLiveData<Boolean> liveData;

            @Override
            public void onChanged(@Nullable FailableData<Token> tokenFailableData) {
                if (tokenFailableData != null) {
                    if (!tokenFailableData.failed) {
                        tokenLocalDataSource.saveToken(tokenFailableData.data.token);
                        liveData.setValue(true);
                    } else {
                        liveData.setValue(false);
                    }
                } else {
                    liveData.setValue(false);
                }
            }

            TokenResultObserver(MutableLiveData<Boolean> liveData) {
                this.liveData = liveData;
            }
        }

    }

}
