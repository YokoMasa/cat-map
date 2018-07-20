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
    private Button button;
    private EditText nameField;
    private Uri imageUri;
    private boolean loading;

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
        nameField = findViewById(R.id.add_cat_name);
        imageView = findViewById(R.id.add_cat_image);
        imageView.setOnClickListener(view -> {
            pick();
        });
        button = findViewById(R.id.add_cat_submit);
        button.setOnClickListener(view -> {
            LatLng latLng = getIntent().getParcelableExtra(EXTRA_LATLNG);
            viewModel.createCat(nameField.getText().toString(), latLng, imageUri);
        });
    }

    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(this).get(AddCatViewModel.class);
        viewModel.getGoBackEvent().observe(this, this::handleGoBackEvent);
        viewModel.getShowToastEvent().observe(this, this::handleToastEvent);
        viewModel.getShowLoadingEvent().observe(this, this::handleShowLoadingEvent);
    }

    private void handleGoBackEvent(ViewEvent e) {
        if (isEventExecutable(e)) {
            loading = false;
            onBackPressed();
            e.handled();
        }
    }

    private void handleShowLoadingEvent(ViewEvent e) {
        if (isEventExecutable(e)) {
            imageView.setImageBitmap(null);
            button.setEnabled(false);
            loading = true;

            Fragment f = new LoadingFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.add_cat_mother, f);
            ft.setCustomAnimations(R.anim.loading_fragment_add_animation, R.anim.loading_fragment_add_animation);
            ft.commit();
            e.handled();
        }
    }

    @Override
    protected void onImagePicked(Uri imageUri) {
        this.imageUri = imageUri;
        Glide.with(this).load(imageUri).into(imageView);
    }

    @Override
    public void onBackPressed() {
        if (loading) {
            return;
        }
        super.onBackPressed();
    }
}
