package com.db.easylearning.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.db.easylearning.R;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.interfaces.AllPlansListener;
import com.db.easylearning.models.AllPlansModel;

import java.util.ArrayList;
import java.util.List;

public class AllPlansAdapter extends RecyclerView.Adapter<AllPlansAdapter.AllPlansViewHolder> {

    private Context context;
    private List<AllPlansModel> allPlansModelList;
    private AllPlansListener allPlansListener;
    private List<Integer> colorList;


    public AllPlansAdapter(Context context, List<AllPlansModel> allPlansModelList, AllPlansListener allPlansListener) {
        this.context = context;
        this.allPlansModelList = allPlansModelList;
        this.allPlansListener = allPlansListener;
    }

    @NonNull
    @Override
    public AllPlansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AllPlansViewHolder(LayoutInflater.from(context).inflate(R.layout.all_plan_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AllPlansViewHolder holder, @SuppressLint("RecyclerView") int position) {
        colorList=new ArrayList<>();
        colorList.add(ContextCompat.getColor(context, R.color.second_color));
        colorList.add(ContextCompat.getColor(context, R.color.first_color));
        colorList.add(ContextCompat.getColor(context, R.color.third_color));
        colorList.add(ContextCompat.getColor(context, R.color.second_color));
        colorList.add(ContextCompat.getColor(context, R.color.first_color));
        colorList.add(ContextCompat.getColor(context, R.color.third_color));
        colorList.add(ContextCompat.getColor(context, R.color.second_color));
        colorList.add(ContextCompat.getColor(context, R.color.first_color));
        colorList.add(ContextCompat.getColor(context, R.color.third_color));
        colorList.add(ContextCompat.getColor(context, R.color.first_color));
        colorList.add(ContextCompat.getColor(context, R.color.second_color));
        colorList.add(ContextCompat.getColor(context, R.color.first_color));
        colorList.add(ContextCompat.getColor(context, R.color.third_color));
        colorList.add(ContextCompat.getColor(context, R.color.second_color));
        colorList.add(ContextCompat.getColor(context, R.color.first_color));
        colorList.add(ContextCompat.getColor(context, R.color.third_color));
        colorList.add(ContextCompat.getColor(context, R.color.second_color));
        colorList.add(ContextCompat.getColor(context, R.color.first_color));
        colorList.add(ContextCompat.getColor(context, R.color.third_color));
        colorList.add(ContextCompat.getColor(context, R.color.first_color));
        holder.txtPlanName.setText(allPlansModelList.get(position).getPlanName());
        holder.txtPlanPrice.setText(allPlansModelList.get(position).getPlanPrice());
        holder.txtPlanDurationDays.setText(allPlansModelList.get(position).getPlanDuration()+" Days");
        //Picasso.get().load(+allPlansModelList.get(position).getPlanImage()).into(holder.imgPlanImage);
        Glide.with(context).load(URLs.ForImageURL+allPlansModelList.get(position).getPlanImage()).into(holder.imgPlanImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allPlansListener.OnItemClick(position);
            }
        });
        holder.planCard.setCardBackgroundColor(colorList.get(position));
    }

    @Override
    public int getItemCount() {
        return allPlansModelList.size();
    }

    class AllPlansViewHolder extends RecyclerView.ViewHolder{
        TextView txtPlanName,txtPlanPrice,txtPlanDurationDays;
        ImageView imgPlanImage;
        CardView planCard;
       public AllPlansViewHolder(@NonNull View itemView) {
           super(itemView);

           txtPlanName=itemView.findViewById(R.id.txtPlanName);
           txtPlanPrice=itemView.findViewById(R.id.txtPlanPrice);
           txtPlanDurationDays=itemView.findViewById(R.id.txtPlanDurationDays);
           imgPlanImage=itemView.findViewById(R.id.imgPlanImage);
           planCard=itemView.findViewById(R.id.planCard);
       }
   }

}
