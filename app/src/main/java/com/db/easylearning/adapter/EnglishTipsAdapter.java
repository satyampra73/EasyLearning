package com.db.easylearning.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.db.easylearning.R;
import com.db.easylearning.models.EnglishTipsModel;

import java.util.List;

public class EnglishTipsAdapter extends  RecyclerView.Adapter<EnglishTipsAdapter.EnglishTipsViewHolder>{
    Context context;
    List<EnglishTipsModel>englishTipsModelList;

    public EnglishTipsAdapter(Context context, List<EnglishTipsModel> englishTipsModelList) {
        this.context = context;
        this.englishTipsModelList = englishTipsModelList;
    }

    @NonNull
    @Override
    public EnglishTipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(context).inflate(R.layout.english_tips_view,parent,false);
        return new EnglishTipsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EnglishTipsViewHolder holder, int position) {
        String serialNo= String.valueOf(holder.getAdapterPosition()+1);
        holder.txtId.setText(serialNo+".");
        holder.txtSimpleText.setText(englishTipsModelList.get(position).getSimple_text());
        holder.txtEnglishPara.setText(Html.fromHtml(englishTipsModelList.get(position).getEnglish_para()));
    }

    @Override
    public int getItemCount() {
        return englishTipsModelList.size();
    }

    class EnglishTipsViewHolder extends RecyclerView.ViewHolder{
        TextView txtId,txtSimpleText,txtEnglishPara;
        public EnglishTipsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId=itemView.findViewById(R.id.txtId);
            txtSimpleText=itemView.findViewById(R.id.txtSimpleText);
            txtEnglishPara=itemView.findViewById(R.id.txtEnglishPara);

        }
    }
}
