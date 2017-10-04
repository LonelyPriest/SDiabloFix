package com.sdiablofix.dt.sdiablofix.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buxianhui on 17/10/4.
 */

public class DiabloBarcodeStock {
    @SerializedName("id")
    private Integer stockId;
    @SerializedName("bcode")
    private String barcode;
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

    public Integer getStockId() {
        return stockId;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getStyleNumber() {
        return styleNumber;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public Integer getSex() {
        return sex;
    }

    public Integer getSeason() {
        return season;
    }

    public Integer getFirmId() {
        return firmId;
    }

    public String getsGroup() {
        return sGroup;
    }

    public Integer getFree() {
        return free;
    }

    public Integer getYear() {
        return year;
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
}
