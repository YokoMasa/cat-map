package com.masaworld.catmap.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.masaworld.catmap.R;
import com.masaworld.catmap.data.repository.CatRepository;
import com.masaworld.catmap.service.CatPostService;
import com.masaworld.catmap.ui.fragment.LoadingFragment;
import com.masaworld.catmap.viewmodel.AddCatViewModel;
import com.masaworld.catmap.viewmodel.ViewEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddCatActivity extends ImagePickableActivity {

    private static final String EXTRA_LATLNG = "latLng";
    private AddCatViewModel viewModel;
    private ImageView imageView;
    private EditText nameField;
    private Button button;
    private LatLng latLng;
    private Uri imageUri;

    public static Intent getIntent(LatLng latLng, Context context) {
        Intent intent = new Intent(context, AddCatActivity.class);
        intent.putExtra(EXTRA_LATLNG, latLng);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cat);
        setUpViewModel();
        latLng = getIntent().getParcelableExtra(EXTRA_LATLNG);
        nameField = findViewById(R.id.add_cat_name);
        imageView = findViewById(R.id.add_cat_image);
        imageView.setOnClickListener(view -> {
            pick();
        });
        button = findViewById(R.id.add_cat_submit);
        button.setOnClickListener(view -> {
            viewModel.createCat(nameField.getText().toString(), latLng, imageUri);
            button.setEnabled(false);
        });
    }

    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(this).get(AddCatViewModel.class);
        viewModel.getGoBackEvent().observe(this, this::handleGoBackEvent);
        viewModel.getShowToastEvent().observe(this, this::handleToastEvent);
        viewModel.getStartServiceEvent().observe(this, this::handleStartServiceEvent);
    }

    private void handleGoBackEvent(ViewEvent e) {
        if (isEventExecutable(e)) {
            onBackPressed();
            e.handled();
        }
    }

    private void handleStartServiceEvent(ViewEvent e) {
        if (isEventExecutable(e)) {
            Intent intent = CatPostService.getIntent(nameField.getText().toString(), latLng, imageUri, this);
            startService(intent);
            e.handled();
        }
    }

    @Override
    protected void onImagePicked(Uri imageUri) {
        this.imageUri = imageUri;
        Glide.with(this).load(imageUri).into(imageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageView.setImageBitmap(null);
    }
}
