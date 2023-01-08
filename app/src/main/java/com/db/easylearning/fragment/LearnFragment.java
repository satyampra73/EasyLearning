package com.db.easylearning.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.BasicGrammarActivity;
import com.db.easylearning.BuildConfig;
import com.db.easylearning.DailyConversationActivity;
import com.db.easylearning.EnglishTipsActivity;
import com.db.easylearning.HRInterviewActivity;
import com.db.easylearning.NoInternetActivity;
import com.db.easylearning.NotificationActivity;
import com.db.easylearning.R;
import com.db.easylearning.TongueTwisterActivity;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.CustomProgressBar;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LearnFragment extends Fragment {
    CardView cardTongueTwist, cardEnglishTips, cardDailyConversation, cardBasicGrammar, cardHRInterview;
    private AdView mAdView;
    SharedPreferences sharedPreferences;
    private String showAds, availabilityStatus, personId, personGender, personName;
    Switch switchStatus;
    TextView txtModeStatus;
    private Dialog dialogProgress;
    CustomProgressBar customProgressBar;
    private ImageView imgNotification;
    private CardView cardNotificationCount;
    private TextView txtNotiCount;

    String Tag = "planL", strNotiCount;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn, container, false);
        cardTongueTwist = view.findViewById(R.id.cardTongueTwist);
        cardEnglishTips = view.findViewById(R.id.cardEnglishTips);
        cardDailyConversation = view.findViewById(R.id.cardDailyConversation);
        cardBasicGrammar = view.findViewById(R.id.cardBasicGrammar);
        cardHRInterview = view.findViewById(R.id.cardHrInterview);
        switchStatus = view.findViewById(R.id.switchStatus);
        txtModeStatus = view.findViewById(R.id.txtModeStatus);
        cardNotificationCount = view.findViewById(R.id.cardNotificationCount);
        cardNotificationCount.setVisibility(View.GONE);
        txtNotiCount = view.findViewById(R.id.txtNotiCount);
        mAdView = view.findViewById(R.id.adView);
        imgNotification = view.findViewById(R.id.imgNotification);
        customProgressBar = new CustomProgressBar();

        sharedPreferences = getActivity().getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
        showAds = sharedPreferences.getString(Preferences.showAds, Preferences.DEFAULT);
        availabilityStatus = sharedPreferences.getString(Preferences.availabilityStatus, Preferences.DEFAULT);
        personId = sharedPreferences.getString(Preferences.userId, Preferences.DEFAULT);
        personName = sharedPreferences.getString(Preferences.userName, Preferences.DEFAULT);
        personGender = sharedPreferences.getString(Preferences.userGender, Preferences.DEFAULT);

        setOnlineStatus();
        if (showAds.equals("no")) {
            mAdView.setVisibility(View.GONE);
        } else {
            mAdView.setVisibility(View.VISIBLE);
        }

        getuserDetails();


        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                super.onAdFailedToLoad(adError);
                mAdView.loadAd(adRequest);
                Log.d("PlanC", "Add loading failed :" + adError.getMessage());
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });


        addClickListeners(view);
        return view;


    }

    private void addClickListeners(View view) {

        cardTongueTwist.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), TongueTwisterActivity.class);
            startActivity(intent);
        });

        cardEnglishTips.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), EnglishTipsActivity.class);
            startActivity(intent);
        });

        cardDailyConversation.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), DailyConversationActivity.class);
            startActivity(intent);
        });

        cardBasicGrammar.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), BasicGrammarActivity.class);
            startActivity(intent);
        });

        cardHRInterview.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), HRInterviewActivity.class);
            startActivity(intent);
        });


        switchStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchStatus.isChecked()) {
                    availabilityStatus = "1";
                    availabilityStatusFun();
                } else {
                    availabilityStatus = "0";
                    availabilityStatusFun();
                }
            }
        });

        imgNotification.setOnClickListener(v -> {
            cardNotificationCount.setVisibility(View.GONE);
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        });
    }

    private void availabilityStatusFun() {
        customProgressBar.showProgress(getActivity());

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, URLs.availabilityStatus, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    switch (status) {
                        case 0: {
                            customProgressBar.hideProgress();
                            switchStatus.setChecked(false);
                            txtModeStatus.setText("Offline");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Preferences.availabilityStatus, "0");
                            editor.apply();

                            break;
                        }
                        case 1: {
                            customProgressBar.hideProgress();
                            switchStatus.setChecked(true);
                            txtModeStatus.setText("Online");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Preferences.availabilityStatus, "1");
                            editor.apply();
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
                customProgressBar.hideProgress();
                SwitchNC();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", personId);
                params.put("user_name", personName);
                params.put("gender", personGender);
                params.put("status", availabilityStatus);

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

    private void setOnlineStatus() {
        if (availabilityStatus.equals("1")) {
            switchStatus.setChecked(true);
            txtModeStatus.setText("Online");
        } else {
            switchStatus.setChecked(false);
            txtModeStatus.setText("Offline");
        }
    }

    private void getuserDetails() throws NullPointerException {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, URLs.userDetails, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    switch (status) {
                        case 0: {
                            break;
                        }
                        case 1: {

                            JSONObject data = respObj.getJSONObject("data");

                            strNotiCount = data.optString("noticount");

                            setNotificationCount(strNotiCount);

                            break;
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(Tag, e.getMessage());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                customProgressBar.hideProgress();
                SwitchNC();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", personId);

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
        Intent i = new Intent(getActivity(), NoInternetActivity.class);
        getActivity().overridePendingTransition(0, 0);
        startActivity(i);
        getActivity().overridePendingTransition(0, 0);
    }

    private void setNotificationCount(String strNotiCount) {
        Log.d(Tag, "Noti Count: " + strNotiCount);
        if (!strNotiCount.equals("0")) {
            cardNotificationCount.setVisibility(View.VISIBLE);
            txtNotiCount.setText(strNotiCount);
        }
    }
}