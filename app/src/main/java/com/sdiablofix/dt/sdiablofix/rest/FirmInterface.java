package com.sdiablofix.dt.sdiablofix.rest;

import com.sdiablofix.dt.sdiablofix.entity.DiabloFirm;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by buxianhui on 17/9/30.
 */

public interface FirmInterface {
    @GET("list_firm")
    Call<List<DiabloFirm>> listFirm(@Header("cookie") String token);
}
