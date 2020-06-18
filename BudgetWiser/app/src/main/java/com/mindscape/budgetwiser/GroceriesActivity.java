package com.mindscape.budgetwiser;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Created by Hakimi on 17/6/2020.
 */
public class GroceriesActivity extends AppCompatActivity {

    private boolean isFabTapped = false;
    public BottomAppBar bar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_layout);

        bar = findViewById(R.id.bottomBar);
        FloatingActionButton fab = findViewById(R.id.fab);
        setSupportActionBar(bar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFabTapped = !isFabTapped;
                if (isFabTapped) {
                    bar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                } else {
                    bar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                }
            }
        });
    }
}
