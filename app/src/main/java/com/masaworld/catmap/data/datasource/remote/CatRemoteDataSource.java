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
import okhttp3.ResponseBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatRemoteDataSource {

    private static final MediaType MEDIA_TEXT = MediaType.parse("text/plain");
    private static final MediaType MEDIA_IMAGE = MediaType.parse("image/*");
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
        Call<ResponseBody> call = createCatCall(token, name, latitude, longitude, areaCode, imageUri);
        call.enqueue(new ResponseHandler(failableData, liveData));
        return liveData;
    }

    public boolean createCatSync(String token, String name, double latitude, double longitude, String areaCode, Uri imageUri) {
        Call<ResponseBody> call = createCatCall(token, name, latitude, longitude, areaCode, imageUri);
        try {
            Response response = call.execute();
            return response.code() == 200;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    private Call<ResponseBody> createCatCall(String token, String name, double latitude, double longitude, String areaCode, Uri imageUri) {
        RequestBody rbName = RequestBody.create(MEDIA_TEXT, name);
        RequestBody rbLongitude = RequestBody.create(MEDIA_TEXT, Double.toString(longitude));
        RequestBody rbLatitude = RequestBody.create(MEDIA_TEXT, Double.toString(latitude));
        RequestBody rbAreaCode = RequestBody.create(MEDIA_TEXT, areaCode);
        RequestBody rbCatImage = createRequestBodyFromUri(MEDIA_IMAGE, imageUri);
        MultipartBody.Part part = MultipartBody.Part.createFormData("cat_image", "image.jpeg", rbCatImage);
        return catMapService.createCat(token, rbName, rbLatitude, rbLongitude, rbAreaCode, part);
    }

    public boolean createCatImageSync(String token, int catId, Uri imageUri) {
        Call<ResponseBody> call = createCatImageCall(token, catId, imageUri);
        try {
            Response response = call.execute();
            return response.code() == 200;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    private Call<ResponseBody> createCatImageCall(String token, int catId, Uri imageUri) {
        RequestBody rbCatImage = createRequestBodyFromUri(MEDIA_IMAGE, imageUri);
        MultipartBody.Part part = MultipartBody.Part.createFormData("cat_image", "image.jpeg", rbCatImage);
        return catMapService.createCatImage(token, catId, part);
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
