package com.masaworld.catmap.data.datasource.local;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenLocalDataSource {

    private static final String TOKEN_KEY = "token";
    private static final String SP_NAME = "user";
    private SharedPreferences sp;

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public void deleteToken() {
        saveToken("");
    }

    public String getToken() {
        return sp.getString(TOKEN_KEY, "");
    }

    public boolean hasToken() {
        return !getToken().equals("");
    }

    public TokenLocalDataSource(Context appContext) {
        this.sp = appContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

}
