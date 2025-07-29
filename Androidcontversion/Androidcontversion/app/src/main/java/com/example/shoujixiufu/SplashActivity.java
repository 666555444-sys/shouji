package com.example.shoujixiufu;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 seconds

    private View bubble1, bubble2, bubble3, bubble4, bubble5;
    private View logoContainer;
    private View dataIcon;
    private View scanLine;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set fullscreen mode
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_splash);
        
        // Initialize views
        initializeViews();
        
        // Start animations
        startAnimations();
        
        // Navigate to main activity after delay
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToMainActivity, SPLASH_DELAY);
    }
    
    private void initializeViews() {
        bubble1 = findViewById(R.id.bubble1);
        bubble2 = findViewById(R.id.bubble2);
        bubble3 = findViewById(R.id.bubble3);
        bubble4 = findViewById(R.id.bubble4);
        bubble5 = findViewById(R.id.bubble5);
        
        logoContainer = findViewById(R.id.logoContainer);
        dataIcon = findViewById(R.id.dataIcon);
        scanLine = findViewById(R.id.scanLine);
    }
    
    private void startAnimations() {
        // Start bubbles animation
        animateBubble(bubble1, 0, 15000);
        animateBubble(bubble2, 2000, 17000);
        animateBubble(bubble3, 1000, 13000);
        animateBubble(bubble4, 3000, 18000);
        animateBubble(bubble5, 500, 12000);
        
        // Animate logo container with pulse effect
        ObjectAnimator logoAnimator = ObjectAnimator.ofPropertyValuesHolder(
            logoContainer,
            PropertyValuesHolder.ofFloat("scaleX", 0.98f, 1.02f),
            PropertyValuesHolder.ofFloat("scaleY", 0.98f, 1.02f)
        );
        logoAnimator.setDuration(2000);
        logoAnimator.setRepeatCount(ValueAnimator.INFINITE);
        logoAnimator.setRepeatMode(ValueAnimator.REVERSE);
        logoAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        logoAnimator.start();
        
        // Animate data icon with floating up and down motion
        ObjectAnimator iconAnimator = ObjectAnimator.ofPropertyValuesHolder(
            dataIcon,
            PropertyValuesHolder.ofFloat("translationY", -10f, 10f)
        );
        iconAnimator.setDuration(2000);
        iconAnimator.setRepeatCount(ValueAnimator.INFINITE);
        iconAnimator.setRepeatMode(ValueAnimator.REVERSE);
        iconAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        iconAnimator.start();
        
        // Animate scan line with fade effect
        ObjectAnimator scanAnimator = ObjectAnimator.ofPropertyValuesHolder(
            scanLine,
            PropertyValuesHolder.ofFloat("alpha", 0.2f, 1.0f),
            PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1.2f)
        );
        scanAnimator.setDuration(1500);
        scanAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scanAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scanAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scanAnimator.start();
    }
    
    private void animateBubble(View view, long delay, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("translationX", 0, 10, -5, -10, 5, 0),
            PropertyValuesHolder.ofFloat("translationY", 0, -10, 15, -5, 10, 0)
        );
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
} 