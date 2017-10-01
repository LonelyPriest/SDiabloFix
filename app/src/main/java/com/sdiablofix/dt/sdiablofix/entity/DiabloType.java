package com.sdiablofix.dt.sdiablofix.entity;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloType extends DiabloEntity {
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("py")
    private String py;

    public DiabloType() {
        id = DiabloEnum.INVALID_INDEX;
        name = DiabloEnum.EMPTY_STRING;
        py = DiabloEnum.EMPTY_STRING;
    }

    public DiabloType(DiabloType type) {
        this.id = type.getId();
        this.name = type.getName();
    }

    public DiabloType(String name) {
        this.id = DiabloEnum.INVALID_INDEX;
        this.name = name;
        this.py = DiabloEnum.EMPTY_STRING;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPy() {
        return py;
    }

    public void setPy(String py) {
        this.py = py;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getViewName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
