package com.db.easylearning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.db.easylearning.R;
import com.db.easylearning.models.PaymentHistoryModel;

import java.util.List;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.PaymentHistoryViewHolder> {

    Context context;
    List<PaymentHistoryModel> paymentHistoryModelList;
    String payType;


    public PaymentHistoryAdapter(Context context, List<PaymentHistoryModel> paymentHistoryModelList) {
        this.context = context;
        this.paymentHistoryModelList = paymentHistoryModelList;
    }

    @NonNull
    @Override
    public PaymentHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.speakify_payment_history_view, parent, false);
        return new PaymentHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentHistoryViewHolder holder, int position) {

        holder.txtPurchaseDate.setText(paymentHistoryModelList.get(position).getStartDate());
        payType=paymentHistoryModelList.get(position).getType();
        holder.txtPayMode.setText(payType);

        switch (payType){
            case "1":
                holder.txtPayMode.setText("Online");
                holder.imgTransactionImage.setBackgroundResource(R.drawable.up_arrow);
                holder.txtPlanPrice.setText(paymentHistoryModelList.get(position).getPlanPrice()+" Rs.");
                break;
            case "2":
                holder.txtPayMode.setText("Wallet");
                holder.imgTransactionImage.setBackgroundResource(R.drawable.up_arrow);
                holder.txtPlanPrice.setText(paymentHistoryModelList.get(position).getPlanPrice()+" Rs.");
                break;
            case "3":
                holder.txtPayMode.setText("Online & Wallet");
                holder.imgTransactionImage.setBackgroundResource(R.drawable.up_arrow);
                holder.txtPlanPrice.setText(paymentHistoryModelList.get(position).getPlanPrice()+" Rs.");
                break;
            default:
                holder.txtPayMode.setText("Wallet");
                holder.txtTransactionDesc.setText("Credited To your Wallet via Refer Code");
                holder.imgTransactionImage.setBackgroundResource(R.drawable.down_arrow);
                holder.txtPlanPrice.setText(paymentHistoryModelList.get(position).getReceive_wallet()+" Rs.");

        }

    }

    @Override
    public int getItemCount() {
        return paymentHistoryModelList.size();
    }

    static class PaymentHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtPlanPrice, txtPurchaseDate;
        TextView txtPayMode,txtTransactionDesc;
        ImageView imgTransactionImage;

        public PaymentHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPlanPrice=itemView.findViewById(R.id.txtPlanPrice);
            txtPurchaseDate=itemView.findViewById(R.id.txtPurchaseDate);
            txtPayMode=itemView.findViewById(R.id.txtPayMode);
            txtTransactionDesc=itemView.findViewById(R.id.txtTransactionDesc);
            imgTransactionImage=itemView.findViewById(R.id.imgTransactionImage);

        }
    }
}
