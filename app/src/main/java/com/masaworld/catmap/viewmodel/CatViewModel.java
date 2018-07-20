package com.masaworld.catmap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.masaworld.catmap.data.model.Cat;
import com.masaworld.catmap.data.repository.CatRepository;

public class CatViewModel extends ViewModel {

    private MutableLiveData<Cat> cat;
    private MutableLiveData<ViewEvent<Integer>> toastEvent;

    public LiveData<Cat> getCat() {
        return cat;
    }

    public LiveData<ViewEvent<Integer>> getToastEvent() {
        return toastEvent;
    }

    public void loadCat(int id) {
        CatRepository.getInstance().getCat(id).observeForever(catFailableData -> {
            if (catFailableData == null) {
                return;
            }
            if (catFailableData.failed) {
                ViewEvent<Integer> e = new ViewEvent<>(catFailableData.error.messageId);
                toastEvent.setValue(e);
            } else {
                cat.setValue(catFailableData.data);
            }
        });
    }

    public CatViewModel() {
        cat = new MutableLiveData<>();
        toastEvent = new MutableLiveData<>();
    }

}
