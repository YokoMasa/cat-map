package com.masaworld.catmap.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.tasks.Task;
import com.masaworld.catmap.R;
import com.masaworld.catmap.viewmodel.LoginViewModel;
import com.masaworld.catmap.viewmodel.ViewEvent;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final int GOOGLE_LOGIN = 10;
    private LoginViewModel viewModel;
    private TwitterLoginButton twitterLoginButton;
    private SignInButton googleLoginButton;
    private LinearLayout buttonWrapper;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        setUpViews();
        setUpObservers();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.google_login_button) {
            handleGoogleLogin();
        }
        buttonWrapper.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void handleGoogleLogin() {
        String serverId = getString(R.string.google_login_server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(serverId)
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, gso);
        Intent intent = client.getSignInIntent();
        startActivityForResult(intent, GOOGLE_LOGIN);
    }

    private void handleGoogleLoginResult(Intent intent) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            viewModel.googleLogin(account.getServerAuthCode());
        } catch (ApiException ae) {
            ae.printStackTrace();
            viewModel.loginFailed();
        }
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
        googleLoginButton = findViewById(R.id.google_login_button);
        googleLoginButton.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GOOGLE_LOGIN:
                handleGoogleLoginResult(data);
                break;
        }
    }

}
