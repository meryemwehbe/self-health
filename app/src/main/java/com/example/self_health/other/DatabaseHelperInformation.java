package com.example.self_health.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pc on 1/11/2017.
 */

public class DatabaseHelperInformation {
    private static final String DB_NAME = "Health";
    private static final int DB_VERSION = 1;
    public static final String DATABASE_TABLE_INFO = "INFORMATION";
    public static final String INDEX = "num";
    public static final String COL_ID = "ID";
    public static final String COL_TYPE ="VALUE_TYPE";
    public static final String COL_INFO ="JSON_INFO";
    public static final String COL_TIME ="TIME";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Context mContext;

    //String for creating database
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE_INFO + " ("
                    + INDEX + " integer primary key autoincrement, "
                    + COL_ID + " text not null, "
                    + COL_TYPE + " text not null, "
                    + COL_INFO + " text not null, "
                    + COL_TIME + " text not null); ";

    public DatabaseHelperInformation(Context ctx) {
        mContext = ctx;
    }
    public void drop(){
        mDb.execSQL("DROP TABLE "+ DATABASE_TABLE_INFO);
    }

    public void createtable(){
        mDb.execSQL(DATABASE_CREATE);
    }

    public long createInstance(String ID,String TYPE, String JSON,String TIME){
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_ID,ID);
        initialValues.put(COL_TYPE,TYPE);
        initialValues.put(COL_INFO,JSON);
        initialValues.put(COL_TIME,TIME);

        return mDb.insert(DATABASE_TABLE_INFO,null, initialValues);
    }

    public Cursor fetchAllReminders(){
        return mDb.query(DATABASE_TABLE_INFO, new String[] {COL_ID, COL_TYPE,
                COL_INFO,COL_TIME}, null, null, null, null, null);
    }

    public DatabaseHelperInformation open() throws android.database.SQLException{
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        mDbHelper.close();
    }
    /*
   *  To create storage dbs
   */
    /*
      * Databasehelper
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DATABASE_CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            // Not used, but you could upgrade the database with ALTER scripts
        }
    }

}
