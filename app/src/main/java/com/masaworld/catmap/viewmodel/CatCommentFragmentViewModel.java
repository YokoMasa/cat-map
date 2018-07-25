package com.masaworld.catmap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.masaworld.catmap.R;
import com.masaworld.catmap.data.model.CatComment;
import com.masaworld.catmap.data.repository.CatRepository;
import com.masaworld.catmap.data.repository.TokenRepository;

import java.util.List;

public class CatCommentFragmentViewModel extends ViewModel {

    private MutableLiveData<ViewEvent<Integer>> toastEvent;
    private MutableLiveData<ViewEvent> loginDialogEvent;
    private MutableLiveData<List<CatComment>> comments;
    private int catId;

    public LiveData<ViewEvent<Integer>> getToastEvent() {
        return toastEvent;
    }

    public LiveData<ViewEvent> getLoginDialogEvent() {
        return loginDialogEvent;
    }

    public LiveData<List<CatComment>> getComments() {
        return comments;
    }

    public void postComment(String comment) {
        if (TokenRepository.getInstance().hasToken()) {
            toastEvent.setValue(new ViewEvent<>(R.string.adding_comment));
            CatRepository.getInstance().createCatComment(comment, catId).observeForever(data -> {
                if (data != null && !data.failed) {
                    load(catId);
                } else {
                    toastEvent.setValue(new ViewEvent<>(R.string.failed_to_add_comment));
                }
            });
        } else {
            loginDialogEvent.setValue(new ViewEvent(null));
        }
    }

    public void load(int catId) {
        this.catId = catId;
        CatRepository.getInstance().getCatComments(catId).observeForever(data -> {
            if (data != null && !data.failed) {
                comments.setValue(data.data);
            } else {
                toastEvent.setValue(new ViewEvent<>(R.string.failed_to_load_comments));
            }
        });
    }

    public CatCommentFragmentViewModel() {
        toastEvent = new MutableLiveData<>();
        comments = new MutableLiveData<>();
        loginDialogEvent = new MutableLiveData<>();
    }
}
