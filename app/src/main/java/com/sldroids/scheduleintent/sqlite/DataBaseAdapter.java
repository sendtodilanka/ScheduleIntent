package com.sldroids.scheduleintent.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sldroids.scheduleintent.Order;

import java.io.IOException;
import java.util.ArrayList;

public class DataBaseAdapter
{
    private static final String TAG = "DataAdapter";
    private static final String ID = "_id";
    private static final String NUMBER = "number";
    private static final String NAME = "name";
    private static final String CONTACT = "contact";
    private static final String ITEM = "item";
    private static final String DESCRIPTION = "descript";
    private static final String QUANTITY = "qty";
    private static final String RATE = "rate";
    private static final String SRC_LAT = "src_lat";
    private static final String SRC_LON = "src_long";
    private static final String DES_LAT = "des_lat";
    private static final String DES_LON = "des_long";
    private static final String STATUS = "status";
    private static final String IMAGE = "image";
    private static final String TABLE = "order_list";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public DataBaseAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DataBaseAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataBaseAdapter open() throws SQLException
    {
        try
        {
            try {
                mDbHelper.openDataBase();
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    public long addOrder(int number, String name, String contact, String item, String descript,
                         double qty, double rate, double src_lat, double src_lon, double des_lat,
                         double des_lon, int status){

        long rowInserted = -1;

        try {
            ContentValues intVal = new ContentValues();
            intVal.put(NUMBER, number);
            intVal.put(NAME, name);
            intVal.put(CONTACT, contact);
            intVal.put(ITEM, item);
            intVal.put(DESCRIPTION, descript);
            intVal.put(QUANTITY, qty);
            intVal.put(RATE, rate);
            intVal.put(SRC_LAT, src_lat);
            intVal.put(SRC_LON, src_lon);
            intVal.put(DES_LAT, des_lat);
            intVal.put(DES_LON, des_lon);
            intVal.put(STATUS, status);

            rowInserted = mDb.insertOrThrow(TABLE, null, intVal);

            //Log.v("add_"+table, "success");
        }catch (Exception e){
            e.printStackTrace();
        }

        return rowInserted;
    }

    public ArrayList<Order> getActiveOrders(){
        ArrayList<Order> orders = new ArrayList<>();
        String[] fields = new String[]{NUMBER, NAME, CONTACT, ITEM, DESCRIPTION,
                QUANTITY, RATE, SRC_LAT, SRC_LON, DES_LAT, DES_LON, STATUS};

        Cursor mCursor = mDb.query(TABLE, fields,
                STATUS + "=?", new String[]{"0"}, null,null, " " + ID + " DESC");

        if (mCursor.moveToFirst()){
            do {
                Order order = new Order();
                order.setNumber(mCursor.getInt(mCursor.getColumnIndex(NUMBER)));
                order.setCname(mCursor.getString(mCursor.getColumnIndex(NAME)));
                order.setContact(mCursor.getString(mCursor.getColumnIndex(CONTACT)));
                order.setItem(mCursor.getString(mCursor.getColumnIndex(ITEM)));
                order.setDescript(mCursor.getString(mCursor.getColumnIndex(DESCRIPTION)));
                order.setQty(mCursor.getDouble(mCursor.getColumnIndex(QUANTITY)));
                order.setRate(mCursor.getDouble(mCursor.getColumnIndex(RATE)));
                order.setSrcLat(mCursor.getDouble(mCursor.getColumnIndex(SRC_LAT)));
                order.setSrcLon(mCursor.getDouble(mCursor.getColumnIndex(SRC_LON)));
                order.setDesLat(mCursor.getDouble(mCursor.getColumnIndex(DES_LAT)));
                order.setDesLon(mCursor.getDouble(mCursor.getColumnIndex(DES_LON)));
                order.setStatus(mCursor.getInt(mCursor.getColumnIndex(STATUS)));
                orders.add(order);
            }while (mCursor.moveToNext());
        }
        return orders;
    }

    public void delLastRow(){
        Cursor cursor = mDb.rawQuery("SELECT * FROM "+ TABLE +" ORDER BY " + ID + " DESC LIMIT 1;", null);
        cursor.moveToLast();
        if(mDb.delete(TABLE , ID + "=" + cursor.getInt(cursor.getColumnIndex(ID)), null) != 0){
            Log.d(TAG, "Deleted");
        }else {
            Log.d(TAG, "Not Deleted");
        }
    }
}
