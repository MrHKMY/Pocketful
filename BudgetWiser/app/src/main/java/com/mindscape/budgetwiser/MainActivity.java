package com.mindscape.budgetwiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Random;

import adapters.LaterAdapter;
import adapters.MainAdapter;
import fragments.ExpensesFragment;
import fragments.GroceriesFragment;
import fragments.HomeFragment;
import fragments.SettingFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_layout);

        /*FloatingActionButton fab = findViewById(R.id.floatButton);

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
                isFabTapped = !isFabTapped;
                if (isFabTapped) {
                    bar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                } else {
                    bar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                }


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


*/
        BottomNavigationView bottomView = findViewById(R.id.bottom_navigation);
        bottomView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_groceries:
                            selectedFragment = new GroceriesFragment();
                            break;
                        case R.id.nav_expenses:
                            selectedFragment = new ExpensesFragment();
                            break;
                        case R.id.nav_setting:
                            selectedFragment = new SettingFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, selectedFragment).commit();

                    return true;
                }
            };

}
