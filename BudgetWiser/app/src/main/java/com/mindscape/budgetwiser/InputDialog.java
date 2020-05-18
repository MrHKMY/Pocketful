package com.mindscape.budgetwiser;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Created by Hakimi on 17/5/2020.
 */
public class InputDialog extends AppCompatDialogFragment {
    public EditText itemEditText, priceEditText;
    private SQLiteDatabase mDatabase;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        mDatabase = dbHelper.getWritableDatabase();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);

        builder.setView(view)
                .setTitle("Input new item")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addItem();
                        Toast.makeText(getActivity(), "Item added.", Toast.LENGTH_SHORT).show();
                    }
                });

        itemEditText = view.findViewById(R.id.itemInputEditText);
        priceEditText = view.findViewById(R.id.priceInputEditText);
        return builder.create();
    }

    private void addItem () {
        if (itemEditText.getText().toString().trim().length() == 0 || priceEditText.getText().toString().trim().length() ==0 ){
            return;
        }

        String name = itemEditText.getText().toString();
        String price = priceEditText.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.DatabaseEntry.COLUMN_NAME, name);
        cv.put(DatabaseContract.DatabaseEntry.COLUMN_AMOUNT, price);

        mDatabase.insert(DatabaseContract.DatabaseEntry.TABLE_NAME, null, cv);
        itemEditText.getText().clear();
        priceEditText.getText().clear();

    }


}
