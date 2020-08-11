package adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.mindscape.pocketful.DatabaseHelper;
import com.mindscape.pocketful.R;

/**
 * Created by MrHKMY on 7/8/2020.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private ViewGroup _parent;
    private Context mContext;
    private Cursor mCursor;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener (OnItemClickListener listener) {
        mListener = listener;
    }

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

        return new NoteViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)){
            return;
        }

        String title = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.NOTE_TITLE));
        String content = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.NOTE_CONTENT));
        long id = mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper._ID));

        holder.title.setText(title);
        holder.content.setText(content);
        holder.itemView.setTag(R.id.key1, id);
        holder.itemView.setTag(R.id.key2, position);

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Clicked : " + position , Toast.LENGTH_SHORT).show();
            }
        });
         */

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView content;

        public NoteViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title);
            content = itemView.findViewById(R.id.note_content);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null){
                        int position = getAdapterPosition();
                        //Toast.makeText(view.getContext(), "The id : " + view.getTag(), Toast.LENGTH_SHORT).show();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
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


