package fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.Label;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.mindscape.budgetwiser.DatabaseHelper;
import com.mindscape.budgetwiser.R;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import adapters.ExpenseHistoryAdapter;
import adapters.MainAdapter;
import adapters.PageAdapter;

/**
 * Created by Hakimi on 2/7/2020.
 */
public class ExpenseHistoryFragment extends Fragment {

    private FragmentAListener listener;

    public interface FragmentAListener {
        void onInputASent(int input);
    }

    private LinearLayout chartsLayout;
    private SQLiteDatabase mDatabase;
    LineChart lineChart;
    BarChart barChart;
    private RecyclerView recyclerView;
    private ExpenseHistoryAdapter expenseHistoryAdapter;
    private ImageView statusImageView, emptyImage;
    int total;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageButton previousButton, currentButton;
    private TextView emptyTextTV;
    public int x = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_expense, container, false);

        //DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        mDatabase = DatabaseHelper.getInstance(getContext()).getWritableDatabase();

        viewPager = view.findViewById(R.id.chartViewPager);
        tabLayout = view.findViewById(R.id.chartTabLayout);
        tabLayout.setupWithViewPager(viewPager, true);

        previousButton = view.findViewById(R.id.prevMonthButton);
        currentButton = view.findViewById(R.id.monthSelectorID);
        emptyImage = view.findViewById(R.id.emptyImageView);
        emptyTextTV = view.findViewById(R.id.emptyStatementTextView);

        statusImageView = view.findViewById(R.id.transactionStatusImageView);
        //barChart = view.findViewById(R.id.historyBarChart);
        chartsLayout = view.findViewById(R.id.layoutForCharts);
        recyclerView = view.findViewById(R.id.recyclerviewExpense);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        expenseHistoryAdapter = new ExpenseHistoryAdapter(getContext(), getCurrentMonth());
        recyclerView.setAdapter(expenseHistoryAdapter);

        expenseHistoryAdapter.notifyDataSetChanged();
        expenseHistoryAdapter.swapCursor(getCurrentMonth());

        currentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseHistoryAdapter = new ExpenseHistoryAdapter(getContext(), getCurrentMonth());
                recyclerView.setAdapter(expenseHistoryAdapter);
                x = 1;
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPreviousMonthData(x);


            }
        });

        //createBarChart();
        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
        expenseHistoryAdapter.swapCursor(getCurrentMonth());

        //createBarChart();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof  FragmentAListener) {
            listener = (FragmentAListener) context;
        } else {
            throw new RuntimeException(context.toString()
            + " must implement FragmentAListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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

    private Cursor getCurrentMonth() {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.EXPENSE_TABLE
                + " WHERE strftime('%m'," + DatabaseHelper.EXPENSE_TIMESTAMP
                + ") = strftime('%m',date('now','localtime')) ORDER BY "+ DatabaseHelper.EXPENSE_TIMESTAMP + " DESC", null);

        chartsLayout.setVisibility(View.VISIBLE);

        if (cursor.getCount() == 0){
            emptyImage.setVisibility(View.VISIBLE);
            emptyTextTV.setText("Oops! No data recorded yet \n Swipe left to add some new data");
            emptyTextTV.setVisibility(View.VISIBLE);
        } else {
            emptyImage.setVisibility(View.GONE);
            emptyTextTV.setVisibility(View.GONE);
        }
        return cursor;
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

    /*private void createBarChart() {

        BarDataSet set = new BarDataSet(getBarDataValueOUT(), "Categories");
        BarData data = new BarData(set);

        List<String> labels = new ArrayList<>();

        labels.add("day1");
        labels.add("day2");
        labels.add("day3");
        labels.add("day4");
        labels.add("day5");
        labels.add("day6");

        barChart.getAxisRight().setEnabled(true);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setLabelCount(12);
        barChart.getLegend().setEnabled(false);

        //set.setColors(ColorTemplate.JOYFUL_COLORS);
        set.setColors(new int[] {R.color.black, R.color.green, R.color.orange, R.color.yellow, R.color.blue, R.color.grey, R.color.pink, R.color.purple, R.color.brown, R.color.red, R.color.colorAccent, R.color.colorPrimary}, getContext());
        set.setDrawValues(false);
        barChart.setFitBars(true);
        barChart.setBackgroundResource(R.color.colorAccent2);
        barChart.setData(data);
        barChart.invalidate();
    }

     */

    private List<BarEntry> getBarDataValueOUT() {

        List<BarEntry> dataValues = new ArrayList<>();
        dataValues.clear();

        for (int nums = 1 ; nums<=12; nums++) {
            Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.EXPENSE_VALUE + ") as Total FROM " + DatabaseHelper.EXPENSE_TABLE + " WHERE Category = '" + nums + "'", null);
            if (cursor.moveToFirst()) {
                total = cursor.getInt(cursor.getColumnIndex("Total"));
            }
            dataValues.add(new BarEntry(nums, total));
        }
        return dataValues;
    }

    private ArrayList<Entry> getDataValueIN() {
        ArrayList<Entry> dataValues = new ArrayList<Entry>();
        dataValues.clear();

        String where = "IN";
        Cursor cursor = mDatabase.rawQuery("SELECT Value FROM " + DatabaseHelper.EXPENSE_TABLE + " where Status = '" + where + "'", null);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dataValues.add(new Entry(i + 1, cursor.getFloat(0)));
        }
        return dataValues;
    }

    private ArrayList<Entry> getDataValueOUT() {
        ArrayList<Entry> dataValues = new ArrayList<Entry>();
        dataValues.clear();

        String where = "OUT";
        Cursor cursor = mDatabase.rawQuery("SELECT Value FROM " + DatabaseHelper.EXPENSE_TABLE + " where Status = '" + where + "'", null);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dataValues.add(new Entry(i + 1, cursor.getFloat(0)));
        }
        return dataValues;
    }

    private void setupViewPager(ViewPager viewPager) {
        PageAdapter adapter = new PageAdapter(getChildFragmentManager());

        adapter.addFragment(new BarChartFragment(),"");
        adapter.addFragment(new LineChartFragment(), "");

        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViewPager(viewPager);
    }

    public void loadPreviousMonthData(int i) {

        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.EXPENSE_TABLE
                + " WHERE strftime('%m'," + DatabaseHelper.EXPENSE_TIMESTAMP
                + ") = strftime('%m',date('now', '-" + i +" month')) ORDER BY "+ DatabaseHelper.EXPENSE_TIMESTAMP + " DESC", null);

        //listener.onInputASent(i);
        chartsLayout.setVisibility(View.GONE);

        x++;

        expenseHistoryAdapter = new ExpenseHistoryAdapter(getContext(), cursor);
        if (cursor.getCount() == 0){
            emptyImage.setVisibility(View.VISIBLE);
            emptyTextTV.setText("No data recorded in the last months");
            emptyTextTV.setVisibility(View.VISIBLE);
        } else {
            emptyImage.setVisibility(View.GONE);
            emptyTextTV.setVisibility(View.GONE);
        }

        recyclerView.setAdapter(expenseHistoryAdapter);
    }
}
