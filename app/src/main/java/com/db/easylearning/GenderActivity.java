package com.db.easylearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GenderActivity extends AppCompatActivity {

    private LinearLayout ltMaleLayout, ltFemaleLayout;
    private TextView txtMale, txtFemale;
    private Button btnContinue;
    private Dialog dialogProgress;
    private String personName, personEmail;
    private SharedPreferences sharedPreferences;
    private String genderPos;
    private TextView txtInviteCode;
    private LinearLayout ltInviteCode;
    private String fcmToken,inviteCode="";
    private EditText etInviteCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);
        ltMaleLayout = findViewById(R.id.ltMaleLayout);
        ltFemaleLayout = findViewById(R.id.ltFemaleLayout);
        txtMale = findViewById(R.id.txtMale);
        txtFemale = findViewById(R.id.txtFemale);
        btnContinue = findViewById(R.id.btnContinue);
        txtInviteCode = findViewById(R.id.txtInviteCode);
        ltInviteCode = findViewById(R.id.ltInviteCode);
        etInviteCode=findViewById(R.id.etInviteCode);


        personName = getIntent().getStringExtra("name");
        personEmail = getIntent().getStringExtra("email");

        sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
        generateFCM();

        Log.d("PlanC","Fcm Token:"+fcmToken);

        ltMaleLayout.setOnClickListener(view -> {
            ltMaleLayout.setBackgroundColor(getResources().getColor(R.color.blue));
            ltFemaleLayout.setBackgroundColor(getResources().getColor(R.color.white));
            txtMale.setTextColor(getResources().getColor(R.color.white));
            txtFemale.setTextColor(getResources().getColor(R.color.black));
            btnContinue.setVisibility(View.VISIBLE);
            genderPos = "0";

        });

        ltFemaleLayout.setOnClickListener(view -> {
            ltMaleLayout.setBackgroundColor(getResources().getColor(R.color.white));
            ltFemaleLayout.setBackgroundColor(getResources().getColor(R.color.blue));
            txtFemale.setTextColor(getResources().getColor(R.color.white));
            txtMale.setTextColor(getResources().getColor(R.color.black));
            btnContinue.setVisibility(View.VISIBLE);
            genderPos = "1";

        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etInviteCode.getText().toString().isEmpty()){
                    inviteCode=etInviteCode.getText().toString().trim();
                }
                Log.d("PlanC",inviteCode);
                signUpUserWithEmail();
            }
        });

        txtInviteCode.setOnClickListener(v -> {
            ltInviteCode.setVisibility(View.VISIBLE);
            txtInviteCode.setVisibility(View.GONE);
        });

    }

    private void signUpUserWithEmail() {

        openDialog("SigningIn");
        RequestQueue queue = Volley.newRequestQueue(GenderActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.SignUp, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    switch (status) {
                        case 0: {
                            dialogProgress.dismiss();
                            Log.d("response",response);
                            Toast.makeText(GenderActivity.this, respObj.optString("msg"), Toast.LENGTH_SHORT).show();

                            break;
                        }
                        case 1: {
                            dialogProgress.dismiss();
                            JSONObject data = respObj.getJSONObject("user_data");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Preferences.userName, data.getString("name"));
                            editor.putString(Preferences.userEmail, data.getString("email"));
                            editor.putString(Preferences.userId, data.getString("id"));
                            editor.putString(Preferences.userGender, data.getString("gender"));
                            editor.apply();

                            Intent intent = new Intent(GenderActivity.this, SinchLoginActivity.class);
                            intent.putExtra("id",data.getString("id"));
                            startActivity(intent);
                            finish();
                            break;
                        }
                        default: {
                            AppConstants.showToast(GenderActivity.this, "Default Condition");
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
                dialogProgress.dismiss();
               SwitchNC();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", personName);
                params.put("email", personEmail);
                params.put("gender", genderPos);
                params.put("signup_inv_code", inviteCode);
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

    private void openDialog(String text) {
        dialogProgress = new Dialog(GenderActivity.this);
        dialogProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogProgress.setCancelable(false);
        dialogProgress.setContentView(R.layout.progress_dialog);
        TextView txtProgressDialog = dialogProgress.findViewById(R.id.txtProgressDialog);
        txtProgressDialog.setText(text);

        dialogProgress.show();
    }

    public void click(View view) {
        finish();
    }

    private void SwitchNC() {
        Intent i = new Intent(GenderActivity.this, NoInternetActivity.class);
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