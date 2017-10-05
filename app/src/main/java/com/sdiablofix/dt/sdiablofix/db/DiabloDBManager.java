package com.sdiablofix.dt.sdiablofix.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.sdiablofix.dt.sdiablofix.entity.DiabloBarcodeStock;
import com.sdiablofix.dt.sdiablofix.entity.DiabloUser;
import com.sdiablofix.dt.sdiablofix.utils.DiabloEnum;
import com.sdiablofix.dt.sdiablofix.utils.DiabloUtils;

import java.util.ArrayList;
import java.util.List;

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

    public void addFix(Integer shop, DiabloBarcodeStock stock) {
        ContentValues v = new ContentValues();
        v.put("order_id", stock.getOrderId());
        v.put("shop", shop);

        v.put("barcode", stock.getBarcode());
        v.put("c_barcode", stock.getCorrectBarcode());
        v.put("style_number", stock.getStyleNumber());
        v.put("brand_id", stock.getBrandId());
        v.put("fix", stock.getFix());
        v.put("color", stock.getColor());
        v.put("size", stock.getSize());

        v.put("type", stock.getTypeId());
        v.put("firm", stock.getFirmId());
        v.put("season", stock.getSeason());
        v.put("year", stock.getYear());
        v.put("tag_price", stock.getTagPrice());

        mSQLiteDB.insert(DiabloEnum.D_FIX, null, v);
    }

    public  List<DiabloBarcodeStock> listFixDetail(Integer shop) {
        String sql0 = "select order_id, shop"
            + ", barcode, c_barcode, style_number, brand_id, fix, color, size"
            + ", type, firm, season, year, tag_price"
            + " from " + DiabloEnum.D_FIX
            + " where shop=? order by order_id";
        Cursor c = mSQLiteDB.rawQuery(sql0, new String[] {DiabloUtils.toString(shop)});

        List<DiabloBarcodeStock> stocks = new ArrayList<>();

        try {
            while (c.moveToNext()){
                Integer orderId = c.getInt(c.getColumnIndex("order_id"));

                String barcode = c.getString(c.getColumnIndex("barcode"));
                String correctBarcode = c.getString(c.getColumnIndex("c_barcode"));
                String styleNumber = c.getString(c.getColumnIndex("style_number"));
                Integer brand = c.getInt(c.getColumnIndex("brand_id"));
                Integer fix = c.getInt(c.getColumnIndex("fix"));
                Integer color = c.getInt(c.getColumnIndex("color"));
                String size = c.getString(c.getColumnIndex("size"));

                Integer type = c.getInt(c.getColumnIndex("type"));
                Integer firm = c.getInt(c.getColumnIndex("firm"));
                Integer season = c.getInt(c.getColumnIndex("season"));
                Integer year = c.getInt(c.getColumnIndex("year"));
                Float tagPrice = c.getFloat(c.getColumnIndex("tag_price"));

                DiabloBarcodeStock stock = new DiabloBarcodeStock();
                stock.setOrderId(orderId);
                stock.setBarcode(barcode);
                stock.setCorrectBarcode(correctBarcode);

                stock.setStyleNumber(styleNumber);
                stock.setBrandId(brand);
                stock.setFix(fix);
                stock.setColor(color);
                stock.setSize(size);

                stock.setTypeId(type);
                stock.setFirmId(firm);
                stock.setSeason(season);
                stock.setYear(year);
                stock.setTagPrice(tagPrice);

                if (color.equals(DiabloEnum.DIABLO_FREE_COLOR)
                    && size.equals(DiabloEnum.DIABLO_FREE_SIZE)) {
                    stock.setFree(DiabloEnum.DIABLO_FREE);
                } else {
                    stock.setFree(DiabloEnum.DIABLO_NON_FREE);
                }

                stocks.add(stock);
            }
        } finally {
            c.close();
        }

        return stocks;
    }

    public void replaceFixDetail(Integer shop, List<DiabloBarcodeStock> stocks) {
        mSQLiteDB.beginTransaction();

        try {
            String sql0 = "delete from " + DiabloEnum.D_FIX + " where shop=?";
            SQLiteStatement s0 = mSQLiteDB.compileStatement(sql0);
            s0.bindString(1, DiabloUtils.toString(shop));
            s0.execute();
            s0.clearBindings();

            String sql3 = "insert into " + DiabloEnum.D_FIX
                + "("
                + "order_id"
                +", shop"

                + ", barcode"
                + ", c_barcode"
                + ", style_number"
                + ", brand_id"
                + ", fix"
                + ", color"
                + ", size"

                + ", type"
                + ", firm"
                + ", season"
                + ", year"
                + ", tag_price)"
                + " values(?, ?,   ?, ?, ?, ?, ?, ?, ?,   ?, ?, ?, ?, ?)";
            SQLiteStatement s3 = mSQLiteDB.compileStatement(sql3);

            for (DiabloBarcodeStock stock : stocks) {
                s3.bindString(1, DiabloUtils.toString(stock.getOrderId()));
                s3.bindString(2, DiabloUtils.toString(shop));

                s3.bindString(3, stock.getBarcode());
                s3.bindString(4, stock.getCorrectBarcode());
                s3.bindString(5, stock.getStyleNumber());
                s3.bindString(6, DiabloUtils.toString(stock.getBrandId()));
                s3.bindString(7, DiabloUtils.toString(stock.getFix()));
                s3.bindString(8, DiabloUtils.toString(stock.getColor()));
                s3.bindString(9, stock.getSize());

                s3.bindString(10, DiabloUtils.toString(stock.getTypeId()));
                s3.bindString(11, DiabloUtils.toString(stock.getFirmId()));
                s3.bindString(12, DiabloUtils.toString(stock.getSeason()));
                s3.bindString(13, DiabloUtils.toString(stock.getYear()));
                s3.bindString(14, DiabloUtils.toString(stock.getTagPrice()));

                s3.execute();
                s3.clearBindings();
            }
            mSQLiteDB.setTransactionSuccessful();
        } finally {
            mSQLiteDB.endTransaction();
        }
        // mSQLiteDB.endTransaction();
    }

    public void deleteFixDetail(Integer shop, Integer orderId) {
        mSQLiteDB.beginTransaction();
        try {
            String sql = "delete from " + DiabloEnum.D_FIX
                + " where order_id=? and shop=?";
            SQLiteStatement s = mSQLiteDB.compileStatement(sql);
            s.bindString(1, DiabloUtils.toString(orderId));
            s.bindString(2, DiabloUtils.toString(shop));
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
