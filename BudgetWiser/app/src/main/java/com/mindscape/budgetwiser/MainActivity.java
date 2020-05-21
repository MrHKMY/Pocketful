package com.mindscape.budgetwiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    private MainAdapter mainAdapter;
    private TextView budgetValue, wishListValue, savingValue;
    public int newBudget;
    EditText budgetEditText;
    int total;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        mDatabase = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainAdapter = new MainAdapter(this, getAllItems());
        recyclerView.setAdapter(mainAdapter);

        budgetValue = findViewById(R.id.budgetAmountTextView);
        wishListValue = findViewById(R.id.wishlistAmountTextView);
        savingValue = findViewById(R.id.savingTextView);
        Button submitButton = findViewById(R.id.submitButton);

        createStartUp();



        Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("Total"));
        }


        wishListValue.setText(String.valueOf(total));
        savingValue.setText(String.valueOf(newBudget-total));


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

    }

    private void createStartUp() {
        String selectQuery = "SELECT * FROM " + DatabaseHelper.BUDGET_TABLE;
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        cursor.moveToLast();
        int theData = cursor.getInt(cursor.getColumnIndex("budget"));
        budgetValue.setText(String.valueOf(theData));
        newBudget = theData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_budget_menu:
                openBudgetDialog();
                return true;
            case R.id.reset_budget_menu:
                budgetValue.setText("0");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Cursor getAllItems() {
        return mDatabase.query(
                DatabaseHelper.WISHLIST_TABLE,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.WISHLIST_TIMESTAMP + " DESC"
        );
    }


    private void openDialog() {
        InputDialog inputDialog = new InputDialog();
        inputDialog.show(getSupportFragmentManager(), "Input Dialog");
        mainAdapter.notifyDataSetChanged();
        mainAdapter = new MainAdapter(this, getAllItems());
        recyclerView.setAdapter(mainAdapter);
    }

    private void openBudgetDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.budget_input_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        budgetEditText = view.findViewById(R.id.newBudgetEditText);
        final ImageButton checkButton = view.findViewById(R.id.newBudgetCheckButton);
        ImageButton crossButton = view.findViewById(R.id.newBudgetCrossButton);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (budgetEditText.getText().toString().length() > 0) {
                    newBudget = Integer.parseInt(budgetEditText.getText().toString());
                    //store new budget into database
                    DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                    long result = dbHelper.createBudget(newBudget);
                    if (result == -1) {
                        Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                    }

                    String selectQuery = "SELECT * FROM " + DatabaseHelper.BUDGET_TABLE;
                    Cursor cursor = mDatabase.rawQuery(selectQuery, null);
                    cursor.moveToLast();
                    int theData = cursor.getInt(cursor.getColumnIndex("budget"));

                    budgetValue.setText(String.valueOf(theData));
                    alertDialog.cancel();
                    //mDatabase = dbHelper.getWritableDatabase();
                    //ContentValues cvalue = new ContentValues();


                    //cvalue.put(DatabaseContract.DatabaseEntry.COLUMN_BUDGET, newBudget);
                    //long result = mDatabase.insert(DatabaseContract.DatabaseEntry.TABLE_NAME, null, cvalue);

                    /*@SuppressLint("Recycle") Cursor cur = mDatabase.rawQuery("SELECT " + DatabaseContract.DatabaseEntry.COLUMN_BUDGET +" FROM "+ DatabaseContract.DatabaseEntry.TABLE_NAME + " WHERE " + DatabaseContract.DatabaseEntry.COLUMN_BUDGET + " IS NOT NULL", null);
                    if (cur.moveToFirst()) {
                        total2 = cur.getInt(cur.getColumnIndex("Total"));
                    }

                    budgetValue.setText(String.valueOf(newBudget));
                    if (result == -1) {
                        Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.cancel();
                    savingValue.setText(String.valueOf(newBudget-total));

                     */
                } else {
                    Toast.makeText(MainActivity.this, "Value cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();

        /*String selectQuery = "SELECT * FROM " + DatabaseHelper.BUDGET_TABLE;
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        cursor.moveToLast();
        int theData = cursor.getInt(cursor.getColumnIndex("budget"));

        budgetValue.setText(String.valueOf(theData));

         */
    }


}
