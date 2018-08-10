package com.sdiablofix.dt.sdiablofix.entity;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

/**
 * Created by buxianhui on 18/8/10.
 */

public class DiabloBigType {
    @SerializedName("ctype")
    private Integer ctype;
    @SerializedName("name")
    private String name;

    public DiabloBigType() {
        ctype = DiabloEnum.INVALID_INDEX;
    }

    public DiabloBigType(String name) {
        this.ctype = DiabloEnum.INVALID_INDEX;
        this.name = name;
    }

    public Integer getctype() {
        return ctype;
    }

    public String getName() {
        return name;
    }
}
