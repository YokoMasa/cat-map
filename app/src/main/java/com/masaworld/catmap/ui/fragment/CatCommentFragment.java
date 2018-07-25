package com.masaworld.catmap.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.masaworld.catmap.R;

public class CatCommentFragment extends Fragment implements Toolbar.OnMenuItemClickListener, OnBackPressedListener {

    private static final String EXTRA_ID = "cat_id";
    private static final String TAG = "cat_comment_fragment";
    private FragmentManager fm;

    public static CatCommentFragment get(int id) {
        CatCommentFragment f = new CatCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ID, id);
        f.setArguments(bundle);
        return f;
    }

    public void show(FragmentManager fm, int motherId) {
        this.fm = fm;
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.rise_up, R.anim.rise_up);
        ft.add(motherId, this, TAG);
        ft.commit();
    }

    public void hide() {
        if (fm == null) {
            return;
        }

        Fragment f = fm.findFragmentByTag(TAG);
        if (f == null) {
            return;
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.dive_down, R.anim.dive_down);
        ft.remove(f);
        ft.commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cat_comment, container, false);
        Toolbar toolbar = view.findViewById(R.id.fragment_cat_toolbar);
        toolbar.inflateMenu(R.menu.cat_comment_menu);
        toolbar.setOnMenuItemClickListener(this);
        return view;
    }

    @Override
    public boolean onBackPressed() {
        hide();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        hide();
        return true;
    }
}
