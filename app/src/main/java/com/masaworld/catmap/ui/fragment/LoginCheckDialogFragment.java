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

    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_MESSAGE = "extra_message";
    private LoginCheckCallback callback;

    public static DialogFragment get(int titleId, int messageId) {
        DialogFragment f = new LoginCheckDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TITLE, titleId);
        bundle.putInt(EXTRA_MESSAGE, messageId);
        f.setArguments(bundle);
        return f;
    }

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
        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new IllegalArgumentException("Please set arguments to this fragment before attaching.");
        }

        int titleId = bundle.getInt(EXTRA_TITLE);
        int messageId = bundle.getInt(EXTRA_MESSAGE);
        return new AlertDialog.Builder(getContext())
                .setTitle(titleId)
                .setMessage(messageId)
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
