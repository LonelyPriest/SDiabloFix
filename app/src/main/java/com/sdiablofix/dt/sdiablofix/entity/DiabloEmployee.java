package com.sdiablofix.dt.sdiablofix.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloEmployee extends DiabloEntity{
    @SerializedName("id")
    private Integer Id;
    @SerializedName("name")
    private String name;
    @SerializedName("number")
    private String number;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("sex")
    private Integer sex;
    @SerializedName("address")
    private String address;
    @SerializedName("entry")
    private String entry;
    @SerializedName("position")
    private Integer position;



    public Integer getId() {
        return this.Id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getViewName() {
        return name;
    }

    public String getNumber() {
        return this.number;
    }

    public String getMobile() {
        return this.mobile;
    }

    public Integer getSex() {
        return this.sex;
    }

    public String getAddress() {
        return this.address;
    }

    public String getEntry() {
        return this.entry;
    }

    public Integer getPosition() {
        return this.position;
    }



    public DiabloEmployee() {

    }
}
