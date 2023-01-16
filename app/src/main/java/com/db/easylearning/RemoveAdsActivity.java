package com.db.easylearning;

import static com.db.easylearning.apphelper.AppConstants.showToast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.db.easylearning.adapter.AllPlansAdapter;
import com.db.easylearning.interfaces.AllPlansListener;
import com.db.easylearning.apphelper.AppConstants;

import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.models.AllPlansModel;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RemoveAdsActivity extends AppCompatActivity implements PaymentResultListener, AllPlansListener {
    private RecyclerView rvAllPlansRecycler;
    private ProgressBar progressBar;
    private AllPlansAdapter allPlansAdapter;
    private List<AllPlansModel> allPlansModelList;
    Checkout checkout;
    Dialog dialogProgress;
    SharedPreferences sharedPreferences;
    private String userId, planId, currentDate, planName, planDuration, planPrice, planImage, strWallet, strPlanCost, strPayableAmount;
    private AppConstants appConstants;
    private TextView txtPlanName, txtPlanDuration, txtPlanPrice;
    private ImageView imgPlanImage;
    CardView curPlanCard;
    private ImageView imgNoData;
    AllPlansModel model;

    //views from include layout
    RelativeLayout mainLayout, includeLayout;
    ImageView imgClose, incImgPlanImage;
    TextView incTxtPlanName, incTxtPlanPrice, incTxtPlanDuration, txtPlanCost, txtWalletAmount, txtPayableAmount;
    CardView cardPayNow;
    String payType, onlinePrice, walletPrice;

    int planCostInt, walletAmountInt, payableAmountInt;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_ads);
        rvAllPlansRecycler = findViewById(R.id.rvAllPlansRecycler);
        progressBar = findViewById(R.id.progressBar);
        txtPlanName = findViewById(R.id.txtPlanName);
        txtPlanDuration = findViewById(R.id.txtPlanDuration);
        txtPlanPrice = findViewById(R.id.txtPlanPrice);
        imgPlanImage = findViewById(R.id.imgPlanImage);
        curPlanCard = findViewById(R.id.curPlanCard);
        imgNoData = findViewById(R.id.imgNoData);

        //From Include
        mainLayout = findViewById(R.id.mainLayout);
        includeLayout = findViewById(R.id.includeLayout);
        imgClose = findViewById(R.id.imgClose);

        incImgPlanImage = findViewById(R.id.incImgPlanImage);
        incTxtPlanName = findViewById(R.id.incTxtPlanName);
        incTxtPlanPrice = findViewById(R.id.incTxtPlanPrice);
        incTxtPlanDuration = findViewById(R.id.incTxtPlanDuration);
        cardPayNow = findViewById(R.id.cardBtnPayNow);

        txtPlanCost = findViewById(R.id.txtPlanCost);
        txtWalletAmount = findViewById(R.id.txtWalletAmount);
        txtPayableAmount = findViewById(R.id.txtPayableAmount);

        sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
        userId = sharedPreferences.getString(Preferences.userId, Preferences.DEFAULT);
        planName = sharedPreferences.getString(Preferences.planName, Preferences.DEFAULT);
        planDuration = sharedPreferences.getString(Preferences.planDuration, Preferences.DEFAULT);
        planPrice = sharedPreferences.getString(Preferences.planPrice, Preferences.DEFAULT);
        planImage = sharedPreferences.getString(Preferences.planImage, Preferences.DEFAULT);
        strWallet = sharedPreferences.getString(Preferences.wallet, Preferences.DEFAULT);
        if (strWallet.equals("N/A")) {
            strWallet = "0";
        }
        allPlansModelList = new ArrayList<>();
        Log.d("PlanC", userId);
        Log.d("PlanC", "Plan Image: " + planImage);
        appConstants = new AppConstants(dialogProgress);

