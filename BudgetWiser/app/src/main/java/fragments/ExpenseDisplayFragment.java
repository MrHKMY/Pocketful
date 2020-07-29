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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.clans.fab.FloatingActionButton;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mindscape.budgetwiser.DatabaseHelper;
import com.mindscape.budgetwiser.R;

import java.util.ArrayList;

/**
 * Created by Hakimi on 2/7/2020.
 */
public class ExpenseDisplayFragment extends Fragment {

    PieChart pieChart;
    DatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    public ArrayList<PieEntry> dataValue = new ArrayList<>();
    private EditText expenseEditText, noteEditText;
    private ImageButton checkButton, crossButton, infoButton;
    private float newExpense;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private String noteExpense, spinnerValue;
    private TextView titleTextView, value1TV, value2TV, value3TV, value4TV, value5TV, value6TV, value7TV, value8TV, value9TV, value10TV, value11TV, value12TV, emptyText;
    float total;
    private FloatingActionButton minusButton, plusButton;
    private ImageView emptyTree;
    private int newSpinnerValue, x;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_expenses, container, false);

        pieChart = view.findViewById(R.id.expensesPieChart);
        //pieChart.setNoDataText("NO DATAAA");

        minusButton = view.findViewById(R.id.minusExpenseButton);
        plusButton = view.findViewById(R.id.plusExpenseButton);
        infoButton = view.findViewById(R.id.infoButton);
        value1TV = view.findViewById(R.id.value1);
        value2TV = view.findViewById(R.id.value2);
        value3TV = view.findViewById(R.id.value3);
        value4TV = view.findViewById(R.id.value4);
        value5TV = view.findViewById(R.id.value5);
        value6TV = view.findViewById(R.id.value6);
        value7TV = view.findViewById(R.id.value7);
        value8TV = view.findViewById(R.id.value8);
        value9TV = view.findViewById(R.id.value9);
        value10TV = view.findViewById(R.id.value10);
        value11TV = view.findViewById(R.id.value11);
        value12TV = view.findViewById(R.id.value12);
        emptyTree = view.findViewById(R.id.emptyChart);
        emptyText = view.findViewById(R.id.emptyTextTV);

        dbHelper = new DatabaseHelper(getContext());
        //sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase = DatabaseHelper.getInstance(getContext()).getWritableDatabase();

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDialog("OUT", "Money OUT");
                for (int i = 1; i <= 12; i++) {
                    getCategoryValue(i);
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDialog("IN", "Money IN");
                for (int i = 1; i <= 12; i++) {
                    getCategoryValue(i);
                }
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog();
            }
        });

        createPieChart();
        for (int i = 1; i <= 12; i++) {
            getCategoryValue(i);
        }
        return view;
    }

    private void createPieChart() {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(0, 0, 0, 0);
        pieChart.setDragDecelerationFrictionCoef(0.75f);
        pieChart.setDrawHoleEnabled(false);
        //pieChart.setHoleColor(getColor(R.color.white));
        pieChart.setTransparentCircleRadius(60f);
        pieChart.animateY(1000, Easing.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(getDataValue(), "");
        pieChart.setNoDataText("No data laaa");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(15f);
        //dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setColors(new int[]{R.color.darkGreen, R.color.green, R.color.orange, R.color.yellow, R.color.blue, R.color.grey, R.color.pink, R.color.purple, R.color.brown, R.color.red, R.color.colorAccent, R.color.colorPrimaryDark}, getContext());

        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1OffsetPercentage(100f); /** When valuePosition is OutsideSlice, indicates offset as percentage out of the slice size */
        dataSet.setValueLinePart1Length(0.6f); /** When valuePosition is OutsideSlice, indicates length of first half of the line */
        dataSet.setValueLinePart2Length(0.6f); /** When valuePosition is OutsideSlice, indicates length of second half of the line */

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLUE);

        pieChart.setData(data);
    }

    private ArrayList<PieEntry> getDataValue() {

        dataValue.clear();

        /*for (int nums = 1 ; nums<=12; nums++) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.EXPENSE_VALUE
                    + ") as Total FROM " + DatabaseHelper.EXPENSE_TABLE
                    + " WHERE Category = '" + nums
                    + "' AND DATE(" + DatabaseHelper.EXPENSE_TIMESTAMP +") = DATE('now') AND Status = 'OUT'",null);
            if (cursor.moveToFirst()) {
                total = cursor.getInt(cursor.getColumnIndex("Total"));
            }
            dataValue.add(new PieEntry(total));
        }

         */

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.EXPENSE_VALUE
                + ") as Total,"
                + DatabaseHelper.EXPENSE_NAME + " as theCategory FROM "
                + DatabaseHelper.EXPENSE_TABLE
                + " WHERE DATE(" + DatabaseHelper.EXPENSE_TIMESTAMP + ") = DATE('now', 'localtime') AND Status = 'OUT'"
                + " GROUP BY " + DatabaseHelper.EXPENSE_CATEGORY
                + "", null);

        if (cursor.getCount() == 0){
            emptyTree.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                dataValue.add(new PieEntry(cursor.getFloat(cursor.getColumnIndex("Total")), cursor.getString(cursor.getColumnIndex("theCategory"))));
            }
            emptyTree.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
        }

        return dataValue;
    }

    private void expenseDialog(final String status, String title) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.expense_input_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        expenseEditText = view.findViewById(R.id.newExpenseEditText);

        expenseEditText.requestFocus();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        noteEditText = view.findViewById(R.id.newExpenseNoteEditText);
        checkButton = view.findViewById(R.id.newExpenseCheckButton);
        crossButton = view.findViewById(R.id.newExpenseCrossButton);
        titleTextView = view.findViewById(R.id.newExpenseTitle);
        spinner = view.findViewById(R.id.expense_spinner);

        titleTextView.setText(title);
        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.expense_category, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expenseEditText.getText().toString().length() > 0) {
                    if (noteEditText.getText().toString().length() > 0) {
                        newExpense = Float.parseFloat(expenseEditText.getText().toString());
                        noteExpense = noteEditText.getText().toString();
                        spinnerValue = spinner.getSelectedItem().toString();
                        switchSpinnerValue(spinnerValue);
                        //store new data into database
                        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                        long result = dbHelper.createExpense(newExpense, newSpinnerValue, spinnerValue, noteExpense, status);
                        if (result == -1) {
                            Toast.makeText(getContext(), "Nope", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        newExpense = Float.parseFloat(expenseEditText.getText().toString());
                        noteExpense = "No detail provided";
                        spinnerValue = spinner.getSelectedItem().toString();
                        switchSpinnerValue(spinnerValue);
                        //store new budget into database
                        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                        long result = dbHelper.createExpense(newExpense, newSpinnerValue, spinnerValue, noteExpense, status);
                        if (result == -1) {
                            Toast.makeText(getContext(), "Nope", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Toast.makeText(getContext(), "Data saved.", Toast.LENGTH_SHORT).show();

                    /*switch (spinnerValue) {
                        case "Groceries":
                            a = 1;
                            getCategoryValue(1);
                            break;
                        case "Clothing":
                            a = 2;
                            getCategoryValue(2);
                            break;
                        case "Leisure":
                            a = 3;
                            getCategoryValue(3);
                            break;
                        case "Transport":
                            a = 4;
                            getCategoryValue(4);
                            break;
                        case "Food":
                            a = 5;
                            getCategoryValue(5);
                            break;
                        case "Health":
                            a = 6;
                            getCategoryValue(6);
                            break;
                        case "Bills":
                            a = 7;
                            getCategoryValue(7);
                            break;
                        case "Family":
                            a = 8;
                            getCategoryValue(8);
                            break;
                        case "Electronics":
                            a = 9;
                            getCategoryValue(9);
                            break;
                        case "Sports":
                            a = 10;
                            getCategoryValue(10);
                            break;
                        case "Pet":
                            a = 11;
                            getCategoryValue(11);
                            break;
                        case "Others":
                            a = 12;
                            getCategoryValue(12);
                            break;
                    }

                     */

                    //Toast.makeText(getContext(), x, Toast.LENGTH_SHORT).show();
                    getCategoryValue(newSpinnerValue);


                    alertDialog.cancel();
                    createPieChart();

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

    private int switchSpinnerValue(String value) {

        switch (value) {
            case "Groceries":
                newSpinnerValue = 1;
                //getCategoryValue(1);
                break;
            case "Clothing":
                newSpinnerValue = 2;
                //getCategoryValue(2);
                break;
            case "Leisure":
                newSpinnerValue = 3;
                //getCategoryValue(3);
                break;
            case "Transport":
                newSpinnerValue = 4;
                //getCategoryValue(4);
                break;
            case "Food":
                newSpinnerValue = 5;
                //getCategoryValue(5);
                break;
            case "Self-care":
                newSpinnerValue = 6;
                //getCategoryValue(6);
                break;
            case "Bills":
                newSpinnerValue = 7;
                //getCategoryValue(7);
                break;
            case "Family":
                newSpinnerValue = 8;
                //getCategoryValue(8);
                break;
            case "Electronics":
                newSpinnerValue = 9;
                //getCategoryValue(9);
                break;
            case "Sports":
                newSpinnerValue = 10;
                //getCategoryValue(10);
                break;
            case "Pet":
                newSpinnerValue = 11;
                //getCategoryValue(11);
                break;
            case "Others":
                newSpinnerValue = 12;
                //getCategoryValue(12);
                break;
        }

    return newSpinnerValue;
    }

    private void getCategoryValue(int num) {

        String nums = String.valueOf(num);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.EXPENSE_VALUE
                + ") as Total FROM " + DatabaseHelper.EXPENSE_TABLE
                + " WHERE Category = '" + nums
                + "' AND DATE(" + DatabaseHelper.EXPENSE_TIMESTAMP + ") = DATE('now', 'localtime') AND Status = 'OUT'", null);

        if (cursor.moveToFirst()) {
            total = cursor.getFloat(cursor.getColumnIndex("Total"));
        }

        if (num == 1) {
            value1TV.setText(String.valueOf(total));
        } else if (num == 2)
            value2TV.setText(String.valueOf(total));
        else if (num == 3)
            value3TV.setText(String.valueOf(total));
        else if (num == 4)
            value4TV.setText(String.valueOf(total));
        else if (num == 5)
            value5TV.setText(String.valueOf(total));
        else if (num == 6)
            value6TV.setText(String.valueOf(total));
        else if (num == 7)
            value7TV.setText(String.valueOf(total));
        else if (num == 8)
            value8TV.setText(String.valueOf(total));
        else if (num == 9)
            value9TV.setText(String.valueOf(total));
        else if (num == 10)
            value10TV.setText(String.valueOf(total));
        else if (num == 11)
            value11TV.setText(String.valueOf(total));
        else if (num == 12)
            value12TV.setText(String.valueOf(total));
    }

    private void infoDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.info_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton infoCheckButton = view.findViewById(R.id.infoCheckButton);
        infoCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        alertDialog.setView(view);
        alertDialog.show();
    }
}
