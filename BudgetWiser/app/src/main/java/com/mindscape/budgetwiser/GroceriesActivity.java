package com.mindscape.budgetwiser;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by Hakimi on 17/6/2020.
 */
public class GroceriesActivity extends AppCompatActivity {

    PieChart pieChart;
    DatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    public ArrayList<PieEntry> dataValue = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_chart);

        pieChart = findViewById(R.id.pieChartID);
        dbHelper = new DatabaseHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();

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

        /*ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(30f, "Item1"));
        yValues.add(new PieEntry(20f, "Item2"));
        yValues.add(new PieEntry(15f, "Item3"));
        yValues.add(new PieEntry(18f, "Item4"));
        yValues.add(new PieEntry(17f, "Item5"));

         */

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

        String[] columns = {"amount"};
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.WISHLIST_TABLE, columns, null,null,null,null,null);

        for (int i = 0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            dataValue.add(new PieEntry(cursor.getFloat(0)));
        }
        return dataValue;
    }

}
