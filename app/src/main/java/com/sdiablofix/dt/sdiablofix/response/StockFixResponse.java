package com.sdiablofix.dt.sdiablofix.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buxianhui on 17/10/6.
 */

public class StockFixResponse extends Response {
    @SerializedName("rsn")
    private String rsn;
//    @SerializedName("s_not_db")
//    private Integer stockNotInDB;
//    @SerializedName("s_not_equal_db")
//    private Integer stockNotEqualDB;
//    @SerializedName("s_not_shop")
//    private Integer stockNotShop;
//    @SerializedName("s_not_equal_shop")
//    private Integer stockNotEqualShop;

    public String getRsn() {
        return rsn;
    }
}
