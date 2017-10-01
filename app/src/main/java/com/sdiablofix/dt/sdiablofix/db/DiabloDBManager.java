package com.sdiablofix.dt.sdiablofix.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.sdiablofix.dt.sdiablofix.entity.DiabloUser;
import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;

/**
 * Created by buxianhui on 17/9/30.
 */

public class DiabloDBManager {
    private static DiabloDBManager mDiabloDBManager;
    private SQLiteDatabase mSQLiteDB;

    public static DiabloDBManager instance(){
        if (null == mDiabloDBManager){
            mDiabloDBManager = new DiabloDBManager();
        }

        return mDiabloDBManager;
    }

    public void init (Context context) {
        if (null != mSQLiteDB){
            mSQLiteDB.close();
        }

        this.mSQLiteDB = DiabloDBOpenHelper.instance(context).getWritableDatabase();
        // DiabloUtils.instance().makeToast(context, mSQLiteDB.getPath());
    }

    private DiabloDBManager() {

    }

    public void addUser(String name, String password) {
        ContentValues v = new ContentValues();
        v.put("name", name);
        v.put("password", password);
        mSQLiteDB.insert(DiabloEnum.W_USER, null, v);
    }

    public DiabloUser getFirstLoginUser(){
        String [] fields = {"name", "password"};

        Cursor cursor = mSQLiteDB.query(DiabloEnum.W_USER, fields, null, null, null, null, null);
        if (cursor.moveToFirst()){
            DiabloUser user = new DiabloUser();
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            cursor.close();

            return user;
        }

        return null;

    }

    public void updateUser(String name, String password) {
        mSQLiteDB.beginTransaction();
        try {
            String sql = "update " + DiabloEnum.W_USER + " set password=? where name=?";
            SQLiteStatement s = mSQLiteDB.compileStatement(sql);
            s.bindString(1, name);
            s.bindString(2, password);
            s.execute();
            s.clearBindings();

            mSQLiteDB.setTransactionSuccessful();
        } finally {
            mSQLiteDB.endTransaction();
        }
    }

    public void clearUser() {
        mSQLiteDB.beginTransaction();
        try {
            String sql = "delete from " + DiabloEnum.W_USER;
            SQLiteStatement s = mSQLiteDB.compileStatement(sql);
            s.execute();
            s.clearBindings();

            mSQLiteDB.setTransactionSuccessful();
        } finally {
            mSQLiteDB.endTransaction();
        }
    }

    synchronized public void close(){
        if (null != mSQLiteDB) {
            mSQLiteDB.close();
        }
    }
}
