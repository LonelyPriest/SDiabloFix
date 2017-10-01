package com.sdiablofix.dt.sdiablofix.utils;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloEnum {
    public static final Integer TABLET = 1;
    public static final String  SESSION_ID = "qzg_dyty_session";

    public static final Integer INVALID_INDEX = -1;
    public static final Integer DIABLO_FREE = 0;
    public static final Integer DIABLO_NON_FREE = 1;
    public static final Integer DIABLO_FALSE = 0;
    public static final Integer DIABLO_TRUE = 1;
    public static final Integer DIABLO_FREE_COLOR = 0;
    public static final String  DIABLO_FREE_SIZE = "0";
    public static final Integer DIABLO_FREE_SIZE_GROUP = 0;
    public static final Integer USE_REPO = 1;
    public static final String  SIZE_SEPARATOR = ",";

    //shop
    public static final Integer SHOP_ONLY = 0;
    public static final Integer REPO_ONLY = 1;
    public static final Integer REPO_BAD  = 2;
    public static final Integer BIND_NONE = -1;
    public static final String  EMPTY_STRING = "";

    public static final long DAY_SECONDS = 86400;

    //tag
    public static final String TAG_STOCK_FIX = "stockFix";
    public static final String TAG_STOCK_IN = "stockIn";
    public static final String TAG_SALE_IN = "saleIn";
    public static final String TAG_SALE_OUT = "saleOut";


    /**
     * DB table
     */
    public static final String W_USER = "user";

    private DiabloEnum(){

    }
}