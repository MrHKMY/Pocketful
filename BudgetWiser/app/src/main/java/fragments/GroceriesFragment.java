package fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.github.clans.fab.FloatingActionButton;
import com.mindscape.pocketful.DatabaseHelper;
import com.mindscape.pocketful.R;

import java.util.List;

import adapters.NoteAdapter;

/**
 * Created by Hakimi on 6/8/2020.
 */
public class GroceriesFragment extends Fragment {

    private SQLiteDatabase mDatabase;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    NoteAdapter adapter;
    private EditText noteTitleEditText, noteContentEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groceries, container, false);

        mDatabase = DatabaseHelper.getInstance(getContext()).getWritableDatabase();

        fab = view.findViewById(R.id.newNoteFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewNote();
            }
        });
        recyclerView = view.findViewById(R.id.noteRecyclerview);
        recyclerView.setHasFixedSize(true);

        int column = 2;
        if (getResources().getConfiguration().orientation == 2) {
            column = 3;
        }

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(column, 1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        final float scale = getResources().getDisplayMetrics().density;
        int spacing = (int) (1 * scale + 0.5f);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spacing));

        adapter = new NoteAdapter(getContext(), getAllNote());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private Cursor getAllNote() {
        return mDatabase.query(
                DatabaseHelper.NOTE_TABLE,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.NOTE_TIMESTAMP + " DESC"
        );
    }

    public void addNewNote() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.note_input, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        noteTitleEditText = view.findViewById(R.id.noteTitleET);
        noteContentEditText = view.findViewById(R.id.noteContentET);
        noteContentEditText.requestFocus();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        final ImageButton noteImageButton = view.findViewById(R.id.newNoteCheckButton);
        noteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noteContentEditText.getText().toString().trim().length() > 0) {
                    String content = noteContentEditText.getText().toString();
                    String title = noteTitleEditText.getText().toString();

                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    long result = dbHelper.createNote(title, content);
                    adapter.swapCursor(getAllNote());

                    noteContentEditText.getText().clear();
                    noteTitleEditText.getText().clear();

                    if (result == -1) {
                        Toast.makeText(getContext(), "Failed storing new note.", Toast.LENGTH_SHORT).show();
                    }

                    alertDialog.cancel();
                } else {
                    Toast.makeText(getContext(), "Cannot save empty note.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setView(view);
        alertDialog.show();
    }
}

