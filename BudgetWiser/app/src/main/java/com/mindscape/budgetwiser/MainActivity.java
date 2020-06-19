package com.mindscape.budgetwiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    private MainAdapter mainAdapter, mainAdapter2;
    private LaterAdapter laterAdapter;
    private TextView budgetValue, wishListValue, savingValue, wishlistTitle, laterTitle;
    public int newBudget;
    EditText budgetEditText, wishlistEditText, priceEditText;
    int total;
    RecyclerView recyclerView, laterRecyclerView;
    private String[] theQuestionString;
    String q;
    int randomIndex;
    LinearLayout savingLayout;
    private boolean isFabTapped = false;
    private BottomAppBar bar;
    PieChart pieChart;
    public ArrayList<PieEntry> dataValue = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bar = findViewById(R.id.bottomBar);
        FloatingActionButton fab = findViewById(R.id.fab);
        setSupportActionBar(bar);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        mDatabase = dbHelper.getWritableDatabase();

        theQuestionString = getResources().getStringArray(R.array.theQuestions);
        randomIndex = new Random().nextInt(theQuestionString.length);
        q = theQuestionString[randomIndex];

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        laterRecyclerView = findViewById(R.id.laterRecyclerview);
        laterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        laterAdapter = new LaterAdapter(this, getLaterItems());
        laterRecyclerView.setAdapter(laterAdapter);

        mainAdapter = new MainAdapter(this, getAllItems());
        recyclerView.setAdapter(mainAdapter);
        //laterRecyclerView.setAdapter(mainAdapter);

        mainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "Position : " + position, Toast.LENGTH_SHORT).show();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                addtoLater((long) viewHolder.itemView.getTag());
                removeWishItem((long) viewHolder.itemView.getTag());
                laterAdapter.swapCursor(getLaterItems());
            }
        }).attachToRecyclerView(recyclerView);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeLaterItem((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(laterRecyclerView);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                moveToWishlist((long) viewHolder.itemView.getTag());
                removeLaterItem((long) viewHolder.itemView.getTag());
                mainAdapter.swapCursor(getAllItems());
            }
        }).attachToRecyclerView(laterRecyclerView);


        budgetValue = findViewById(R.id.budgetAmountTextView);
        wishListValue = findViewById(R.id.wishlistAmountTextView);
        savingValue = findViewById(R.id.savingTextView);
        savingLayout = findViewById(R.id.savingLinearLayout);
        wishlistTitle = findViewById(R.id.wishlistText);
        laterTitle = findViewById(R.id.laterText);
        Button goButton = findViewById(R.id.goButton);
        laterRecyclerView.setVisibility(View.GONE);
        pieChart = findViewById(R.id.pieChartID);

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomNavigationDrawerFragment bottomNavigationDrawerFragment = new BottomNavigationDrawerFragment();
                bottomNavigationDrawerFragment.show(getSupportFragmentManager(), bottomNavigationDrawerFragment.getTag());
            }
        });

        createStartUp();
        createPieChart();

        Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("Total"));
        }
        wishListValue.setText(String.valueOf(total));
        savingValue.setText(String.valueOf(newBudget-total));
        if (newBudget-total > 0 ){
            savingLayout.setBackgroundResource(R.color.colorPrimary);
        } else if (newBudget - total < 0){
            savingLayout.setBackgroundResource(R.color.red);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWishlistDialog();
                /*isFabTapped = !isFabTapped;
                if (isFabTapped) {
                    bar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                } else {
                    bar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                }

                 */
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuestion();
            }
        });

        wishlistTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                laterRecyclerView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                wishlistTitle.setBackgroundResource(R.color.colorAccent);
                laterTitle.setBackgroundResource(R.color.colorPrimaryDark);
            }
        });
        laterTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                laterRecyclerView.setVisibility(View.VISIBLE);
                laterTitle.setBackgroundResource(R.color.colorAccent);
                wishlistTitle.setBackgroundResource(R.color.colorPrimaryDark);
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
            case R.id.grocery_menu:
                Intent i = new Intent(MainActivity.this, GroceriesActivity.class);
                //bar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                startActivity(i);
                return true;
            case R.id.expense_menu:
                Intent a = new Intent(MainActivity.this, ExpensesActivity.class);
                startActivity(a);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeWishItem(long id){
        mDatabase.delete(DatabaseHelper.WISHLIST_TABLE, DatabaseHelper._ID + "=" + id, null);
        mainAdapter.swapCursor(getAllItems());
        Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("Total"));
        }
        wishListValue.setText(String.valueOf(total));
        savingValue.setText(String.valueOf(newBudget-total));
        if (newBudget-total > 0 ){
            savingLayout.setBackgroundResource(R.color.colorPrimary);
        } else if (newBudget - total < 0){
            savingLayout.setBackgroundResource(R.color.red);
        }
        createPieChart();
    }

    private void removeLaterItem(long id){
        mDatabase.delete(DatabaseHelper.LATER_TABLE, DatabaseHelper._ID + "=" + id, null);
        laterAdapter.swapCursor(getLaterItems());
        /*Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("Total"));
        }
        wishListValue.setText(String.valueOf(total));
        savingValue.setText(String.valueOf(newBudget-total));
        if (newBudget-total > 0 ){
            savingLayout.setBackgroundResource(R.color.darkGreen);
        } else if (newBudget - total < 0){
            savingLayout.setBackgroundResource(R.color.red);
        }

         */
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

    private Cursor getLaterItems() {
        return mDatabase.query(
                DatabaseHelper.LATER_TABLE,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.LATER_TIMESTAMP + " DESC"
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
                    if (newBudget-total > 0 ){
                        savingLayout.setBackgroundResource(R.color.colorPrimary);
                    } else if (newBudget - total < 0){
                        savingLayout.setBackgroundResource(R.color.red);
                    }
                    createPieChart();

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
                    if (newBudget-total > 0 ){
                        savingLayout.setBackgroundResource(R.color.colorPrimary);
                    } else if (newBudget - total < 0){
                        savingLayout.setBackgroundResource(R.color.red);
                    }
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
        if (newBudget-total > 0 ){
            savingLayout.setBackgroundResource(R.color.colorPrimary);
        } else if (newBudget - total < 0){
            savingLayout.setBackgroundResource(R.color.red);
        }
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

                mainAdapter2.swapCursor(getAllItems());
                addtoLater((long) viewHolder.itemView.getTag());
                removeWishItem((long) viewHolder.itemView.getTag());

                laterAdapter = new LaterAdapter(getApplicationContext(), getLaterItems());
                laterRecyclerView.setAdapter(laterAdapter);
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
                createPieChart();
            }
        });
    }

    private void addtoLater(long id) {
        String[] coloumns = new String[] { DatabaseHelper._ID, DatabaseHelper.WISHLIST_NAME, DatabaseHelper.WISHLIST_AMOUNT, DatabaseHelper.WISHLIST_TIMESTAMP };
        String name;
        String price;
        Cursor c = mDatabase.query(DatabaseHelper.WISHLIST_TABLE, coloumns, DatabaseHelper._ID + "=" + id, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
            name = c.getString(1);
            price = c.getString(2);
            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
            long result = dbHelper.createLater(name, price);
            if (result == -1) {
                Toast.makeText(MainActivity.this, "Failed storing new item.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveToWishlist(long id) {
        String[] coloumns = new String[] { DatabaseHelper._ID, DatabaseHelper.LATER_NAME, DatabaseHelper.LATER_AMOUNT, DatabaseHelper.LATER_TIMESTAMP };
        String name;
        String price;
        Cursor c = mDatabase.query(DatabaseHelper.LATER_TABLE, coloumns, DatabaseHelper._ID + "=" + id, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
            name = c.getString(1);
            price = c.getString(2);
            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
            long result = dbHelper.createWishList(name, price);
            if (result == -1) {
                Toast.makeText(MainActivity.this, "Failed storing new item.", Toast.LENGTH_SHORT).show();
            }
        }

        Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("Total"));
        }
        wishListValue.setText(String.valueOf(total));
        savingValue.setText(String.valueOf(newBudget-total));
        createPieChart();
    }

    private void createPieChart() {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.75f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(getColor(R.color.white));
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setNoDataText("At least one data are required");

        pieChart.animateY(1000, Easing.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(getDataValue(), "Labels");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(15f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLUE);

        pieChart.setData(data);
    }

    private ArrayList<PieEntry> getDataValue() {
        dataValue.clear();

        String[] columns = {"amount"};
        Cursor cursor = mDatabase.query(DatabaseHelper.WISHLIST_TABLE, columns, null,null,null,null,null);

        for (int i = 0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            dataValue.add(new PieEntry(cursor.getFloat(0)));
        }

        Cursor cursor2 = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);
        if (cursor2.moveToFirst()) {
            total = cursor2.getInt(cursor2.getColumnIndex("Total"));
        }
        if (total == 0){
            pieChart.setNoDataText("No Data");
            pieChart.setBackgroundResource(R.color.colorPrimaryDark);
        } else
            pieChart.setBackgroundResource(R.color.white);
        return dataValue;
    }
}
