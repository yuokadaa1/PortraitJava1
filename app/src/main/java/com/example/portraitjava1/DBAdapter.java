package com.example.portraitjava1;

import java.sql.Blob;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//DBにはpost:サムネイル情報、kbn：区分、id:モデルＩＤをセットしておく。
public class DBAdapter {
    static final String DATABASE_NAME = "photo.db";
    static final int DATABASE_VERSION = 3;
    public static final String TABLE_NAME_POST = "posts";
    public static final String COL_POST_ID = "id";
    public static final String COL_POST_MODELID = "modelid";
    public static final String COL_POST_MODELIDNUM = "modelidnum";
    public static final String COL_POST_MODELNAME = "modelname";
    public static final String COL_POST_MODELINSERTNUM = "modelinsertnum";
//    public static final String COL_POST_KBNID = "kbnid";
    public static final String COL_POST_THUMBNAIL = "thumbnail";
    public static final String COL_POST_CREATED = "createdat";
//    public static final String TABLE_NAME_ID = "ids";
//    public static final String COL_ID_MODELID = "modelid";
//    public static final String COL_ID_MODELNAME = "modelidnum";
//    public static final String COL_ID_CREATED = "createdat";
//    public static final String TABLE_NAME_KBN = "kbn";
///    public static final String COL_KBN_KBNID = "kbnid";
//    public static final String COL_KBN_KBNNAME = "kbnname";
//    public static final String COL_KBN_CREATED = "createdat";

    protected final Context context;
    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    public DBAdapter(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            //再構築する場合はこれのコメントを外す
//            context.deleteDatabase(DATABASE_NAME);
        }

        //DBの構造はBLOB、データ格納はbyte[]
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME_POST + " (" +
                    COL_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_POST_MODELID + " INTEGER ," +
                    COL_POST_MODELIDNUM + " TEXT NOT NULL," +
                    COL_POST_MODELNAME + " TEXT NOT NULL," +
                    COL_POST_MODELINSERTNUM + " TEXT NOT NULL," +
                    COL_POST_THUMBNAIL + " BLOB NOT NULL," +
                    COL_POST_CREATED + " TEXT NOT NULL ," +
                    "UNIQUE("  + COL_POST_MODELID + "," + COL_POST_MODELID + "," + COL_POST_MODELINSERTNUM + "));");

//            db.execSQL("CREATE TABLE " + TABLE_NAME_ID + " (" + COL_ID_MODELID + " INTEGER PRIMARY KEY ," +
//                    COL_ID_MODELNAME + " TEXT NOT NULL," +
//                    COL_POST_CREATED + " TEXT);");

//            db.execSQL("CREATE TABLE " + TABLE_NAME_KBN + " (" + COL_KBN_KBNID + " INTEGER PRIMARY KEY ," +
//                    COL_KBN_KBNNAME + " TEXT NOT NULL," +
//                    COL_KBN_CREATED + " TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_POST);
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ID);
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_KBN);
            onCreate(db);
        }
    }

    public DBAdapter open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    //
    // App Methods
    //
    public boolean deleteAllNotes() {
        return db.delete(TABLE_NAME_POST, null, null) > 0;
    }

    public boolean deleteNote(int id) {
        return db.delete(TABLE_NAME_POST, COL_POST_MODELID + "=" + id, null) > 0;
    }


    public Cursor getAllNotes() {
        return db.query(TABLE_NAME_POST, null, null, null, null, null, null, null);
    }

    public void saveNote(int modelid, int modelidnum,String modelname, int modelinsertnum, byte[] thumnail,String date) {
        ContentValues values = new ContentValues();
        values.put(COL_POST_MODELID, modelid);
        values.put(COL_POST_MODELIDNUM, modelidnum);
        values.put(COL_POST_MODELNAME, modelname);
        values.put(COL_POST_MODELINSERTNUM, modelinsertnum);
        //DBの構造はBLOB、データ格納はbyte[]、受け取った状態ではbase64encode済み->
        values.put(COL_POST_THUMBNAIL, thumnail);
        values.put(COL_POST_CREATED, date);
        //sqliteではreplaceがupsert
        db.replace(TABLE_NAME_POST, null, values);
    }

}