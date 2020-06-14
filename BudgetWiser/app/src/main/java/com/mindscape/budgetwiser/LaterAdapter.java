package com.mindscape.budgetwiser;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Hakimi on 12/6/2020.
 */
class LaterAdapter extends RecyclerView.Adapter<LaterAdapter.LaterViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public LaterAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.later_content,parent,false);
        return new LaterViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LaterViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String item = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.LATER_NAME));
        String price = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.LATER_AMOUNT));
        long id = mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper._ID));
        //int abc = mCursor.getInt(position);


        holder.itemText.setText(item);
        holder.priceText.setText(price);
        holder.itemView.setTag(id);
        //holder.checkBox.setChecked(abc);
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

    public static class LaterViewHolder extends RecyclerView.ViewHolder {
        public TextView itemText;
        public TextView priceText;
        public CheckBox checkBox;

        public LaterViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemText = itemView.findViewById(R.id.itemAdapter);
            priceText = itemView.findViewById(R.id.priceAdapter);
            checkBox = itemView.findViewById(R.id.checkbox);
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