package com.masaworld.catmap.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.masaworld.catmap.R;
import com.masaworld.catmap.ui.fragment.LoadingFragment;
import com.masaworld.catmap.viewmodel.ViewEvent;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String LOADING_FRAGMENT_TAG = "loading";

    protected void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    protected void handleToastEvent(ViewEvent<Integer> e) {
        if (isEventExecutable(e)) {
            showToast(e.getPayload());
            e.handled();
        }
    }

    protected boolean isEventExecutable(ViewEvent e) {
        return e != null && !e.isHandled();
    }

    protected void showLoadingFragment(int motherId) {
        Fragment f = new LoadingFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(motherId, f, LOADING_FRAGMENT_TAG);
        ft.commit();
    }

    protected void hideLoadingFragment() {
        Fragment f = getSupportFragmentManager().findFragmentByTag(LOADING_FRAGMENT_TAG);
        if (f != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_out);
            ft.remove(f);
            ft.commit();
        }
    }

}
