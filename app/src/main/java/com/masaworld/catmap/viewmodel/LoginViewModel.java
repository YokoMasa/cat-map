package com.masaworld.catmap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.masaworld.catmap.data.repository.TokenRepository;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<ViewEvent<Integer>> toastEvent;
    private MutableLiveData<ViewEvent<String>> loginFailedEvent;
    private MutableLiveData<ViewEvent<String>> loginSuccessEvent;

    public LiveData<ViewEvent<Integer>> getToastEvent() {
        return toastEvent;
    }

    public LiveData<ViewEvent<String>> getLoginFailedEvent() {
        return loginFailedEvent;
    }

    public LiveData<ViewEvent<String>> getLoginSuccessEvent() {
        return loginSuccessEvent;
    }

    public void loginFailed() {
        loginFailedEvent.setValue(new ViewEvent<>(""));
    }

    public void twitterLogin(String tokenKey, String tokenSecret) {
        TokenRepository.getInstance().twitterLogin(tokenKey, tokenSecret).observeForever(aBoolean -> {
            if (aBoolean != null) {
                if (aBoolean) {
                    loginSuccessEvent.setValue(new ViewEvent<>(""));
                } else {
                    loginFailedEvent.setValue(new ViewEvent<>(""));
                }
            } else {
                loginFailedEvent.setValue(new ViewEvent<>(""));
            }
        });
    }

    public LoginViewModel() {
        toastEvent = new MutableLiveData<>();
        loginSuccessEvent = new MutableLiveData<>();
        loginFailedEvent = new MutableLiveData<>();
    }

}
