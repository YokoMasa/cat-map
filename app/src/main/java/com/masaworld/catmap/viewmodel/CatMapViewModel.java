package com.masaworld.catmap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.masaworld.catmap.data.DataDripper;
import com.masaworld.catmap.data.model.Cat;
import com.masaworld.catmap.data.repository.CatRepository;
import com.masaworld.catmap.data.repository.TokenRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CatMapViewModel extends ViewModel {

    private final int[] surroundings = new int[]{1, 0, -1};
    private CatRepository repository;
    private DataDripper<Cat> cats;
    private Set<String> loadedAreas = new HashSet<>();
    private MutableLiveData<ViewEvent<Integer>> toastEvent;
    private MutableLiveData<ViewEvent> loginDialogEvent;
    private MutableLiveData<ViewEvent<Integer>> navigateToCatEvent;
    private MutableLiveData<ViewEvent<LatLng>> navigateToAddCatEvent;

    public DataDripper<Cat> getCats() {
        return cats;
    }

    public LiveData<ViewEvent<Integer>> getToastEvent() {
        return toastEvent;
    }

    public LiveData<ViewEvent> getLoginDialogEvent() {
        return loginDialogEvent;
    }

    public LiveData<ViewEvent<Integer>> getNavigateToCatEvent() {
        return navigateToCatEvent;
    }

    public LiveData<ViewEvent<LatLng>> getNavigateToAddCatEvent() { return navigateToAddCatEvent; }

    public void loadCats(LatLng newPosition) {
        String[] areaCodes = getSurroundingAreaCode(newPosition);
        if (areaCodes.length != 0) {
            repository.getCatsByArea(areaCodes).observeForever(listFailableData -> {
                if (listFailableData != null) {
                    if (listFailableData.failed) {
                        ViewEvent<Integer> e = new ViewEvent<>(listFailableData.error.messageId);
                        toastEvent.setValue(e);
                    } else {
                        cats.add(listFailableData.data);
                    }
                }
            });
        }
    }

    public void addNewCat(LatLng latLng) {
        if (TokenRepository.getInstance().hasToken()) {
            ViewEvent<LatLng> e = new ViewEvent<>(latLng);
            navigateToAddCatEvent.setValue(e);
        } else {
            ViewEvent e = new ViewEvent<>("");
            loginDialogEvent.setValue(e);
        }
    }

    public void showCat(int id) {
        ViewEvent<Integer> e = new ViewEvent<>(id);
        navigateToCatEvent.setValue(e);
    }

    public void clear() {
        cats.clear();
        loadedAreas.clear();
    }

    private String[] getSurroundingAreaCode(LatLng latLng) {
        int lat = (int) (latLng.latitude * 100);
        int lng = (int) (latLng.longitude * 100);
        List<String> codeList = new ArrayList<>();

        for (int latDelta : surroundings) {
            for (int lngDelta : surroundings) {
                String code = Integer.toString(lat + latDelta) + ":" +  Integer.toString(lng + lngDelta);
                if (!loadedAreas.contains(code)) {
                    codeList.add(code);
                    loadedAreas.add(code);
                }
            }
        }
        String[] codes = new String[codeList.size()];
        codes = codeList.toArray(codes);
        return codes;
    }

    public CatMapViewModel() {
        repository = CatRepository.getInstance();
        cats = new DataDripper<>();
        toastEvent = new MutableLiveData<>();
        loginDialogEvent = new MutableLiveData<>();
        navigateToCatEvent = new MutableLiveData<>();
        navigateToAddCatEvent = new MutableLiveData<>();
    }
}
