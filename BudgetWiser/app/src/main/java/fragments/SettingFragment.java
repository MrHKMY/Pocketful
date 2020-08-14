package fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.mindscape.pocketful.R;

/**
 * Created by Hakimi on 25/6/2020.
 */
public class SettingFragment extends Fragment {

    SwitchCompat switchMode;
    RelativeLayout mainLayout;
    TextView textView;
    SharedPreferences sharedPreferences;
    RelativeLayout rateLayout, aboutLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container,false);

        switchMode = view.findViewById(R.id.switchDarkMode);
        rateLayout = view.findViewById(R.id.rateAppLayout);
        aboutLayout = view.findViewById(R.id.aboutLayout);
        //mainLayout = view.findViewById(R.id.mainRelativeSetting);
        //textView = view.findViewById(R.id.settingFragment);
        final String appId = getContext().getPackageName();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        checkNightMode();

        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchMode.isChecked()){
                    //mainLayout.setBackgroundColor(getResources().getColor(R.color.black));
                    switchMode.setTextColor(getResources().getColor(R.color.white));
                    //textView.setTextColor(getResources().getColor(R.color.white));
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    saveNightModeState(true);
                } else {
                    //mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    switchMode.setTextColor(getResources().getColor(R.color.black));
                    //textView.setTextColor(getResources().getColor(R.color.black));
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    saveNightModeState(false);
                }
            }
        });

        rateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mindscape.pocketful"));

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            }
        });

        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AboutFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, fragment).addToBackStack(null).commit();
            }
        });

        return view;
    }

    public void saveNightModeState(boolean nightmode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("THEME_KEY", nightmode);
        editor.apply();
    }

    public void checkNightMode(){
        if(sharedPreferences.getBoolean("THEME_KEY", false)){
            switchMode.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            switchMode.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
