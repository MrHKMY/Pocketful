package com.mindscape.budgetwiser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

/**
 * Created by Hakimi on 18/6/2020.
 */
public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {

    public BottomNavigationDrawerFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bottomsheet, container, false);
        NavigationView navigationView = view.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav1:
                        Toast.makeText(getActivity(), "Navigation: " + item, Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav2:
                        Toast.makeText(getActivity(), "Navigation now: " + item, Toast.LENGTH_SHORT).show();
                        return true;
                }
                return true;
            }
        });
        return view;
    }
}
