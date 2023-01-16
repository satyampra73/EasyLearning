package com.db.easylearning.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.BuildConfig;
import com.db.easylearning.LoginActivity;
import com.db.easylearning.NoInternetActivity;
import com.db.easylearning.NotificationActivity;
import com.db.easylearning.R;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.CustomProgressBar;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.voicecalling.PlaceCallActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {
    LinearLayout ltCall;
    private AdView mAdView;
    SharedPreferences sharedPreferences;
    private String personName, personId, personGender, availabilityStatus, currentDate, endDate, planDuration, showAds, Tag = "planM", versionCode, strNotiCount, fcmToken;


    Switch switchStatus;
    private TextView txtModeStatus, txtUserName,txtText1,txtText2;
    private Dialog dialogProgress;
    LinearLayout ltAvailableSwitch;
    GoogleSignInClient mGoogleSignInClient;
    CustomProgressBar customProgressBar;
    private ImageView imgNotification;
    private CardView cardNotificationCount;
    private TextView txtNotiCount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        View view = inflater.inflate(R.layout.fragment_home, parent, false);

        generateFCM();
        init(view);

        return view;

    }


    private void init(View view) {
        switchStatus = view.findViewById(R.id.switchStatus);
        ltCall = view.findViewById(R.id.ltCall);
        mAdView = view.findViewById(R.id.adView);

        txtModeStatus = view.findViewById(R.id.txtModeStatus);
        txtUserName = view.findViewById(R.id.txtUserName);
        ltAvailableSwitch = view.findViewById(R.id.ltAvailableSwitch);
        customProgressBar = new CustomProgressBar();
        imgNotification = view.findViewById(R.id.imgNotification);
        cardNotificationCount = view.findViewById(R.id.cardNotificationCount);
        cardNotificationCount.setVisibility(View.GONE);
        txtNotiCount = view.findViewById(R.id.txtNotiCount);
        txtText1=view.findViewById(R.id.txtText1);
        txtText2=view.findViewById(R.id.txtText2);


        sharedPreferences = getActivity().getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);

        personId = sharedPreferences.getString(Preferences.userId, Preferences.DEFAULT);
        personName = sharedPreferences.getString(Preferences.userName, Preferences.DEFAULT);
        personGender = sharedPreferences.getString(Preferences.userGender, Preferences.DEFAULT);
        showAds = sharedPreferences.getString(Preferences.showAds, Preferences.DEFAULT);
        availabilityStatus = sharedPreferences.getString(Preferences.availabilityStatus, Preferences.DEFAULT);
        fcmToken=sharedPreferences.getString(Preferences.token,Preferences.DEFAULT);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        Log.d(Tag, "Show ads: " + showAds);
        Log.d(Tag, "online Status: " + availabilityStatus);
        Log.d("planH", "person Id: " + personId);

        if (showAds.equals("no")) {
            mAdView.setVisibility(View.VISIBLE);
        } else {
            mAdView.setVisibility(View.VISIBLE);
        }

        txtUserName.setText(personName);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        setOnlineStatus();
        getuserDetails();

        if (mAdView.getVisibility() == View.VISIBLE) {
            // Its visible
            Log.d("PlanC", "Its visible");
        } else {
            // Either gone or invisible
            Log.d("PlanC", "Its inVisible");
        }

        ltCall.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PlaceCallActivity.class);
            startActivity(intent);
        });

        imgNotification.setOnClickListener(v -> {
            cardNotificationCount.setVisibility(View.GONE);
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
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

    }

    private void getuserDetails() {
        Log.d(Tag,"fcmToken for userDetail "+fcmToken);
        customProgressBar.showProgress(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, URLs.userDetails, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    int availStatus;
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    switch (status) {
                        case 0: {
                            customProgressBar.hideProgress();
                            AppConstants.showToast(getActivity(), "user Details Not found");
                            SharedPreferences sp = getActivity().getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.clear();
                            editor.apply();
                            mGoogleSignInClient.signOut();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                            break;
                        }
                        case 1: {
                            customProgressBar.hideProgress();
                            JSONObject data = respObj.getJSONObject("data");
                            JSONObject referDetails = respObj.getJSONObject("reffer_earn_details");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Preferences.planName, data.optString("plan_name"));
                            editor.putString(Preferences.planImage, data.optString("plan_image"));
                            editor.putString(Preferences.planId, data.optString("plan_id"));
                            editor.putString(Preferences.purchaseDate, data.optString("start_date"));
                            editor.putString(Preferences.planDuration, data.optString("plan_duration"));
                            editor.putString(Preferences.availabilityStatus, data.optString("online_status"));
                            availStatus = data.optInt("online_status");
                            editor.putString(Preferences.wallet, data.optString("wallet"));
                            editor.putString(Preferences.planPrice, data.optString("plan_price"));
                            editor.putString(Preferences.MyInvCode, data.optString("my_inv_code"));
                            editor.putString(Preferences.FromReferPrice, referDetails.optString("from_reffer_price"));
                            editor.putString(Preferences.ToReferPrice, referDetails.optString("to_reffer_price"));

                            editor.apply();

                            //setting home text
                            txtText1.setText(data.optString("text1"));
                            txtText2.setText(data.optString("text2"));


                            versionCode = data.optString("version_code");
                            strNotiCount = data.optString("noticount");


                            Log.d("planCCode", versionCode);
                            int versionCodeInt = BuildConfig.VERSION_CODE;
                            if (versionCode.equals(String.valueOf(versionCodeInt))) {
                                Log.d("planCCode", "Matched");
                            } else {
                                openDialog();
                                Log.d("planCCode", "Not Matched");
                            }

                            switch (availStatus) {
                                case 0: {
                                    switchStatus.setChecked(false);
                                    txtModeStatus.setText("Offline");
                                }
                                case 1: {
                                    switchStatus.setChecked(true);
                                    txtModeStatus.setText("Online");
                                }
                                default: {
                                    //do nothing
                                }
                            }

                            planDuration = data.optString("plan_duration");
                            if (planDuration.isEmpty()) {
                                Log.d(Tag, "No Plan has been taken by user");
                                mAdView.setVisibility(View.VISIBLE);
                                editor.putString(Preferences.showAds, "yes");
                                editor.apply();

                            } else {
                                makeChanges(data.getString("start_date"), data.getString("plan_duration"));
                            }

                            setOnlineStatus();

                            setNotificationCount(strNotiCount);


                            break;
                        }
                        default: {
                            dialogProgress.dismiss();
                            Toast.makeText(getActivity(), respObj.optString("msg"), Toast.LENGTH_SHORT).show();

                            SharedPreferences sp = getActivity().getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.clear();
                            editor.apply();
                            mGoogleSignInClient.signOut();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
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
                params.put("fcm_id", fcmToken);


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

    private void setNotificationCount(String strNotiCount) {
        if (!strNotiCount.equals("0")) {
            cardNotificationCount.setVisibility(View.VISIBLE);
            txtNotiCount.setText(strNotiCount);
        }
    }


    private void makeChanges(String perDate, String planDur) {
        customProgressBar.showProgress(getActivity());
        availabilityStatus = sharedPreferences.getString(Preferences.availabilityStatus, Preferences.DEFAULT);

        endDate = getEndDate(perDate, planDur);
        try {
            compareCurrentAndEndDate(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setOnlineStatus() {
        Log.d("planC", "avail status" + availabilityStatus);
        if (availabilityStatus.equals("1")) {
            switchStatus.setChecked(true);
            txtModeStatus.setText("Online");
        } else {
            switchStatus.setChecked(false);
            txtModeStatus.setText("Offline");
        }
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
                params.put("offline_time", AppConstants.getCurrentDateAndTime());

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

    private void compareCurrentAndEndDate(String endDate) throws ParseException {

        customProgressBar.hideProgress();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        currentDate = sdf.format(new Date());
        Date cur = sdf.parse(currentDate);
        Date end = sdf.parse(endDate);
        if (cur.compareTo(end) > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Preferences.showAds, "yes");
            editor.apply();

            PlanCancle();
            mAdView.setVisibility(View.VISIBLE);
            //Expire

        } else if (cur.compareTo(end) <= 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Preferences.showAds, "no");
            editor.apply();
            //time Left or same Day
            mAdView.setVisibility(View.VISIBLE);

        }
    }

    private void PlanCancle() {
        customProgressBar.showProgress(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, URLs.planCancel, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    switch (status) {
                        case 0: {
                            customProgressBar.hideProgress();
                            Log.d(Tag, "Plan Could not be Cancelled");
                            break;
                        }
                        case 1: {
                            customProgressBar.hideProgress();
                            Log.d(Tag, "Plan Cancelled");
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
                Toast.makeText(getActivity(), "Something Went Wrong\n" + error.toString(), Toast.LENGTH_SHORT).show();
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

    private String getEndDate(String perDate, String planDur) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(Objects.requireNonNull(sdf.parse(perDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, Integer.parseInt(planDur));  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE

        return sdf.format(c.getTime());
    }

    private void SwitchNC() {
        Intent i = new Intent(getActivity(), NoInternetActivity.class);
        getActivity().overridePendingTransition(0, 0);
        startActivity(i);
        getActivity().overridePendingTransition(0, 0);
    }

    private void openDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.app_update_dialog);

        Button btnPlayStore = dialog.findViewById(R.id.btnPlayStore);
        ImageView imgClose = dialog.findViewById(R.id.imgClose);

        btnPlayStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getActivity().getPackageName(); // package name of the app
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void generateFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }


                        SharedPreferences.Editor editor= sharedPreferences.edit();
                        editor.putString(Preferences.token, task.getResult());
                        editor.apply();
                        Log.d("planM", "fcmToken from firebase from Fragment: " + fcmToken);


                    }
                });

    }

}