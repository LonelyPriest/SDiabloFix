package com.sdiablofix.dt.sdiablofix.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloDBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "diablo";
    private static final Integer DB_VERSION = 2;

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

            + ", unique(order_id, shop) ON CONFLICT REPLACE)";

        String fixBase = "create table if not exists fix_base ("
            + "_id integer primary key autoincrement"
            + ", shop integer not null"
            + ", datetime text not null"
            + ", unique(shop) ON CONFLICT REPLACE)";

        db.execSQL(user);
        db.execSQL(fixDetail);
        db.execSQL(fixBase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String user = "drop table if exists user";
        String fix = "drop table if exists d_fix";

        db.execSQL(user);
        db.execSQL(fix);
        onCreate(db);
    }
}
