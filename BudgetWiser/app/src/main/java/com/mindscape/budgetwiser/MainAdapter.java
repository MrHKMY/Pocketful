package com.mindscape.budgetwiser;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by Hakimi on 16/5/2020.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnItemClickListener  mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public MainAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.row_content,parent,false);
        return new MainViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String item = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.WISHLIST_NAME));
        String price = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.WISHLIST_AMOUNT));
        long id = mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper._ID));

        holder.itemText.setText(item);
        holder.priceText.setText(price);
        holder.itemView.setTag(id);
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Clicked : " + position, Toast.LENGTH_SHORT).show();
            }
        });
         */

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        public TextView itemText;
        public TextView priceText;

        public MainViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemText = itemView.findViewById(R.id.itemAdapter);
            priceText = itemView.findViewById(R.id.priceAdapter);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
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
