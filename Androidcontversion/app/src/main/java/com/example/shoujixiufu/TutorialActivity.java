package com.example.shoujixiufu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {

    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        
        // 初始化视图
        backBtn = findViewById(R.id.back_btn);
        
        // 设置返回按钮点击事件
        backBtn.setOnClickListener(v -> finish());
    }
} 