package com.db.easylearning;

import static com.db.easylearning.voicecalling.SinchService.APP_KEY;
import static com.db.easylearning.voicecalling.SinchService.APP_SECRET;
import static com.db.easylearning.voicecalling.SinchService.ENVIRONMENT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.voicecalling.BaseActivity;
import com.db.easylearning.voicecalling.JWT;
import com.db.easylearning.voicecalling.SinchService;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushTokenRegistrationCallback;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.UserController;
import com.sinch.android.rtc.UserRegistrationCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends BaseActivity implements SinchService.StartFailedListener, PushTokenRegistrationCallback, UserRegistrationCallback {
    private SharedPreferences sharedPreferences;
    private String userEmail;
    RelativeLayout relativeLayout;
    //    private ProgressDialog mSpinner;
    private String sinchUser;
    String mUserId,availStatus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Log.d("planS", "onCreate");
        init();
        getKeyValues();

    }

    private void init() {
        relativeLayout = findViewById(R.id.rlLayout);
        sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
        userEmail = sharedPreferences.getString(Preferences.userEmail, Preferences.DEFAULT);
        sinchUser = sharedPreferences.getString(Preferences.userId, Preferences.DEFAULT);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Preferences.availabilityStatus,"1");
        editor.apply();
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    public void onFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStarted() {
        openHomeActivity();
    }

    private void loginClicked() {

        String username = sinchUser;
        getSinchServiceInterface().setUsername(username);

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        /*
            IMPORTANT!
            Make sure that the SinchClient is not started until the registration with the UserController is finished.
            See `onPushTokenRegistered()` callback from where we trigger chain of events that will start SinchClient.
         */

        mUserId = username;
        UserController uc = Sinch.getUserControllerBuilder()
                .context(getApplicationContext())
                .applicationKey(APP_KEY)
                .userId(mUserId)
                .environmentHost(ENVIRONMENT)
                .build();
        uc.registerUser(this, this);
    }

    private void openHomeActivity() {
        Intent mainActivity = new Intent(this, HomeActivity.class);
        mainActivity.putExtra("flag","1");
        startActivity(mainActivity);
        finish();
    }

    private void getKeyValues() {
        RequestQueue queue = Volley.newRequestQueue(SplashScreen.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.SERVICES, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("planC", "response for keys: " + response);
                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    switch (status) {
                        case 1: {
                            JSONObject dataObj = respObj.getJSONObject("data");


                            SinchService.setAppKey(dataObj.optString("key"));
                            SinchService.setAppSecret(dataObj.optString("value"));


                            if (sinchUser.equals("N/A")) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }, 3000);
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginClicked();
                                    }
                                }, 1500);
                            }

                            break;
                        }
                        case 0: {

                            AppConstants.showToast(SplashScreen.this, "Failed");
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

    private void startClientAndOpenPlaceCallActivity() {
        // start Sinch Client, it'll result onStarted() callback from where the place call activity will be started
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void SwitchNC() {
        Intent i = new Intent(SplashScreen.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }


    @Override
    public void onPushTokenRegistered() {

        startClientAndOpenPlaceCallActivity();
    }

    @Override
    public void onPushTokenRegistrationFailed(SinchError sinchError) {
        Toast.makeText(this, "Push token registration failed - incoming calls can't be received!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCredentialsRequired(ClientRegistration clientRegistration) {
        // NB: This implementation just emulates what should be an async procedure, with JWT.create() being
        // run on your backend.
        clientRegistration.register(JWT.create(APP_KEY, APP_SECRET, mUserId));
    }

    @Override
    public void onUserRegistered() {
        // Instance is registered, but we'll wait for another callback, assuring that the push token is
        // registered as well, meaning we can receive incoming calls.
    }

    @Override
    public void onUserRegistrationFailed(SinchError sinchError) {

        Toast.makeText(this, "Registration failed!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("planS", "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("planS", "onResume");


    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("planS", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("planS", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("planS", "onDestroy");
    }

}