package com.sdiablofix.dt.sdiablofix.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buxianhui on 17/10/4.
 */

public class GetStockByBarcodeRequest {
    @SerializedName("barcode")
    private String barcode;
    @SerializedName("shop")
    private Integer shop;

    public GetStockByBarcodeRequest(String barcode, Integer shop) {
        this.barcode = barcode;
        this.shop = shop;
    }
}
