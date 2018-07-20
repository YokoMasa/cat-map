package com.masaworld.catmap.data.datasource.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.masaworld.catmap.Config;
import com.masaworld.catmap.data.service.CatMapService;
import com.masaworld.catmap.data.FailableData;
import com.masaworld.catmap.data.model.Cat;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatRemoteDataSource {

    private static final String MIME_TEXT = "text/plain";
    private static final String MIME_IMAGE = "image/*";
    private CatMapService catMapService;
    private Context appContext;

    public CatRemoteDataSource(Context appContext) {
        this.appContext = appContext;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(MyOkHttpClient.getInstance())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        catMapService = retrofit.create(CatMapService.class);
    }

    public LiveData<FailableData<Cat>> getCat(int id) {
        MutableLiveData<FailableData<Cat>> cat = new MutableLiveData<>();
        FailableData<Cat> data = new FailableData<>();
        catMapService.getCat(id).enqueue(new ResponseHandler<>(data, cat));
        return cat;
    }

    public LiveData<FailableData<List<Cat>>> getCatsByArea(String[] areaCode) {
        MutableLiveData<FailableData<List<Cat>>> catList = new MutableLiveData<>();
        FailableData<List<Cat>> data = new FailableData<>();
        catMapService.getCatList(areaCode).enqueue(new ResponseHandler<>(data, catList));
        return catList;
    }

    public LiveData<FailableData> createCat(String token, String name, double latitude, double longitude, String areaCode, Uri imageUri) {
        MutableLiveData<FailableData> liveData = new MutableLiveData<>();
        FailableData failableData = new FailableData();
        RequestBody rbName = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody rbLongitude = RequestBody.create(MediaType.parse("text/plain"), Double.toString(longitude));
        RequestBody rbLatitude = RequestBody.create(MediaType.parse("text/plain"), Double.toString(latitude));
        RequestBody rbAreaCode = RequestBody.create(MediaType.parse("text/plain"), areaCode);
        RequestBody rbCatImage = createRequestBodyFromUri(MediaType.parse("image/*"), imageUri);
        MultipartBody.Part part = MultipartBody.Part.createFormData("cat_image", "image.jpeg", rbCatImage);
        catMapService.createCat(token, rbName, rbLatitude, rbLongitude, rbAreaCode, part).enqueue(new ResponseHandler(failableData, liveData));
        return liveData;
    }

    private RequestBody createRequestBodyFromUri(MediaType mediaType, Uri uri) {
        RequestBody requestBody = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                InputStream is = appContext.getContentResolver().openInputStream(uri);
                if (is == null) {
                    return;
                }
                int count = 0;
                int i = is.read();
                while (i != -1) {
                    sink.writeByte(i);
                    i = is.read();
                    count++;
                    if (1024 * 100 < count) {
                        count = 0;
                        sink.emit();
                    }
                }
                is.close();
                sink.flush();
            }
        };
        return requestBody;
    }

}
