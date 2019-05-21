package com.kabu.kabi.fridgeinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kabu.kabi.fridgeinventory.data.FridgeContract.FridgeEntry;

public class FridgeDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "items.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FridgeEntry.TABLE_NAME + " (" +
                    FridgeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FridgeEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL," +
                    FridgeEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 1," +
                    FridgeEntry.COLUMN_ITEM_UNIT + " INTEGER NOT NULL DEFAULT 0" + ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FridgeEntry.TABLE_NAME;

    public FridgeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Helper", SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
