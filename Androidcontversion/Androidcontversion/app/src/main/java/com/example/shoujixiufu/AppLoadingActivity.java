package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class AppLoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_loading);
        
        // Delayed transition to splash screen after 500ms
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AppLoadingActivity.this, LoadingActivity.class);
                startActivity(intent);
                finish();
            }
        }, 500);
    }
} 