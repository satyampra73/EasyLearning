package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.db.easylearning.apphelper.Preferences;

public class ShareActivity extends AppCompatActivity {
    AppCompatButton btnShare;
    SharedPreferences sharedPreferences;
    String personName, myCode,to_referPrice="",from_referPrice= "";
    TextView txtShareText;

    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        btnShare = findViewById(R.id.btnShare);
        txtShareText = findViewById(R.id.txtShareText);
        sharedPreferences = getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
        personName = sharedPreferences.getString(Preferences.userName, Preferences.DEFAULT);
        myCode = sharedPreferences.getString(Preferences.MyInvCode, Preferences.DEFAULT);
        to_referPrice=sharedPreferences.getString(Preferences.ToReferPrice,Preferences.DEFAULT);
        from_referPrice=sharedPreferences.getString(Preferences.FromReferPrice,Preferences.DEFAULT);

        String text = "Share Speakify With your Friends to get Instant increment on your wallet after using your refer code they will get "+to_referPrice+" Rs. on their Wallet and you will also get "+from_referPrice+" Rs. after their SignUp. ";

        SpannableString spannableString = new SpannableString(text);

        // It is used to set foreground color.
        ForegroundColorSpan yellow = new ForegroundColorSpan(Color.BLACK);

        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        // It is used to set the span to the string
        spannableString.setSpan(yellow,
                6, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(boldSpan, 6, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtShareText.setText(spannableString);


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    String shareMessage = "\nYour Friend " + personName + " is inviting you to download Speakify apk his invite code is -" + myCode + " \n" +
                            "Android:https://play.google.com/store/apps/details?id=com.db.speakify";
                    shareMessage = shareMessage /*+ "https://play.google.com/store/apps/details?id="*/ /*+ BuildConfig.APPLICATION_ID + "\n\n"*/;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share Using"));
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });

    }

    public void click(View view) {
        finish();
    }
}