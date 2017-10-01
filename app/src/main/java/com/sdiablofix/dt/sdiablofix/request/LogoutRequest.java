package com.sdiablofix.dt.sdiablofix.request;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

/**
 * Created by buxianhui on 17/9/30.
 */

public class LogoutRequest {
    @SerializedName("operation")
    private String operation;
    @SerializedName("tablet")
    private Integer tablet;

    public LogoutRequest(String operation) {
        this.operation = operation;
        this.tablet = DiabloEnum.TABLET;
    }
}
