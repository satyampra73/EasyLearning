package com.db.easylearning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.db.easylearning.R;
import com.db.easylearning.interfaces.ItemClickListener;
import com.db.easylearning.models.DailyConversationModel;

import java.util.List;

public class DailyConversationAdapter extends RecyclerView.Adapter<DailyConversationAdapter.DailyConversationViewHolder> {
    private Context context;
    private List<DailyConversationModel> dailyConversationList;
    private ItemClickListener mItemClickListener;

    public DailyConversationAdapter(Context context, List<DailyConversationModel> dailyConversationList,ItemClickListener itemClickListener) {
        this.context = context;
        this.dailyConversationList = dailyConversationList;
        this.mItemClickListener=itemClickListener;
    }

    @NonNull
    @Override
    public DailyConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DailyConversationViewHolder(LayoutInflater.from(context).inflate(R.layout.daily_conversation_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DailyConversationViewHolder holder, int position) {
        holder.txtHeadText.setText(dailyConversationList.get(position).getHeadText());
        holder.itemView.setOnClickListener(view -> {
            mItemClickListener.onItemClick(position,dailyConversationList);
        });
    }

    @Override
    public int getItemCount() {
        return dailyConversationList.size();
    }

    class DailyConversationViewHolder extends RecyclerView.ViewHolder {
        TextView txtHeadText;

        public DailyConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHeadText = itemView.findViewById(R.id.txtHeadText);
        }
    }
}
