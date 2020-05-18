package com.mindscape.budgetwiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private TextView budgetTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainAdapter = new MainAdapter(this, getAllItems());
        recyclerView.setAdapter(mainAdapter);

        budgetTextView = findViewById(R.id.budgetAmountTextView);
        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addItem();
                openDialog();
                mainAdapter.notifyDataSetChanged();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_budget_menu:
                openBudgetDialog();
                return true;
            case R.id.reset_budget_menu:
                budgetTextView.setText("0");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*private void addItem () {
        if (itemEditText.getText().toString().trim().length() == 0 || amountEditText.getText().toString().trim().length() ==0 ){
            return;
        }

        String name = itemEditText.getText().toString();
        String price = amountEditText.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.DatabaseEntry.COLUMN_NAME, name);
        cv.put(DatabaseContract.DatabaseEntry.COLUMN_AMOUNT, price);

        mDatabase.insert(DatabaseContract.DatabaseEntry.TABLE_NAME, null, cv);
        mainAdapter.swapCursor(getAllItems());
        itemEditText.getText().clear();
        amountEditText.getText().clear();
    }

     */

    private Cursor getAllItems() {
        return  mDatabase.query(
                DatabaseContract.DatabaseEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DatabaseContract.DatabaseEntry.COLUMN_TIMESTAMP + " DESC"
        );
    }

    private void openDialog() {
        InputDialog inputDialog = new InputDialog();
        inputDialog.show(getSupportFragmentManager(), "Input Dialog");
    }

    private void openBudgetDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.budget_input_dialog,null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText budgetEditText = view.findViewById(R.id.newBudgetEditText);
        TextView textView = view.findViewById(R.id.newBudgetTitle);
        ImageButton button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "button tapped", Toast.LENGTH_SHORT).show();
            }
        });



        alertDialog.setView(view);
        alertDialog.show();

    }

}
