package com.sdiablofix.dt.sdiablofix.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloDBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "diablo";
    private static final Integer DB_VERSION = 1;

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
        String User = "create table if not exists user ("
            + "_id integer primary key autoincrement"
            + ", name text not null"
            + ", password text not null"
            + ", unique(name) ON CONFLICT REPLACE)";
        db.execSQL(User);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String User = "drop table if exists user";

        db.execSQL(User);

        onCreate(db);
    }
}
