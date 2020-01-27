package com.sdiablofix.dt.sdiablofix.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buxianhui on 2020/1/26.
 */

public class StockOutResponse extends Response {
    @SerializedName("rsn")
    private String rsn;
    public String getRsn() {
        return rsn;
    }
}
