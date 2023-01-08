package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.adapter.DailyConversationAdapter;
import com.db.easylearning.interfaces.ItemClickListener;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.databinding.ActivityDailyConversationBinding;
import com.db.easylearning.models.DailyConversationModel;
import com.google.android.gms.ads.rewarded.RewardedAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyConversationActivity extends AppCompatActivity implements ItemClickListener {

    private ActivityDailyConversationBinding binding;
    private int value = 0;
    private DailyConversationAdapter dailyConversationAdapter;
    private List<DailyConversationModel> dailyConversationModelList;

    private RewardedAd rewardedAd;
    boolean isLoading;
    AppConstants appConstants;
    SharedPreferences sharedPreferences;
    private String showAds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDailyConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences=getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
        showAds=sharedPreferences.getString(Preferences.showAds,Preferences.DEFAULT);
        Log.d("Planc",showAds);
        appConstants=new AppConstants(rewardedAd,isLoading);

        if (showAds.equals("yes")) {
            appConstants.loadRewardedAd(DailyConversationActivity.this,DailyConversationActivity.this);
        }

        dailyConversationModelList = new ArrayList<>();
        value = getIntent().getIntExtra("value", 0);
        switch (value) {
            case 1:
                setTextTenseOverView();
                break;
            case 2:
                setTestSpeech();
                break;
            case 3:
                setPopularIdeams();
                break;
            case 0:
                addDailyConversation();
                break;

            default:
                //do nothing
        }

    }

    private void addDailyConversation() {
        RequestQueue queue = Volley.newRequestQueue(DailyConversationActivity.this);
        StringRequest request = new StringRequest(Request.Method.GET, URLs.DailyConversation, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    JSONArray jsonArray = respObj.getJSONArray("data");
                    if (respObj.optInt("status") == 1) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.recyclerViewDC.setVisibility(View.VISIBLE);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(i);
                            DailyConversationModel model = new DailyConversationModel();
                            model.setId(details.getString("id"));
                            model.setHeadText(details.getString("head_text"));
                            model.setBodyText(details.getString("body_text"));
                            dailyConversationModelList.add(model);
                        }

                        binding.recyclerViewDC.setLayoutManager(new LinearLayoutManager(DailyConversationActivity.this));
                        dailyConversationAdapter = new DailyConversationAdapter(DailyConversationActivity.this, dailyConversationModelList, DailyConversationActivity.this);
                        binding.recyclerViewDC.setAdapter(dailyConversationAdapter);

                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.progressBar.setVisibility(View.GONE);
                        binding.imgNoData.setVisibility(View.VISIBLE);
                        Toast.makeText(DailyConversationActivity.this, respObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressBar.setVisibility(View.GONE);
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

    private void setTextTenseOverView() {
        binding.toolbarTitle.setText("Tense Overview");

        RequestQueue queue = Volley.newRequestQueue(DailyConversationActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.TenseOverview, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    JSONArray jsonArray = respObj.getJSONArray("data");
                    if (respObj.optInt("status") == 1) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.recyclerViewDC.setVisibility(View.VISIBLE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(i);
                            DailyConversationModel model = new DailyConversationModel();
                            model.setId(details.getString("id"));
                            model.setHeadText(details.getString("head_text"));
                            model.setBodyText(details.getString("body_text"));
                            dailyConversationModelList.add(model);
                        }

                        binding.recyclerViewDC.setLayoutManager(new LinearLayoutManager(DailyConversationActivity.this));
                        dailyConversationAdapter = new DailyConversationAdapter(DailyConversationActivity.this, dailyConversationModelList, DailyConversationActivity.this);
                        binding.recyclerViewDC.setAdapter(dailyConversationAdapter);

                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.recyclerViewDC.setVisibility(View.VISIBLE);
                        Toast.makeText(DailyConversationActivity.this, respObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressBar.setVisibility(View.GONE);
                SwitchNC();
                // method to handle errors.
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

    private void setTestSpeech() {
        binding.toolbarTitle.setText("Speech");

        RequestQueue queue = Volley.newRequestQueue(DailyConversationActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.Speech, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    JSONArray jsonArray = respObj.getJSONArray("data");
                    if (respObj.optInt("status") == 1) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.recyclerViewDC.setVisibility(View.VISIBLE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(i);
                            DailyConversationModel model = new DailyConversationModel();
                            model.setId(details.getString("id"));
                            model.setHeadText(details.getString("head_text"));
                            model.setBodyText(details.getString("body_text"));
                            dailyConversationModelList.add(model);
                        }

                        binding.recyclerViewDC.setLayoutManager(new LinearLayoutManager(DailyConversationActivity.this));
                        dailyConversationAdapter = new DailyConversationAdapter(DailyConversationActivity.this, dailyConversationModelList, DailyConversationActivity.this);
                        binding.recyclerViewDC.setAdapter(dailyConversationAdapter);

                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(DailyConversationActivity.this, respObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressBar.setVisibility(View.GONE);
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

    private void setPopularIdeams() {
        binding.toolbarTitle.setText("Popular Ideams");
        RequestQueue queue = Volley.newRequestQueue(DailyConversationActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.PopularIdioms, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    JSONArray jsonArray = respObj.getJSONArray("data");
                    if (respObj.optInt("status") == 1) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.recyclerViewDC.setVisibility(View.VISIBLE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(i);
                            DailyConversationModel model = new DailyConversationModel();
                            model.setId(details.getString("id"));
                            model.setHeadText(details.getString("head_text"));
                            model.setBodyText(details.getString("body_text"));
                            dailyConversationModelList.add(model);
                        }

                        binding.recyclerViewDC.setLayoutManager(new LinearLayoutManager(DailyConversationActivity.this));
                        dailyConversationAdapter = new DailyConversationAdapter(DailyConversationActivity.this, dailyConversationModelList, DailyConversationActivity.this);
                        binding.recyclerViewDC.setAdapter(dailyConversationAdapter);

                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(DailyConversationActivity.this, respObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressBar.setVisibility(View.GONE);
                // method to handle errors.
                Toast.makeText(DailyConversationActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                Log.e("satyam", error.toString());
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


    @Override
    public void onItemClick(int position, List<DailyConversationModel> dailyConversationModelList) {
        TextView txtHeadDialog, txtBodyText;
        Dialog dialog = new Dialog(DailyConversationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        switch (value) {
            case 1:
            case 2:
            case 3:
                dialog.setContentView(R.layout.daily_conversation_dialog2);
                txtHeadDialog = dialog.findViewById(R.id.txtHeadDialog);
                txtBodyText = dialog.findViewById(R.id.txtBodyDialog);
                txtBodyText.setText(Html.fromHtml(dailyConversationModelList.get(position).getBodyText()));
                txtHeadDialog.setText(dailyConversationModelList.get(position).getHeadText());

                dialog.show();
                break;
            case 0:

                dialog.setContentView(R.layout.daily_conversation_dialog2);
                txtHeadDialog = dialog.findViewById(R.id.txtHeadDialog);
                txtBodyText = dialog.findViewById(R.id.txtBodyDialog);
                txtBodyText.setText(Html.fromHtml(dailyConversationModelList.get(position).getBodyText()));
                txtHeadDialog.setVisibility(View.GONE);
                txtHeadDialog.setText(dailyConversationModelList.get(position).getHeadText());

                dialog.show();
                break;
        }

    }


    private void SwitchNC() {
        Intent i = new Intent(DailyConversationActivity.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
}