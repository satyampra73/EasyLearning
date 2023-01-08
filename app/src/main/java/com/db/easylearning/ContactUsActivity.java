package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContactUsActivity extends AppCompatActivity {
    private EditText etMessage,etName,etEmail;
    private Button btnSend;
    private SharedPreferences sharedPreferences;
    private String Name, personId,personEmail,personMobile;
    private Dialog dialogProgress;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        etMessage=findViewById(R.id.etMessage);
        etName=findViewById(R.id.etName);
        btnSend=findViewById(R.id.btnSend);
        etEmail=findViewById(R.id.etEmail);

        sharedPreferences =getSharedPreferences("MyData", MODE_PRIVATE);
        personId = sharedPreferences.getString(Preferences.userId, Preferences.DEFAULT);
        Name = sharedPreferences.getString(Preferences.userName, Preferences.DEFAULT);
        personEmail = sharedPreferences.getString(Preferences.userEmail, Preferences.DEFAULT);
        personMobile = sharedPreferences.getString(Preferences.userMobile, Preferences.DEFAULT);
        etName.setText(Name);

        if (personMobile.equals("N/A")) {
           etEmail.setText(personEmail);
           etEmail.requestFocus();
           etEmail.setCursorVisible(false);
           etEmail.setFocusable(false);
        }


        btnSend.setOnClickListener(View->{
            if (etMessage.getText().toString().trim().isEmpty()){
                AppConstants.showToast(ContactUsActivity.this,"Message Field Can't be Empty");
            }
            else if(etEmail.getText().toString().trim().isEmpty()){
                AppConstants.showToast(ContactUsActivity.this,"Please Enter your Email To Proceed");
            }
            else {
                sendMessage();
            }
        });

    }

    private void sendMessage() {
        openDialog("Sending Please Wait");

        RequestQueue queue = Volley.newRequestQueue(ContactUsActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.ContactUs, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    switch (status) {
                        case 0: {
                            dialogProgress.dismiss();
                            AppConstants.showToast(ContactUsActivity.this,"Message Could not Sent");

                            break;
                        }
                        case 1: {
                            dialogProgress.dismiss();
                          AppConstants.showToast(ContactUsActivity.this,"Message Sent Successfully");
                          finish();
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
                params.put("user_id", personId);
                params.put("message", etMessage.getText().toString());
                params.put("email", etEmail.getText().toString());

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

    public void click(View view) {
        finish();
    }

    private void openDialog(String text) {
        dialogProgress = new Dialog(ContactUsActivity.this);
        dialogProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogProgress.setCancelable(false);
        dialogProgress.setContentView(R.layout.progress_dialog);
        TextView txtProgressDialog = dialogProgress.findViewById(R.id.txtProgressDialog);
        txtProgressDialog.setText(text);

        dialogProgress.show();
    }

    private void SwitchNC() {
        Intent i = new Intent(ContactUsActivity.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
}