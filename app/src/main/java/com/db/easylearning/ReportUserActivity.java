package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.adapter.ReportUserAdapter;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.CustomProgressBar;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.interfaces.ReportUserClickListener;
import com.db.easylearning.models.ReportUserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportUserActivity extends AppCompatActivity implements ReportUserClickListener {
    RecyclerView rvReportsRecycler;
    CustomProgressBar customProgressBar;
    AppCompatButton btnSend;
    SharedPreferences sharedPreferences;
    String userId,toReportUserId,type="";



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);

        initial();
        addListeners();


    }

    private void initial() {
        customProgressBar = new CustomProgressBar();
        btnSend = findViewById(R.id.btnSend);
        sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
        userId=sharedPreferences.getString(Preferences.userId,Preferences.PREFER_NAME);

        toReportUserId= getIntent().getStringExtra("callingUser");
        Log.d("planH","ToReportUser : "+toReportUserId);

        rvReportsRecycler = findViewById(R.id.rvReportsRecycler);
        getReportUserData();
    }

    private void addListeners() {

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.isEmpty()){
                    AppConstants.showToast(ReportUserActivity.this,"Please Select An Option");
                }
                else {
                    ReportUser();
                }
            }
        });
    }

    private void ReportUser() {
        List<ReportUserModel> reportUserModelList = new ArrayList<>();

        customProgressBar.showProgress(ReportUserActivity.this);
        RequestQueue queue = Volley.newRequestQueue(ReportUserActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.REPORTUSER, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("planC", "response from ReportUser Api: " + response);

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    customProgressBar.hideProgress();

                    switch (status) {
                        case 1: {
                           AppConstants.showToast(ReportUserActivity.this,"Your Report Submitted Successfully");
                           Intent intent=new Intent(ReportUserActivity.this,HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                           startActivity(intent);

                            break;
                        }
                        case 0: {
                            AppConstants.showToast(ReportUserActivity.this, "No Data");
                            break;
                        }
                        default: {
                            //do nothing
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    Log.d("planC", e.getLocalizedMessage());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                SwitchNC();
                Log.d("planResponse",error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reported_id_from",userId);
                params.put("reported_id_to",toReportUserId);
                params.put("type",type);
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

    private void getReportUserData() {
        List<ReportUserModel> reportUserModelList = new ArrayList<>();

        customProgressBar.showProgress(ReportUserActivity.this);
        RequestQueue queue = Volley.newRequestQueue(ReportUserActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.REPORTUSER, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("planC", "response from ReportUser Api: " + response);

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    customProgressBar.hideProgress();

                    switch (status) {
                        case 1: {
                            JSONArray jsonArray = respObj.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                ReportUserModel reportUserModel = new ReportUserModel();
                                reportUserModel.setText(object.optString("text"));
                                reportUserModel.setId(object.optString("id"));
                                reportUserModelList.add(reportUserModel);
                            }

                            rvReportsRecycler.setLayoutManager(new LinearLayoutManager(ReportUserActivity.this));
                            ReportUserAdapter adapter = new ReportUserAdapter(ReportUserActivity.this, reportUserModelList, ReportUserActivity.this);

                            rvReportsRecycler.setAdapter(adapter);


                            break;
                        }
                        case 0: {
                            AppConstants.showToast(ReportUserActivity.this, "No Data");
                            break;
                        }
                        default: {
                            //do nothing
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    Log.d("planC", e.getLocalizedMessage());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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


    private void SwitchNC() {
        Intent i = new Intent(ReportUserActivity.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    @Override
    public void OnItemClick(int position, List<ReportUserModel> reportUserModelList, RadioButton rbBtnReport) {
        type=reportUserModelList.get(position).getId();
    }
}