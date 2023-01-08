package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.easylearning.apphelper.AppConstants;
import com.db.easylearning.apphelper.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WebViewData extends AppCompatActivity {
    private String value;
    private TextView toolbar_title, simpleTextView;
    private Dialog dialogProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_data);


        toolbar_title = findViewById(R.id.toolbar_title);
        simpleTextView = findViewById(R.id.simpleTextView);


        value = getIntent().getStringExtra("value");
        if (value == null) {

        } else {
            switch (value) {
                case "1": {
                    toolbar_title.setText("Terms & Condition");
                    getTermsAndCondition();
                    break;
                }
                case "2": {
                    toolbar_title.setText("Privacy Policy");
                    getPrivacyPolicy();
                    break;
                }
                case "3": {
                    toolbar_title.setText("About Speakify");
                    getAboutSpeakify();
                    break;
                }

                default: {
                    //do nothing here
                }
            }
        }

    }

    private void getAboutSpeakify() {
        AppConstants appConstatnts = new AppConstants(dialogProgress);
        appConstatnts.openDialog("Loading Please Wait", WebViewData.this);
        RequestQueue queue = Volley.newRequestQueue(WebViewData.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.AboutUs, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    JSONArray jsonArray = respObj.getJSONArray("data");

                    if (respObj.optInt("status") == 1) {
                       appConstatnts.closeDialog();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(0);
                            simpleTextView.setText(Html.fromHtml(details.getString("about_us")));
                        }

                    } else {
                        AppConstants.showToast(WebViewData.this, "There is No Data");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // method to handle errors.
                appConstatnts.closeDialog();
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

    private void getPrivacyPolicy() {
        AppConstants appConstatnts = new AppConstants(dialogProgress);
        appConstatnts.openDialog("Loading Please Wait", WebViewData.this);
        RequestQueue queue = Volley.newRequestQueue(WebViewData.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.PrivacyPolicy, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    JSONArray jsonArray = respObj.getJSONArray("data");

                    if (respObj.optInt("status") == 1) {
                      appConstatnts.closeDialog();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(0);
                            simpleTextView.setText(Html.fromHtml(details.getString("privacy_policy")));
                        }

                    } else {
                        AppConstants.showToast(WebViewData.this, "There is No Data");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // method to handle errors.
                appConstatnts.closeDialog();
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

    private void getTermsAndCondition() {
        AppConstants appConstatnts = new AppConstants(dialogProgress);
        appConstatnts.openDialog("Loading Please Wait", WebViewData.this);
        RequestQueue queue = Volley.newRequestQueue(WebViewData.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.TermsAndCondition, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    JSONArray jsonArray = respObj.getJSONArray("data");

                    if (respObj.optInt("status") == 1) {
                        appConstatnts.closeDialog();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject details = jsonArray.getJSONObject(0);
                            simpleTextView.setText(Html.fromHtml(details.getString("terms")));
                        }

                    } else {
                        AppConstants.showToast(WebViewData.this, "There is No Data");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                appConstatnts.closeDialog();
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


    public void click(View view) {
        finish();
    }


    private void SwitchNC() {
        Intent i = new Intent(WebViewData.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
}