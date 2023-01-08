package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.adapter.PaymentHistoryAdapter;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.models.PaymentHistoryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentHistoryActivity extends AppCompatActivity {

    private RecyclerView rvPaymentHistory;
    private ProgressBar progressBar;
    private PaymentHistoryAdapter paymentHistoryAdapter;
    private List<PaymentHistoryModel> paymentHistoryModelList;
    SharedPreferences sharedPreferences;
    private String userId,walletBalance="";
    private ImageView imgNoData;
    private TextView txtWalletBalance,txtLastTrans;
    CardView cardWallet;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
        rvPaymentHistory = findViewById(R.id.rvPaymentHistory);
        progressBar = findViewById(R.id.progressBar);
        txtWalletBalance =findViewById(R.id.txtWalletBalance);
        imgNoData=findViewById(R.id.imgNoData);
        txtLastTrans=findViewById(R.id.txtLastTrans);
        cardWallet=findViewById(R.id.cardWallet);
        paymentHistoryModelList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("MyData",MODE_PRIVATE);
        userId=sharedPreferences.getString(Preferences.userId,Preferences.DEFAULT);


        getAllPlanHistory();
    }

    private void getAllPlanHistory() {
        progressBar.setVisibility(View.VISIBLE);
        cardWallet.setVisibility(View.GONE);
        txtLastTrans.setVisibility(View.GONE);
        RequestQueue queue = Volley.newRequestQueue(PaymentHistoryActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.allTransaction, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);


                    if (respObj.optInt("status")==1) {
                        JSONArray jsonArray = respObj.getJSONArray("data");
                        cardWallet.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        imgNoData.setVisibility(View.GONE);
                        txtLastTrans.setVisibility(View.VISIBLE);
                        rvPaymentHistory.setVisibility(View.VISIBLE);
                        txtWalletBalance.setText(respObj.optString("wallet_remaining_price"));

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(i);
                            PaymentHistoryModel model = new PaymentHistoryModel();
                            model.setTransactionIdApi(details.optString("id"));
                            model.setPlanPrice(details.optString("plan_price"));
                            model.setStartDate(details.optString("start_date"));
                            model.setType(details.optString("type"));
                            model.setReceive_wallet(details.getString("receive_wallet"));
                            paymentHistoryModelList.add(model);
                        }

                        rvPaymentHistory.setLayoutManager(new LinearLayoutManager(PaymentHistoryActivity.this));
                        paymentHistoryAdapter = new PaymentHistoryAdapter(PaymentHistoryActivity.this, paymentHistoryModelList);
                        rvPaymentHistory.setAdapter(paymentHistoryAdapter);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        rvPaymentHistory.setVisibility(View.GONE);
                        imgNoData.setVisibility(View.VISIBLE);
                        cardWallet.setVisibility(View.VISIBLE);
                        txtLastTrans.setVisibility(View.VISIBLE);
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
                params.put("user_id", userId);
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
        Intent i = new Intent(PaymentHistoryActivity.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
}