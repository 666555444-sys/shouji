package com.example.shoujixiufu;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextView policyContent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        
        initViews();
        setupClickListeners();
        loadPolicyContent();
    }
    
    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        policyContent = findViewById(R.id.policyContent);
    }
    
    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> finish());
    }
    
    private void loadPolicyContent() {
        // 设置内容可滚动
        policyContent.setMovementMethod(new ScrollingMovementMethod());
        
        // 加载隐私政策内容
        policyContent.setText(getString(R.string.privacy_policy_content));
    }
} 