package com.db.easylearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
    }

    public void onRetry(View view) {
            Intent i = new Intent(NoInternetActivity.this, HomeActivity.class);
            overridePendingTransition(0, 0);
            startActivity(i);
            overridePendingTransition(0, 0);

    }
}