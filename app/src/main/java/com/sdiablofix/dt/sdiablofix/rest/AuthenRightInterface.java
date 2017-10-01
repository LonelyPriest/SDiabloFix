package com.sdiablofix.dt.sdiablofix.rest;

import com.sdiablofix.dt.sdiablofix.response.LoginUserInfoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by buxianhui on 17/9/30.
 */

public interface AuthenRightInterface {
    @GET("get_login_user_info")
    Call<LoginUserInfoResponse> getLoginUserInfo(@Header("cookie") String token);
}
