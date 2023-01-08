package com.db.easylearning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.db.easylearning.R;
import com.db.easylearning.models.NotificationModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    Context context;
    List<NotificationModel> notificationModelList;

    public NotificationAdapter(Context context, List<NotificationModel> notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.txtTitle.setText(notificationModelList.get(position).getTitle());
        holder.txtDesc.setText(notificationModelList.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDesc;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle=itemView.findViewById(R.id.txtTitle);
            txtDesc =itemView.findViewById(R.id.txtDesc);
        }
    }
}
