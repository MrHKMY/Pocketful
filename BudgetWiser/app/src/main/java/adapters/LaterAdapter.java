package adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mindscape.pocketful.DatabaseHelper;
import com.mindscape.pocketful.R;

/**
 * Created by Hakimi on 12/6/2020.
 */
public class LaterAdapter extends RecyclerView.Adapter<LaterAdapter.LaterViewHolder> {

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
        String logo = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.LATER_CATEGORY));
        //int abc = mCursor.getInt(position);

        holder.itemText.setText(item);
        holder.priceText.setText(price);
        holder.itemView.setTag(id);

        switch (logo) {
            case "Select Platform":
                holder.logoImage.setImageResource(R.drawable.others_icon);
                break;
            case "Alibaba":
                holder.logoImage.setImageResource(R.drawable.alibaba_icon);
                break;
            case "Amazon":
                holder.logoImage.setImageResource(R.drawable.amazon_icon2);
                break;
            case "Asos":
                holder.logoImage.setImageResource(R.drawable.asos_icon);
                break;
            case "Ebay":
                holder.logoImage.setImageResource(R.drawable.ebay_icon);
                break;
            case "Flipkart":
                holder.logoImage.setImageResource(R.drawable.flipkart_icon);
                break;
            case "Hermo":
                holder.logoImage.setImageResource(R.drawable.hermo_icon);
                break;
            case "Lazada":
                holder.logoImage.setImageResource(R.drawable.lazada_icon);
                break;
            case "Sephora":
                holder.logoImage.setImageResource(R.drawable.sephora_icon);
                break;
            case "Shopee":
                holder.logoImage.setImageResource(R.drawable.shopee_icon);
                break;
            case "Taobao":
                holder.logoImage.setImageResource(R.drawable.taobao_icon);
                break;
            case "Zalora":
                holder.logoImage.setImageResource(R.drawable.zalora_icon);
                break;
            case "PhysicalStore":
                holder.logoImage.setImageResource(R.drawable.shop_icon);
                break;
        }
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
        public ImageView logoImage;

        public LaterViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemText = itemView.findViewById(R.id.itemAdapter);
            priceText = itemView.findViewById(R.id.priceAdapter);
            checkBox = itemView.findViewById(R.id.checkbox);
            logoImage = itemView.findViewById(R.id.hyperlinkLogo);

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