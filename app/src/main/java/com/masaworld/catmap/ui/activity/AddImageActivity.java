package com.masaworld.catmap.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.masaworld.catmap.R;
import com.masaworld.catmap.service.CatImagePostService;

public class AddImageActivity extends ImagePickableActivity {

    private static final String EXTRA_ID = "cat_id";
    private ImageView imageView;
    private Uri imageUri;

    public static Intent getIntent(int catId, Context context) {
        Intent intent = new Intent(context, AddImageActivity.class);
        intent.putExtra(EXTRA_ID, catId);
        return intent;
    }

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
            if (imageUri != null) {
                startImagePostService();
                onBackPressed();
            } else {
                showToast(R.string.uncompleted_form);
            }
        });
    }

    private void startImagePostService() {
        int catId = getIntent().getIntExtra(EXTRA_ID, -1);
        Intent intent = CatImagePostService.getIntent(catId, imageUri, this);
        startService(intent);
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
