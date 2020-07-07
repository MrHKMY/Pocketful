package fragments;

import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mindscape.budgetwiser.DatabaseHelper;
import com.mindscape.budgetwiser.R;

import java.util.ArrayList;

import adapters.ExpenseHistoryAdapter;
import adapters.MainAdapter;

/**
 * Created by Hakimi on 2/7/2020.
 */
public class ExpenseHistoryFragment extends Fragment {

    private SQLiteDatabase mDatabase;
    LineChart lineChart;
    private RecyclerView recyclerView;
    private ExpenseHistoryAdapter expenseHistoryAdapter;
    private ImageView statusImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_expense, container, false);

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        mDatabase = dbHelper.getWritableDatabase();

        statusImageView = view.findViewById(R.id.transactionStatusImageView);
        lineChart = view.findViewById(R.id.historyLineChart);
        recyclerView = view.findViewById(R.id.recyclerviewExpense);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        expenseHistoryAdapter = new ExpenseHistoryAdapter(getContext(), getAllItems());
        recyclerView.setAdapter(expenseHistoryAdapter);

        expenseHistoryAdapter.notifyDataSetChanged();
        expenseHistoryAdapter.swapCursor(getAllItems());

        createLineChart();
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        expenseHistoryAdapter.swapCursor(getAllItems());
        createLineChart();
    }

    private Cursor getAllItems() {
        return mDatabase.query(
                DatabaseHelper.EXPENSE_TABLE,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.EXPENSE_TIMESTAMP + " DESC"
        );
    }

    private void createLineChart() {

        LineDataSet lineDataSet1 = new LineDataSet(getDataValueIN(), "IN");
        LineDataSet lineDataSet2 = new LineDataSet(getDataValueOUT(), "OUT");

        lineDataSet1.setColor(getResources().getColor(R.color.green));
        lineDataSet1.setCircleColor(getResources().getColor(R.color.green));
        lineDataSet1.setDrawCircleHole(false);
        lineDataSet1.setLineWidth(2);

        lineDataSet2.setColor(getResources().getColor(R.color.red));
        lineDataSet2.setCircleColor(getResources().getColor(R.color.red));
        lineDataSet2.setDrawCircleHole(false);
        lineDataSet2.setLineWidth(2);

        lineChart.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
        lineChart.setDrawBorders(true);
        lineChart.setDescription(null);
        lineChart.setNoDataText("No Data");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
    }

    private ArrayList<Entry> getDataValueIN() {
        ArrayList<Entry> dataValues = new ArrayList<Entry>();
        dataValues.clear();

        String where = "IN";
        Cursor cursor = mDatabase.rawQuery("SELECT Value FROM " + DatabaseHelper.EXPENSE_TABLE + " where Status = '" + where + "'", null);
        //String[] columns = {DatabaseHelper.EXPENSE_VALUE};
        //Cursor cursor = mDatabase.query(DatabaseHelper.EXPENSE_TABLE, columns, where,null,null,null,null);

        for (int i = 0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            dataValues.add(new Entry(i+1,cursor.getFloat(0)));
        }
        return dataValues;
    }

    private ArrayList<Entry> getDataValueOUT() {
        ArrayList<Entry> dataValues = new ArrayList<Entry>();
        dataValues.clear();

        String where = "OUT";
        Cursor cursor = mDatabase.rawQuery("SELECT Value FROM " + DatabaseHelper.EXPENSE_TABLE + " where Status = '" + where + "'", null);
        //String[] columns = {DatabaseHelper.EXPENSE_VALUE};
        //Cursor cursor = mDatabase.query(DatabaseHelper.EXPENSE_TABLE, columns, null,null,null,null,null);

        for (int i = 0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            dataValues.add(new Entry(i+1,cursor.getFloat(0)));
        }
        return dataValues;
    }

}
