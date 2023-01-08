package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.adapter.NotificationAdapter;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.models.NotificationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView rvNotification;
    List<NotificationModel> notificationModelList;
    NotificationAdapter notificationAdapter;
    ProgressBar progressBar;
    ImageView imgNoData;
    String userId;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initial();
        getNotification();
    }


    private void initial() {
        rvNotification = findViewById(R.id.rvNotification);
        progressBar = findViewById(R.id.progressBar);
        imgNoData = findViewById(R.id.imgNoData);
        sharedPreferences=getSharedPreferences(Preferences.PREFER_NAME,MODE_PRIVATE);
        userId=sharedPreferences.getString(Preferences.userId,Preferences.DEFAULT);
        Log.d("planN","userId: "+userId);
        notificationModelList = new ArrayList<>();
    }


    private void getNotification() {
        progressBar.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(NotificationActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, URLs.Notification, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);


                    if (respObj.optInt("status") == 1) {
                        JSONArray jsonArray = respObj.getJSONArray("data");
                        progressBar.setVisibility(View.GONE);
                        imgNoData.setVisibility(View.GONE);
                        rvNotification.setVisibility(View.VISIBLE);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(i);
                            NotificationModel model = new NotificationModel();
                            model.setId(details.optString("id"));
                            model.setTitle(details.optString("title"));
                            model.setDescription(details.optString("description"));
                            notificationModelList.add(model);
                        }

                        rvNotification.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
                        notificationAdapter = new NotificationAdapter(NotificationActivity.this, notificationModelList);
                        rvNotification.setAdapter(notificationAdapter);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        rvNotification.setVisibility(View.GONE);
                        imgNoData.setVisibility(View.VISIBLE);
                        //Toast.makeText(PaymentHistoryActivity.this, respObj.getString("msg"), Toast.LENGTH_SHORT).show();
                        Log.e("PlanC", respObj.optString("status"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("PlanC", e.getMessage());
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id",userId);
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

    private void SwitchNC() {
        Intent i = new Intent(NotificationActivity.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
}