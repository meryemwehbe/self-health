package com.example.self_health.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pc on 1/11/2017.
 */

public class DataBaseHelperRelations {

    private static final String DATABASE_NAME = "Health";
    private static final String DATABASE_TABLE = "relations";
    private static final int DATABASE_VERSION = 1;
    public static final String INDEX = "num";
    public static final String KEY_DOC = "doc_ID";
    public static final String KEY_PAT = "pat_ID";
    public static final String KEY_NAME = "name";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    // Be careful to the spaces in the String!
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " ("
                    + INDEX + " integer primary key autoincrement, "
                    + KEY_DOC + " text not null, "
                    + KEY_PAT + " text not null, "
                    + KEY_NAME + " text not null);";

    private final Context mCtx;

    public DataBaseHelperRelations(Context ctx){
        this.mCtx = ctx;
    }

    public void recreatetable(){
        mDb.execSQL("DROP TABLE "+ DATABASE_TABLE);
        mDb.execSQL(DATABASE_CREATE);
    }

    public DataBaseHelperRelations open() throws android.database.SQLException{
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    public void createeTable(){
        mDb.execSQL(DATABASE_CREATE);
    }
    public void close(){
        mDbHelper.close();
    }

    public long insertPatient(String doc, String pat,String name){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DOC,doc);
        initialValues.put(KEY_PAT, pat);
        initialValues.put(KEY_NAME, name);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public Cursor fetchAllReminders(){
        return mDb.query(DATABASE_TABLE, new String[] {KEY_DOC, KEY_PAT,
                KEY_NAME}, null, null, null, null, null);
    }
    public Cursor getPatients(String id){
        return mDb.rawQuery("SELECT * FROM "+ DATABASE_TABLE + " WHERE "+ KEY_DOC + " = '"+ id + "';",null);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
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


