package com.masaworld.catmap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.masaworld.catmap.R;
import com.masaworld.catmap.data.repository.CatRepository;

public class AddCatViewModel extends ViewModel {

    MutableLiveData<ViewEvent> showLoadingEvent;
    MutableLiveData<ViewEvent<Integer>> showToastEvent;
    MutableLiveData<ViewEvent> goBackEvent;

    public LiveData<ViewEvent> getShowLoadingEvent() {
        return showLoadingEvent;
    }

    public LiveData<ViewEvent<Integer>> getShowToastEvent() {
        return showToastEvent;
    }

    public LiveData<ViewEvent> getGoBackEvent() {
        return goBackEvent;
    }

    public void createCat(String name, LatLng latLng, Uri imageUri) {
        if (!validateCreateCat(name, latLng, imageUri)) {
            showToastEvent.setValue(new ViewEvent<>(R.string.uncompleted_form));
            return;
        }
        showLoadingEvent.setValue(new ViewEvent(null));


        CatRepository.getInstance().createCat(name, latLng, imageUri).observeForever(result -> {
            if (result.failed) {
                showToastEvent.setValue(new ViewEvent<>(result.error.messageId));
            } else {
                showToastEvent.setValue(new ViewEvent<>(R.string.added_cat));
            }
            goBackEvent.setValue(new ViewEvent(null));
        });

    }

    public boolean validateCreateCat(String name, LatLng latLng, Uri uri) {
        if (name == null || name.length() == 0 || latLng == null || uri == null) {
            return false;
        }
        return true;
    }

    public AddCatViewModel() {
        showLoadingEvent = new MutableLiveData<>();
        showToastEvent = new MutableLiveData<>();
        goBackEvent = new MutableLiveData<>();
    }

}
