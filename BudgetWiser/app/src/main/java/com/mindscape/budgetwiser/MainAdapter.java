package com.mindscape.budgetwiser;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Hakimi on 16/5/2020.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    public MainAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.row_content,parent,false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String item = mCursor.getString(mCursor.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_NAME));
        String price = mCursor.getString(mCursor.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_AMOUNT));

        holder.itemText.setText(item);
        holder.priceText.setText(price);

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public TextView itemText;
        public TextView priceText;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            itemText = itemView.findViewById(R.id.itemAdapter);
            priceText = itemView.findViewById(R.id.priceAdapter);
        }
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }


}
