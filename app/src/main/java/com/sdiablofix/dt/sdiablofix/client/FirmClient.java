package com.sdiablofix.dt.sdiablofix.client;

import com.sdiablofix.dt.sdiablofix.entity.DiabloProfile;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by buxianhui on 17/9/30.
 */

public class FirmClient {
    private static Retrofit retrofit;
    private static final  String mUrl = "firm/";

    private FirmClient(){

    }

    public static Retrofit getClient() {
        String baseUrl = DiabloProfile.instance().getServer() + mUrl;
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit;
    }

    public static void resetClient() {
        retrofit = null;
    }
}
