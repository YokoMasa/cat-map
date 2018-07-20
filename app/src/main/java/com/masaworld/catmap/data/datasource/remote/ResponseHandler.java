package com.masaworld.catmap.data.datasource.remote;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.masaworld.catmap.data.Error;
import com.masaworld.catmap.data.FailableData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Reader;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResponseHandler<T> implements Callback<T> {
    private FailableData<T> failableData;
    private MutableLiveData<FailableData<T>> mutableLiveData;

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        handleResponse(failableData, response);
        mutableLiveData.setValue(failableData);
        Log.i("catdebug", "request: " + call.request().toString());
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        handleConnectionError(failableData, t);
        mutableLiveData.setValue(failableData);
    }

    private void handleResponse(FailableData failableData, Response response) {
        //Log.i("catdebug", "response code: " + response.code());
        if (200 <= response.code() && response.code() < 400) {
            failableData.data = response.body();
        } else {
            failableData.failed = true;
            ResponseBody errorBody = response.errorBody();
            failableData.error = errorBody == null ? Error.UNEXPECTED : getError(errorBody.charStream());
        }
    }

    private void handleConnectionError(FailableData failableData, Throwable e) {
        e.printStackTrace();
        failableData.failed = true;
        failableData.error = Error.SERVER_CONNECTION;
    }

    private Error getError(Reader errorStream) {
        try {
            BufferedReader reader = new BufferedReader(errorStream);
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }

            Log.i("catdebug", "error body: " + sb.toString());
            JSONObject jo = new JSONObject(sb.toString());
            int errorCode = jo.getInt("error_code");
            //return getErrorFromErrorCode(errorCode);
            return Error.SERVER_CONNECTION;
        } catch (Exception e) {
            return Error.UNEXPECTED;
        }
    }

    private Error getErrorFromErrorCode(int code) {
        switch (code) {
            case 1000:
                return Error.SERVER_CONNECTION;
            case 1001:
                return Error.LOGIN_FAILED;
            default:
                return Error.UNEXPECTED;
        }
    }

    public ResponseHandler(FailableData<T> failableData, MutableLiveData<FailableData<T>> mutableLiveData) {
        this.failableData = failableData;
        this.mutableLiveData = mutableLiveData;
    }
}

