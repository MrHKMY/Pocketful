package com.mindscape.pocketful;

import android.content.ContentValues;
import android.content.Context;
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
    public static final String WISHLIST_CATEGORY = "category";
    public static final String WISHLIST_TIMESTAMP = "Wishlisttimestamp";

    public static final String BUDGET_TABLE = "budgetTable";
    public static final String BUDGET_AMOUNT = "budget";
    public static final String BUDGET_TIMESTAMP = "budgetTimestamp";

    public static final String LATER_TABLE = "latertable";
    public static final String LATER_NAME = "lateritems";
    public static final String LATER_AMOUNT = "lateramount";
    public static final String LATER_CATEGORY = "latercategory";
    public static final String LATER_TIMESTAMP = "latertimestamp";

    public static final String EXPENSE_TABLE = "ExpenseTable";
    public static final String EXPENSE_CATEGORY = "Category";
    public static final String EXPENSE_VALUE = "Value";
    public static final String EXPENSE_STATUS = "Status";
    public static final String EXPENSE_NOTE = "Note";
    public static final String EXPENSE_TIMESTAMP = "Date";
    public static final String EXPENSE_NAME = "NAME";

    public static final String NOTE_TABLE = "NoteTable";
    public static final String NOTE_TITLE = "Title";
    public static final String NOTE_CONTENT = "Content";
    public static final String NOTE_TIMESTAMP = "Time";

    private static DatabaseHelper mInstance;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createWishTable = "CREATE TABLE "
                + WISHLIST_TABLE
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WISHLIST_NAME + " TEXT, "
                + WISHLIST_AMOUNT + " FLOAT, "
                + WISHLIST_CATEGORY + " TEXT, "
                + WISHLIST_TIMESTAMP + " TIMESTAMP DEFAULT (datetime('now','localtime')));";

        String createBudgetTable = "CREATE TABLE "
                + BUDGET_TABLE
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BUDGET_AMOUNT + " FLOAT, "
                + BUDGET_TIMESTAMP + " TIMESTAMP DEFAULT (datetime('now','localtime')));";

        String createLaterTable = "CREATE TABLE "
                + LATER_TABLE
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LATER_NAME + " TEXT, "
                + LATER_AMOUNT + " FLOAT, "
                + LATER_CATEGORY + " TEXT, "
                + LATER_TIMESTAMP + " TIMESTAMP DEFAULT (datetime('now','localtime')));";

        String createExpenseTable = "CREATE TABLE "
                + EXPENSE_TABLE
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EXPENSE_CATEGORY + " TEXT, "
                + EXPENSE_NAME + " TEXT, "
                + EXPENSE_VALUE + " FLOAT, "
                + EXPENSE_STATUS + " TEXT, "
                + EXPENSE_NOTE + " TEXT, "
                + EXPENSE_TIMESTAMP + " TIMESTAMP DEFAULT (datetime('now','localtime')));";

        String createNoteTable = "CREATE TABLE "
                + NOTE_TABLE
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NOTE_TITLE + " TEXT, "
                + NOTE_CONTENT + " TEXT, "
                + NOTE_TIMESTAMP + " TIMESTAMP DEFAULT (datetime('now','localtime')));";

        sqLiteDatabase.execSQL(createWishTable);
        sqLiteDatabase.execSQL(createBudgetTable);
        sqLiteDatabase.execSQL(createLaterTable);
        sqLiteDatabase.execSQL(createExpenseTable);
        sqLiteDatabase.execSQL(createNoteTable);
        String sql = "INSERT INTO " + BUDGET_TABLE + "(" + _ID + ", " + BUDGET_AMOUNT + ", " + BUDGET_TIMESTAMP + ") VALUES('1', '0', CURRENT_TIMESTAMP)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WISHLIST_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BUDGET_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LATER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE);

        onCreate(sqLiteDatabase);

    }

    public long createWishList(String item, float price, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(WISHLIST_NAME, item);
        cv.put(WISHLIST_AMOUNT, price);
        cv.put(WISHLIST_CATEGORY, category);

        long result = db.insert(WISHLIST_TABLE, null, cv);

        return result;
    }

    public long createBudget(float budget){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(BUDGET_AMOUNT, budget);

        long result = db.insert(BUDGET_TABLE, null, cv);
        return result;

    }

    public long createLater(String item, float price, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LATER_NAME, item);
        cv.put(LATER_AMOUNT, price);
        cv.put(LATER_CATEGORY, category);

        long result = db.insert(LATER_TABLE, null, cv);
        return result;
    }

    public long createExpense(float value, int category, String name, String note, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(EXPENSE_STATUS, status);
        cv.put(EXPENSE_CATEGORY, category);
        cv.put(EXPENSE_NAME, name);
        cv.put(EXPENSE_NOTE, note);
        cv.put(EXPENSE_VALUE, value);

        long result = db.insert(EXPENSE_TABLE, null, cv);
        return result;
    }

    public long createNote(String title, String content){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NOTE_TITLE, title);
        cv.put(NOTE_CONTENT, content);

        long result = db.insert(NOTE_TABLE, null, cv);
        return result;
    }

}
