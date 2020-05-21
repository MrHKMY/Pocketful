package com.mindscape.budgetwiser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

/**
 * Created by Hakimi on 15/5/2020.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String DATABASE_NAME = "budgetdb.db";
    public static final String WISHLIST_TABLE = "wishlistable";
    public static final String WISHLIST_NAME = "items";
    public static final String WISHLIST_AMOUNT = "amount";
    public static final String WISHLIST_TIMESTAMP = "Wishlisttimestamp";

    public static final String BUDGET_TABLE = "budgetTable";
    public static final String BUDGET_AMOUNT = "budget";
    public static final String BUDGET_TIMESTAMP = "budgetTimestamp";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createWishTable = "CREATE TABLE "
                + WISHLIST_TABLE
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WISHLIST_NAME + " TEXT, "
                + WISHLIST_AMOUNT + " TEXT, "
                + WISHLIST_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";

        String createBudgetTable = "CREATE TABLE "
                + BUDGET_TABLE
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BUDGET_AMOUNT + " INTEGER, "
                + BUDGET_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";

        sqLiteDatabase.execSQL(createWishTable);
        sqLiteDatabase.execSQL(createBudgetTable);
        String sql = "INSERT INTO " + BUDGET_TABLE + "(" + _ID + ", " + BUDGET_AMOUNT + ", " + BUDGET_TIMESTAMP + ") VALUES('1', '0', CURRENT_TIMESTAMP)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WISHLIST_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BUDGET_TABLE);

        onCreate(sqLiteDatabase);

    }

    public long createWishList(String item, String price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(WISHLIST_NAME, item);
        cv.put(WISHLIST_AMOUNT, price);

        long result = db.insert(WISHLIST_TABLE, null, cv);

        return result;
    }

    public long createBudget(int budget){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(BUDGET_AMOUNT, budget);

        long result = db.insert(BUDGET_TABLE, null, cv);
        return result;

    }

    public long getBudget(){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + BUDGET_TABLE + " ORDER BY " + BUDGET_TIMESTAMP + " DESC LIMIT 1" ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();
        long result = cursor.getColumnIndex("New Budget");


        return result;
    }




}