package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ScanActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private Button albumTab, wechatTab, qqTab, otherTab;
    private ProgressBar progressBar;
    private TextView progressText;
    private Button cancelBtn, recoverBtn, unlockBtn, goToPaymentBtn;
    private RelativeLayout unlockModal;
    private TextView closeModalBtn;
    
    private Timer scanTimer;
    private int currentProgress = 13;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // 初始化视图
        initViews();
        
        // 设置点击监听
        setupClickListeners();
        
        // 设置初始进度
        updateProgress(currentProgress);
        
        // 模拟扫描进度（注释掉，因为初始设计中默认是不自动扫描的）
        // startScanSimulation();
    }
    
    private void initViews() {
        // 头部
        backBtn = findViewById(R.id.backBtn);
        
        // 标签页
        albumTab = findViewById(R.id.albumTab);
        wechatTab = findViewById(R.id.wechatTab);
        qqTab = findViewById(R.id.qqTab);
        otherTab = findViewById(R.id.otherTab);
        
        // 进度
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        
        // 按钮
        cancelBtn = findViewById(R.id.cancelBtn);
        recoverBtn = findViewById(R.id.recoverBtn);
        unlockBtn = findViewById(R.id.unlockBtn);
        
        // 模态框
        unlockModal = findViewById(R.id.unlockModal);
        closeModalBtn = findViewById(R.id.closeModalBtn);
        goToPaymentBtn = findViewById(R.id.goToPaymentBtn);
    }
    
    private void setupClickListeners() {
        // 返回按钮
        backBtn.setOnClickListener(v -> goBack());
        
        // 标签页
        albumTab.setOnClickListener(v -> selectTab(albumTab));
        wechatTab.setOnClickListener(v -> selectTab(wechatTab));
        qqTab.setOnClickListener(v -> selectTab(qqTab));
        otherTab.setOnClickListener(v -> selectTab(otherTab));
        
        // 操作按钮
        cancelBtn.setOnClickListener(v -> showCancelConfirmation());
        recoverBtn.setOnClickListener(v -> showUnlockModal());
        unlockBtn.setOnClickListener(v -> showUnlockModal());
        
        // 模态框
        closeModalBtn.setOnClickListener(v -> hideUnlockModal());
        goToPaymentBtn.setOnClickListener(v -> goToPayment());
    }
    
    private void selectTab(Button selectedTab) {
        // 重置所有标签样式
        albumTab.setBackgroundResource(R.drawable.bg_tab_unselected);
        albumTab.setTextColor(getResources().getColor(R.color.text_primary));
        wechatTab.setBackgroundResource(R.drawable.bg_tab_unselected);
        wechatTab.setTextColor(getResources().getColor(R.color.text_primary));
        qqTab.setBackgroundResource(R.drawable.bg_tab_unselected);
        qqTab.setTextColor(getResources().getColor(R.color.text_primary));
        otherTab.setBackgroundResource(R.drawable.bg_tab_unselected);
        otherTab.setTextColor(getResources().getColor(R.color.text_primary));
        
        // 设置选中标签样式
        selectedTab.setBackgroundResource(R.drawable.bg_tab_selected);
        selectedTab.setTextColor(getResources().getColor(R.color.white));
    }
    
    private void updateProgress(int progress) {
        progressBar.setProgress(progress);
        progressText.setText(progress + "%");
    }
    
    private void startScanSimulation() {
        scanTimer = new Timer();
        scanTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (currentProgress < 100) {
                    currentProgress += random.nextInt(5) + 1;
                    if (currentProgress > 100) {
                        currentProgress = 100;
                    }
                    
                    runOnUiThread(() -> updateProgress(currentProgress));
                } else {
                    scanTimer.cancel();
                }
            }
        }, 1000, 1000); // 每秒更新一次
    }
    
    private void showCancelConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.cancel_scan)
            .setMessage(R.string.cancel_scan_confirm)
            .setPositiveButton(R.string.confirm_action, (dialog, which) -> {
                // 停止扫描
                if (scanTimer != null) {
                    scanTimer.cancel();
                }
                finish();
            })
            .setNegativeButton(R.string.cancel_action, null)
            .show();
    }
    
    private void showUnlockModal() {
        unlockModal.setVisibility(View.VISIBLE);
    }
    
    private void hideUnlockModal() {
        unlockModal.setVisibility(View.GONE);
    }
    
    private void goToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        startActivity(intent);
    }
    
    private void goBack() {
        onBackPressed();
    }
    
    @Override
    public void onBackPressed() {
        if (unlockModal.getVisibility() == View.VISIBLE) {
            hideUnlockModal();
        } else {
            showCancelConfirmation();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanTimer != null) {
            scanTimer.cancel();
        }
    }
} 