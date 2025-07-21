package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentSuccessActivity extends AppCompatActivity {

    private TextView serviceNameText;
    private Button viewResultsButton;
    private Button backToMainButton;
    private String serviceType;
    private String returnActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // 初始化视图
        initViews();
        
        // 获取传递的服务类型
        Intent intent = getIntent();
        if (intent != null) {
            serviceType = intent.getStringExtra("service_type");
            
            if (serviceType == null) {
                serviceType = getString(R.string.deep_scan_service);
            }
            
            // 根据服务类型设置返回的Activity
            if (serviceType.contains(getString(R.string.video_repair))) {
                returnActivity = "VideoScanActivity";
            } else if (serviceType.contains(getString(R.string.audio_repair))) {
                returnActivity = "AudioScanActivity";
            } else if (serviceType.contains(getString(R.string.file_repair))) {
                returnActivity = "FileScanActivity";
            } else {
                returnActivity = "MainActivity";
            }
            
            // 设置服务名称
            serviceNameText.setText(serviceType);
        }
        
        // 设置查看结果按钮
        viewResultsButton.setOnClickListener(v -> viewScanResults());
        
        // 设置返回主页按钮
        backToMainButton.setOnClickListener(v -> backToMainPage());
    }
    
    private void initViews() {
        serviceNameText = findViewById(R.id.service_name);
        viewResultsButton = findViewById(R.id.view_results_button);
        backToMainButton = findViewById(R.id.back_to_main_button);
        
        // 设置成功图标动画效果
        ImageView successIcon = findViewById(R.id.success_icon);
        successIcon.setAlpha(0f);
        successIcon.animate()
                .alpha(1f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(500)
                .withEndAction(() -> {
                    successIcon.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .start();
                })
                .start();
    }
    
    private void viewScanResults() {
        Intent intent = null;
        
        // 根据服务类型跳转到相应的扫描结果页面
        switch (returnActivity) {
            case "VideoScanActivity":
                intent = new Intent(this, VideoScanActivity.class);
                intent.putExtra("deepScanEnabled", true);
                break;
            case "AudioScanActivity":
                intent = new Intent(this, AudioScanActivity.class);
                intent.putExtra("deepScanEnabled", true);
                break;
            case "FileScanActivity":
                intent = new Intent(this, FileScanActivity.class);
                intent.putExtra("deepScanEnabled", true);
                break;
            default:
                intent = new Intent(this, MainActivity.class);
                break;
        }
        
        startActivity(intent);
        finish();
    }
    
    private void backToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onBackPressed() {
        // 禁用返回按钮，用户必须选择一个操作
        // 可以显示提示
        viewResultsButton.setVisibility(View.VISIBLE);
    }
} 