package fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mindscape.budgetwiser.DatabaseHelper;
import com.mindscape.budgetwiser.R;

import java.util.ArrayList;

/**
 * Created by MrHKMY on 11/7/2020.
 */
public class LineChartFragment extends Fragment {

    private SQLiteDatabase mDatabase;
    LineChart lineChart;
    int total;
    int dailyTotal, theDay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.line_chart_fragment, container, false);

        //DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        mDatabase = DatabaseHelper.getInstance(getContext()).getWritableDatabase();

        lineChart = view.findViewById(R.id.lineChart);

        createLineChart();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        createLineChart();
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

        lineChart.setBackgroundColor(getResources().getColor(R.color.white));
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
        /*Cursor cursor = mDatabase.rawQuery("SELECT Value FROM " + DatabaseHelper.EXPENSE_TABLE + " where Status = '" + where + "'", null);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dataValues.add(new Entry(i + 1, cursor.getFloat(0)));
        }
         */

        Cursor cursor2 = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.EXPENSE_VALUE
                + ") as DailyTotal, substr(" + DatabaseHelper.EXPENSE_TIMESTAMP + ",9,2) as TheDay FROM " + DatabaseHelper.EXPENSE_TABLE
                + " WHERE Status = '" + where
                + "' AND strftime('%m'," + DatabaseHelper.EXPENSE_TIMESTAMP
                + ") = strftime('%m',date('now')) GROUP BY substr(" + DatabaseHelper.EXPENSE_TIMESTAMP + ",9,2)", null);

        if (cursor2.moveToFirst()) {
            do {
                Entry entry = new Entry();
                entry.setY(cursor2.getInt(cursor2.getColumnIndex("DailyTotal")));
                entry.setX(cursor2.getInt(cursor2.getColumnIndex("TheDay")));
                dataValues.add(entry);
            } while (cursor2.moveToNext());
        }
        return dataValues;
    }

    private ArrayList<Entry> getDataValueOUT() {
        ArrayList<Entry> dataValues = new ArrayList<Entry>();
        dataValues.clear();

        String where = "OUT";
        /*Cursor cursor = mDatabase.rawQuery("SELECT Value FROM " + DatabaseHelper.EXPENSE_TABLE + " where Status = '" + where + "'", null);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            dataValues.add(new Entry(i + 1, cursor.getFloat(0)));
        }

        Cursor cursor = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.EXPENSE_VALUE
                + ") as Total FROM " + DatabaseHelper.EXPENSE_TABLE
                + " WHERE Status = '" + where
                + "' AND DATE(" + DatabaseHelper.EXPENSE_TIMESTAMP +") = DATE('now')",null);


        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("Total"));
        }
        dataValues.add(new Entry(theDay, dailyTotal));
        */

        Cursor cursor2 = mDatabase.rawQuery("SELECT SUM(" + DatabaseHelper.EXPENSE_VALUE
                + ") as DailyTotal, substr(" + DatabaseHelper.EXPENSE_TIMESTAMP + ",9,2) as TheDay FROM " + DatabaseHelper.EXPENSE_TABLE
                + " WHERE Status = '" + where
                + "' AND strftime('%m'," + DatabaseHelper.EXPENSE_TIMESTAMP
                + ") = strftime('%m',date('now')) GROUP BY substr(" + DatabaseHelper.EXPENSE_TIMESTAMP + ",9,2)", null);

        if (cursor2.moveToFirst()) {
            do {
                Entry entry = new Entry();
                entry.setY(cursor2.getInt(cursor2.getColumnIndex("DailyTotal")));
                entry.setX(cursor2.getInt(cursor2.getColumnIndex("TheDay")));
                dataValues.add(entry);
            } while (cursor2.moveToNext());
        }
        return dataValues;
    }
}
