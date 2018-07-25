package com.masaworld.catmap.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Toast;

import com.masaworld.catmap.R;
import com.masaworld.catmap.data.model.CatComment;
import com.masaworld.catmap.ui.adapter.CommentListAdapter;
import com.masaworld.catmap.viewmodel.CatCommentFragmentViewModel;
import com.masaworld.catmap.viewmodel.ViewEvent;

import java.util.List;

public class CatCommentFragment extends Fragment implements Toolbar.OnMenuItemClickListener, OnBackPressedListener {

    private static final String EXTRA_ID = "cat_id";
    private static final String TAG = "cat_comment_fragment";
    private FragmentManager fm;
    private CatCommentFragmentViewModel viewModel;
    private EditText editText;
    private CommentListAdapter adapter;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CatCommentFragmentViewModel.class);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int id = bundle.getInt(EXTRA_ID);
            viewModel.load(id);
            viewModel.getToastEvent().observe(this, this::handleToastEvent);
            viewModel.getLoginDialogEvent().observe(this, this::handleLoginDialogEvent);
            viewModel.getComments().observe(this, this::handleComments);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cat_comment, container, false);
        Toolbar toolbar = view.findViewById(R.id.fragment_cat_toolbar);
        toolbar.inflateMenu(R.menu.cat_comment_menu);
        toolbar.setOnMenuItemClickListener(this);
        editText = view.findViewById(R.id.fragment_cat_edit_text);
        AppCompatImageButton button = view.findViewById(R.id.fragment_cat_button);
        button.setOnClickListener(view1 -> {
            viewModel.postComment(editText.getText().toString());
            editText.setText("");
        });
        RecyclerView recyclerView = view.findViewById(R.id.fragment_cat_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new CommentListAdapter(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void handleComments(List<CatComment> comments) {
        adapter.setComments(comments);
    }

    private void handleToastEvent(ViewEvent<Integer> e) {
        if (isEventExecutable(e)) {
            Toast.makeText(getContext(), e.getPayload(), Toast.LENGTH_SHORT).show();
            e.handled();
        }
    }

    private void handleLoginDialogEvent(ViewEvent e) {
        if (isEventExecutable(e)) {

        }
    }

    private boolean isEventExecutable(ViewEvent e) {
        return e != null && !e.isHandled();
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