//setting values from preferences

        if (planName.equals("")) {
            curPlanCard.setVisibility(View.GONE);
        } else {
            txtPlanName.setText(planName);
            txtPlanPrice.setText(planPrice);
            txtPlanDuration.setText(planDuration + "Days");
            Glide.with(RemoveAdsActivity.this).load(URLs.ForImageURL + planImage).into(imgPlanImage);
        }


        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = sdf.format(new Date());
        getAllPlans();
        imgClose.setOnClickListener(view -> {
            Intent i = new Intent(RemoveAdsActivity.this, RemoveAdsActivity.class);
            finish();
            overridePendingTransition(0, 0);
            startActivity(i);
            overridePendingTransition(0, 0);
        });

        cardPayNow.setOnClickListener(v -> {
            onlinePrice = String.valueOf(payableAmountInt);

            if (onlinePrice.equals("0")){
                purchasePlanCall("");
            }
            else {
                startPayment(Integer.parseInt(onlinePrice));
            }
        });

    }


    private void getAllPlans() {
        RequestQueue queue = Volley.newRequestQueue(RemoveAdsActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.allPlan, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);

                    if (respObj.optInt("status") == 1) {
                        JSONArray jsonArray = respObj.getJSONArray("data");
                        progressBar.setVisibility(View.GONE);
                        rvAllPlansRecycler.setVisibility(View.VISIBLE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(i);
                            model = new AllPlansModel();
                            model.setPlanId(details.optString("id"));
                            model.setPlanName(details.optString("name"));
                            model.setPlanImage(details.optString("image"));
                            model.setPlanPrice(details.optString("price"));
                            model.setPlanDuration(details.optString("duration"));
                            allPlansModelList.add(model);
                        }

                        rvAllPlansRecycler.setLayoutManager(new LinearLayoutManager(RemoveAdsActivity.this));
                        allPlansAdapter = new AllPlansAdapter(RemoveAdsActivity.this, allPlansModelList, RemoveAdsActivity.this);
                        rvAllPlansRecycler.setAdapter(allPlansAdapter);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        rvAllPlansRecycler.setVisibility(View.GONE);
                        imgNoData.setVisibility(View.VISIBLE);
                        Toast.makeText(RemoveAdsActivity.this, respObj.getString("msg"), Toast.LENGTH_SHORT).show();
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

    private void startPayment(int amount) {
        /**
         * Instantiate Checkout
         */
        checkout = new Checkout();
        checkout.setKeyID("rzp_live_r5kljMeHUbXgFw");
        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "EasyLearning");
            options.put("description", "Membership Purchase");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//          options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", amount * 100);//pass amount in currency subunits
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        purchasePlanCall(s);
    }

    private void purchasePlanCall(String transactionId) {
        appConstants.openDialog("Loading", RemoveAdsActivity.this);
        RequestQueue queue = Volley.newRequestQueue(RemoveAdsActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.purchasePlan, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");
                    JSONObject data = respObj.getJSONObject("data");


                    switch (status) {
                        case 0: {
                            appConstants.closeDialog();
                            showToast(RemoveAdsActivity.this, "Plan Purchasing Failed");

                            break;
                        }
                        case 1: {
                            appConstants.closeDialog();
                            showToast(RemoveAdsActivity.this, "Plan Purchased Sucessfully");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Preferences.showAds, "no");
                            editor.apply();

                            Intent intent=new Intent(RemoveAdsActivity.this,HomeActivity.class);
                            startActivity(intent);

                            break;
                        }
                        default: {
                            //do nothing
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                appConstants.closeDialog();
                SwitchNC();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("plan_id", planId);
                params.put("start_date", currentDate);
                params.put("transaction_id", transactionId);
                params.put("type", payType);
                params.put("online_price", onlinePrice);
                params.put("wallet_price", walletPrice);
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

    @Override
    public void onPaymentError(int i, String s) {
        showToast(RemoveAdsActivity.this, "Failed");


    }

    private void setIncValues(int pos) {
        incTxtPlanDuration.setText(allPlansModelList.get(pos).getPlanDuration() + " Days");
        incTxtPlanName.setText(allPlansModelList.get(pos).getPlanName());
        Glide.with(RemoveAdsActivity.this).load(URLs.ForImageURL + allPlansModelList.get(pos).getPlanImage()).into(incImgPlanImage);
        incTxtPlanPrice.setText(allPlansModelList.get(pos).getPlanPrice());

        txtPlanCost.setText(allPlansModelList.get(pos).getPlanPrice() + " Rs.");
//        calculations();
    }

    private void calculations() {
        planCostInt = Integer.parseInt(strPlanCost);
        walletAmountInt = Integer.parseInt(strWallet);
        payableAmountInt = 0;
        if (walletAmountInt >= planCostInt) {
            payableAmountInt = planCostInt - planCostInt;
            //condition where payment is going to done with wallet type=2
            txtWalletAmount.setText("-" + strPlanCost + " Rs.");
            walletPrice = strPlanCost;
            txtPayableAmount.setText(String.valueOf(payableAmountInt) + " Rs.");
            payType = "2";
        } else if (walletAmountInt == 0) {
            payableAmountInt = planCostInt;
            txtWalletAmount.setText("-" + "0" + " Rs.");
            txtPayableAmount.setText(String.valueOf(payableAmountInt) + " Rs.");
            walletPrice = "0";
            //type 1  online
            payType = "1";
        } else {
            payableAmountInt = planCostInt - walletAmountInt;
            //type3 wallet and online both
            txtWalletAmount.setText("-" + strWallet + " Rs.");
            txtPayableAmount.setText(String.valueOf(payableAmountInt) + " Rs.");
            walletPrice=strWallet;
            payType = "3";
        }

    }

    @Override
    public void OnItemClick(int position) {
        planId = allPlansModelList.get(position).getPlanId();
//        int amount = Integer.parseInt(allPlansModelList.get(position).getPlanPrice());
//        startPayment(amount);
        strPlanCost = allPlansModelList.get(position).getPlanPrice();
        setIncValues(position);
        calculations();
        mainLayout.setVisibility(View.GONE);
        includeLayout.setVisibility(View.VISIBLE);

    }



    private void SwitchNC() {
        Intent i = new Intent(RemoveAdsActivity.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
}