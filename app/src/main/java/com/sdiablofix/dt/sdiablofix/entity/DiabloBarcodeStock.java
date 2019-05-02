package com.sdiablofix.dt.sdiablofix.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buxianhui on 17/10/4.
 */

public class DiabloBarcodeStock {
    @SerializedName("id")
    private Integer stockId;
    @SerializedName("fix_pos")
    private Integer fixPos;
    @SerializedName("order_id")
    private Integer orderId;

    @SerializedName("bcode")
    private String barcode;
    @SerializedName("correct_barcode")
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
    @SerializedName("ediscount")
    private Float eDiscount;

    @SerializedName("amount")
    private Integer amount;

    @SerializedName("discount")
    private Float discount;
    @SerializedName("path")
    private String path;
    @SerializedName("entry_date")
    private String entryDate;
    @SerializedName("brand")
    private String brand;
    @SerializedName("type")
    private String type;

    @SerializedName("color")
    private Integer color;
    @SerializedName("size")
    private String size;
    @SerializedName("fix")
    private Integer fix;

    public Integer getStockId() {
        return stockId;
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

    public Integer getSex() {
        return sex;
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

    public Float getTagPrice() {
        return tagPrice;
    }

    public void setTagPrice(Float tagPrice) {
        this.tagPrice = tagPrice;
    }

    public Float geteDiscount() {
        return eDiscount;
    }

    public Float getDiscount() {
        return discount;
    }

    public String getPath() {
        return path;
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
}
