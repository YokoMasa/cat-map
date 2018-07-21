package com.masaworld.catmap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.masaworld.catmap.R;
import com.masaworld.catmap.data.repository.CatRepository;

public class AddCatViewModel extends ViewModel {

    MutableLiveData<ViewEvent<Integer>> showToastEvent;
    MutableLiveData<ViewEvent> goBackEvent;
    MutableLiveData<ViewEvent> startServiceEvent;

    public LiveData<ViewEvent<Integer>> getShowToastEvent() {
        return showToastEvent;
    }

    public LiveData<ViewEvent> getGoBackEvent() {
        return goBackEvent;
    }

    public LiveData<ViewEvent> getStartServiceEvent() {
        return startServiceEvent;
    }

    public void createCat(String name, LatLng latLng, Uri imageUri) {
        if (!validateCreateCat(name, latLng, imageUri)) {
            showToastEvent.setValue(new ViewEvent<>(R.string.uncompleted_form));
            return;
        }

        startServiceEvent.setValue(new ViewEvent(null));
        goBackEvent.setValue(new ViewEvent(null));
    }

    public boolean validateCreateCat(String name, LatLng latLng, Uri uri) {
        if (name == null || name.length() == 0 || latLng == null || uri == null) {
            return false;
        }
        return true;
    }

    public AddCatViewModel() {
        showToastEvent = new MutableLiveData<>();
        goBackEvent = new MutableLiveData<>();
        startServiceEvent = new MutableLiveData<>();
    }

}
