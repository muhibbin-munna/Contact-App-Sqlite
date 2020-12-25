package com.app.contactappsp.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    String TAG = "MyDatabaseHelper";
    private static int DB_VERSION = 1;
    private static String DATABASE_NAME = "ContactsNotDB";
    public static String TABLE1_NAME = "EventTable";
    public static String KEY_ID = "id";
    public static String CONTACT_ID = "contact_id";
    public static String DES = "description";
    public static String REM_1 = "remark1";
    public static String REM_2 = "remark2";
    public static String NOTIFY = "notify";
    public static String EVENT = "event";
    public static String ROW = "row_index";
    public static String DATE = "date";
    public static String STATUS = "status";

    private static String CREATE_TABLE1 = "CREATE TABLE "+TABLE1_NAME+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+CONTACT_ID+" VARCHAR(500),"+EVENT+" VARCHAR(500),"+ROW+" VARCHAR(500),"+DES+" VARCHAR(500),"+REM_1+" VARCHAR(500),"+REM_2+" VARCHAR(500),"+NOTIFY+" VARCHAR(500),"+DATE+" VARCHAR(500),"+STATUS+" VARCHAR(500) );";
    private static String READ_EVENT_1 = "";

    private Context context;
    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            Log.d(TAG, "onCreate: Success");
            db.execSQL(CREATE_TABLE1);
        }
        catch (Exception e)
        {
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreate: "+e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE1_NAME);
            onCreate(db);
        } catch (Exception e) {
            Log.d(TAG, "onUpgrade: "+e.getMessage());
        }
    }


    public long insertDetailsData(String event,String row,String contactId,String description, String remark1, String remark2,String notify, String date, String status)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENT,event);
        contentValues.put(ROW,row);
        contentValues.put(CONTACT_ID,contactId);
        contentValues.put(DES,description);
        contentValues.put(REM_1,remark1);
        contentValues.put(REM_2,remark2);
        contentValues.put(NOTIFY,notify);
        contentValues.put(DATE,date);
        contentValues.put(STATUS,status);
        return sqLiteDatabase.insert(TABLE1_NAME,null,contentValues);

    }

    public Cursor read_event_1(String contactId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "SELECT * FROM "+TABLE1_NAME+ " WHERE "+CONTACT_ID+"='"+contactId+"' AND "+EVENT+"= 1";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null,null);
        return cursor;
    }
    public Cursor read_event_2(String contactId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "SELECT * FROM "+TABLE1_NAME+ " WHERE "+CONTACT_ID+"='"+contactId+"' AND "+EVENT+"= 2";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null,null);
        return cursor;
    }public Cursor read_event_3(String contactId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "SELECT * FROM "+TABLE1_NAME+ " WHERE "+CONTACT_ID+"='"+contactId+"' AND "+EVENT+"= 3";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null,null);
        return cursor;
    }

    public boolean updateData(String id, String description, String remark1, String remark2, String date, String status)
    {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ID,id);
        contentValues.put(DES,description);
        contentValues.put(REM_1,remark1);
        contentValues.put(REM_2,remark2);
        contentValues.put(DATE,date);
        contentValues.put(STATUS,status);

        int i = sqLiteDatabase.update(TABLE1_NAME,contentValues,KEY_ID+" = ?",new String[]{id});
        return i != 0;
    }
    public int updateNotification(String id, String notify)
    {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ID,id);
        contentValues.put(NOTIFY,notify);
        int i = sqLiteDatabase.update(TABLE1_NAME,contentValues,KEY_ID+" = ?",new String[]{id});
        return i;

    }

    public Cursor read_all_remarks() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "SELECT * FROM "+TABLE1_NAME+ " WHERE "+NOTIFY+ " != 0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null,null);
        return cursor;
    }
    public int deleteData(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE1_NAME, CONTACT_ID+" = ?",new String[]{id});
    }
    public Cursor raw() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE1_NAME , new String[]{});
        return res;
    }

}
