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
import com.db.easylearning.adapter.HRInterViewAdapter;
import com.db.easylearning.interfaces.HRInterviewListener;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.models.HRInterviewModel;
import com.google.android.gms.ads.rewarded.RewardedAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HRInterviewActivity extends AppCompatActivity implements HRInterviewListener {

    private RecyclerView recyclerViewHr;
    private List<HRInterviewModel> hrInterviewList;
    private HRInterViewAdapter hrInterViewAdapter;
    private ProgressBar progressBarHr;

    private RewardedAd rewardedAd;
    boolean isLoading;
    AppConstants appConstants;
    private SharedPreferences sharedPreferences;
    private String showAds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrinterview);
        hrInterviewList=new ArrayList<>();
        recyclerViewHr=findViewById(R.id.recyclerViewHr);
        progressBarHr=findViewById(R.id.progressBarHr);

        sharedPreferences=getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
        showAds=sharedPreferences.getString(Preferences.showAds,Preferences.DEFAULT);
        Log.d("Planc",showAds);
        appConstants=new AppConstants(rewardedAd,isLoading);

        if (showAds.equals("yes")) {
            appConstants.loadRewardedAd(HRInterviewActivity.this,HRInterviewActivity.this);
        }

        getHrInterViewData();

    }

    private void getHrInterViewData() {
        RequestQueue queue = Volley.newRequestQueue(HRInterviewActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.HrInterview, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    JSONArray jsonArray = respObj.getJSONArray("data");
                    if (respObj.optInt("status") == 1) {
                        progressBarHr.setVisibility(View.GONE);
                        recyclerViewHr.setVisibility(View.VISIBLE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(i);
                            HRInterviewModel model = new HRInterviewModel();
                            model.setHeadText(details.getString("head_text"));
                            model.setTraps(details.getString("traps"));
                            model.setBestAnswer(details.getString("best_answer"));
                            hrInterviewList.add(model);
                        }

                        recyclerViewHr.setLayoutManager(new LinearLayoutManager(HRInterviewActivity.this));
                        hrInterViewAdapter = new HRInterViewAdapter( hrInterviewList,HRInterviewActivity.this,HRInterviewActivity.this);
                        recyclerViewHr.setAdapter(hrInterViewAdapter);


                    } else {
                        progressBarHr.setVisibility(View.GONE);
                        recyclerViewHr.setVisibility(View.VISIBLE);
                        Toast.makeText(HRInterviewActivity.this, respObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarHr.setVisibility(View.GONE);
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

    public void OpenActivity(View view) {
        Intent intent = new Intent(HRInterviewActivity.this, HrQuestionDescription.class);
        startActivity(intent);
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent=new Intent(HRInterviewActivity.this,HrQuestionDescription.class);
        intent.putExtra("question",hrInterviewList.get(position).getHeadText());
        intent.putExtra("trapText",hrInterviewList.get(position).getTraps());
        intent.putExtra("bestAnswer",hrInterviewList.get(position).getBestAnswer());
        startActivity(intent);
    }

    private void SwitchNC() {
        Intent i = new Intent(HRInterviewActivity.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
}