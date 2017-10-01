package com.sdiablofix.dt.sdiablofix.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buxianhui on 17/9/30.
 */

public class MatchStockRequest {
    @SerializedName("shop")
    private Integer shop;
    @SerializedName("qtype")
    private Integer queryType;

    public MatchStockRequest(Integer shop, Integer queryType){
        this.shop = shop;
        this.queryType = queryType;
    }

    public Integer getShop() {
        return shop;
    }

    public void setShop(Integer shop) {
        this.shop = shop;
    }

    public Integer getQueryType() {
        return queryType;
    }

    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
    }
}
