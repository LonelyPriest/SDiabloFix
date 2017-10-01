package com.sdiablofix.dt.sdiablofix.rest;

import com.sdiablofix.dt.sdiablofix.entity.DiabloBrand;
import com.sdiablofix.dt.sdiablofix.entity.DiabloColor;
import com.sdiablofix.dt.sdiablofix.entity.DiabloColorKind;
import com.sdiablofix.dt.sdiablofix.entity.DiabloSizeGroup;
import com.sdiablofix.dt.sdiablofix.entity.DiabloType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by buxianhui on 17/9/30.
 */

public interface GoodInterface {
    @GET("list_w_color")
    Call<List<DiabloColor>> listColor(@Header("cookie") String token);

    @GET("list_color_type")
    Call<List<DiabloColorKind>> listColorKind(@Header("cookie") String token);

    @GET("list_w_size")
    Call<List<DiabloSizeGroup>> listSizeGroup(@Header("cookie") String token);

    @GET("list_brand")
    Call<List<DiabloBrand>> listBrand(@Header("cookie") String token);

    @GET("list_type")
    Call<List<DiabloType>> listType(@Header("cookie") String token);
}
