package com.db.easylearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.db.easylearning.voicecalling.SinchLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class OTPActivity extends AppCompatActivity {

    private OtpTextView otpTextView;
    private Button btnVerify;
    private String mobileNo, enteredOtp;
    private String verificationId;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    TextView txtResendOtp;

    CustomProgressBar customProgressBar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);


        initialization();
        addListeners();
        otpSend();

    }

    private void initialization() {
        otpTextView = findViewById(R.id.otp_view);
        btnVerify = findViewById(R.id.btnVerify);
        progressBar = findViewById(R.id.progressBar);
        txtResendOtp = findViewById(R.id.txtResendOTP);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);

        mobileNo = getIntent().getStringExtra("mobile");
        verificationId = getIntent().getStringExtra("verificationId");

        customProgressBar = new CustomProgressBar();
        Log.d("PlanC", "Mobile No(otp): " + mobileNo + "verificationOtp " + verificationId);


    }

    private void addListeners() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otpTextView.getOTP().isEmpty()) {
                    Toast.makeText(OTPActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else if (enteredOtp.equals("")) {
                    Toast.makeText(OTPActivity.this, "Please Enter Complete OTP", Toast.LENGTH_SHORT).show();
                } else {

                    if (verificationId != null) {
                        String code = enteredOtp;

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                        FirebaseAuth
                                .getInstance()
                                .signInWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.VISIBLE);
                                            btnVerify.setVisibility(View.INVISIBLE);
                                            logInWithMobile(mobileNo);

                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            btnVerify.setVisibility(View.VISIBLE);
                                            Toast.makeText(OTPActivity.this, "OTP is not Valid!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }

            }
        });

        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                enteredOtp = "";
            }

            @Override
            public void onOTPComplete(String otp) {

                enteredOtp = otp;
                // fired when user has entered the OTP fully.

            }
        });

        txtResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(mobileNo,mResendToken);
            }
        });

    }

    private void logInWithMobile(String mobileNo) {

        RequestQueue queue = Volley.newRequestQueue(OTPActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.Login, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    switch (status) {
                        case 0: {
                            progressBar.setVisibility(View.GONE);
                            btnVerify.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(OTPActivity.this, SignUp.class);
                            intent.putExtra("mobile", mobileNo);
                            startActivity(intent);
                            finish();
                            break;
                        }
                        case 1: {
                            progressBar.setVisibility(View.GONE);
                            btnVerify.setVisibility(View.VISIBLE);
                            JSONObject data = respObj.getJSONObject("User details");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Preferences.userName, data.getString("name"));
                            editor.putString(Preferences.userEmail, data.getString("email"));
                            editor.putString(Preferences.userId, data.getString("id"));
                            editor.putString(Preferences.userMobile, data.getString("mobile"));
                            editor.putString(Preferences.userGender, data.getString("gender"));
                            editor.apply();

                            Intent intent = new Intent(OTPActivity.this, SinchLoginActivity.class);
                            intent.putExtra("id",data.getString("id"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            break;
                        }
                        default: {
                            progressBar.setVisibility(View.GONE);
                            btnVerify.setVisibility(View.VISIBLE);
                            Toast.makeText(OTPActivity.this, respObj.optString("msg"), Toast.LENGTH_SHORT).show();
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
                params.put("mobile", mobileNo);

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
        Intent i = new Intent(OTPActivity.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    private void otpSend() {
        customProgressBar.showProgress(OTPActivity.this);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
             customProgressBar.hideProgress();
                Toast.makeText(OTPActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Otp", e.getLocalizedMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verification_Id,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                customProgressBar.hideProgress();
                Toast.makeText(OTPActivity.this, "OTP is successfully send.", Toast.LENGTH_SHORT).show();
                verificationId = verification_Id;
                mResendToken=token;

            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mobileNo)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        customProgressBar.showProgress(OTPActivity.this);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                customProgressBar.hideProgress();
                Toast.makeText(OTPActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Otp", e.getLocalizedMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verification_Id,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                customProgressBar.hideProgress();
                Toast.makeText(OTPActivity.this, "OTP is successfully send.", Toast.LENGTH_SHORT).show();
                verificationId = verification_Id;
                mResendToken=token;

            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
}