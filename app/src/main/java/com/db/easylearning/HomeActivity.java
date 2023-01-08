package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.CustomProgressBar;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.fragment.HomeFragment;
import com.db.easylearning.fragment.LearnFragment;
import com.db.easylearning.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    SharedPreferences sharedPreferences;
    CustomProgressBar customProgressBar;

    String userId, userName, personGender, dateTime;
    public static String availStatus, flag = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
        personGender = sharedPreferences.getString(Preferences.userGender, Preferences.DEFAULT);
        userName = sharedPreferences.getString(Preferences.userName, Preferences.DEFAULT);
        userId = sharedPreferences.getString(Preferences.userId, Preferences.DEFAULT);
        availStatus = sharedPreferences.getString(Preferences.availabilityStatus, Preferences.DEFAULT);
        customProgressBar = new CustomProgressBar();

        Log.d("planA", "formatted date and time: " + AppConstants.getCurrentDateAndTime());

        if (availStatus.equals("1")) {
            availabilityStatusFun();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String to_id = bundle.getString("to_id");
            String status = bundle.getString("status");
            String type = bundle.getString("type");
            if (to_id != null) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (to_id != null) {
                            try {
                                callHistory(to_id, status,type);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    }
                }, 1000);
            }


        }


        bottomNavigationView = findViewById(R.id.bottomNavigation);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, new HomeFragment());
        transaction.commit();
        bottomNavigationView.setSelectedItemId(R.id.idHome);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();//do nothing
            if (itemId == R.id.idHome) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.idLearn) {
                replaceFragment(new LearnFragment());
            } else if (itemId == R.id.idProfile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });
    }

    private void callHistory(String to_id, String status,String type) throws Exception {
        Log.d("planHome", "to_id: " + to_id);
        Log.d("planHome", "planHome: " + status);
        Log.d("planHome", "planHome: " + type);

        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.CallHistory, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");
                    Log.d("planA", "FromHomeActivity: " + response);

                    switch (status) {
                        case 0: {
                            //case0
                            break;
                        }
                        case 1: {
                            //case1
                            Log.d("home", "case1 done");
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

                Log.d("planA", "FromHomeActivity: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("from_user_id", userId);
                params.put("to_user_id", to_id);
                params.put("status", status);
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

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    private void availabilityStatusFun() {
        customProgressBar.showProgress(HomeActivity.this);

        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.availabilityStatus, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");
                    Log.d("planA", "FromHomeActivity: " + response);

                    switch (status) {
                        case 0: {
                            customProgressBar.hideProgress();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Preferences.availabilityStatus, "0");
                            editor.apply();
                            availStatus = "0";
                            break;
                        }
                        case 1: {
                            customProgressBar.hideProgress();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Preferences.availabilityStatus, "1");
                            editor.apply();

                            availStatus = "1";
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
                Log.d("planA", "FromHomeActivity: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("user_name", userName);
                params.put("gender", personGender);
                params.put("status", "1");
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

    private void SwitchNC() {
        Intent i = new Intent(HomeActivity.this, NoInternetActivity.class);
        HomeActivity.this.overridePendingTransition(0, 0);
        startActivity(i);
        HomeActivity.this.overridePendingTransition(0, 0);
    }

}