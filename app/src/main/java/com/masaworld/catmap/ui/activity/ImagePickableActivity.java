package com.masaworld.catmap.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.masaworld.catmap.ui.fragment.ImagePickDialogFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ImagePickableActivity extends BaseActivity implements ImagePickDialogFragment.Callback {

    private static final String IMG_ROOT = "images";
    private static final String AUTHORITY = "com.masaworld.fileprovider";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_ALBUM = 2;

    private Uri photoUri;

    protected void pick() {
        new ImagePickDialogFragment().show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    handleCameraResult();
                    break;
                case REQUEST_ALBUM:
                    handleAlbumResult(data);
                    break;
            }
        }
    }

    private void handleCameraResult() {
        if (photoUri != null) {
            revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            onImagePicked(photoUri);
        } else {
            showToast("photo uri is null. try other camera app.");
        }
    }

    private void handleAlbumResult(Intent data) {
        if (data != null && data.getData() != null) {
            onImagePicked(data.getData());
        } else {
            showToast("returned data is null. try other album app.");
        }
    }

    protected abstract void onImagePicked(Uri imageUri);

    @Override
    public final void onPickFromCamera() {
        File file = createTempFile();
        if (file != null) {
            photoUri = FileProvider.getUriForFile(this, AUTHORITY, file);
            grantUriPermissionToAllCameraApps(photoUri);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                showToast("camera not installed");
            }
        } else {
            showToast("failed to access file system");
        }
    }

    @Override
    public final void onPickFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_ALBUM);
    }

    private File createTempFile() {
        File imgRoot = new File(getFilesDir(), IMG_ROOT);
        if (!imgRoot.exists()) {
            if (!imgRoot.mkdir()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        try {
            return File.createTempFile(imageFileName, ".jpg", imgRoot);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    private void grantUriPermissionToAllCameraApps(Uri uri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Log.i("catdebug", "permit uri: " + uri.toString());
        for (ResolveInfo appInfo : getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)) {
            grantUriPermission(appInfo.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //Log.i("catdebug", "grant: " + appInfo.activityInfo.packageName);
        }
    }

}
