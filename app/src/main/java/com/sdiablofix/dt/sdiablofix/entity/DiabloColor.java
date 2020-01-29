package com.sdiablofix.dt.sdiablofix.entity;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

import java.util.List;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloColor extends DiabloEntity{
    @SerializedName("id")
    private Integer colorId;
    @SerializedName("bcode")
    private Integer colorBarcode;
    @SerializedName("name")
    private String name;
    @SerializedName("tid")
    private Integer typeId;
    @SerializedName("type")
    private String typeName;
    @SerializedName("remark")
    private String remark;

    /**
     * default color is free color, notice do not changed
     */
    DiabloColor(){
        this.colorId = DiabloEnum.DIABLO_FREE_COLOR;
        this.name = "F/";
    }

    public DiabloColor(String name, Integer typeId) {
        this.name = name;
        this.typeId = typeId;
        this.colorId = DiabloEnum.INVALID_INDEX;
    }

    public DiabloColor(Integer colorId){
        this.colorId = colorId;
    }

    public Integer getColorId() {
        return colorId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getViewName() {
        return name;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public Integer getColorBarcode() {
        return colorBarcode;
    }

    public boolean includeIn(List<DiabloColor> colors){
        boolean include = false;
        for (int i = 0; i < colors.size(); i++) {
            if (colors.get(i).getColorId().equals(colorId)){
                include = true;
                break;
            }
        }
        return include;
    }
}
