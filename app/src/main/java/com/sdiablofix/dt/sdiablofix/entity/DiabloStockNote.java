package com.sdiablofix.dt.sdiablofix.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buxianhui on 2020/1/28.
 */

public class DiabloStockNote {
    @SerializedName("style_number")
    private String styleNumber;
    @SerializedName("brand_id")
    private Integer brand;
    @SerializedName("color_id")
    private Integer color;
    @SerializedName("size")
    private String size;
    @SerializedName("total")
    private Integer count;
    @SerializedName("shop_id")
    private Integer shop;

    public String getStyleNumber() {
        return styleNumber;
    }

    public Integer getBrand() {
        return brand;
    }

    public Integer getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public Integer getCount() {
        return count;
    }
}
