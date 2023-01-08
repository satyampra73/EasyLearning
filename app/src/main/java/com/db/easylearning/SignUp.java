package com.db.easylearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.db.easylearning.voicecalling.SinchLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteGender;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    private TextInputLayout txtInputLayoutMobile;
    private String mobileNo,inviteCode,fcmToken;
    private EditText etName, etMobile;
    private Button btnSignUp;
    private ProgressBar progressBar;
    String genderPos;
    private SharedPreferences sharedPreferences;
    private TextView txtInviteCode;
    private LinearLayout ltInviteCode;
    private EditText etInviteCode;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        autoCompleteGender = findViewById(R.id.autoCompleteGender);
        txtInputLayoutMobile = findViewById(R.id.txtInputLayoutMobile);
        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.progressBar);
        txtInviteCode=findViewById(R.id.txtInviteCode);
        etInviteCode=findViewById(R.id.etInviteCode);
        ltInviteCode=findViewById(R.id.ltInviteCode);
        sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);


        arrayList = new ArrayList<>();
        arrayList.add("Male");
        arrayList.add("Female");
        mobileNo = getIntent().getStringExtra("mobile");

        etMobile.setText(mobileNo);
        generateFCM();

        arrayAdapter = new ArrayAdapter<>(SignUp.this, android.R.layout.simple_list_item_1, arrayList);
        autoCompleteGender.setAdapter(arrayAdapter);

        autoCompleteGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtInviteCode.setOnClickListener(v -> {
            ltInviteCode.setVisibility(View.VISIBLE);
            txtInviteCode.setVisibility(View.GONE);
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etInviteCode.getText().toString().isEmpty()){
                    inviteCode=etInviteCode.getText().toString().trim();
                }
                else {
                    inviteCode="";
                }
//                Log.d("PlanC",inviteCode);

                if (etName.getText().toString().trim().isEmpty()) {
                    AppConstants.showToast(SignUp.this, "Please Enter Name");
                } else if (etName.getText().toString().length() < 4) {
                    AppConstants.showToast(SignUp.this, "Name Should Have At Least 4 Characters");
                } else if (autoCompleteGender.getText().toString().isEmpty()) {
                    AppConstants.showToast(SignUp.this, "Please Select an Gender");
                } else {
                    if (autoCompleteGender.getText().toString().compareTo("Male")==0){
                        genderPos="0";
                    }
                    else {
                        genderPos="1";
                    }
                    signUpWithMobile();
                }
            }
        });

    }

    private void signUpWithMobile() {
        btnSignUp.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Log.d("PlanC",etName.getText().toString()+"\n"+
                inviteCode+genderPos+etMobile.getText().toString());
        RequestQueue queue = Volley.newRequestQueue(SignUp.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.SignUp, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    switch (status) {
                        case 0: {
                            progressBar.setVisibility(View.GONE);
                            btnSignUp.setVisibility(View.VISIBLE);
                            Toast.makeText(SignUp.this, respObj.optString("msg"), Toast.LENGTH_SHORT).show();

                            break;
                        }
                        case 1: {
                            progressBar.setVisibility(View.GONE);
                            btnSignUp.setVisibility(View.VISIBLE);
                            JSONObject data = respObj.getJSONObject("user_data");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Preferences.userName, data.getString("name"));
                            editor.putString(Preferences.userId,data.getString("id"));
                            editor.putString(Preferences.userEmail, data.getString("email"));
                            editor.putString(Preferences.userMobile, data.getString("mobile"));
                            editor.putString(Preferences.userGender, data.getString("gender"));
                            editor.apply();

                            Intent intent = new Intent(SignUp.this, SinchLoginActivity.class);
                            intent.putExtra("id",data.getString("id"));
                            startActivity(intent);
                            finish();
                            break;
                        }
                        default:{
                            AppConstants.showToast(SignUp.this,"Default Condition");
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
                progressBar.setVisibility(View.GONE);
                btnSignUp.setVisibility(View.VISIBLE);
                SwitchNC();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", etName.getText().toString());
                params.put("mobile", etMobile.getText().toString());
                params.put("gender", genderPos);
                params.put("signup_inv_code",inviteCode);
                params.put("fcm_id",fcmToken);
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
        Intent i = new Intent(SignUp.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    private void generateFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        fcmToken = task.getResult();
                        Log.d("planM", "fcmToken from firebase: " + fcmToken);

                    }
                });
    }
}