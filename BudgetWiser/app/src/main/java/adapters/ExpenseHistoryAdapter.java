package adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.Entry;
import com.mindscape.budgetwiser.DatabaseHelper;
import com.mindscape.budgetwiser.R;

/**
 * Created by Hakimi on 3/7/2020.
 */
public class ExpenseHistoryAdapter extends RecyclerView.Adapter<ExpenseHistoryAdapter.ExpenseHistoryViewHolder> {
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
        View view = inflater.inflate(R.layout.expenses_row_content, parent, false);
        return new ExpenseHistoryViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseHistoryViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String date = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.EXPENSE_TIMESTAMP));
        String category = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.EXPENSE_NAME));
        String value = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.EXPENSE_VALUE));
        String note = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.EXPENSE_NOTE));
        String status = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.EXPENSE_STATUS));
        long id = mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper._ID));

        switch (category){
            case "Groceries":
                holder.iconImageView.setImageResource(R.drawable.groceries_icon);
                break;
            case "Clothing":
                holder.iconImageView.setImageResource(R.drawable.clothing_icon);
                break;
            case "Leisure":
                holder.iconImageView.setImageResource(R.drawable.leisure_icon);
                break;
            case "Transport":
                holder.iconImageView.setImageResource(R.drawable.transport_icon);
                break;
            case "Food":
                holder.iconImageView.setImageResource(R.drawable.food_icon);
                break;
            case "Self-care":
                holder.iconImageView.setImageResource(R.drawable.health_icon);
                break;
            case "Bills":
                holder.iconImageView.setImageResource(R.drawable.bills_icon);
                break;
            case "Family":
                holder.iconImageView.setImageResource(R.drawable.family_icon);
                break;
            case "Electronics":
                holder.iconImageView.setImageResource(R.drawable.electronics_icon);
                break;
            case "Sports":
                holder.iconImageView.setImageResource(R.drawable.sports_icon);
                break;
            case "Pet":
                holder.iconImageView.setImageResource(R.drawable.pet_icon);
                break;
            case "Others":
                holder.iconImageView.setImageResource(R.drawable.others_icon);
                break;
        }

        //holder.categoryText.setText(category);
        holder.dateText.setText(date);
        holder.value.setText(value);
        holder.notes.setText(note);
        holder.itemView.setTag(id);

        if (status.equals("IN")) {
            holder.statusImageView.setImageResource(R.drawable.green_expense_icon);
        } else {
            holder.statusImageView.setImageResource(R.drawable.red_expense_icon);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public static class ExpenseHistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView dateText;
        //public TextView categoryText;
        public TextView value;
        public TextView notes;
        public ImageView statusImageView, iconImageView;

        public ExpenseHistoryViewHolder(@NonNull View itemView, final MainAdapter.OnItemClickListener listener) {
            super(itemView);
            dateText = itemView.findViewById(R.id.transactionDateID);
            //categoryText = itemView.findViewById(R.id.categoryID);
            value = itemView.findViewById(R.id.valueID);
            notes = itemView.findViewById(R.id.noteID);
            statusImageView = itemView.findViewById(R.id.transactionStatusImageView);
            iconImageView = itemView.findViewById(R.id.iconImageView);

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