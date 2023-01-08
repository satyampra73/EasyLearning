package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.Preferences;
import com.google.android.gms.ads.rewarded.RewardedAd;

public class BasicGrammarActivity extends AppCompatActivity {
    private LinearLayout ltTenseOverview, ltSpeech, ltPopularIdeams;

    private RewardedAd rewardedAd;
    boolean isLoading;
    AppConstants appConstants;

    SharedPreferences sharedPreferences;
    private String showAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_grammar);
        ltTenseOverview = findViewById(R.id.ltTenseOverview);
        ltSpeech = findViewById(R.id.ltSpeech);
        ltPopularIdeams = findViewById(R.id.ltPopularIdeams);

        sharedPreferences=getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
        showAds=sharedPreferences.getString(Preferences.showAds,Preferences.DEFAULT);
        Log.d("Planc",showAds);
        appConstants=new AppConstants(rewardedAd,isLoading);

        if (showAds.equals("yes")) {
            appConstants.loadRewardedAd(BasicGrammarActivity.this,BasicGrammarActivity.this);
        }


        ltTenseOverview.setOnClickListener(View -> {
            Intent intent = new Intent(BasicGrammarActivity.this, DailyConversationActivity.class);
            intent.putExtra("value", 1);
            startActivity(intent);
        });
        ltSpeech.setOnClickListener(View -> {
            Intent intent = new Intent(BasicGrammarActivity.this, DailyConversationActivity.class);
            intent.putExtra("value", 2);
            startActivity(intent);
        });
        ltPopularIdeams.setOnClickListener(view -> {
            Intent intent = new Intent(BasicGrammarActivity.this, DailyConversationActivity.class);
            intent.putExtra("value", 3);
            startActivity(intent);
        });

    }

    public void click(View view) {
        finish();
    }
}