package com.sdiablofix.dt.sdiablofix.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buxianhui on 2020/1/28.
 */

public class GetStockNoteRequest {
    @SerializedName("shop")
    private Integer shop;
    @SerializedName("style_number")
    private String styleNumber;
    @SerializedName("brand")
    private Integer brand;
    @SerializedName("color")
    private Integer color;
    @SerializedName("size")
    private String size;

    public GetStockNoteRequest(Integer shop, String styleNumber, Integer brand, Integer color, String size) {
        this.shop = shop;
        this.styleNumber = styleNumber;
        this.brand = brand;
        this.color = color;
        this.size = size;
    }
}
