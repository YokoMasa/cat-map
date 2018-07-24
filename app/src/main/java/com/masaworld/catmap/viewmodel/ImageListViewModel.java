package com.masaworld.catmap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.masaworld.catmap.R;
import com.masaworld.catmap.data.model.ImageInfo;
import com.masaworld.catmap.data.repository.CatRepository;
import com.masaworld.catmap.data.repository.TokenRepository;

import java.util.List;

public class ImageListViewModel extends ViewModel {

    private MutableLiveData<List<ImageInfo>> images;
    private MutableLiveData<ViewEvent<Integer>> toastEvent;
    private MutableLiveData<ViewEvent> navigateToAddImageEvent;
    private MutableLiveData<ViewEvent> loginDialogEvent;
    private int catId;

    public LiveData<List<ImageInfo>> getImages() {
        return images;
    }

    public LiveData<ViewEvent<Integer>> getToastEvent() {
        return toastEvent;
    }

    public LiveData<ViewEvent> getNavigateToAddImageEvent() {
        return navigateToAddImageEvent;
    }

    public LiveData<ViewEvent> getLoginDialogEvent() {
        return loginDialogEvent;
    }

    public void loadImages(int catId) {
        this.catId = catId;
        CatRepository.getInstance().getCat(catId).observeForever(failableData -> {
            if (failableData != null) {
                if (!failableData.failed) {
                    images.setValue(failableData.data.images);
                } else {
                    toastEvent.setValue(new ViewEvent<>(failableData.error.messageId));
                }
            }
        });
    }

    public void addImage() {
        if (TokenRepository.getInstance().hasToken()) {
            navigateToAddImageEvent.setValue(new ViewEvent(null));
        } else {
            loginDialogEvent.setValue(new ViewEvent(null));
        }
    }

    public void notifyImagePostResult(boolean b) {
        if (b) {
            toastEvent.setValue(new ViewEvent<>(R.string.added_image));
            loadImages(catId);
        } else {
            toastEvent.setValue(new ViewEvent<>(R.string.failed_to_add_image));
        }
    }

    public ImageListViewModel() {
        images = new MutableLiveData<>();
        toastEvent = new MutableLiveData<>();
        navigateToAddImageEvent = new MutableLiveData<>();
        loginDialogEvent = new MutableLiveData<>();
    }
}
