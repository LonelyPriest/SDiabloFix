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
    public static final Integer DIABLO_EXT_BARCODE_LENGTH_OF_COLOR_SIZE = 6;

    public static final String DIABLO_CONFIG_YES = "1";
    public static final String DIABLO_CONFIG_NO  = "0";
    public static final String SETTING_AUTO_BARCODE = "bcode_auto";
    public static final String DIABLO_SCANNER_DEVICE = "p_balance";
    public static final String DIABLO_DEFAULT_SCANNER_DEVICE = "000000000";

    public static final Integer STOCK_FIX = 99;
    public static final Integer STOCK_OUT = 98;

    public static final Integer SUCCESS = 0;
    public static final Integer HTTP_OK = 200;

    public static final Integer DEFAULT_PAGE = 1;
    public static final Integer DEFAULT_ITEMS_PER_PAGE = 20;
    public static final Integer START_DEFAULT_INDEX = 1;

    //tag
    public static final String TAG_STOCK_FIX = "stockFix";
    public static final String TAG_STOCK_IN = "stockIn";
    public static final String TAG_STOCK_OUT = "stockOut";
    public static final String TAG_SALE_IN = "saleIn";
    public static final String TAG_SALE_OUT = "saleOut";


    /**
     * DB table
     */
    public static final String W_USER = "user";
    public static final String D_FIX  = "d_fix";
    public static final String B_FIX  = "fix_base";
    public static final String S_OUT  = "stock_out";
    
    public static final Integer SCAN_FIX = 0;
    public static final Integer SCAN_STOCK_OUT = 1;

    public static final String [] DIABLO_SIZE_TO_BARCODE = {
        "FF",
        "XS",  "S",   "M",   "L",   "XL",  "2XL", "3XL", "4XL", "5XL", "6XL", "7XL",
        "0",   "8",   "9",   "10",  "11",  "12",  "13",  "14",  "15",  "16",  "17",
        "18",  "19",  "20",  "21",  "22",  "23",  "24",  "25",  "26",  "27",  "28",
        "29",  "30",  "31",  "32",  "33",  "34",  "35",  "36",  "37",  "38",  "39",
        "40",  "41",  "42",  "43",  "44",  "46",  "48",  "50",  "52",  "54",  "56",
        "58",  "80",  "90",  "100", "105", "110", "115", "120", "125", "130", "135",
        "140", "145", "150", "155", "160", "165", "170", "175", "180", "185", "190",
        "195", "200", "4",   "6",   "7",    "5" , "45",  "47",

        "70A", "70B", "70C", "70D", "70E",
        "75A", "75B", "75C", "75D", "75E",
        "80A", "80B", "80C", "80D", "80E", "80F",
        "85A", "85B", "85C", "85D", "85E", "85F",
        "90A", "90B", "90C", "90D", "90E", "90F",
        "95A", "95B", "95C", "95D", "95E", "95F",

        "55", "60", "65", "70", "75", "85", "95", "73", "78", "66", "51",
        "62", "67", "79", "72", "84", "59", "53", "2",  "3",  "8XL", "9XL"
    };

    private DiabloEnum(){

    }
}
