package com.example.shoujixiufu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ServiceAgreementActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private Button agreeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_agreement);
        
        // 初始化视图
        initViews();
        
        // 设置点击监听
        setClickListeners();
    }
    
    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        agreeBtn = findViewById(R.id.agreeBtn);
    }
    
    private void setClickListeners() {
        // 返回按钮
        backBtn.setOnClickListener(v -> {
            finish();
        });
        
        // 同意按钮
        agreeBtn.setOnClickListener(v -> {
            finish();
        });
    }
} 