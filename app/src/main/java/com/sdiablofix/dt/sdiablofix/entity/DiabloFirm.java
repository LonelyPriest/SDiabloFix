package com.sdiablofix.dt.sdiablofix.entity;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloFirm extends DiabloEntity{
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("address")
    private String address;
    @SerializedName("balance")
    private Float balance;
    @SerializedName("entry_date")
    private String datetime;
    @SerializedName("py")
    private String py;

    public DiabloFirm() {
        init();
    }

    public DiabloFirm(DiabloFirm firm) {
        this.id = firm.getId();
        this.name = firm.getName();
        this.mobile = firm.getMobile();
        this.address = firm.getAddress();
        this.balance = firm.getBalance();
        this.datetime = firm.getDatetime();
        this.py = firm.getPy();
    }

    public DiabloFirm(String name) {
        this.name = name;
        init();
    }

    private void init() {
        id = 0;
        balance = 0f;
        mobile = DiabloEnum.EMPTY_STRING;
        address = DiabloEnum.EMPTY_STRING;
        datetime = DiabloEnum.EMPTY_STRING;
        py = DiabloEnum.EMPTY_STRING;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
