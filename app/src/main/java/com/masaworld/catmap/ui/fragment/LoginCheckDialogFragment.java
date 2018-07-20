package com.masaworld.catmap.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.masaworld.catmap.R;

public class LoginCheckDialogFragment extends DialogFragment {

    private LoginCheckCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginCheckCallback) {
            callback = (LoginCheckCallback) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.login_check_title)
                .setMessage(R.string.login_check_message)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    notifyCallback();
                    dismiss();
                })
                .setNegativeButton(R.string.no, (dialogInterface, i) -> dismiss())
                .create();
    }

    private void notifyCallback() {
        if (callback != null) {
            callback.onLoginAccepted();
        }
    }

    public interface LoginCheckCallback {
        public void onLoginAccepted();
    }
}
