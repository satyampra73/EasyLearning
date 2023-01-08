package com.db.easylearning.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.db.easylearning.R;
import com.db.easylearning.interfaces.HRInterviewListener;
import com.db.easylearning.models.HRInterviewModel;

import java.util.List;

public class HRInterViewAdapter extends RecyclerView.Adapter<HRInterViewAdapter.HRInterViewViewHolder> {
    private List<HRInterviewModel> hrInterviewList;
    private Context context;
    private HRInterviewListener listener;

    public HRInterViewAdapter(List<HRInterviewModel> hrInterviewList, Context context,HRInterviewListener listener) {
        this.hrInterviewList = hrInterviewList;
        this.context = context;
        this.listener=listener;
    }

    @NonNull
    @Override
    public HRInterViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HRInterViewViewHolder(LayoutInflater.from(context).inflate(R.layout.hr_interview_qus_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HRInterViewViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String serialNo= String.valueOf(holder.getAdapterPosition()+1);
        holder.txtHrQsSerial.setText(serialNo+".");
        holder.txtHrQuestion.setText(hrInterviewList.get(position).getHeadText());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hrInterviewList.size();
    }

    class HRInterViewViewHolder extends RecyclerView.ViewHolder {
        TextView txtHrQsSerial,txtHrQuestion;

        public HRInterViewViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHrQsSerial=itemView.findViewById(R.id.txtHrQSSerial);
            txtHrQuestion=itemView.findViewById(R.id.txtHrQuestion);
        }
    }
}
