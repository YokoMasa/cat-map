package com.masaworld.catmap.ui.activity;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.masaworld.catmap.R;

public class ImageActivity extends BaseActivity {

    private static final String EXTRA_URL = "image_url";

    public static Intent getIntent(String imageUrl, Context context) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(EXTRA_URL, imageUrl);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        showLoadingFragment(R.id.image_mother);
        String url = getIntent().getStringExtra(EXTRA_URL);
        ImageView imageView = findViewById(R.id.image_image);
        Glide.with(this).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                safelyHideLoading();
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                safelyHideLoading();
                return false;
            }
        }).into(imageView);
    }

    private void safelyHideLoading() {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            hideLoadingFragment();
        }
    }
}
