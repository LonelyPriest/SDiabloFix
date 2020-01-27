package com.sdiablofix.dt.sdiablofix.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloDBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "diablo";
    private static final Integer DB_VERSION = 4;

    private static DiabloDBOpenHelper diabloDBHelper;

    private DiabloDBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DiabloDBOpenHelper instance(Context context) {
        if (null == diabloDBHelper) {
            diabloDBHelper = new DiabloDBOpenHelper(context);
        }

        return diabloDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String user = "create table if not exists user ("
            + "_id integer primary key autoincrement"
            + ", name text not null"
            + ", password text not null"
            + ", unique(name) ON CONFLICT REPLACE)";

        String fixDetail = "create table if not exists d_fix ("
            + "_id integer primary key autoincrement"

            + ", order_id integer not null"
            + ", fix_pos integer not null"
            + ", shop integer not null"

            + ", barcode text not null"
            + ", c_barcode text not null"
            + ", style_number text not null"
            + ", brand_id integer not null"
            + ", fix integer not null"
            + ", color integer not null"
            + ", size text not null"

            + ", type integer not null"
            + ", firm integer not null"
            + ", season integer not null"
            + ", year integer not null"
            + ", tag_price real not null"
            + ", amount integer not null"

            + ", unique(order_id, shop) ON CONFLICT REPLACE)";

        String stockOutDetail = "create table if not exists stock_out ("
            + "_id integer primary key autoincrement"

            + ", order_id integer not null"
            + ", fix_pos integer not null"
            + ", shop integer not null"

            + ", barcode text not null"
            + ", c_barcode text not null"
            + ", style_number text not null"
            + ", brand_id integer not null"
            + ", fix integer not null"
            + ", color integer not null"
            + ", size text not null"
            + ", s_group text not null"
            + ", path text not null"

            + ", type integer not null"
            + ", sex integer not null"
            + ", free integer not null"
            + ", alarm_day integer not null"
            + ", firm integer not null"
            + ", season integer not null"
            + ", year integer not null"
            + ", tag_price real not null"
            + ", org_price real not null"
            + ", discount real not null"
            + ", ediscount real not null"

            + ", amount integer not null"
            + ", unique(order_id, shop) ON CONFLICT REPLACE)";

        // 0:fix, 1:reject
        String base = "create table if not exists fix_base ("
            + "_id integer primary key autoincrement"
            + ", shop integer not null"
            + ", type integer default 0 not null"
            + ", datetime text not null"
            + ", unique(shop) ON CONFLICT REPLACE)";

        db.execSQL(user);
        db.execSQL(fixDetail);
        db.execSQL(stockOutDetail);
        db.execSQL(base);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String user = "drop table if exists user";
        String fix = "drop table if exists d_fix";
        String out = "drop table if exists stock_out";
        String base = "drop table if exists fix_base";

        db.execSQL(user);
        db.execSQL(fix);
        db.execSQL(out);
        db.execSQL(base);
        onCreate(db);
    }
}
