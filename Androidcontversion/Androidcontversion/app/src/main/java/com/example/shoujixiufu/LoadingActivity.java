package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Redirect to splash screen after a short delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        }, 500); // 500ms delay, same as in the HTML
    }
} 