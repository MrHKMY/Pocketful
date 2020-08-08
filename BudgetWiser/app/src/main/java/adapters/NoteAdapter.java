package adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mindscape.pocketful.DatabaseHelper;
import com.mindscape.pocketful.R;

/**
 * Created by MrHKMY on 7/8/2020.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private ViewGroup _parent;
    private Context mContext;
    private Cursor mCursor;


    public NoteAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_row, null);
        _parent = parent;

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)){
            return;
        }

        String title = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.NOTE_TITLE));
        String content = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.NOTE_CONTENT));

        holder.title.setText(title);
        holder.content.setText(content);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title);
            content = itemView.findViewById(R.id.note_content);
        }
    }

    public void swapCursor (Cursor newCursor) {
        if (mCursor != null){
            mCursor.close();
        }

        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}


