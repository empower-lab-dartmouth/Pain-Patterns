package com.example.raymondyao.painpatterns;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PainPatternsSQLDatabase extends SQLiteOpenHelper {

    public static final String DBName = "Pain-Patterns-DB";
    public static final String ROW_ENTRY_TABLE_NAME = "rowEntry";
    public static final String ROW_ENTRY_ENTRY_NUM = "entryNum";
    public static final String ROW_ENTRY_TIMESTAMP = "timestamp";
    public static final String ROW_ENTRY_DEVICE_ID= "deviceID";
    public static final String ROW_ENTRY_VALUE_1 = "value_1";
    public static final String ROW_ENTRY_VALUE_2 = "value_2";
    public static final String ROW_ENTRY_VALUE_3 = "value_3";

    public static final int DATABASE_VS = 1;
    public SQLiteDatabase database;

    public PainPatternsSQLDatabase(Context context) {
        super(context, DBName, null, DATABASE_VS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table rowEntry " +
                        "(entryNum integer primary key, timestamp, deviceID, value_1, value_2, value_3)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS entryNum");
        onCreate(db);
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return this.getWritableDatabase();
    }

    public void addEntry(int entryNum, String timestamp, String deviceID, float value_1, float value_2, float value_3) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("entryNum", entryNum);
        values.put("timestamp", timestamp);
        values.put("deviceID", deviceID);
        values.put("value_1", value_1);
        values.put("value_2", value_2);
        values.put("value_3", value_3);
        database.insert(ROW_ENTRY_TABLE_NAME, null, values);
    }

    public Cursor getRowEntry(int entryNum) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from rowEntry where entryNum=" + entryNum + "", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ROW_ENTRY_TABLE_NAME);
        return numRows;
    }
}
