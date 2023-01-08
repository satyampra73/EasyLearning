package com.db.easylearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.yesterselga.countrypicker.CountryPicker;
import com.yesterselga.countrypicker.CountryPickerListener;
import com.yesterselga.countrypicker.Theme;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout countryImagell;
    private ImageView countryImage;
    private TextView countryCodeText,txtTerms,txtPrivacyPolicy;
    private String country_iso_code = "IN";
    private Button btnContinue;
    private EditText editMobile;
    private CardView cardGoogleLogin;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Dialog dialogProgress;
    private String personName;
    private String personEmail;
    static final int REQUEST_CODE = 123;
    String token;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        countryImagell = findViewById(R.id.countryimagell);
        countryImage = findViewById(R.id.countryimage);
        countryCodeText = findViewById(R.id.countryCodeText);
        btnContinue = findViewById(R.id.btnContinue);
        editMobile = findViewById(R.id.editmobile);
        cardGoogleLogin = findViewById(R.id.cardGoogleLogin);
        progressBar = findViewById(R.id.progressBar);
        txtTerms=findViewById(R.id.txtTerms);
        txtPrivacyPolicy=findViewById(R.id.txtPrivacyPolicy);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        cardGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);

//        token=sharedPreferences.getString(Preferences.token,Preferences.DEFAULT);
//        Toast.makeText(this, token, Toast.LENGTH_SHORT).show();

        txtTerms.setOnClickListener(v -> {
            Intent intent=new Intent(LoginActivity.this,WebViewData.class);
            intent.putExtra("value","1");
            startActivity(intent);
        });

        txtPrivacyPolicy.setOnClickListener(v -> {
            Intent intent=new Intent(LoginActivity.this,WebViewData.class);
            intent.putExtra("value","2");
            startActivity(intent);
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMobile.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Enter your Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (editMobile.getText().toString().length() < 8) {
                    Toast.makeText(LoginActivity.this, "Please Enter Complete Mobile No.", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(LoginActivity.this, OTPActivity.class);
                    intent.putExtra("mobile", countryCodeText.getText().toString() + editMobile.getText().toString().trim());
                    startActivity(intent);

                }
            }
        });
        countryImagell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCountryPicker();
            }
        });


        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE)
                + ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                + ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if ( ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.MODIFY_AUDIO_SETTINGS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.RECORD_AUDIO)) {

                builder.setTitle("Grant those permission");
                builder.setMessage("Phone call and Microphone");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ActivityCompat.requestPermissions(
                                    LoginActivity.this,
                                    new String[]{
                                            Manifest.permission.READ_PHONE_STATE,
                                            Manifest.permission.MODIFY_AUDIO_SETTINGS,
                                            Manifest.permission.RECORD_AUDIO,
                                    }, REQUEST_CODE);
                        }

                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
//                        startActivity(getIntent());
                        android.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            } else {
                ActivityCompat.requestPermissions(
                        LoginActivity.this,
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                                Manifest.permission.RECORD_AUDIO,
                        }, REQUEST_CODE);


            }

        }
        else {
           //do something
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                       ) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {

                    Toast.makeText(this, "Please allow Permissions to use Application", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    @SuppressWarnings("deprecation")
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                LoginUserByEmail(personName,personEmail);
            }
        } catch (ApiException e) {

            Log.e("Pra", e.toString());
        }

    }
    private void LoginUserByEmail(String personName, String personEmail) {
        openDialog("VerifyingEmail");

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.Login, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respObj = new JSONObject(response);
                    int status = respObj.optInt("status");

                    switch (status) {
                        case 0: {
                            dialogProgress.dismiss();
                            Intent intent=new Intent(LoginActivity.this,GenderActivity.class);
                            intent.putExtra("name",personName);
                            intent.putExtra("email",personEmail);
                            startActivity(intent);

                            break;
                        }
                        case 1: {
                            dialogProgress.dismiss();
                            JSONObject data = respObj.getJSONObject("User details");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Preferences.userName, data.getString("name"));
                            editor.putString(Preferences.userId,data.getString("id"));
                            editor.putString(Preferences.userEmail, data.getString("email"));
                            editor.putString(Preferences.userGender, data.getString("gender"));
                            editor.putString(Preferences.MyInvCode, data.getString("my_inv_code"));
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, SinchLoginActivity.class);
                            intent.putExtra("id",data.getString("id"));
                            startActivity(intent);
                            finish();
                            break;
                        }
                        default:{
                            dialogProgress.dismiss();
                            Toast.makeText(LoginActivity.this,respObj.optString("msg"),Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialogProgress.dismiss();
                // method to handle errors.
                SwitchNC();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", personEmail);

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
        dialogProgress = new Dialog(LoginActivity.this);
        dialogProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogProgress.setCancelable(false);
        dialogProgress.setContentView(R.layout.progress_dialog);
        TextView txtProgressDialog = dialogProgress.findViewById(R.id.txtProgressDialog);
        txtProgressDialog.setText(text);

        dialogProgress.show();
    }

    @SuppressLint("WrongConstant")
    private void openCountryPicker() {
        final CountryPicker picker = CountryPicker.newInstance("Select Country", Theme.LIGHT);
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                // Implement your code here
                //   countrytxt.setText(name);
                countryCodeText.setText(dialCode);
                countryImage.setImageResource(flagDrawableResID);
                picker.dismiss();
                country_iso_code = code;
            }
        });
        picker.setStyle(R.style.countrypicker_style, R.style.countrypicker_style);
        picker.show(getSupportFragmentManager(), "Select Country");
    }

    private void SwitchNC() {
        Intent i = new Intent(LoginActivity.this, NoInternetActivity.class);
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
}