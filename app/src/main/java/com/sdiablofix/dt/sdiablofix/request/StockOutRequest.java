package com.sdiablofix.dt.sdiablofix.request;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.utils.DiabloUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buxianhui on 2020/1/26.
 */

public class StockOutRequest {
    @SerializedName("base")
    private StockOutBase base;
    @SerializedName("stock")
    private List<StockOut> mStocks;

    public StockOutRequest(StockOutBase base) {
        this.base = base;
        mStocks = new ArrayList<>();
    }

    public void addStock(StockOut stock) {
        boolean exist = false;
        for(StockOut s: mStocks) {
            if (stock.getStyleNumber().equals(s.getStyleNumber())
                && stock.getBrand().equals(s.getBrand())
                && stock.getColor().equals(s.getColor())
                && stock.getSize().equals(s.getSize())) {
                s.setTotal(s.getTotal() + stock.getTotal());
                exist = true;
                break;
            }
        }

        if (!exist) {
            mStocks.add(stock);
        }
    }

    public static class StockOutBase {
        @SerializedName("total")
        private Integer total;
        @SerializedName("shop")
        private Integer shop;
        @SerializedName("ctype")
        private Integer ctype;
        @SerializedName("datetime")
        private String datetime;
        @SerializedName("employee")
        private String employee;

        public StockOutBase() {
            this.datetime = DiabloUtils.currentDatetime();
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public void setShop(Integer shop) {
            this.shop = shop;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public String getShortDatetime() {
            return this.datetime.substring(5);
        }

        public void setEmployee(String employee) {
            this.employee = employee;
        }
    }

    public static class StockOut {
        @SerializedName("style_number")
        private String styleNumber;
        @SerializedName("brand")
        private Integer brand;
        @SerializedName("color")
        private Integer color;
        @SerializedName("size")
        private String size;

        @SerializedName("type")
        private Integer type;
        @SerializedName("firm")
        private Integer firm;
        @SerializedName("season")
        private Integer season;
        @SerializedName("year")
        private Integer year;
        @SerializedName("s_group")
        private String s_group;
        @SerializedName("free")
        private Integer free;
        @SerializedName("alarm_day")
        private Integer alarm_day;

        @SerializedName("path")
        private String path;
        @SerializedName("total")
        private Integer total;

        @SerializedName("tag_price")
        private Float tagPrice;
        @SerializedName("org_price")
        private Float orgPrice;
        @SerializedName("ediscount")
        private Float ediscount;
        @SerializedName("discount")
        private Float discount;

        public String getStyleNumber() {
            return styleNumber;
        }

        public void setStyleNumber(String styleNumber) {
            this.styleNumber = styleNumber;
        }

        public Integer getBrand() {
            return brand;
        }

        public void setBrand(Integer brand) {
            this.brand = brand;
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

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getFirm() {
            return firm;
        }

        public void setFirm(Integer firm) {
            this.firm = firm;
        }

        public Integer getSeason() {
            return season;
        }

        public void setSeason(Integer season) {
            this.season = season;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public String getS_group() {
            return s_group;
        }

        public void setS_group(String s_group) {
            this.s_group = s_group;
        }

        public Integer getFree() {
            return free;
        }

        public void setFree(Integer free) {
            this.free = free;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Float getTagPrice() {
            return tagPrice;
        }

        public void setTagPrice(Float tagPrice) {
            this.tagPrice = tagPrice;
        }

        public Float getOrgPrice() {
            return orgPrice;
        }

        public void setOrgPrice(Float orgPrice) {
            this.orgPrice = orgPrice;
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

        public void setAlarm_day(Integer alarm_day) {
            this.alarm_day = alarm_day;
        }
    }
}
