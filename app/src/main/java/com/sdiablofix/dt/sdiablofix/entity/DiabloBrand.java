package com.sdiablofix.dt.sdiablofix.entity;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloBrand extends DiabloEntity{
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("supplier_id")
    private Integer firmId;
    @SerializedName("supplier")
    private String firm;
    @SerializedName("py")
    private String py;

    public DiabloBrand() {
        name = DiabloEnum.EMPTY_STRING;
        init();
    }

    public DiabloBrand(String name) {
        this.name = name;
        init();
    }

    public DiabloBrand(DiabloBrand brand) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.firmId = brand.getFirmId();
        this.firm = brand.getFirm();
        this.py = DiabloEnum.EMPTY_STRING;
    }

    private void init() {
        id = DiabloEnum.INVALID_INDEX;
        firmId = DiabloEnum.INVALID_INDEX;
        firm = DiabloEnum.EMPTY_STRING;
        py = DiabloEnum.EMPTY_STRING;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getFirmId() {
        return firmId;
    }

    public void setFirmId(Integer firmId) {
        this.firmId = firmId;
    }

    public String getFirm() {
        return firm;
    }

    public String getPy() {
        return py;
    }

    public void setPy(String py) {
        this.py = py;
    }


}
