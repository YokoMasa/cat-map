package com.masaworld.catmap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.masaworld.catmap.R;
import com.masaworld.catmap.data.DataDripper;
import com.masaworld.catmap.data.model.Cat;
import com.masaworld.catmap.data.repository.CatRepository;
import com.masaworld.catmap.data.repository.TokenRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CatMapViewModel extends ViewModel {

    private static final float ZOOM_THRESHOLD = 13;
    private final int[] surroundings = new int[]{1, 0, -1};
    private CatRepository repository;
    private DataDripper<Cat> cats;
    private Set<String> loadedAreas = new HashSet<>();
    private MutableLiveData<ViewEvent<Integer>> toastEvent;
    private MutableLiveData<ViewEvent> loginDialogEvent;
    private MutableLiveData<ViewEvent> reloadEvent;
    private MutableLiveData<ViewEvent<Integer>> navigateToCatEvent;
    private MutableLiveData<ViewEvent<LatLng>> navigateToAddCatEvent;
    private MutableLiveData<ViewEvent> currentLocationEvent;
    private MutableLiveData<ViewEvent<Integer>> changeNavigationMenuEvent;
    private MutableLiveData<ViewEvent> logoutFromGoogleEvent;
    private MutableLiveData<ViewEvent<Boolean>> zoomWarningEvent;

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

    public LiveData<ViewEvent> getReloadEvent() { return reloadEvent; }

    public LiveData<ViewEvent> getCurrentLocationEvent() { return currentLocationEvent; }

    public LiveData<ViewEvent<Integer>> getChangeNavigationMenuEvent() { return changeNavigationMenuEvent; }

    public LiveData<ViewEvent> getLogoutFromGoogleEvent() { return logoutFromGoogleEvent; }

    public LiveData<ViewEvent<Boolean>> getZoomWarningEvent() { return zoomWarningEvent; }

    public void loadCatsIfPossible(LatLng newPosition, float zoom) {
        ViewEvent<Boolean> e = zoomWarningEvent.getValue();
        boolean showed = e == null ? false : e.getPayload();
        if (zoom < ZOOM_THRESHOLD) {
            if (!showed) {
                zoomWarningEvent.setValue(new ViewEvent<>(true));
            }
        } else {
            if (showed) {
                zoomWarningEvent.setValue(new ViewEvent<>(false));
            }
            loadCats(newPosition);
        }
    }

    private void loadCats(LatLng position) {
        String[] areaCodes = getSurroundingAreaCode(position);
        if (areaCodes.length != 0) {
            repository.getCatsByArea(areaCodes).observeForever(listFailableData -> {
                if (listFailableData != null) {
                    if (listFailableData.failed) {
                        toastEvent.setValue(new ViewEvent<>(listFailableData.error.messageId));
                    } else {
                        cats.add(listFailableData.data);
                    }
                }
            });
        }
    }

    public void loadNavMenu() {
        if (TokenRepository.getInstance().hasToken()) {
            changeNavigationMenuEvent.setValue(new ViewEvent<>(R.menu.main_drawer_menu));
        } else {
            changeNavigationMenuEvent.setValue(new ViewEvent<>(R.menu.main_drawer_menu_loggedout));
        }
    }

    public void logout() {
        TokenRepository.getInstance().deleteToken();
        logoutFromGoogleEvent.setValue(new ViewEvent(null));
        toastEvent.setValue(new ViewEvent<>(R.string.did_logout));
        changeNavigationMenuEvent.setValue(new ViewEvent<>(R.menu.main_drawer_menu_loggedout));
    }

    public void addNewCat(LatLng latLng) {
        if (TokenRepository.getInstance().hasToken()) {
            navigateToAddCatEvent.setValue(new ViewEvent<>(latLng));
        } else {
            loginDialogEvent.setValue(new ViewEvent(null));
        }
    }

    public void showCat(int id) {
        ViewEvent<Integer> e = new ViewEvent<>(id);
        navigateToCatEvent.setValue(e);
    }

    public void notifyCatPostResult(boolean result) {
        if (result) {
            toastEvent.setValue(new ViewEvent<>(R.string.added_cat));
            reloadEvent.setValue(new ViewEvent(null));
        } else {
            toastEvent.setValue(new ViewEvent<>(R.string.failed_to_add_cat));
        }
    }

    public void moveToCurrentLocation() {
        toastEvent.setValue(new ViewEvent<>(R.string.finding_current_location));
        currentLocationEvent.setValue(new ViewEvent(null));
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
        reloadEvent = new MutableLiveData<>();
        currentLocationEvent = new MutableLiveData<>();
        changeNavigationMenuEvent = new MutableLiveData<>();
        logoutFromGoogleEvent = new MutableLiveData<>();
        zoomWarningEvent = new MutableLiveData<>();
    }
}
