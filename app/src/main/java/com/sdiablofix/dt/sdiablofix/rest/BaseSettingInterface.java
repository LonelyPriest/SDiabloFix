package com.sdiablofix.dt.sdiablofix.rest;

import com.sdiablofix.dt.sdiablofix.entity.DiabloBaseSetting;
import com.sdiablofix.dt.sdiablofix.request.LogoutRequest;
import com.sdiablofix.dt.sdiablofix.response.Response;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by buxianhui on 17/9/30.
 */

public interface BaseSettingInterface {
    @GET("list_base_setting")
    Call<List<DiabloBaseSetting>> listBaseSetting(@Header("cookie") String token);

    @POST("destroy_login_user")
    Call<Response> logout(@Header("cookie") String token, @Body LogoutRequest request);
}
