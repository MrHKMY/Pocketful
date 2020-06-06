package com.mindscape.budgetwiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    private MainAdapter mainAdapter, mainAdapter2;
    private TextView budgetValue, wishListValue, savingValue;
    public int newBudget;
    EditText budgetEditText, wishlistEditText, priceEditText;
    int total;
    RecyclerView recyclerView;
    private String[] theQuestionString;
    String q;
    int randomIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        mDatabase = dbHelper.getWritableDatabase();

        theQuestionString = getResources().getStringArray(R.array.theQuestions);
        randomIndex = new Random().nextInt(theQuestionString.length);
        q = theQuestionString[randomIndex];

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainAdapter = new MainAdapter(this, getAllItems());
        recyclerView.setAdapter(mainAdapter);

        mainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "Open the destination/website.", Toast.LENGTH_SHORT).show();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);

        budgetValue = findViewById(R.id.budgetAmountTextView);
        wishListValue = findViewById(R.id.wishlistAmountTextView);
        savingValue = findViewById(R.id.savingTextView);
        Button submitButton = findViewById(R.id.submitButton);
        Button goButton = findViewById(R.id.goButton);

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
                addWishlistDialog();
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuestion();
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
                addBudgetDialog();
                return true;
            case R.id.reset_budget_menu:
                resetBudget();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeItem(long id){
        mDatabase.delete(DatabaseHelper.WISHLIST_TABLE, DatabaseHelper._ID + "=" + id, null);
        mainAdapter.swapCursor(getAllItems());
        Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("Total"));
        }
        wishListValue.setText(String.valueOf(total));
        savingValue.setText(String.valueOf(newBudget-total));
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

    private void addWishlistDialog() {
        /*InputDialog inputDialog = new InputDialog();
        inputDialog.show(getSupportFragmentManager(), "Input Dialog");
         */


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.wishlist_input_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        wishlistEditText = view.findViewById(R.id.newWishlistEditText);
        priceEditText = view.findViewById(R.id.newPriceEditText);
        final ImageButton wishlistcheckButton = view.findViewById(R.id.newWishlistCheckButton);
        ImageButton wishlistcrossButton = view.findViewById(R.id.newWishlistCrossButton);

        wishlistcheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wishlistEditText.getText().toString().trim().length() > 0 && priceEditText.getText().toString().trim().length() > 0) {

                    String name = wishlistEditText.getText().toString();
                    String price = priceEditText.getText().toString();

                    DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                    long result = dbHelper.createWishList(name, price);
                    mainAdapter.swapCursor(getAllItems());

                    wishlistEditText.getText().clear();
                    priceEditText.getText().clear();

                    if (result == -1) {
                        Toast.makeText(MainActivity.this, "Failed storing new item.", Toast.LENGTH_SHORT).show();
                    }

                    Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);

                    if (cursor.moveToFirst()) {
                        total = cursor.getInt(cursor.getColumnIndex("Total"));
                    }


                    wishListValue.setText(String.valueOf(total));
                    savingValue.setText(String.valueOf(newBudget-total));

                    alertDialog.cancel();
                } else {
                    Toast.makeText(MainActivity.this, "Cannot save empty value.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        wishlistcrossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void addBudgetDialog() {
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
                    savingValue.setText(String.valueOf(newBudget-total));
                    alertDialog.cancel();

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

    }

    private void resetBudget() {
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
        newBudget = 0;
        long result = dbHelper.createBudget(newBudget);
        if (result == -1) {
            Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
        }

        String selectQuery = "SELECT * FROM " + DatabaseHelper.BUDGET_TABLE;
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        cursor.moveToLast();
        int theData = cursor.getInt(cursor.getColumnIndex("budget"));

        budgetValue.setText(String.valueOf(theData));
        savingValue.setText(String.valueOf(newBudget - total));
    }

    private void startQuestion() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.question_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView newRecyclerView;
        newRecyclerView = view.findViewById(R.id.newrecyclerview);
        newRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainAdapter2 = new MainAdapter(this, getAllItems());
        newRecyclerView.setAdapter(mainAdapter2);

        mainAdapter2.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "delete or do something ", Toast.LENGTH_SHORT).show();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((long) viewHolder.itemView.getTag());
                mainAdapter2.swapCursor(getAllItems());
            }
        }).attachToRecyclerView(newRecyclerView);



        alertDialog.setView(view);
        alertDialog.show();

        final TextView questions = alertDialog.findViewById(R.id.questionsTextView);
        questions.setText(q);

        ImageButton check = view.findViewById(R.id.checkButton);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomIndex = new Random().nextInt(theQuestionString.length);
                q = theQuestionString[randomIndex];
                questions.setText(q);
            }
        });
    }


}
