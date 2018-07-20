package com.masaworld.catmap.data.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.masaworld.catmap.data.FailableData;
import com.masaworld.catmap.data.datasource.remote.CatRemoteDataSource;
import com.masaworld.catmap.data.model.Cat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class CatRepository {

    private static CatRepositoryImpl instance;

    public static CatRepository getInstance() {
        if (instance == null) {
            throw new RuntimeException("init() must be called before getting the instance.");
        }
        return instance;
    }

    public static void init(Context appContext) {
        instance = new CatRepositoryImpl(appContext);
    }

    public abstract LiveData<FailableData<List<Cat>>> getCatsByArea(String[] areaCode);

    public abstract LiveData<FailableData<Cat>> getCat(int id);

    public abstract LiveData<FailableData> createCat(String name, LatLng latLng, Uri imageUri);

    private static class CatRepositoryImpl extends CatRepository {

        private CatRemoteDataSource catRemoteDataSource;
        private ExecutorService executorService;
        private Context appContext;

        @Override
        public LiveData<FailableData<List<Cat>>> getCatsByArea(String[] areaCode) {
            return catRemoteDataSource.getCatsByArea(areaCode);
        }

        @Override
        public LiveData<FailableData<Cat>> getCat(int id) {
            return catRemoteDataSource.getCat(id);
        }

        @Override
        public LiveData<FailableData> createCat(String name, LatLng latLng, Uri imageUri) {
            return catRemoteDataSource.createCat(
                    TokenRepository.getInstance().getToken(),
                    name,
                    latLng.latitude,
                    latLng.longitude,
                    createAreaCode(latLng),
                    imageUri
            );
        }

        private String createAreaCode(LatLng latLng) {
            int lat = (int) (100 * latLng.latitude);
            int lng = (int) (100 * latLng.longitude);
            return Integer.toString(lat) + ":" + Integer.toString(lng);
        }

        CatRepositoryImpl(Context appContext) {
            this.appContext = appContext;
            this.catRemoteDataSource = new CatRemoteDataSource(appContext);
            this.executorService = Executors.newSingleThreadExecutor();
        }

    }

}
