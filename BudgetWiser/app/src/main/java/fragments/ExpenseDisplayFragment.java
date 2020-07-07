package fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mindscape.budgetwiser.DatabaseHelper;
import com.mindscape.budgetwiser.R;

import java.util.ArrayList;

import adapters.ExpenseDisplayAdapter;
import adapters.ExpenseHistoryAdapter;
import adapters.MainAdapter;

/**
 * Created by Hakimi on 2/7/2020.
 */
public class ExpenseDisplayFragment extends Fragment {

    private SQLiteDatabase mDatabase;
    PieChart pieChart;
    DatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    public ArrayList<PieEntry> dataValue = new ArrayList<>();
    private Button minusButton, plusButton;
    private EditText expenseEditText, noteEditText;
    private ImageButton checkButton, crossButton;
    private int newExpense;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private String noteExpense, spinnerValue;
    private TextView titleTextView;
    private RecyclerView recyclerView;
    private ExpenseDisplayAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_expenses, container,false);

        pieChart = view.findViewById(R.id.expensesPieChart);
        minusButton = view.findViewById(R.id.minusExpenseButton);
        plusButton = view.findViewById(R.id.plusExpenseButton);
        recyclerView = view.findViewById(R.id.displayRecyclerView);

        adapter = new ExpenseDisplayAdapter(getContext(), getAllItems());
        recyclerView.setAdapter(adapter);

        dbHelper = new DatabaseHelper(getContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();

        createPieChart();

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDialog("OUT", "Money OUT");
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDialog("IN", "Money IN");
            }
        });

        return view;
    }

    private Cursor getAllItems() {
        return null;
    }

    private void createPieChart() {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.75f);

        pieChart.setDrawHoleEnabled(false);
        //pieChart.setHoleColor(getColor(R.color.white));
        pieChart.setTransparentCircleRadius(60f);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(30f, "Item1"));
        yValues.add(new PieEntry(20f, "Item2"));
        yValues.add(new PieEntry(15f, "Item3"));
        yValues.add(new PieEntry(18f, "Item4"));
        yValues.add(new PieEntry(17f, "Item5"));

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

        String[] columns = {DatabaseHelper.EXPENSE_VALUE};
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.EXPENSE_TABLE, columns, null,null,null,null,null);

        for (int i = 0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            dataValue.add(new PieEntry(cursor.getFloat(0)));
        }
        return dataValue;
    }

    private void expenseDialog(final String status, String title) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.expense_input_doalog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        expenseEditText = view.findViewById(R.id.newExpenseEditText);
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
                        newExpense = Integer.parseInt(expenseEditText.getText().toString());
                        noteExpense = noteEditText.getText().toString();
                        spinnerValue = spinner.getSelectedItem().toString();
                        //store new data into database
                        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                        long result = dbHelper.createExpense(newExpense, spinnerValue, noteExpense, status);
                        if (result == -1) {
                            Toast.makeText(getContext(), "Nope", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        newExpense = Integer.parseInt(expenseEditText.getText().toString());
                        noteExpense = "No detail provided";
                        spinnerValue = spinner.getSelectedItem().toString();
                        //store new budget into database
                        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                        long result = dbHelper.createExpense(newExpense, spinnerValue, noteExpense,status);
                        if (result == -1) {
                            Toast.makeText(getContext(), "Nope", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Toast.makeText(getContext(), "Data saved.", Toast.LENGTH_SHORT).show();
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
}
