package com.db.easylearning.voicecalling;

import static com.db.easylearning.voicecalling.SinchService.APP_KEY;
import static com.db.easylearning.voicecalling.SinchService.APP_SECRET;
import static com.db.easylearning.voicecalling.SinchService.ENVIRONMENT;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.db.easylearning.HomeActivity;
import com.db.easylearning.R;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushTokenRegistrationCallback;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.UserController;
import com.sinch.android.rtc.UserRegistrationCallback;

public class SinchLoginActivity extends BaseActivity implements SinchService.StartFailedListener, PushTokenRegistrationCallback, UserRegistrationCallback {

    private ProgressDialog mSpinner;
    private String sinchUser;
    private String mUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sinch_login);

        sinchUser = getIntent().getStringExtra("id");

        Log.d("planS","sinchUser: "+sinchUser);



        showSpinner();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loginClicked();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onServiceConnected() {
        if (getSinchServiceInterface().isStarted()) {
            openPlaceCallActivity();
        } else {
            getSinchServiceInterface().setStartListener(this);
        }
    }

    @Override
    protected void onPause() {
        dismissSpinner();
        super.onPause();
    }

    @Override
    public void onFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        dismissSpinner();
    }

    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }

    private void startClientAndOpenPlaceCallActivity() {
        // start Sinch Client, it'll result onStarted() callback from where the place call activity will be started
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient();
            showSpinner();
        }
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

    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, HomeActivity.class);
        startActivity(mainActivity);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    private void dismissSpinner() {
        if (mSpinner != null) {
            mSpinner.dismiss();
            mSpinner = null;
        }
    }

    @Override
    public void onUserRegistrationFailed(SinchError sinchError) {
        dismissSpinner();
        Toast.makeText(this, "Registration failed!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserRegistered() {
        // Instance is registered, but we'll wait for another callback, assuring that the push token is
        // registered as well, meaning we can receive incoming calls.
    }

    @Override
    public void onPushTokenRegistered() {
        dismissSpinner();
        startClientAndOpenPlaceCallActivity();
    }

    @Override
    public void onPushTokenRegistrationFailed(SinchError sinchError) {
        dismissSpinner();
        Toast.makeText(this, "Push token registration failed - incoming calls can't be received!", Toast.LENGTH_LONG).show();
    }

    // The most secure way is to obtain the credentials from the backend,
    // since storing the Application Secret in the app is not safe.
    // Following code demonstrates how the JWT that serves as credential should be created,
    // provided the Application Key (APP_KEY), Application Secret (APP_SECRET) and User ID.

    @Override
    public void onCredentialsRequired(ClientRegistration clientRegistration) {
        // NB: This implementation just emulates what should be an async procedure, with JWT.create() being
        // run on your backend.
        clientRegistration.register(JWT.create(APP_KEY, APP_SECRET,mUserId));
    }
}
