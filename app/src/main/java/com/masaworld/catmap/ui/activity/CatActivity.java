package com.masaworld.catmap.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.masaworld.catmap.Config;
import com.masaworld.catmap.R;
import com.masaworld.catmap.data.model.Cat;
import com.masaworld.catmap.viewmodel.CatViewModel;
import com.masaworld.catmap.viewmodel.ViewEvent;

public class CatActivity extends BaseActivity {

    private static final String EXTRA_ID = "id";
    private CatViewModel viewModel;
    private ImageView imageView;

    public static Intent getIntent(int id, Context context) {
        Intent intent = new Intent(context, CatActivity.class);
        intent.putExtra(EXTRA_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_activity);
        imageView = findViewById(R.id.cat_image);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);

        viewModel = ViewModelProviders.of(this).get(CatViewModel.class);
        viewModel.loadCat(id);
        viewModel.getToastEvent().observe(this, this::handleToastEvent);
        viewModel.getCat().observe(this, this::handleCat);

        Toolbar toolbar = findViewById(R.id.cat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void handleCat(Cat cat) {
        if (cat == null) {
            return;
        }

        getSupportActionBar().setTitle(cat.name);
        if (0 < cat.images.size()) {
            String imageUrl = Config.BASE_URL + cat.images.get(0).raw_image;
            Glide.with(this).load(imageUrl).into(imageView);
        }
    }

    private void showImageList() {
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        Intent intent = ImageListActivity.getIntent(id, this);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_comments:
                showToast("comments");
                return true;
            case R.id.menu_images:
                showImageList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageView.setImageBitmap(null);
    }
}
