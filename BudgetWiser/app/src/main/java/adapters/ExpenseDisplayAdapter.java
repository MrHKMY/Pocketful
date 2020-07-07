package adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mindscape.budgetwiser.DatabaseHelper;
import com.mindscape.budgetwiser.R;

/**
 * Created by MrHKMY on 7/7/2020.
 */
public class ExpenseDisplayAdapter extends RecyclerView.Adapter<ExpenseDisplayAdapter.ExpenseDisplayViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private MainAdapter.OnItemClickListener mListener;

    public ExpenseDisplayAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.expense_display_row_content, parent, false);
        return new ExpenseDisplayViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseDisplayViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String title = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.EXPENSE_TIMESTAMP));
        String value = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.EXPENSE_VALUE));

        holder.title.setText(title);
        holder.value.setText(value);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    public static class ExpenseDisplayViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView value;
        public ImageView categoryImageView;

        public ExpenseDisplayViewHolder(@NonNull View itemView, final MainAdapter.OnItemClickListener listener) {
            super(itemView);

            value = itemView.findViewById(R.id.catValueTextView);
            title = itemView.findViewById(R.id.catNameTextView);
            categoryImageView = itemView.findViewById(R.id.catImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
