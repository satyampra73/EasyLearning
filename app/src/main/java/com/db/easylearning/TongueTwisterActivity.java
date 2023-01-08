package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.adapter.TongueTwisterAdapter;
import com.db.easylearning.interfaces.TongueTwisterListener;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.models.TongueTwisterModel;
import com.google.android.gms.ads.rewarded.RewardedAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TongueTwisterActivity extends AppCompatActivity implements TongueTwisterListener {
    private List<TongueTwisterModel> tongueTwisterList;
    private TongueTwisterAdapter tongueTwisterAdapter;
    private RecyclerView recyclerViewTT;
    private ProgressBar progressBar;
    MediaPlayer mediaPlayer;
    private RewardedAd rewardedAd;
    boolean isLoading;
    AppConstants appConstants;
    SharedPreferences sharedPreferences;
    private String showAds;
    ImageView imgNoData;


    @SuppressLint("MissingInflatedId")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tongue_twister);
        recyclerViewTT = findViewById(R.id.recyclerViewTT);
        progressBar = findViewById(R.id.progressBar);
        imgNoData=findViewById(R.id.imgNoData);

        tongueTwisterList = new ArrayList<>();

        sharedPreferences=getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
        showAds=sharedPreferences.getString(Preferences.showAds,Preferences.DEFAULT);
        Log.d("Planc",showAds);
        appConstants=new AppConstants(rewardedAd,isLoading);

        if (showAds.equals("yes")) {
            appConstants.loadRewardedAd(TongueTwisterActivity.this,TongueTwisterActivity.this);

        }


        addTongueTwister();

    }


    private void addTongueTwister() {
        RequestQueue queue = Volley.newRequestQueue(TongueTwisterActivity.this);
        StringRequest request = new StringRequest(Request.Method.GET, URLs.TongueTwister, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    JSONArray jsonArray = respObj.getJSONArray("data");
                    if (respObj.optInt("status") == 1) {
                        progressBar.setVisibility(View.GONE);
                        recyclerViewTT.setVisibility(View.VISIBLE);
                        imgNoData.setVisibility(View.GONE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(i);
                            TongueTwisterModel model = new TongueTwisterModel();
                            model.setId(details.getString("id"));
                            model.setTitle_text(details.getString("title_text"));
                            model.setAudio_file(details.getString("audio_file"));
                            tongueTwisterList.add(model);
                        }

                        recyclerViewTT.setLayoutManager(new LinearLayoutManager(TongueTwisterActivity.this));
                        tongueTwisterAdapter = new TongueTwisterAdapter(TongueTwisterActivity.this, tongueTwisterList, TongueTwisterActivity.this);
                        recyclerViewTT.setAdapter(tongueTwisterAdapter);


                    } else {
                        progressBar.setVisibility(View.GONE);
                        recyclerViewTT.setVisibility(View.VISIBLE);
                        Toast.makeText(TongueTwisterActivity.this, respObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                imgNoData.setVisibility(View.VISIBLE);
                // method to handle errors.
                SwitchNC();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AppConstants.SPEAKIFY_API_KEY, AppConstants.SPEAKIFYKEYVALUE);
                return params;
            }
        };
        queue.add(request);
    }

    public void click(View view) {
        finish();
    }

    @Override
    public void onPlayClick(int position, List<TongueTwisterModel> tongueTwisterModelList, LinearLayout ltPlayBtn, LinearLayout ltPauseBtn, ProgressBar progressBar) {

//        ExampleAsync task = new ExampleAsync();
//        task.execute();
        playAudio("https://panel.speakify.co.in/"+tongueTwisterModelList.get(position).getAudio_file(), ltPlayBtn, ltPauseBtn, progressBar);

    }

    @Override
    public void onPauseClick(int position, List<TongueTwisterModel> tongueTwisterModelList, LinearLayout ltPlayBtn, LinearLayout ltPauseBtn, ProgressBar progressBar) {
        if (mediaPlayer.isPlaying()) {

            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

        }
    }


    private void playAudio(String audioUrl, LinearLayout ltPlayBtn, LinearLayout ltPauseBtn, ProgressBar progressPlayPause) {

        progressPlayPause.setVisibility(View.VISIBLE);
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                ltPauseBtn.setVisibility(View.GONE);
                ltPlayBtn.setVisibility(View.VISIBLE);
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressPlayPause.setVisibility(View.GONE);
                ltPauseBtn.setVisibility(View.VISIBLE);
                ltPlayBtn.setVisibility(View.GONE);
                mp.start();
            }
        });
    }


    private void SwitchNC() {
        Intent i = new Intent(TongueTwisterActivity.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

}