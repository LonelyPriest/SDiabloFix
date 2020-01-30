package com.sdiablofix.dt.sdiablofix.entity;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

/**
 * Created by buxianhui on 17/10/4.
 */

public class DiabloBarcodeStock {
    private Integer orderId;
    private Integer fixPos;
    private Integer csFixPos;

    @SerializedName("bcode")
    private String barcode;
    private String correctBarcode;

    @SerializedName("style_number")
    private String styleNumber;
    @SerializedName("brand_id")
    private Integer brandId;
    @SerializedName("type_id")
    private Integer typeId;
    @SerializedName("sex")
    private Integer sex;
    @SerializedName("season")
    private Integer season;
    @SerializedName("firm_id")
    private Integer firmId;
    @SerializedName("s_group")
    private String sGroup;
    @SerializedName("free")
    private Integer free;
    @SerializedName("year")
    private Integer year;
    @SerializedName("pid")
    private Integer pid;
    @SerializedName("sid")
    private Integer sid;
    @SerializedName("org_price")
    private Float orgPrice;
    @SerializedName("tag_price")
    private Float tagPrice;

    // total with style number
    @SerializedName("amount")
    private Integer amount;

    @SerializedName("discount")
    private Float discount;
    @SerializedName("ediscount")
    private Float ediscount;
    @SerializedName("path")
    private String path;
    @SerializedName("entry_date")
    private String entryDate;
    @SerializedName("brand")
    private String brand;
    @SerializedName("type")
    private String type;
    @SerializedName("alarm_day")
    private Integer alarm_day;

    private Integer color;
    private String size;
    private Integer fix;
    // total with color and size
    private Integer count;

    public DiabloBarcodeStock() {
        orderId  = 0;
        fixPos   = 0;
        csFixPos = 0;

        color = DiabloEnum.INVALID_INDEX;
        size  = DiabloEnum.EMPTY_STRING;
        fix   = 0;
        count = 0;
    }

    public DiabloBarcodeStock(DiabloBarcodeStock stock) {
        this.orderId = 0;
        this.fixPos = 0;
        this.csFixPos = 0;

        this.barcode = stock.getBarcode();
        // this.correctBarcode = stock.getCorrectBarcode();

        this.styleNumber = stock.getStyleNumber();
        this.brandId = stock.getBrandId();
        this.typeId = stock.getTypeId();
        this.sex = stock.getSex();
        this.season = stock.getSeason();
        this.firmId = stock.getFirmId();
        this.sGroup = stock.getsGroup();
        this.free = stock.getFree();
        this.year = stock.getYear();
        this.pid = stock.getPid();
        this.sid = stock.getSid();
        this.orgPrice = stock.getOrgPrice();
        this.tagPrice = stock.getTagPrice();

        this.amount = stock.getAmount();

        this.discount = stock.getDiscount();
        this.ediscount = stock.getEdiscount();
        this.path = stock.getPath();
        this.entryDate = stock.getEntryDate();
        this.brand = stock.getBrand();
        this.type = stock.getType();
        this.alarm_day = stock.getAlarm_day();

        this.color = DiabloEnum.INVALID_INDEX;
        this.size = DiabloEnum.EMPTY_STRING;
        this.fix = 0;
        this.count = stock.getCount();
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCorrectBarcode() {
        return correctBarcode;
    }

    public void setCorrectBarcode(String correctBarcode) {
        this.correctBarcode = correctBarcode;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getFixPos() {
        return fixPos;
    }

    public void setFixPos(Integer fixPos) {
        this.fixPos = fixPos;
    }

    public Integer getCSFixPos() {
        return csFixPos;
    }

    public void setCSFixPos(Integer csFixPos) {
        this.csFixPos = csFixPos;
    }

    public String getStyleNumber() {
        return styleNumber;
    }

    public void setStyleNumber(String styleNumber) {
        this.styleNumber = styleNumber;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getAlarm_day() {
        return alarm_day;
    }

    public void setAlarm_day(Integer alarm_day) {
        this.alarm_day = alarm_day;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public Integer getFirmId() {
        return firmId;
    }

    public void setFirmId(Integer firmId) {
        this.firmId = firmId;
    }

    public String getsGroup() {
        return sGroup;
    }

    public void setsGroup(String group) {
        this.sGroup = group;
    }

    public Integer getFree() {
        return free;
    }

    public void setFree(Integer free) {
        this.free = free;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getPid() {
        return pid;
    }

    public Integer getSid() {
        return sid;
    }

    public Float getOrgPrice() {
        return orgPrice;
    }

    public void setOrgPrice(Float orgPrice) {
        this.orgPrice = orgPrice;
    }

    public Float getTagPrice() {
        return tagPrice;
    }

    public void setTagPrice(Float tagPrice) {
        this.tagPrice = tagPrice;
    }

    public Float getEdiscount() {
        return ediscount;
    }

    public void setEdiscount(Float ediscount) {
        this.ediscount = ediscount;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getBrand() {
        return brand;
    }

    public String getType() {
        return type;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getFix() {
        return fix;
    }

    public void setFix(Integer fix) {
        this.fix = fix;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
