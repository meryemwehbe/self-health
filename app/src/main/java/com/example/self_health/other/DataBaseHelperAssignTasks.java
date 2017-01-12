package com.example.self_health.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pc on 1/12/2017.
 */

public class DataBaseHelperAssignTasks {
    private static final String DB_NAME = "Health";
    private static final int DB_VERSION = 1;
    public static final String DATABASE_TABLE_ASS = "Assigntasks";
    public static final String INDEX = "num";
    public static final String COL_ID = "ID";
    public static final String COL_ACTION ="Action";
    public static final String COL_TYPE ="type";
    public static final String COL_MSG ="msg";
    public static final String COL_DONE ="done";
    public static final String COL_TIME ="Time";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Context mContext;

    //String for creating database
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE_ASS + " ("
                    + INDEX + " integer primary key autoincrement, "
                    + COL_ID + " text not null, "
                    + COL_ACTION + " text not null, "
                    + COL_TYPE + " text not null, "
                    + COL_MSG + " text not null, "
                    + COL_DONE + " text not null, "
                    + COL_TIME + " datetime not null); ";

    public DataBaseHelperAssignTasks(Context ctx) {
        mContext = ctx;
    }
    public void drop(){
        mDb.execSQL("DROP TABLE "+ DATABASE_TABLE_ASS);
    }

    public void createtable(){
        mDb.execSQL(DATABASE_CREATE);
    }

    public long createInstance(String ID,String ACTION,String TYPE, String MSG,String DONE,String TIME){
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_ID,ID);
        initialValues.put(COL_ACTION,ACTION);
        initialValues.put(COL_TYPE,TYPE);
        initialValues.put(COL_MSG,MSG);
        initialValues.put(COL_DONE,DONE);
        initialValues.put(COL_TIME,TIME);

        return mDb.insert(DATABASE_TABLE_ASS,null, initialValues);
    }

    public Cursor fetchAllReminders(){
        return mDb.query(DATABASE_TABLE_ASS, new String[] {COL_ID,COL_ACTION, COL_TYPE,
                COL_MSG,COL_DONE,COL_TIME}, null, null, null, null, null);
    }

    public void recreatetable(){
       // mDb.execSQL("DROP TABLE "+ DATABASE_TABLE_ASS);
        mDb.execSQL(DATABASE_CREATE);
    }
    public Cursor getTasks(String id, String time){
        return mDb.query(DATABASE_TABLE_ASS,
                new String [] {COL_ID,COL_ACTION, COL_TYPE,
                        COL_MSG,COL_DONE,COL_TIME},
                COL_ID +"=?" +" AND " + COL_DONE +"=?"+" AND " + COL_TIME +" >=?"+" AND " + COL_ACTION+ "<>?",
                new String[] {id, "0", time,"Take Pills" },
                null, null, null);
    }

    public int assignTaskDone(String id, String action,String date){
        ContentValues cv = new ContentValues();
        cv.put(COL_DONE,"1"); //These Fields should be your String values of actual column names
        return mDb.update(DATABASE_TABLE_ASS, cv, COL_ID+"='"+id+"' AND "+COL_ACTION +" = '"+action +"' AND "+COL_TIME+ "='"+date+"';", null);
    }


    public DataBaseHelperAssignTasks open() throws android.database.SQLException{
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
