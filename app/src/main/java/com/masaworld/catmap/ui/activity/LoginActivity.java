package com.masaworld.catmap.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.masaworld.catmap.R;
import com.masaworld.catmap.viewmodel.LoginViewModel;
import com.masaworld.catmap.viewmodel.ViewEvent;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private LoginViewModel viewModel;
    private TwitterLoginButton twitterLoginButton;
    private LinearLayout buttonWrapper;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        setUpViews();
        setUpObservers();
    }

    @Override
    public void onClick(View view) {
        buttonWrapper.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void handleLoginFailedEvent(ViewEvent e) {
        buttonWrapper.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        showToast(R.string.error_login_failed);
    }

    private void handleLoginSuccessEvent(ViewEvent e) {
        showToast(R.string.login_succeeded);
        onBackPressed();
    }

    private void setUpObservers() {
        viewModel.getLoginSuccessEvent().observe(this, this::handleLoginSuccessEvent);
        viewModel.getLoginFailedEvent().observe(this, this::handleLoginFailedEvent);
    }

    private void setUpViews() {
        buttonWrapper = findViewById(R.id.login_button_wrapper);
        progressBar = findViewById(R.id.login_progress_bar);
        twitterLoginButton = findViewById(R.id.twitter_login_button);
        twitterLoginButton.setTextSize(14);
        twitterLoginButton.setOnClickListener(this);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                String tokenKey = result.data.getAuthToken().token;
                String tokenSecret = result.data.getAuthToken().secret;
                viewModel.twitterLogin(tokenKey, tokenSecret);
            }

            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
                viewModel.loginFailed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

}
