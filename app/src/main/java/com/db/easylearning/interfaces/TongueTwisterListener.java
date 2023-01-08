package com.db.easylearning.interfaces;

import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.db.easylearning.models.TongueTwisterModel;

import java.util.List;

public interface TongueTwisterListener {

 void onPlayClick(int position, List<TongueTwisterModel>tongueTwisterModelList, LinearLayout ltPlayBtn, LinearLayout ltPauseBtn, ProgressBar progressPlayPause);
 void onPauseClick(int position, List<TongueTwisterModel>tongueTwisterModelList,LinearLayout ltPlayBtn,LinearLayout ltPauseBtn,ProgressBar progressPlayPause);

}
