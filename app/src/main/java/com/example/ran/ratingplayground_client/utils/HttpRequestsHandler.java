package com.example.ran.ratingplayground_client.utils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequestsHandler {

    public interface ResponseListener{
        void onSuccess(String myResponse , String event);
        void onFailed(String error,String event);
    }

    private static  HttpRequestsHandler sInstance;
    private OkHttpClient mClient;
    private ResponseListener mListener;

    private HttpRequestsHandler() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(25, TimeUnit.SECONDS);
        builder.readTimeout(25, TimeUnit.SECONDS);
        builder.writeTimeout(25, TimeUnit.SECONDS);
        mClient = builder.build();
//        mClient = new OkHttpClient();
    }

    public static HttpRequestsHandler getInstance() {
        if (sInstance == null) {
            sInstance = new HttpRequestsHandler();
        }
        return sInstance;
    }

    public void setResponseListener (ResponseListener listener) {
        mListener = listener;
    }




    public void putRequest(String url , final String event , JSONObject jsonObject) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                e.printStackTrace();
                final String error = e.toString();
                if (mListener != null)
                    mListener.onFailed(error , event);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    final String errorBodyString = response.body().string();
                    if (mListener != null)
                        mListener.onFailed(errorBodyString , event);
                } else {
                    final String myResponse = response.body().string();
                    if (mListener != null)
                        mListener.onSuccess(myResponse , event);

                }
            }
        });
    }


    public void postRequest(String url ,final String event, JSONObject jsonObject ) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                e.printStackTrace();
                final String error = e.toString();
                if (mListener != null)
                    mListener.onFailed(error , event);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    final String errorBodyString = response.body().string();
                    if (mListener != null)
                        mListener.onFailed(errorBodyString , event);
                } else {
                    final String myResponse = response.body().string();
                    if (mListener != null)
                        mListener.onSuccess(myResponse , event);

                }
            }
        });
    }


    public void getRequest(String url , final String event) {
        final Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    final String errorBodyString = response.body().string();
                    if (mListener != null){
                        mListener.onFailed(errorBodyString , event);
                    }
                } else {
                    final String myResponse = response.body().string();
                    if (mListener != null){
                        mListener.onSuccess(myResponse , event);
                    }
                }
            }
        });
    }
}
