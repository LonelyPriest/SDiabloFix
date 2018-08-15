package com.sdiablofix.dt.sdiablofix.request;

import com.google.gson.annotations.SerializedName;
import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

/**
 * Created by buxianhui on 17/10/4.
 */

public class GetStockByBarcodeRequest {
    @SerializedName("barcode")
    private String barcode;
    @SerializedName("shop")
    private Integer shop;
    @SerializedName("ctype")
    private Integer ctype;

    public GetStockByBarcodeRequest(String barcode, Integer shop, Integer ctype) {
        this.barcode = barcode;
        this.shop = shop;
        this.ctype = ctype;
    }
}
