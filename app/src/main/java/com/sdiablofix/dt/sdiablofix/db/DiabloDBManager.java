package com.sdiablofix.dt.sdiablofix.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.sdiablofix.dt.sdiablofix.entity.DiabloBarcodeStock;
import com.sdiablofix.dt.sdiablofix.entity.DiabloDevice;
import com.sdiablofix.dt.sdiablofix.entity.DiabloUser;
import com.sdiablofix.dt.sdiablofix.request.StockFixRequest;
import com.sdiablofix.dt.sdiablofix.request.StockOutRequest;
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

    public void addDevice(String uuid, Integer device) {
        ContentValues v = new ContentValues();
        v.put("uuid", uuid);
        v.put("device", device);
        mSQLiteDB.insert(DiabloEnum.W_DEVICE, null, v);
    }

    public void getDevice(DiabloDevice device, String uuid){
        String [] fields = {"uuid", "device"};
        String [] args = {uuid};
        Cursor cursor = mSQLiteDB.query(DiabloEnum.W_DEVICE, fields, "uuid=?", args, null, null, null);

        if (cursor.moveToFirst()){
            device.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
            device.setDevice(cursor.getInt(cursor.getColumnIndex("device")));
            cursor.close();
        }
    }

    public void updateDevice(String uuid, Integer device) {
        mSQLiteDB.beginTransaction();
        try {
            String sql = "update " + DiabloEnum.W_DEVICE + " set device=? where uuid=?";
            SQLiteStatement s = mSQLiteDB.compileStatement(sql);
            s.bindString(1, DiabloUtils.toString(device));
            s.bindString(2, uuid);
            s.execute();
            s.clearBindings();

            mSQLiteDB.setTransactionSuccessful();
        } finally {
            mSQLiteDB.endTransaction();
        }
    }

    public void addUser(String name, String password) {
        ContentValues v = new ContentValues();
        v.put("name", name);
        v.put("password", password);
        mSQLiteDB.insert(DiabloEnum.W_USER, null, v);
    }

    public DiabloUser getFirstLoginUser(){
        String [] fields = {"name", "password"};

        Cursor cursor = mSQLiteDB.query(DiabloEnum.W_USER, fields, null, null, null, null, "_id desc", "1");
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
        mSQLiteDB.beginTransaction();
        try {
            String sql = "insert into " + DiabloEnum.D_FIX
                + "("
                + "order_id"
                + ", fix_pos"
                + ", cs_pos"
                + ", shop"

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
                + ", tag_price"
                + ", amount)"
                + " values(?, ?, ?, ?,"
                + "?, ?, ?, ?, ?, ?, ?,"
                + "?, ?, ?, ?, ?, ?)";

            SQLiteStatement s3 = mSQLiteDB.compileStatement(sql);
            s3.bindString(1, DiabloUtils.toString(stock.getOrderId()));
            s3.bindString(2, DiabloUtils.toString(stock.getFixPos()));
            s3.bindString(3, DiabloUtils.toString(stock.getCSFixPos()));
            s3.bindString(4, DiabloUtils.toString(shop));

            s3.bindString(5, stock.getBarcode());
            s3.bindString(6, stock.getCorrectBarcode());
            s3.bindString(7, stock.getStyleNumber());
            s3.bindString(8, DiabloUtils.toString(stock.getBrandId()));
            s3.bindString(9, DiabloUtils.toString(stock.getFix()));
            s3.bindString(10, DiabloUtils.toString(stock.getColor()));
            s3.bindString(11, stock.getSize());

            s3.bindString(12, DiabloUtils.toString(stock.getTypeId()));
            s3.bindString(13, DiabloUtils.toString(stock.getFirmId()));
            s3.bindString(14, DiabloUtils.toString(stock.getSeason()));
            s3.bindString(15, DiabloUtils.toString(stock.getYear()));
            s3.bindString(16, DiabloUtils.toString(stock.getTagPrice()));

            s3.bindString(17, DiabloUtils.toString(stock.getAmount()));

            s3.execute();
            s3.clearBindings();

            mSQLiteDB.setTransactionSuccessful();
        } finally {
            mSQLiteDB.endTransaction();
        }
    }

    public  List<DiabloBarcodeStock> listFixDetail(Integer shop) {
        String sql0 = "select "
            + "order_id"
            + ", fix_pos"
            + ", cs_pos"
            + ", shop"
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
            + ", tag_price"
            + ", amount"
            + " from " + DiabloEnum.D_FIX
            + " where shop=? order by order_id";
        Cursor c = mSQLiteDB.rawQuery(sql0, new String[] {DiabloUtils.toString(shop)});

        List<DiabloBarcodeStock> stocks = new ArrayList<>();

        try {
            while (c.moveToNext()){
                Integer orderId = c.getInt(c.getColumnIndex("order_id"));
                Integer fixPos = c.getInt(c.getColumnIndex("fix_pos"));
                Integer csPos  = c.getInt(c.getColumnIndex("cs_pos"));

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

                Integer amount = c.getInt(c.getColumnIndex("amount"));

                DiabloBarcodeStock stock = new DiabloBarcodeStock();
                stock.setOrderId(orderId);
                stock.setFixPos(fixPos);
                stock.setCSFixPos(csPos);

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

                stock.setAmount(amount);

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
                + ", fix_pos"
                + ", cs_pos"
                + ", shop"

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
                + ", tag_price"
                + ", amount)"
                + " values(?, ?, ?, ?,   ?, ?, ?, ?, ?, ?, ?,   ?, ?, ?, ?, ?, ?)";

            SQLiteStatement s3 = mSQLiteDB.compileStatement(sql3);
            for (DiabloBarcodeStock stock : stocks) {
                s3.bindString(1, DiabloUtils.toString(stock.getOrderId()));
                s3.bindString(2, DiabloUtils.toString(stock.getFixPos()));
                s3.bindString(3, DiabloUtils.toString(stock.getCSFixPos()));
                s3.bindString(4, DiabloUtils.toString(shop));

                s3.bindString(5, stock.getBarcode());
                s3.bindString(6, stock.getCorrectBarcode());
                s3.bindString(7, stock.getStyleNumber());
                s3.bindString(8, DiabloUtils.toString(stock.getBrandId()));
                s3.bindString(9, DiabloUtils.toString(stock.getFix()));
                s3.bindString(10, DiabloUtils.toString(stock.getColor()));
                s3.bindString(11, stock.getSize());

                s3.bindString(12, DiabloUtils.toString(stock.getTypeId()));
                s3.bindString(13, DiabloUtils.toString(stock.getFirmId()));
                s3.bindString(14, DiabloUtils.toString(stock.getSeason()));
                s3.bindString(15, DiabloUtils.toString(stock.getYear()));
                s3.bindString(16, DiabloUtils.toString(stock.getTagPrice()));

                s3.bindString(17, DiabloUtils.toString(stock.getAmount()));

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
            String sql = "delete from " + DiabloEnum.D_FIX + " where order_id=? and shop=?";
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

    public void updateFixDetail(Integer shop, List<DiabloBarcodeStock> stocks) {
        mSQLiteDB.beginTransaction();
        try {
            String sql0 = "update "
                + DiabloEnum.D_FIX
                + " set fix_pos=?, cs_pos=?, fix=? where shop=? and order_id=?";

            SQLiteStatement s0 = mSQLiteDB.compileStatement(sql0);
            for (DiabloBarcodeStock stock : stocks) {
                s0.bindString(1, DiabloUtils.toString(stock.getFixPos()));
                s0.bindString(2, DiabloUtils.toString(stock.getCSFixPos()));
                s0.bindString(3, DiabloUtils.toString(stock.getFix()));
                s0.bindString(4, DiabloUtils.toString(shop));
                s0.bindString(5, DiabloUtils.toString(stock.getOrderId()));
                s0.execute();
                s0.clearBindings();
            }

            mSQLiteDB.setTransactionSuccessful();
        } finally {
            mSQLiteDB.endTransaction();
        }
        // mSQLiteDB.endTransaction();
    }


    public void addBase(Integer shop, Integer type) {
        ContentValues v = new ContentValues();
        v.put("shop", shop);
        v.put("type", type);
        v.put("datetime", DiabloUtils.currentDatetime());
        mSQLiteDB.insert(DiabloEnum.B_FIX, null, v);
    }

    public void updateBase(Integer shop, Integer type) {
        mSQLiteDB.beginTransaction();
        try {
            String sql = "update " + DiabloEnum.B_FIX + " set datetime=? where shop=? and type=?";
            SQLiteStatement s = mSQLiteDB.compileStatement(sql);
            s.bindString(1, DiabloUtils.currentDatetime());
            s.bindString(2, DiabloUtils.toString(shop));
            s.bindString(2, DiabloUtils.toString(type));
            s.execute();
            s.clearBindings();

            mSQLiteDB.setTransactionSuccessful();
        } finally {
            mSQLiteDB.endTransaction();
        }
    }

    public StockFixRequest.StockFixBase getFixBase(Integer shop, Integer type){
        String [] fields = {"shop", "datetime"};
        String [] args = {DiabloUtils.toString(shop), DiabloUtils.toString(type)};
        Cursor cursor = mSQLiteDB.query(DiabloEnum.B_FIX, fields, "shop=? and type=?", args, null, null, null);

        if (cursor.moveToFirst()){
            StockFixRequest.StockFixBase base = new StockFixRequest.StockFixBase();
            base.setShop(cursor.getInt(cursor.getColumnIndex("shop")));
            base.setDatetime(cursor.getString(cursor.getColumnIndex("datetime")));
            cursor.close();
            return base;
        }

        return null;
    }

    public StockOutRequest.StockOutBase getStockOutBase(Integer shop, Integer type){
        String [] fields = {"shop", "datetime"};
        String [] args = {DiabloUtils.toString(shop), DiabloUtils.toString(type)};
        Cursor cursor = mSQLiteDB.query(DiabloEnum.B_FIX, fields, "shop=? and type=?", args, null, null, null);

        if (cursor.moveToFirst()){
            StockOutRequest.StockOutBase base = new StockOutRequest.StockOutBase();
            base.setShop(cursor.getInt(cursor.getColumnIndex("shop")));
            base.setDatetime(cursor.getString(cursor.getColumnIndex("datetime")));
            cursor.close();
            return base;
        }

        return null;
    }

//    public void clearFixBase(Integer shop, Integer type) {
//        mSQLiteDB.beginTransaction();
//        try {
//            String sql = "delete from " + DiabloEnum.B_FIX + "where shop=? and type=?";
//            SQLiteStatement s = mSQLiteDB.compileStatement(sql);
//            s.bindString(1, DiabloUtils.toString(shop));
//            s.bindString(1, DiabloUtils.toString(type));
//            s.execute();
//            s.clearBindings();
//
//            mSQLiteDB.setTransactionSuccessful();
//        } finally {
//            mSQLiteDB.endTransaction();
//        }
//    }

    public void clearAllDraft() {
        mSQLiteDB.beginTransaction();
        try {
            String sql0 = "delete from " + DiabloEnum.B_FIX;
            SQLiteStatement s0 = mSQLiteDB.compileStatement(sql0);
            s0.execute();

            String sql1 = "delete from " + DiabloEnum.D_FIX;
            SQLiteStatement s1 = mSQLiteDB.compileStatement(sql1);
            s1.execute();

            String sql2 = "delete from " + DiabloEnum.S_OUT;
            SQLiteStatement s2 = mSQLiteDB.compileStatement(sql2);
            s2.execute();

            mSQLiteDB.setTransactionSuccessful();
        } finally {
            mSQLiteDB.endTransaction();
        }
    }

    public void clearDraft(Integer shop, Integer type) {
        mSQLiteDB.beginTransaction();
        try {
            String sql0 = "delete from " + DiabloEnum.B_FIX + " where shop=? and type=?";
            SQLiteStatement s0 = mSQLiteDB.compileStatement(sql0);
            s0.bindString(1, DiabloUtils.toString(shop));
            s0.bindString(2, DiabloUtils.toString(type));
            s0.execute();
            s0.clearBindings();

            if (DiabloEnum.SCAN_FIX.equals(type)) {
                String sql1 = "delete from " + DiabloEnum.D_FIX + " where shop=?";
                SQLiteStatement s1 = mSQLiteDB.compileStatement(sql1);
                s1.bindString(1, DiabloUtils.toString(shop));
                s1.execute();
                s1.clearBindings();
            } else if (DiabloEnum.SCAN_STOCK_OUT.equals(type)) {
                String sql2 = "delete from " + DiabloEnum.S_OUT + " where shop=?";
                SQLiteStatement s2 = mSQLiteDB.compileStatement(sql2);
                s2.bindString(1, DiabloUtils.toString(shop));
                s2.execute();
                s2.clearBindings();
            }


            mSQLiteDB.setTransactionSuccessful();
        } finally {
            mSQLiteDB.endTransaction();
        }
    }

    public void addReject(Integer shop, DiabloBarcodeStock stock) {
        mSQLiteDB.beginTransaction();
        try {
            String sql = "insert into " + DiabloEnum.S_OUT
                + "("
                + "order_id"
                + ", fix_pos"
                + ", shop"

                + ", barcode"
                + ", c_barcode"
                + ", style_number"
                + ", brand_id"
                + ", fix"
                + ", color"
                + ", size"
                + ", s_group"
                + ", path"

                + ", type"
                + ", sex"
                + ", free"
                + ", alarm_day"
                + ", firm"
                + ", season"
                + ", year"
                + ", tag_price"
                + ", org_price"
                + ", discount"
                + ", ediscount"
                + ", amount"
                + ", count)"
                + " values("
                + "?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?, ?,"
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            SQLiteStatement s3 = mSQLiteDB.compileStatement(sql);
            s3.bindString(1, DiabloUtils.toString(stock.getOrderId()));
            s3.bindString(2, DiabloUtils.toString(stock.getFixPos()));
            s3.bindString(3, DiabloUtils.toString(shop));

            s3.bindString(4, stock.getBarcode());
            s3.bindString(5, stock.getCorrectBarcode());
            s3.bindString(6, stock.getStyleNumber());
            s3.bindString(7, DiabloUtils.toString(stock.getBrandId()));
            s3.bindString(8, DiabloUtils.toString(stock.getFix()));
            s3.bindString(9, DiabloUtils.toString(stock.getColor()));
            s3.bindString(10, stock.getSize());
            s3.bindString(11, stock.getsGroup());
            s3.bindString(12, stock.getPath());

            s3.bindString(13, DiabloUtils.toString(stock.getTypeId()));
            s3.bindString(14, DiabloUtils.toString(stock.getSex()));
            s3.bindString(15, DiabloUtils.toString(stock.getFree()));
            s3.bindString(16, DiabloUtils.toString(stock.getAlarm_day()));
            s3.bindString(17, DiabloUtils.toString(stock.getFirmId()));
            s3.bindString(18, DiabloUtils.toString(stock.getSeason()));
            s3.bindString(19, DiabloUtils.toString(stock.getYear()));
            s3.bindString(20, DiabloUtils.toString(stock.getTagPrice()));
            s3.bindString(21, DiabloUtils.toString(stock.getOrgPrice()));
            s3.bindString(22, DiabloUtils.toString(stock.getDiscount()));
            s3.bindString(23, DiabloUtils.toString(stock.getEdiscount()));
            s3.bindString(24, DiabloUtils.toString(stock.getAmount()));
            s3.bindString(25, DiabloUtils.toString(stock.getCount()));

            s3.execute();
            s3.clearBindings();

            mSQLiteDB.setTransactionSuccessful();
        } finally {
            mSQLiteDB.endTransaction();
        }
    }

    public  List<DiabloBarcodeStock> listReject(Integer shop) {
        String sql0 = "select "
            + "order_id"
            + ", fix_pos"
            + ", shop"

            + ", barcode"
            + ", c_barcode"
            + ", style_number"
            + ", brand_id"
            + ", fix"
            + ", color"
            + ", size"
            + ", s_group"
            + ", path"

            + ", type"
            + ", sex"
            + ", free"
            + ", alarm_day"
            + ", firm"
            + ", season"
            + ", year"
            + ", tag_price"
            + ", org_price"
            + ", discount"
            + ", ediscount"
            + ", amount"
            + ", count"
            + " from " + DiabloEnum.S_OUT
            + " where shop=? order by order_id";
        Cursor c = mSQLiteDB.rawQuery(sql0, new String[] {DiabloUtils.toString(shop)});

        List<DiabloBarcodeStock> stocks = new ArrayList<>();

        try {
            while (c.moveToNext()){
                Integer orderId = c.getInt(c.getColumnIndex("order_id"));
                Integer fixPos = c.getInt(c.getColumnIndex("fix_pos"));

                String barcode = c.getString(c.getColumnIndex("barcode"));
                String correctBarcode = c.getString(c.getColumnIndex("c_barcode"));
                String styleNumber = c.getString(c.getColumnIndex("style_number"));
                Integer brand = c.getInt(c.getColumnIndex("brand_id"));
                Integer fix = c.getInt(c.getColumnIndex("fix"));
                Integer color = c.getInt(c.getColumnIndex("color"));
                String size = c.getString(c.getColumnIndex("size"));
                String sGroup = c.getString(c.getColumnIndex("s_group"));
                String path = c.getString(c.getColumnIndex("path"));

                Integer type = c.getInt(c.getColumnIndex("type"));
                Integer sex = c.getInt(c.getColumnIndex("sex"));
                Integer free = c.getInt(c.getColumnIndex("free"));
                Integer alarm_day = c.getInt(c.getColumnIndex("alarm_day"));
                Integer firm = c.getInt(c.getColumnIndex("firm"));
                Integer season = c.getInt(c.getColumnIndex("season"));
                Integer year = c.getInt(c.getColumnIndex("year"));
                Float tagPrice = c.getFloat(c.getColumnIndex("tag_price"));
                Float orgPrice = c.getFloat(c.getColumnIndex("org_price"));
                Float discount = c.getFloat(c.getColumnIndex("discount"));
                Float ediscount = c.getFloat(c.getColumnIndex("ediscount"));
                Integer amount = c.getInt(c.getColumnIndex("amount"));
                Integer count = c.getInt(c.getColumnIndex("count"));

                DiabloBarcodeStock stock = new DiabloBarcodeStock();
                stock.setOrderId(orderId);
                stock.setFixPos(fixPos);

                stock.setBarcode(barcode);
                stock.setCorrectBarcode(correctBarcode);
                stock.setStyleNumber(styleNumber);
                stock.setBrandId(brand);
                stock.setFix(fix);
                stock.setColor(color);
                stock.setSize(size);
                stock.setsGroup(sGroup);
                stock.setPath(path);

                stock.setTypeId(type);
                stock.setSex(sex);
                stock.setFree(free);
                stock.setAlarm_day(alarm_day);
                stock.setFirmId(firm);
                stock.setSeason(season);
                stock.setYear(year);
                stock.setTagPrice(tagPrice);
                stock.setOrgPrice(orgPrice);
                stock.setDiscount(discount);
                stock.setEdiscount(ediscount);
                stock.setAmount(amount);
                stock.setCount(count);

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

    public void replaceReject(Integer shop, List<DiabloBarcodeStock> stocks) {
        mSQLiteDB.beginTransaction();

        try {
            String sql0 = "delete from " + DiabloEnum.S_OUT + " where shop=?";
            SQLiteStatement s0 = mSQLiteDB.compileStatement(sql0);
            s0.bindString(1, DiabloUtils.toString(shop));
            s0.execute();
            s0.clearBindings();

            String sql3 = "insert into " + DiabloEnum.S_OUT
                + "("
                + "order_id"
                + ", fix_pos"
                + ", shop"

                + ", barcode"
                + ", c_barcode"
                + ", style_number"
                + ", brand_id"
                + ", fix"
                + ", color"
                + ", size"
                + ", s_group"
                + ", path"

                + ", type"
                + ", sex"
                + ", free"
                + ", alarm_day"
                + ", firm"
                + ", season"
                + ", year"
                + ", tag_price"
                + ", org_price"
                + ", discount"
                + ", ediscount"
                + ", amount"
                + ", count)"
                + " values("
                + "?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?, ?,"
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            SQLiteStatement s3 = mSQLiteDB.compileStatement(sql3);
            for (DiabloBarcodeStock stock : stocks) {
                s3.bindString(1, DiabloUtils.toString(stock.getOrderId()));
                s3.bindString(2, DiabloUtils.toString(stock.getFixPos()));
                s3.bindString(3, DiabloUtils.toString(shop));

                s3.bindString(4, stock.getBarcode());
                s3.bindString(5, stock.getCorrectBarcode());
                s3.bindString(6, stock.getStyleNumber());
                s3.bindString(7, DiabloUtils.toString(stock.getBrandId()));
                s3.bindString(8, DiabloUtils.toString(stock.getFix()));
                s3.bindString(9, DiabloUtils.toString(stock.getColor()));
                s3.bindString(10, stock.getSize());
                s3.bindString(11, stock.getsGroup());
                s3.bindString(12, stock.getPath());

                s3.bindString(13, DiabloUtils.toString(stock.getTypeId()));
                s3.bindString(14, DiabloUtils.toString(stock.getSex()));
                s3.bindString(15, DiabloUtils.toString(stock.getFree()));
                s3.bindString(16, DiabloUtils.toString(stock.getAlarm_day()));
                s3.bindString(17, DiabloUtils.toString(stock.getFirmId()));
                s3.bindString(18, DiabloUtils.toString(stock.getSeason()));
                s3.bindString(19, DiabloUtils.toString(stock.getYear()));
                s3.bindString(20, DiabloUtils.toString(stock.getTagPrice()));
                s3.bindString(21, DiabloUtils.toString(stock.getOrgPrice()));
                s3.bindString(22, DiabloUtils.toString(stock.getDiscount()));
                s3.bindString(23, DiabloUtils.toString(stock.getEdiscount()));
                s3.bindString(24, DiabloUtils.toString(stock.getAmount()));
                s3.bindString(25, DiabloUtils.toString(stock.getCount()));

                s3.execute();
                s3.clearBindings();
            }

            mSQLiteDB.setTransactionSuccessful();
        } finally {
            mSQLiteDB.endTransaction();
        }
        // mSQLiteDB.endTransaction();
    }

    public void deleteReject(Integer shop, Integer orderId) {
        mSQLiteDB.beginTransaction();
        try {
            String sql = "delete from " + DiabloEnum.S_OUT + " where order_id=? and shop=?";
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
