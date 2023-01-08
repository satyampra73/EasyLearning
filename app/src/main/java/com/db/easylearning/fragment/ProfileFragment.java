package com.db.easylearning.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.db.easylearning.ContactUsActivity;
import com.db.easylearning.LoginActivity;
import com.db.easylearning.NoInternetActivity;
import com.db.easylearning.PaymentHistoryActivity;
import com.db.easylearning.R;
import com.db.easylearning.RemoveAdsActivity;
import com.db.easylearning.ShareActivity;
import com.db.easylearning.WebViewData;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {
    Button btnSignOut;
    ImageView imgEditName, imgPlanImage;
    TextView txtPlanName, txtInviteCode;
    GoogleSignInClient mGoogleSignInClient;
    RelativeLayout ltRemoveAds, ltShareApp, ltRateApp, ltAboutSpeakify, ltContactUs, ltPrivacyPolicy, ltTermsAndCondition, ltPaymentHistory;
    String url = "https://www.google.co.in/", personName, personEmail, personMobile, showAds, planImage, TAG = "PlanC", planName, myCode,userId;
    TextView txtName, txtProfileEmailORMobile;
    SharedPreferences sharedPreferences;
    private AdView mAdView;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnSignOut = view.findViewById(R.id.btnSignOut);
        ltShareApp = view.findViewById(R.id.ltShareApp);
        ltRateApp = view.findViewById(R.id.ltRateApp);
        imgEditName = view.findViewById(R.id.imgEditName);
        txtInviteCode = view.findViewById(R.id.txtInviteCode);
        ltAboutSpeakify = view.findViewById(R.id.ltAboutSpeakify);
        ltContactUs = view.findViewById(R.id.ltContactUs);
        ltPrivacyPolicy = view.findViewById(R.id.ltPrivacyPolicy);
        ltTermsAndCondition = view.findViewById(R.id.ltTermsAndCondition);
        txtProfileEmailORMobile = view.findViewById(R.id.txtProfileEmailORMobile);
        ltRemoveAds = view.findViewById(R.id.ltRemoveAds);
        txtName = view.findViewById(R.id.txtName);
        ltPaymentHistory = view.findViewById(R.id.ltPaymentHistory);
        mAdView = view.findViewById(R.id.adView);
        txtPlanName = view.findViewById(R.id.txtPlanName);
        imgPlanImage = view.findViewById(R.id.imgPlanImage);
        txtPlanName = view.findViewById(R.id.txtPlanName);

        sharedPreferences = getActivity().getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
        showAds = sharedPreferences.getString(Preferences.showAds, Preferences.DEFAULT);
        personName = sharedPreferences.getString(Preferences.userName, Preferences.DEFAULT);
        personEmail = sharedPreferences.getString(Preferences.userEmail, Preferences.DEFAULT);
        personMobile = sharedPreferences.getString(Preferences.userMobile, Preferences.DEFAULT);
        planImage = sharedPreferences.getString(Preferences.planImage, Preferences.DEFAULT);
        planName = sharedPreferences.getString(Preferences.planName, Preferences.DEFAULT);
        myCode = sharedPreferences.getString(Preferences.MyInvCode, Preferences.DEFAULT);
        userId = sharedPreferences.getString(Preferences.userId, Preferences.DEFAULT);

        txtInviteCode.setText(myCode);
        Log.e(TAG, planImage);
//        Picasso.get().load(URLs.ForImageURL+planImage).into(imgPlanImage);
        Glide.with(getActivity()).load(URLs.ForImageURL + planImage).into(imgPlanImage);
        txtPlanName.setText(planName);


        if (showAds.equals("no")) {
            mAdView.setVisibility(View.GONE);
        } else {
            mAdView.setVisibility(View.VISIBLE);
        }

        txtName.setText(personName);
        Log.d("planC", "email: " +personEmail);
        Log.d("planC", "mobile: " +personMobile);
        if (personMobile.equals("N/A")) {
            txtProfileEmailORMobile.setText(personEmail);
        } else {
            txtProfileEmailORMobile.setText(personMobile);
        }
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

        imgEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        ltShareApp.setOnClickListener(View -> {

          Intent intent=new Intent(getActivity(), ShareActivity.class);
          startActivity(intent);
        });
        ltRateApp.setOnClickListener(View -> {
            Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
            }
        });
        ltAboutSpeakify.setOnClickListener(View -> {
            Intent intent = new Intent(getActivity(), WebViewData.class);
            intent.putExtra("value", "3");
            intent.putExtra("view", url);
            startActivity(intent);
        });
        ltContactUs.setOnClickListener(View -> {
            Intent intent = new Intent(getActivity(), ContactUsActivity.class);
            startActivity(intent);
        });

        ltPrivacyPolicy.setOnClickListener(View -> {
            Intent intent = new Intent(getActivity(), WebViewData.class);
            intent.putExtra("value", "2");
            intent.putExtra("view", url);
            startActivity(intent);
        });

        ltTermsAndCondition.setOnClickListener(View -> {
            Intent intent = new Intent(getActivity(), WebViewData.class);
            intent.putExtra("value", "1");
            intent.putExtra("view", url);
            startActivity(intent);
        });
        ltPaymentHistory.setOnClickListener(View -> {

            Intent intent = new Intent(getActivity(), PaymentHistoryActivity.class);
            startActivity(intent);
        });

        ltRemoveAds.setOnClickListener(View -> {
            Intent intent = new Intent(getActivity(), RemoveAdsActivity.class);
            startActivity(intent);
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getActivity().getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;

    }

    private void openDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.name_edit_dialog);

        EditText etEdit = dialog.findViewById(R.id.etEdit);
        ProgressBar pbBtnEditProgress = dialog.findViewById(R.id.pbBtnEditProgress);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(view -> {

            if (etEdit.getText().toString().isEmpty()){
                AppConstants.showToast(getActivity(),"Please enter Name");
            }
            else {
                txtName.setText(etEdit.getText().toString());
                UpdateName(etEdit.getText().toString(),dialog,pbBtnEditProgress,btnUpdate);
            }


        });

        dialog.show();
    }

    private void UpdateName(String name, Dialog dialog, ProgressBar pbBtnEditProgress, Button btnUpdate) {

        pbBtnEditProgress.setVisibility(View.VISIBLE);
        btnUpdate.setVisibility(View.GONE);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, URLs.UpdateProfile, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    switch (status) {

                        case 0: {
                            pbBtnEditProgress.setVisibility(View.GONE);
                            btnUpdate.setVisibility(View.VISIBLE);
                            AppConstants.showToast(getActivity(),respObj.optString("msg"));
                            break;
                        }
                        case 1: {
                            pbBtnEditProgress.setVisibility(View.GONE);
                            btnUpdate.setVisibility(View.VISIBLE);
                            SharedPreferences.Editor editor= sharedPreferences.edit();
                            editor.putString(Preferences.userName,name);
                            dialog.dismiss();
                            editor.apply();

                            break;
                        }
                        default:{
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
                SwitchNC();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("user_name", name);

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

}