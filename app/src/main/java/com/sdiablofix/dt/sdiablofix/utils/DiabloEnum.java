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
    public static final Integer DIABLO_BARCODE_LENGTH_OF_COLOR_SIZE = 5;

    public static final String DIABLO_CONFIG_YES = "1";
    public static final String DIABLO_CONFIG_NO  = "0";
    public static final String SETTING_AUTO_BARCODE = "bcode_auto";

    public static final Integer STOCK_FIX = 99;

    public static final Integer SUCCESS = 0;
    public static final Integer HTTP_OK = 200;

    //tag
    public static final String TAG_STOCK_FIX = "stockFix";
    public static final String TAG_STOCK_IN = "stockIn";
    public static final String TAG_SALE_IN = "saleIn";
    public static final String TAG_SALE_OUT = "saleOut";


    /**
     * DB table
     */
    public static final String W_USER = "user";
    public static final String D_FIX = "d_fix";
    public static final String B_FIX = "fix_base";

    public static final String [] DIABLO_SIZE_TO_BARCODE = {
        "FF",
        "XS",  "S",   "M",   "L",   "XL",  "2XL", "3XL", "4XL", "5XL", "6XL", "7XL",
        "0",   "8",   "9",   "10",  "11",  "12",  "13",  "14",  "15",  "16",  "17",
        "18",  "19",  "20",  "21",  "22",  "23",  "24",  "25",  "26",  "27",  "28",
        "29",  "30",  "31",  "32",  "33",  "34",  "35",  "36",  "37",  "38",  "39",
        "40",  "41",  "42",  "43",  "44",  "46",  "48",  "50",  "52",  "54",  "56",
        "58",  "80",  "90",  "100", "105", "110", "115", "120", "125", "130", "135",
        "140", "145", "150", "155", "160", "165", "170", "175", "180", "185", "190",
        "195", "200"
    };

    private DiabloEnum(){

    }
}
