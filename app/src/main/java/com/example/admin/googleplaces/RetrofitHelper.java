package com.example.admin.googleplaces;

import android.util.Log;

import com.example.admin.googleplaces.model.PlaceResults;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import static android.content.ContentValues.TAG;

/**
 * Created by Admin on 11/2/2017.
 */

public class RetrofitHelper {
    public static final String BASE_URL = "https://maps.googleapis.com";

    static public Retrofit create(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }

    static public Observable<PlaceResults> getCall(Map<String, String> query){
        Retrofit retrofit = create();
        RequestService service = retrofit.create(RequestService.class);
        Log.d(TAG, "getCall: " + query);
        return service.responseService(query);
    }

    public interface RequestService{

        @GET("/maps/api/place/nearbysearch/json")
        Observable<PlaceResults> responseService(@QueryMap Map<String, String> query);
    }
}
