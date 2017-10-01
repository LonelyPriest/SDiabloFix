package com.sdiablofix.dt.sdiablofix.entity;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloColorKind extends DiabloEntity {
    @SerializedName("id")
    private Integer kindId;
    @SerializedName("name")
    private String kindName;

    public DiabloColorKind() {
        kindId = DiabloEnum.INVALID_INDEX;
        kindName = DiabloEnum.EMPTY_STRING;
    }

    public Integer getId() {
        return kindId;
    }

    @Override
    public String getName() {
        return kindName;
    }

    @Override
    public String getViewName() {
        return kindName;
    }
}
