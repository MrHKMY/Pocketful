package com.mindscape.budgetwiser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    private MainAdapter mainAdapter;
    public EditText itemEditText;
    public EditText amountEditText;

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

        itemEditText = findViewById(R.id.itemEditText);
        amountEditText = findViewById(R.id.priceEditText);
        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addItem();
                openDialog();
            }
        });
    }

    private void addItem () {
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
}
