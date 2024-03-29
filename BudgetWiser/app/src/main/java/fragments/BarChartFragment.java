package fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.mindscape.pocketful.DatabaseHelper;
import com.mindscape.pocketful.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrHKMY on 11/7/2020.
 */
public class BarChartFragment extends Fragment {

    private SQLiteDatabase mDatabase;
    BarChart barChart;
    float total;
    int data;

    public BarChartFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bar_chart_fragment, container, false);

        //DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        mDatabase = DatabaseHelper.getInstance(getContext()).getWritableDatabase();
        barChart = view.findViewById(R.id.barchart);
        createBarChart();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        createBarChart();
    }

    private void createBarChart() {

        BarDataSet set = new BarDataSet(getBarDataValueOUT(), "Categories");
        BarData data = new BarData(set);

        List<String> labels = new ArrayList<>();


        labels.add("1");
        labels.add("2");
        labels.add("3");
        labels.add("4");
        labels.add("5");
        labels.add("6");
        labels.add("7");
        labels.add("8");
        labels.add("9");
        labels.add("10");
        labels.add("11");
        labels.add("12");

        barChart.getAxisRight().setEnabled(true);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setLabelCount(12);
        barChart.getLegend().setEnabled(false);
        barChart.setFitBars(true);
        //set.setColors(ColorTemplate.JOYFUL_COLORS);
        set.setColors(new int[]{R.color.bar1, R.color.bar2, R.color.bar3, R.color.bar4, R.color.bar5, R.color.bar6, R.color.bar7, R.color.bar8, R.color.bar9, R.color.bar10, R.color.bar11, R.color.bar12}, getContext());
        set.setDrawValues(false);
        barChart.setFitBars(true);
        barChart.setBackgroundResource(R.color.white);
        barChart.setData(data);
        barChart.invalidate();
    }

    private List<BarEntry> getBarDataValueOUT() {

        List<BarEntry> dataValues = new ArrayList<>();
        dataValues.clear();

            for (int nums = 1; nums <= 12; nums++) {
                Cursor cursor2 = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.EXPENSE_VALUE
                        + ") as Total FROM " + DatabaseHelper.EXPENSE_TABLE
                        + " WHERE Category = '" + nums + "' AND Status = 'OUT' AND strftime('%m'," + DatabaseHelper.EXPENSE_TIMESTAMP
                        + ") = strftime('%m',date('now'))", null);
                if (cursor2.moveToFirst()) {
                    total = cursor2.getFloat(cursor2.getColumnIndex("Total"));
                }
                dataValues.add(new BarEntry(nums, total));
                cursor2.close();
            }

        return dataValues;
    }

    public void updateData(int x) {
        data = x;
        Log.d("THE VALUE OF DATA: ", "value: " + data);
    }
}
