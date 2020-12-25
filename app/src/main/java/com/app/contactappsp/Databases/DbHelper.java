package com.app.contactappsp.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "contacts.db";
    public static final String TABLE_NAME = "CONTACTS";
    public static final int DB_VERSION = 3;

    public static final String COLUMN_1 = "ID";
    public static final String COLUMN_2 = "NAME";
    public static final String COLUMN_3 = "PHONE";
    public static final String COLUMN_4 = "GENDER";

    public static final String COLUMN_5 = "AGE";
    public static final String COLUMN_6 = "ADDRESS_1";
    public static final String COLUMN_7 = "ADDRESS_2";
    public static final String COLUMN_8 = "CITY";
    public static final String COLUMN_9 = "POST_CODE";
    public static final String COLUMN_10 = "REMARK";
    public static final String COLUMN_11 = "IMAGE";
    public static final String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +COLUMN_1+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +COLUMN_2+" TEXT, "
            +COLUMN_3+" TEXT, "
            +COLUMN_4+" TEXT, "
            +COLUMN_5+" TEXT, "
            +COLUMN_6+" TEXT, "
            +COLUMN_7+" TEXT, "
            +COLUMN_8+" TEXT, "
            +COLUMN_9+" TEXT, "
            +COLUMN_10+" TEXT, "
            +COLUMN_11+" TEXT)";

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(QUERY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public Cursor raw() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME , new String[]{});

        return res;
    }
}
