package com.sdiablofix.dt.sdiablofix.response;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.entity.DiabloStockNote;

/**
 * Created by buxianhui on 2020/1/28.
 */

public class GetStockNoteResponse extends Response {
    @SerializedName("data")
    private DiabloStockNote stockNote;

    public DiabloStockNote getStockNote() {
        return stockNote;
    }
}
