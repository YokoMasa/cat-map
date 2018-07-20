package com.masaworld.catmap.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialog;

import com.masaworld.catmap.R;

public class ImagePickDialogFragment extends DialogFragment {

    private Callback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            this.callback = (Callback) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new AppCompatDialog(getContext());
        dialog.setContentView(R.layout.image_pick_dialog);
        dialog.findViewById(R.id.image_pick_dialog_camera).setOnClickListener(view -> {
            if (callback != null) {
                callback.onPickFromCamera();
            }
            dismiss();
        });
        dialog.findViewById(R.id.image_pick_dialog_album).setOnClickListener(view -> {
            if (callback != null) {
                callback.onPickFromAlbum();
            }
            dismiss();
        });
        return dialog;
    }

    public interface Callback {
        public void onPickFromCamera();

        public void onPickFromAlbum();
    }

}
