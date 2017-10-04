package com.sdiablofix.dt.sdiablofix.response;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.entity.DiabloBarcodeStock;

/**
 * Created by buxianhui on 17/10/4.
 */

public class GetStockByBarcodeResponse extends Response {
    @SerializedName("stock")
    private DiabloBarcodeStock barcodeStock;

    public DiabloBarcodeStock getBarcodeStock() {
        return barcodeStock;
    }
}
