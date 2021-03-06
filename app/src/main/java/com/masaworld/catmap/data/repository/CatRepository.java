package com.masaworld.catmap.data.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.masaworld.catmap.data.FailableData;
import com.masaworld.catmap.data.datasource.remote.CatRemoteDataSource;
import com.masaworld.catmap.data.model.Cat;
import com.masaworld.catmap.data.model.CatComment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;

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

    public abstract LiveData<FailableData<List<CatComment>>> getCatComments(int catId);

    public abstract LiveData<FailableData> createCat(String name, LatLng latLng, Uri imageUri);

    public abstract LiveData<FailableData> createCatComment(String comment, int catId);

    public abstract boolean createCatSync(String name, LatLng latLng, Uri imageUri);

    public abstract boolean createCatImageSync(int catId, Uri imageUri);

    private static class CatRepositoryImpl extends CatRepository {

        private CatRemoteDataSource catRemoteDataSource;

        @Override
        public LiveData<FailableData<List<Cat>>> getCatsByArea(String[] areaCode) {
            return catRemoteDataSource.getCatsByArea(areaCode);
        }

        @Override
        public LiveData<FailableData<Cat>> getCat(int id) {
            return catRemoteDataSource.getCat(id);
        }

        @Override
        public LiveData<FailableData<List<CatComment>>> getCatComments(int catId) {
            return catRemoteDataSource.getCatComments(catId);
        }

        @Override
        public LiveData<FailableData> createCatComment(String comment, int catId) {
            return catRemoteDataSource.createCatComment(
                    TokenRepository.getInstance().getToken(),
                    comment,
                    catId
            );
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

        @Override
        public boolean createCatSync(String name, LatLng latLng, Uri imageUri) {
            return catRemoteDataSource.createCatSync(
                    TokenRepository.getInstance().getToken(),
                    name,
                    latLng.latitude,
                    latLng.longitude,
                    createAreaCode(latLng),
                    imageUri
            );
        }

        @Override
        public boolean createCatImageSync(int catId, Uri imageUri) {
            return catRemoteDataSource.createCatImageSync(
                    TokenRepository.getInstance().getToken(),
                    catId,
                    imageUri
            );
        }

        private String createAreaCode(LatLng latLng) {
            int lat = (int) (10 * latLng.latitude);
            int lng = (int) (10 * latLng.longitude);
            return Integer.toString(lat) + ":" + Integer.toString(lng);
        }

        CatRepositoryImpl(Context appContext) {
            this.catRemoteDataSource = new CatRemoteDataSource(appContext);
        }

    }

}
