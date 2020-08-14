package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mindscape.pocketful.BuildConfig;
import com.mindscape.pocketful.R;

/**
 * Created by MrHKMY on 14/8/2020.
 */
public class AboutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container,false);

        TextView appVersion = view.findViewById(R.id.versionTextView);
        appVersion.setText("Version: " + BuildConfig.VERSION_NAME);

        return view;
    }
}
