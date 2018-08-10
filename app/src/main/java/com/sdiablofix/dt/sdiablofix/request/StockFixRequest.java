package com.sdiablofix.dt.sdiablofix.request;

import com.google.gson.annotations.SerializedName;

import com.sdiablofix.dt.sdiablofix.utils.DiabloUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buxianhui on 17/10/6.
 */

public class StockFixRequest {
    @SerializedName("base")
    private StockFixBase mFixBase;
    @SerializedName("stock")
    private List<StockFix> mStocks;

    public StockFixRequest(StockFixBase base) {
        this.mFixBase = base;
        mStocks = new ArrayList<>();
    }

    public void addStock(StockFix stock) {
        boolean exist = false;
        for(StockFix s: mStocks) {
            if (stock.getStyleNumber().equals(s.getStyleNumber())
                && stock.getBrand().equals(s.getBrand())
                && stock.getColor().equals(s.getColor())
                && stock.getSize().equals(s.getSize())) {
                s.setFix(s.getFix() + stock.getFix());
                exist = true;
                break;
            }
        }

        if (!exist) {
            mStocks.add(stock);
        }
    }

    public static class StockFixBase {
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

        public StockFixBase() {
            this.datetime = DiabloUtils.currentDatetime();
        }


        public void setTotal(Integer total) {
            this.total = total;
        }

        public void setShop(Integer shop) {
            this.shop = shop;
        }

        public void setBigType(Integer ctype) {
            this.ctype = ctype;
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

    public static class StockFix {
        @SerializedName("style_number")
        private String styleNumber;
        @SerializedName("brand")
        private Integer brand;
        @SerializedName("fix")
        private Integer fix;
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
        @SerializedName("tag_price")
        private Float tagPrice;

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

        public Integer getFix() {
            return fix;
        }

        public void setFix(Integer fix) {
            this.fix = fix;
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

        public void setSeason(Integer season) {
            this.season = season;
        }


        public void setYear(Integer year) {
            this.year = year;
        }

        public void setTagPrice(Float tagPrice) {
            this.tagPrice = tagPrice;
        }
    }
}
