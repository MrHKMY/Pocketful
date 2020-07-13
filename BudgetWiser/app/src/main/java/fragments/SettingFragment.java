package fragments;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.mindscape.budgetwiser.R;

/**
 * Created by Hakimi on 25/6/2020.
 */
public class SettingFragment extends Fragment {

    SwitchCompat switchMode;
    RelativeLayout mainLayout;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container,false);

        switchMode = view.findViewById(R.id.switchDarkMode);
        mainLayout = view.findViewById(R.id.mainRelativeSetting);
        textView = view.findViewById(R.id.settingFragment);

        switchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchMode.isChecked()){
                    mainLayout.setBackgroundColor(getResources().getColor(R.color.black));
                    switchMode.setTextColor(getResources().getColor(R.color.white));
                    textView.setTextColor(getResources().getColor(R.color.white));
                } else {
                    mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    switchMode.setTextColor(getResources().getColor(R.color.black));
                    textView.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        return view;
    }
}
