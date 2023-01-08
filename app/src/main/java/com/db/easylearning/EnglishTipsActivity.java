package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.adapter.EnglishTipsAdapter;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.models.EnglishTipsModel;
import com.google.android.gms.ads.rewarded.RewardedAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnglishTipsActivity extends AppCompatActivity {
    private EnglishTipsAdapter englishTipsAdapter;
    private RecyclerView recyclerViewET;
    private List<EnglishTipsModel> englishTipsModelList;
    private ProgressBar progressBar;

    private RewardedAd rewardedAd;
    boolean isLoading;
    AppConstants appConstants;
    private SharedPreferences sharedPreferences;
    private String showAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_english_tips);
        englishTipsModelList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        recyclerViewET = findViewById(R.id.recyclerViewET);

        sharedPreferences=getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
        showAds=sharedPreferences.getString(Preferences.showAds,Preferences.DEFAULT);
        Log.d("Planc",showAds);
        appConstants=new AppConstants(rewardedAd,isLoading);

        if (showAds.equals("yes")) {
            appConstants.loadRewardedAd(EnglishTipsActivity.this,EnglishTipsActivity.this);
        }

        addEnglishTips();

    }

    private void addEnglishTips() {
        RequestQueue queue = Volley.newRequestQueue(EnglishTipsActivity.this);
        StringRequest request = new StringRequest(Request.Method.GET, URLs.EnglishTips, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    JSONArray jsonArray = respObj.getJSONArray("data");
                    if (respObj.optInt("status") == 1) {
                        progressBar.setVisibility(View.GONE);
                        recyclerViewET.setVisibility(View.VISIBLE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(i);
                            EnglishTipsModel model = new EnglishTipsModel();
                            model.setId(details.getString("id"));
                            model.setSimple_text(details.getString("simple_text"));
                            model.setEnglish_para(details.getString("eng_para"));
                            englishTipsModelList.add(model);
                        }

                        recyclerViewET.setLayoutManager(new LinearLayoutManager(EnglishTipsActivity.this));
                        englishTipsAdapter = new EnglishTipsAdapter(EnglishTipsActivity.this, englishTipsModelList);
                        recyclerViewET.setAdapter(englishTipsAdapter);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        recyclerViewET.setVisibility(View.VISIBLE);
                        Toast.makeText(EnglishTipsActivity.this, respObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                // method to handle errors.
                SwitchNC();
            }
        }) {

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

    private void SwitchNC() {
        Intent i = new Intent(EnglishTipsActivity.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
}