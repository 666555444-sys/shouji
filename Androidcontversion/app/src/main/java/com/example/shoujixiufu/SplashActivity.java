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

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 seconds

    private View bubble1, bubble2, bubble3, bubble4, bubble5;
    private View logoContainer;
    private View loadingDot1, loadingDot2, loadingDot3;
    private View arrowIcon;
    
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
        
        loadingDot1 = findViewById(R.id.loadingDot1);
        loadingDot2 = findViewById(R.id.loadingDot2);
        loadingDot3 = findViewById(R.id.loadingDot3);
        
        arrowIcon = findViewById(R.id.arrowIcon);
    }
    
    private void startAnimations() {
        // Start bubbles animation
        animateBubble(bubble1, 0, 15000);
        animateBubble(bubble2, 2000, 17000);
        animateBubble(bubble3, 1000, 13000);
        animateBubble(bubble4, 3000, 18000);
        animateBubble(bubble5, 500, 12000);
        
        // Animate logo with pulse effect
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
        
        // Animate loading dots
        animateLoadingDot(loadingDot1, 0);
        animateLoadingDot(loadingDot2, 200);
        animateLoadingDot(loadingDot3, 400);
        
        // Animate arrow icon
        ObjectAnimator arrowAnimator = ObjectAnimator.ofFloat(arrowIcon, "translationY", 0, -5);
        arrowAnimator.setDuration(1500);
        arrowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        arrowAnimator.setRepeatMode(ValueAnimator.REVERSE);
        arrowAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        arrowAnimator.start();
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
    
    private void animateLoadingDot(View dot, long delay) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(dot, "scaleX", 0.6f, 1f, 0.6f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(dot, "scaleY", 0.6f, 1f, 0.6f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(dot, "alpha", 0.6f, 1f, 0.6f);
        
        scaleX.setDuration(1400);
        scaleY.setDuration(1400);
        alpha.setDuration(1400);
        
        scaleX.setStartDelay(delay);
        scaleY.setStartDelay(delay);
        alpha.setStartDelay(delay);
        
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        alpha.setRepeatCount(ValueAnimator.INFINITE);
        
        scaleX.start();
        scaleY.start();
        alpha.start();
    }
    
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
} 