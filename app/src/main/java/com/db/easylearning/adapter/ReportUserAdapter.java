package com.db.easylearning.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.db.easylearning.R;
import com.db.easylearning.interfaces.ReportUserClickListener;
import com.db.easylearning.models.ReportUserModel;

import java.util.List;

public class ReportUserAdapter extends RecyclerView.Adapter<ReportUserAdapter.ReportUserViewHolder>{

    Context context;
    List<ReportUserModel> reportTextList;
    ReportUserClickListener reportUserClickListener;
    int row_index=100;

    public ReportUserAdapter(Context context, List<ReportUserModel> reportTextList, ReportUserClickListener reportUserClickListener) {
        this.context = context;
        this.reportTextList = reportTextList;
        this.reportUserClickListener = reportUserClickListener;
    }

    @NonNull
    @Override
    public ReportUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReportUserViewHolder(LayoutInflater.from(context).inflate(R.layout.reportuser_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReportUserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txtReportText.setText(reportTextList.get(position).getText());
        holder.rbBtnReport.setClickable(false);
        holder.itemView.setOnClickListener(v -> {
            reportUserClickListener.OnItemClick(position,reportTextList,holder.rbBtnReport);

            row_index = position;
            notifyDataSetChanged();
        });

        if (row_index == position) {
            holder.rbBtnReport.setChecked(true);

        } else {
            holder.rbBtnReport.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return reportTextList.size();
    }

    class ReportUserViewHolder extends RecyclerView.ViewHolder {

        RadioButton rbBtnReport;
        TextView txtReportText;
        public ReportUserViewHolder(@NonNull View itemView) {
            super(itemView);
            rbBtnReport =itemView.findViewById(R.id.rbBtnReport);
            txtReportText=itemView.findViewById(R.id.txtReportText);
        }
    }
}
