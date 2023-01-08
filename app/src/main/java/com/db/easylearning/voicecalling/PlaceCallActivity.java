package com.db.easylearning.voicecalling;

import androidx.core.app.ActivityCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.R;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.Preferences;
import com.db.easylearning.apphelper.URLs;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PlaceCallActivity extends BaseActivity {

    private Button mCallButton;
    private EditText mCallName;
    SharedPreferences sharedPreferences;
    private String personGender,userName,userId;
    int terms=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_call);
        mCallName = (EditText) findViewById(R.id.callName);
        mCallButton = (Button) findViewById(R.id.callButton);
        mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(buttonClickListener);
        sharedPreferences=getSharedPreferences(Preferences.PREFER_NAME,MODE_PRIVATE);
        userId=sharedPreferences.getString(Preferences.userId,Preferences.DEFAULT);
        Log.d("planC","userId: "+userId);

        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(buttonClickListener);
        personGender = "2";
//      AppConstants.showToast(PlaceCallActivity.this, personGender);
        callRandomUsers();



    }

    @Override
    protected void onServiceConnected() {
        mCallButton.setEnabled(true);
    }

    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    private void callButtonClicked(String user) {
        if (user.isEmpty()) {
            Toast.makeText(this, "No User to call", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Call call = getSinchServiceInterface().callUser(user);
            if (call == null) {
                // Service failed for some reason, show a Toast and abort
//                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before "
//                        + "placing a call.", Toast.LENGTH_LONG).show();
                Log.d("planC_Call","Service is not started try stopping the service and starting it again before placing call");
                finish();

                return;
            }
            String callId = call.getCallId();
            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callId);
            callScreen.putExtra("calling","1");
            startActivity(callScreen);
            finish();
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.callButton:
                    callButtonClicked(userName);
                    break;

                case R.id.stopButton:
                    stopButtonClicked();
                    break;

            }
        }
    };


    private void callRandomUsers() {
        openDialog();

        RequestQueue queue = Volley.newRequestQueue(PlaceCallActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.callRandomUsers, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);

                    int status = respObj.optInt("status");

                    switch (status) {
                        case 0: {
                            AppConstants.showToast(PlaceCallActivity.this, "No user Found");
                            finish();
                            break;

                        }
                        case 1: {

                            JSONObject idObj=respObj.getJSONObject("data");

                            userName=idObj.getString("user_id");

//                            userName="94";

                            if (userName.equals(userId)){
                                terms++;
                                Log.d("terms","number of terms: "+String.valueOf(terms));
                                if (terms<4){
                                    callRandomUsers();
                                }
                                else {
                                    finish();
                                    AppConstants.showToast(PlaceCallActivity.this,"No User Found");
                                }

                            }
                            else {
                                callButtonClicked(userName);
                                Log.d("planC","Calling to user"+ userName);
                            }

                            break;
                        }
                        default: {
                            //do nothing
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                  Log.d("planC","jsonFailear: "+e.getLocalizedMessage());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(PlaceCallActivity.this, "Something went wrong\n"+error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("planC","ResponseError: "+error.getLocalizedMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("gender", personGender);
                params.put("user_id",userId);
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


    private void openDialog() {
        final Dialog dialog = new Dialog(PlaceCallActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.search_user_animated_dialog);

        dialog.show();
    }
}