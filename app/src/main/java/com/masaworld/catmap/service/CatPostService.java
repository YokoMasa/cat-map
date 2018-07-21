package com.masaworld.catmap.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.masaworld.catmap.R;
import com.masaworld.catmap.data.repository.CatRepository;

public class CatPostService extends IntentService {

    public static final String CAT_POST_ACTION = "com.masaworld.cat_post_action";
    public static final String EXTRA_RESULT = "cat_post_result";

    private static final String SERVICE_NAME = "cat_post_service";
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_LATLNG = "latLng";
    private static final String EXTRA_IMAGE_URI = "uri";

    public static Intent getIntent(String name, LatLng latLng, Uri imageUri, Context context) {
        Intent intent = new Intent(context, CatPostService.class);
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_LATLNG, latLng);
        intent.putExtra(EXTRA_IMAGE_URI, imageUri);
        return intent;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        String name = intent.getStringExtra(EXTRA_NAME);
        LatLng latLng = intent.getParcelableExtra(EXTRA_LATLNG);
        Uri uri = intent.getParcelableExtra(EXTRA_IMAGE_URI);
        showToast(R.string.adding_cat);

        boolean succeeded = CatRepository.getInstance().createCatSync(name, latLng, uri);
        notifyResult(succeeded);
    }

    private void notifyResult(boolean succeeded) {
        Intent intent = new Intent(CAT_POST_ACTION);
        intent.putExtra(EXTRA_RESULT, succeeded);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void showToast(int resId) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
        });
    }

    public CatPostService(String name) {
        super(name);
    }

    public CatPostService() {
        this(SERVICE_NAME);
    }
}
