package com.db.easylearning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.db.easylearning.R;
import com.db.easylearning.interfaces.TongueTwisterListener;
import com.db.easylearning.models.TongueTwisterModel;

import java.util.List;

public class TongueTwisterAdapter extends RecyclerView.Adapter<TongueTwisterAdapter.TongueTwisterViewHolder> {

    private Context context;
    private List<TongueTwisterModel> tongueTwisterModelsList;
    private TongueTwisterListener tongueTwisterListener;

    public TongueTwisterAdapter(Context context, List<TongueTwisterModel> tongueTwisterModelsList, TongueTwisterListener tongueTwisterListener) {
        this.context = context;
        this.tongueTwisterModelsList = tongueTwisterModelsList;
        this.tongueTwisterListener = tongueTwisterListener;
    }

    @NonNull
    @Override
    public TongueTwisterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new TongueTwisterViewHolder(LayoutInflater.from(context).inflate(R.layout.tongue_twist_view, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull TongueTwisterViewHolder holder, int position) {
        holder.txtTitleTwist.setText(tongueTwisterModelsList.get(position).getTitle_text());
        holder.ltPlayBtn.setOnClickListener(view -> {
//            holder.ltPlayBtn.setVisibility(View.GONE);
//            holder.ltPauseBtn.setVisibility(View.VISIBLE);
            tongueTwisterListener.onPlayClick(position, tongueTwisterModelsList, holder.ltPlayBtn, holder.ltPauseBtn, holder.progressPlayPause);

        });

        holder.ltPauseBtn.setOnClickListener(view -> {
            tongueTwisterListener.onPauseClick(position, tongueTwisterModelsList, holder.ltPlayBtn, holder.ltPauseBtn,holder.progressPlayPause);
            holder.ltPauseBtn.setVisibility(View.GONE);
            holder.ltPlayBtn.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public int getItemCount() {
        return tongueTwisterModelsList.size();
    }

    class TongueTwisterViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitleTwist;
        LinearLayout ltPlayBtn, ltPauseBtn;
        ProgressBar progressPlayPause;

        public TongueTwisterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitleTwist = itemView.findViewById(R.id.txtTitleText);
            ltPlayBtn = itemView.findViewById(R.id.ltPlayBtn);
            ltPauseBtn = itemView.findViewById(R.id.ltPauseBtn);
            progressPlayPause=itemView.findViewById(R.id.progressPlayPause);
        }
    }
}
