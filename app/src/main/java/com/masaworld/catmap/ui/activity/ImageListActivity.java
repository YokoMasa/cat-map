package com.masaworld.catmap.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.masaworld.catmap.R;
import com.masaworld.catmap.data.model.ImageInfo;
import com.masaworld.catmap.service.CatImagePostService;
import com.masaworld.catmap.ui.adapter.ImageListAdapter;
import com.masaworld.catmap.ui.fragment.LoginCheckDialogFragment;
import com.masaworld.catmap.viewmodel.ImageListViewModel;
import com.masaworld.catmap.viewmodel.ViewEvent;

import java.util.List;

public class ImageListActivity extends BaseActivity implements LoginCheckDialogFragment.LoginCheckCallback, ImageListAdapter.ImageClickListener {

    private static final int COLUMN_COUNT = 3;
    private static final String EXTRA_ID = "cat_id";

    private ImageListViewModel viewModel;
    private ImageListAdapter adapter;
    private int catId;

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
        setUpBroadcastReceiver();

        catId = getIntent().getIntExtra(EXTRA_ID, -1);
        viewModel.loadImages(catId);
    }

    private void setUpViews() {
        Toolbar toolbar = findViewById(R.id.image_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.image_list_recycler_view);
        adapter = new ImageListAdapter(this);
        adapter.setImageClickeListener(this);
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_COUNT);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = findViewById(R.id.image_list_fab);
        fab.setOnClickListener(view -> {
            viewModel.addImage();
        });
    }

    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(this).get(ImageListViewModel.class);
        viewModel.getImages().observe(this, this::handleImages);
        viewModel.getToastEvent().observe(this, this::handleToastEvent);
        viewModel.getLoginDialogEvent().observe(this, this::handleShowLoginDialogEvent);
        viewModel.getNavigateToAddImageEvent().observe(this, this::handleNavigateToAddImageEvent);
    }

    private void setUpBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(CatImagePostService.CAT_IMAGE_POST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new CatImagePostBroadcastReceiver(), intentFilter);
    }

    private void handleImages(List<ImageInfo>images) {
        adapter.setImages(images);
    }

    private void handleShowLoginDialogEvent(ViewEvent e) {
        if (isEventExecutable(e)) {
            showLoginDialog(R.string.login_check_title_add_image, R.string.login_check_message_add_image);
            e.handled();
        }
    }

    private void handleNavigateToAddImageEvent(ViewEvent e) {
        if (isEventExecutable(e)) {
            Intent intent = AddImageActivity.getIntent(catId, this);
            startActivity(intent);
            e.handled();
        }
    }

    @Override
    public void onImageClicked(String imageUrl) {
        Intent intent = ImageActivity.getIntent(imageUrl, this);
        startActivity(intent);
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

    class CatImagePostBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean b = intent.getBooleanExtra(CatImagePostService.EXTRA_RESULT, false);
            viewModel.notifyImagePostResult(b);
        }
    }
}
