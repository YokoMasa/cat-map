package com.masaworld.catmap.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.masaworld.catmap.R;
import com.masaworld.catmap.data.repository.CatRepository;

public class CatImagePostService extends IntentService {

    public static final String CAT_IMAGE_POST_ACTION = "com.masaworld.cat_image_post_action";
    public static final String EXTRA_RESULT = "cat_image_post_result";

    private static final String SERVICE_NAME = "cat_image_post_service";
    private static final String EXTRA_ID = "cat_id";
    private static final String EXTRA_URI = "image_uri";

    public static Intent getIntent(int catId, Uri imageUri, Context context) {
        Intent intent = new Intent(context, CatImagePostService.class);
        intent.putExtra(EXTRA_ID, catId);
        intent.putExtra(EXTRA_URI, imageUri);
        return intent;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        int id = intent.getIntExtra(EXTRA_ID, -1);
        Uri uri = intent.getParcelableExtra(EXTRA_URI);
        showToast(R.string.adding_image);
        boolean succeeded = CatRepository.getInstance().createCatImageSync(id, uri);
        notifyResult(succeeded);
    }

    private void notifyResult(boolean succeeded) {
        Intent intent = new Intent(CAT_IMAGE_POST_ACTION);
        intent.putExtra(EXTRA_RESULT, succeeded);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void showToast(int resId) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
        });
    }

    public CatImagePostService() {
        super(SERVICE_NAME);
    }

}
