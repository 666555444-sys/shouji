package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecoveryReminderActivity extends AppCompatActivity {

    private TextView closeBtn;
    private Button repairNowBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 设置全屏、透明状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_recovery_reminder);

        // 初始化视图
        initViews();
        
        // 设置点击监听
        setupClickListeners();
        
        // 添加动画效果
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    
    private void initViews() {
        closeBtn = findViewById(R.id.closeBtn);
        repairNowBtn = findViewById(R.id.repairNowBtn);
    }
    
    private void setupClickListeners() {
        closeBtn.setOnClickListener(v -> {
            finish();
        });
        
        repairNowBtn.setOnClickListener(v -> {
            // 跳转到支付页面
            Intent intent = new Intent(this, PaymentActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    @Override
    public void onBackPressed() {
        // 点击返回键直接关闭
        finish();
    }
    
    @Override
    public void finish() {
        super.finish();
        // 添加退出动画
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
} 