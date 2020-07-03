package adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mindscape.budgetwiser.DatabaseHelper;
import com.mindscape.budgetwiser.R;

/**
 * Created by Hakimi on 3/7/2020.
 */
public class ExpenseHistoryAdapter extends RecyclerView.Adapter<ExpenseHistoryAdapter.ExpenseHistoryViewHolder>{
    private Context mContext;
    private Cursor mCursor;
    private MainAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MainAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public ExpenseHistoryAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.expenses_row_content,parent,false);
        return new ExpenseHistoryViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseHistoryViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String date = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.WISHLIST_AMOUNT));
        String category = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.WISHLIST_NAME));
        String value = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.WISHLIST_AMOUNT));
        long id = mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper._ID));

        holder.categoryText.setText(category);
        holder.dateText.setText(date);
        holder.value.setText(value);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public static class ExpenseHistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView dateText;
        public TextView categoryText;
        public TextView value;

        public ExpenseHistoryViewHolder(@NonNull View itemView, final MainAdapter.OnItemClickListener listener) {
            super(itemView);
            dateText = itemView.findViewById(R.id.transactionDateID);
            categoryText = itemView.findViewById(R.id.categoryID);
            value = itemView.findViewById(R.id.valueID);
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