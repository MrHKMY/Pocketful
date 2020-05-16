package com.mindscape.budgetwiser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Created by Hakimi on 15/5/2020.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "budgetDatabase.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE "
                + DatabaseContract.DatabaseEntry.TABLE_NAME
                + " ("
                + DatabaseContract.DatabaseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.DatabaseEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + DatabaseContract.DatabaseEntry.COLUMN_AMOUNT + " TEXT NOT NULL, "
                + DatabaseContract.DatabaseEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";
        sqLiteDatabase.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "
                + DatabaseContract.DatabaseEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
