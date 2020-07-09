package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mindscape.budgetwiser.R;

import adapters.PageAdapter;

/**
 * Created by Hakimi on 25/6/2020.
 */
public class ExpensesFragment extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;
    View mIndicator;
    private int indicatorWidth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container,false);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                indicatorWidth = tabLayout.getWidth() / tabLayout.getTabCount();

                //FrameLayout.LayoutParams indicatorParams = mIndicator.getLayoutParams();
                //indicatorParams.width = indicatorWidth;
                //mIndicator.setLayoutParams(indicatorParams);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        PageAdapter adapter = new PageAdapter(getChildFragmentManager());

        adapter.addFragment(new ExpenseDisplayFragment(), "Daily Display");
        adapter.addFragment(new ExpenseHistoryFragment(), "Monthly History");

        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}