package com.sdiablofix.dt.sdiablofix.rest;

import com.sdiablofix.dt.sdiablofix.entity.DiabloEmployee;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by buxianhui on 17/9/30.
 */

public interface EmployeeInterface {
    @GET("list_employe")
    Call<List<DiabloEmployee>> listEmployee(@Header("cookie") String token);
}
