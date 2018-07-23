package com.masaworld.catmap.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.masaworld.catmap.R;

public class AddImageActivity extends ImagePickableActivity {

    private ImageView imageView;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        Toolbar toolbar = findViewById(R.id.add_image_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = findViewById(R.id.add_image_image);
        imageView.setOnClickListener(view -> {
            pick();
        });
        Button button = findViewById(R.id.add_image_submit);
        button.setOnClickListener(view -> {

        });
    }

    @Override
    protected void onImagePicked(Uri imageUri) {
        Glide.with(this).load(imageUri).into(imageView);
        this.imageUri = imageUri;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
