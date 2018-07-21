package com.masaworld.catmap.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.masaworld.catmap.R;
import com.masaworld.catmap.data.model.ImageInfo;
import com.masaworld.catmap.ui.adapter.ImageListAdapter;
import com.masaworld.catmap.ui.fragment.LoginCheckDialogFragment;
import com.masaworld.catmap.viewmodel.ImageListViewModel;
import com.masaworld.catmap.viewmodel.ViewEvent;

import java.util.List;

public class ImageListActivity extends BaseActivity implements LoginCheckDialogFragment.LoginCheckCallback {

    private static final int COLUMN_COUNT = 3;
    private static final String EXTRA_ID = "cat_id";

    private ImageListViewModel viewModel;
    private ImageListAdapter adapter;

    public static Intent getIntent(int id, Context context) {
        Intent intent = new Intent(context, ImageListActivity.class);
        intent.putExtra(EXTRA_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        setUpViews();
        setUpViewModel();

        int catId = getIntent().getIntExtra(EXTRA_ID, -1);
        viewModel.loadImages(catId);
    }

    private void setUpViews() {
        Toolbar toolbar = findViewById(R.id.image_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.image_list_recycler_view);
        adapter = new ImageListAdapter(this);
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_COUNT);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(this).get(ImageListViewModel.class);
        viewModel.getImages().observe(this, this::handleImages);
        viewModel.getToastEvent().observe(this, this::handleToastEvent);
        viewModel.getLoginDialogEvent().observe(this, this::handleShowLoginDialogEvent);
    }

    private void handleImages(List<ImageInfo>images) {
        adapter.setImages(images);
    }

    private void handleShowLoginDialogEvent(ViewEvent e) {
        if (isEventExecutable(e)) {
            LoginCheckDialogFragment f = new LoginCheckDialogFragment();
            f.show(getSupportFragmentManager(), null);
            e.handled();
        }
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

    @Override
    public void onLoginAccepted() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
