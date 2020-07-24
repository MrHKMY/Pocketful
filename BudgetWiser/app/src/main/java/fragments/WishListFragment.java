package fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mindscape.budgetwiser.DatabaseHelper;

import adapters.ExpenseHistoryAdapter;
import adapters.LaterAdapter;
import adapters.MainAdapter;

import com.mindscape.budgetwiser.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Hakimi on 25/6/2020.
 */
public class WishListFragment extends Fragment {

    public WishListFragment() {
    }

    private SQLiteDatabase mDatabase;
    private MainAdapter mainAdapter, mainAdapter2;
    private LaterAdapter laterAdapter;
    private TextView budgetValue, wishListValue, savingValue, wishlistTitle, laterTitle, wishlistAnalysis;
    public float newBudget;
    private EditText budgetEditText, wishlistEditText, priceEditText;
    Float total;
    private RecyclerView recyclerView, laterRecyclerView;
    private String[] theQuestionString;
    private String q;
    int randomIndex;
    private LinearLayout savingLayout, budgetBox;
    private PieChart pieChart;
    public ArrayList<PieEntry> dataValue = new ArrayList<>();
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private  Button goButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        FloatingActionButton fab = view.findViewById(R.id.floatButton);

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        mDatabase = dbHelper.getWritableDatabase();

        theQuestionString = getResources().getStringArray(R.array.theQuestions);
        randomIndex = new Random().nextInt(theQuestionString.length);
        q = theQuestionString[randomIndex];

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        laterRecyclerView = view.findViewById(R.id.laterRecyclerview);
        laterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        laterAdapter = new LaterAdapter(getContext(), getLaterItems());
        laterRecyclerView.setAdapter(laterAdapter);

        mainAdapter = new MainAdapter(getContext(), getAllItems());
        recyclerView.setAdapter(mainAdapter);
        //laterRecyclerView.setAdapter(mainAdapter);

        mainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Toast.makeText(getActivity(), "Position : " + position, Toast.LENGTH_SHORT).show();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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

        budgetValue = view.findViewById(R.id.budgetAmountTextView);
        wishListValue = view.findViewById(R.id.wishlistAmountTextView);
        savingValue = view.findViewById(R.id.savingTextView);
        savingLayout = view.findViewById(R.id.savingLinearLayout);
        wishlistTitle = view.findViewById(R.id.wishlistText);
        laterTitle = view.findViewById(R.id.laterText);
        goButton = view.findViewById(R.id.goButton);
        laterRecyclerView.setVisibility(View.GONE);
        pieChart = view.findViewById(R.id.pieChartID);
        budgetBox = view.findViewById(R.id.budget_box);
        wishlistAnalysis = view.findViewById(R.id.wishlistAnalysis);

        budgetBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBudgetDialog();
            }
        });

        createStartUp();
        createPieChart();

        Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);
        if (cursor.moveToFirst()) {
            total = cursor.getFloat(cursor.getColumnIndex("Total"));
        }
        updateSavingLayout();

        fab.setOnClickListener(new View.OnClickListener() {
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

        wishlistTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                laterRecyclerView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                wishlistTitle.setBackgroundResource(R.drawable.wishlist_selected);
                laterTitle.setBackgroundResource(R.drawable.wishlist_default);
            }
        });
        laterTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                laterRecyclerView.setVisibility(View.VISIBLE);
                laterTitle.setBackgroundResource(R.drawable.wishlist_selected);
                wishlistTitle.setBackgroundResource(R.drawable.wishlist_default);
            }
        });

        return view;
    }

    private void updateSavingLayout() {
        wishListValue.setText(String.valueOf(total));
        savingValue.setText(String.valueOf(newBudget - total));
        if (newBudget - total > 0) {
            savingLayout.setBackgroundResource(R.color.green);
            wishlistAnalysis.setText("Your got some saving. Great job!");
            goButton.setVisibility(View.GONE);
        } else if (newBudget - total < 0) {
            savingLayout.setBackgroundResource(R.color.red);
            wishlistAnalysis.setText("Need help to decide which really matter?");
            goButton.setVisibility(View.VISIBLE);
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

    private void createStartUp() {
        String selectQuery = "SELECT * FROM " + DatabaseHelper.BUDGET_TABLE;
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        cursor.moveToLast();
        int theData = cursor.getInt(cursor.getColumnIndex("budget"));
        budgetValue.setText(String.valueOf(theData));
        newBudget = theData;
    }

    private void removeWishItem(long id) {
        mDatabase.delete(DatabaseHelper.WISHLIST_TABLE, DatabaseHelper._ID + "=" + id, null);
        mainAdapter.swapCursor(getAllItems());
        Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);
        if (cursor.moveToFirst()) {
            total = cursor.getFloat(cursor.getColumnIndex("Total"));
        }
        updateSavingLayout();
        createPieChart();
    }

    private void removeLaterItem(long id) {
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

    private void addWishlistDialog() {
        /*InputDialog inputDialog = new InputDialog();
        inputDialog.show(getSupportFragmentManager(), "Input Dialog");
         */


        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.wishlist_input_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        wishlistEditText = view.findViewById(R.id.newWishlistEditText);
        priceEditText = view.findViewById(R.id.newPriceEditText);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        final Spinner spinner = view.findViewById(R.id.spinner_cats);
        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.wishlist_category, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        final ImageButton wishlistcheckButton = view.findViewById(R.id.newWishlistCheckButton);
        ImageButton wishlistcrossButton = view.findViewById(R.id.newWishlistCrossButton);

        wishlistcheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wishlistEditText.getText().toString().trim().length() > 0 && priceEditText.getText().toString().trim().length() > 0) {

                    String name = wishlistEditText.getText().toString();
                    Float price = Float.parseFloat(priceEditText.getText().toString());
                    String spinnerValue = spinner.getSelectedItem().toString();

                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    long result = dbHelper.createWishList(name, price, spinnerValue);
                    mainAdapter.swapCursor(getAllItems());

                    wishlistEditText.getText().clear();
                    priceEditText.getText().clear();

                    if (result == -1) {
                        Toast.makeText(getContext(), "Failed storing new item.", Toast.LENGTH_SHORT).show();
                    }

                    Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);

                    if (cursor.moveToFirst()) {
                        total = cursor.getFloat(cursor.getColumnIndex("Total"));
                    }
                    updateSavingLayout();
                    createPieChart();

                    alertDialog.cancel();
                } else {
                    Toast.makeText(getContext(), "Cannot save empty value.", Toast.LENGTH_SHORT).show();
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
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.budget_input_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        budgetEditText = view.findViewById(R.id.newBudgetEditText);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        final ImageButton checkButton = view.findViewById(R.id.newBudgetCheckButton);
        ImageButton crossButton = view.findViewById(R.id.newBudgetCrossButton);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (budgetEditText.getText().toString().length() > 0) {
                    newBudget = Float.parseFloat(budgetEditText.getText().toString());
                    //store new budget into database
                    DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                    long result = dbHelper.createBudget(newBudget);
                    if (result == -1) {
                        Toast.makeText(getContext(), "Nope", Toast.LENGTH_SHORT).show();
                    }

                    String selectQuery = "SELECT * FROM " + DatabaseHelper.BUDGET_TABLE;
                    Cursor cursor = mDatabase.rawQuery(selectQuery, null);
                    cursor.moveToLast();
                    float theData = cursor.getFloat(cursor.getColumnIndex("budget"));

                    budgetValue.setText(String.valueOf(theData));
                    updateSavingLayout();
                    alertDialog.cancel();

                } else {
                    Toast.makeText(getContext(), "Value cannot be empty", Toast.LENGTH_SHORT).show();
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
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        newBudget = 0;
        long result = dbHelper.createBudget(newBudget);
        if (result == -1) {
            Toast.makeText(getContext(), "Nope", Toast.LENGTH_SHORT).show();
        }

        String selectQuery = "SELECT * FROM " + DatabaseHelper.BUDGET_TABLE;
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        cursor.moveToLast();
        int theData = cursor.getInt(cursor.getColumnIndex("budget"));

        budgetValue.setText(String.valueOf(theData));
        savingValue.setText(String.valueOf(newBudget - total));
        if (newBudget - total > 0) {
            savingLayout.setBackgroundResource(R.color.green);
        } else if (newBudget - total < 0) {
            savingLayout.setBackgroundResource(R.color.red);
        }
    }

    private void startQuestion() {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.question_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView newRecyclerView;
        newRecyclerView = view.findViewById(R.id.newrecyclerview);
        newRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainAdapter2 = new MainAdapter(getContext(), getAllItems());
        newRecyclerView.setAdapter(mainAdapter2);


        mainAdapter2.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), "Swipe to move item into Maybe List", Toast.LENGTH_SHORT).show();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                mainAdapter2.swapCursor(getAllItems());
                addtoLater((long) viewHolder.itemView.getTag());
                removeWishItem((long) viewHolder.itemView.getTag());

                laterAdapter = new LaterAdapter(getContext(), getLaterItems());
                laterRecyclerView.setAdapter(laterAdapter);
                updateSavingLayout();
            }
        }).attachToRecyclerView(newRecyclerView);

        alertDialog.setView(view);
        alertDialog.show();

        final TextView questions = alertDialog.findViewById(R.id.questionsTextView);
        randomIndex = new Random().nextInt(theQuestionString.length);
        q = theQuestionString[randomIndex];
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
        String[] coloumns = new String[]{DatabaseHelper._ID, DatabaseHelper.WISHLIST_NAME, DatabaseHelper.WISHLIST_AMOUNT, DatabaseHelper.WISHLIST_CATEGORY, DatabaseHelper.WISHLIST_TIMESTAMP};
        String name;
        Float price;
        String category;
        Cursor c = mDatabase.query(DatabaseHelper.WISHLIST_TABLE, coloumns, DatabaseHelper._ID + "=" + id, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
            name = c.getString(1);
            price = c.getFloat(2);
            category = c.getString(3);
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            long result = dbHelper.createLater(name, price, category);
            if (result == -1) {
                Toast.makeText(getContext(), "Failed storing new item.", Toast.LENGTH_SHORT).show();
            }
        }

        updateSavingLayout();
    }

    private void moveToWishlist(long id) {
        String[] coloumns = new String[]{DatabaseHelper._ID, DatabaseHelper.LATER_NAME, DatabaseHelper.LATER_AMOUNT, DatabaseHelper.LATER_CATEGORY, DatabaseHelper.LATER_TIMESTAMP};
        String name;
        Float price;
        String category;
        Cursor c = mDatabase.query(DatabaseHelper.LATER_TABLE, coloumns, DatabaseHelper._ID + "=" + id, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
            name = c.getString(1);
            price = c.getFloat(2);
            category = c.getString(3);
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            long result = dbHelper.createWishList(name, price, category);
            if (result == -1) {
                Toast.makeText(getContext(), "Failed storing new item.", Toast.LENGTH_SHORT).show();
            }
        }

        Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);
        if (cursor.moveToFirst()) {
            total = cursor.getFloat(cursor.getColumnIndex("Total"));
        }
        updateSavingLayout();
        createPieChart();
    }

    private void createPieChart() {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDrawSliceText(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHoleRadius(40);

        pieChart.setDragDecelerationFrictionCoef(0.75f);

        pieChart.setDrawHoleEnabled(true);
        //pieChart.setHoleColor(getColor(R.color.white));
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setNoDataText("At least one data are required");

        pieChart.animateY(1000, Easing.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(getDataValue(), "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(15f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1OffsetPercentage(100f); /** When valuePosition is OutsideSlice, indicates offset as percentage out of the slice size */
        dataSet.setValueLinePart1Length(0.5f); /** When valuePosition is OutsideSlice, indicates length of first half of the line */
        dataSet.setValueLinePart2Length(0.5f); /** When valuePosition is OutsideSlice, indicates length of second half of the line */

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLUE);

        pieChart.setData(data);
    }

    private ArrayList<PieEntry> getDataValue() {
        dataValue.clear();

        String[] columns = {"amount", "category"};
        //Cursor cursor = mDatabase.query(DatabaseHelper.WISHLIST_TABLE, columns, null, null, null, null, null);

        Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT
                + ") as categoryTotal,"
                + DatabaseHelper.WISHLIST_CATEGORY + " as theCategory FROM "
                + DatabaseHelper.WISHLIST_TABLE
                + " GROUP BY " + DatabaseHelper.WISHLIST_CATEGORY
                + "", null);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dataValue.add(new PieEntry(cursor.getFloat(cursor.getColumnIndex("categoryTotal")), cursor.getString(cursor.getColumnIndex("theCategory"))));
        }

        Cursor cursor2 = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.WISHLIST_AMOUNT + ") as Total FROM " + DatabaseHelper.WISHLIST_TABLE, null);
        if (cursor2.moveToFirst()) {
            total = cursor2.getFloat(cursor2.getColumnIndex("Total"));
        }
        if (total == 0) {
            pieChart.setNoDataText("No Data");
            pieChart.setBackgroundResource(R.drawable.ic_launcher_foreground);
        } else
            pieChart.setBackgroundResource(R.color.white);
        return dataValue;
    }
}