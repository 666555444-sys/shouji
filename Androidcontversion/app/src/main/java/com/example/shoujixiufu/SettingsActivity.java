package com.example.shoujixiufu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {
    
    private SwitchCompat notificationSwitch;
    private SwitchCompat darkModeSwitch;
    private SwitchCompat fingerprintSwitch;
    private SwitchCompat backupSwitch;
    private LinearLayout languageOption;
    private LinearLayout accountSecurityOption;
    private LinearLayout privacyOption;
    private LinearLayout storageOption;
    private LinearLayout checkUpdateOption;
    private LinearLayout helpOption;
    private Button clearCacheBtn;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化视图
        initViews();
        // 设置点击事件
        setupClickListeners();
    }

    private void initViews() {
        // 开关按钮
        notificationSwitch = findViewById(R.id.notificationSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        fingerprintSwitch = findViewById(R.id.fingerprintSwitch);
        backupSwitch = findViewById(R.id.backupSwitch);
        
        // 选项项
        languageOption = findViewById(R.id.languageOption);
        accountSecurityOption = findViewById(R.id.accountSecurityOption);
        privacyOption = findViewById(R.id.privacyOption);
        storageOption = findViewById(R.id.storageOption);
        checkUpdateOption = findViewById(R.id.checkUpdateOption);
        helpOption = findViewById(R.id.helpOption);
        
        // 按钮
        clearCacheBtn = findViewById(R.id.clearCacheBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        
        // 返回按钮
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }
    
    private void setupClickListeners() {
        // 语言选项
        languageOption.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.only_support_simplified_chinese), Toast.LENGTH_SHORT).show();
        });
        
        // 账户安全
        accountSecurityOption.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.account_security_developing), Toast.LENGTH_SHORT).show();
        });
        
        // 隐私设置
        privacyOption.setOnClickListener(v -> {
            Intent intent = new Intent(this, ServiceAgreementActivity.class);
            startActivity(intent);
        });
        
        // 存储空间
        storageOption.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.storage_usage_info), Toast.LENGTH_SHORT).show();
        });
        
        // 检查更新
        checkUpdateOption.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.latest_version), Toast.LENGTH_SHORT).show();
        });
        
        // 帮助与反馈
        helpOption.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactServiceActivity.class);
            startActivity(intent);
        });
        
        // 清除缓存
        clearCacheBtn.setOnClickListener(v -> {
            showClearCacheConfirmDialog();
        });
        
        // 退出登录
        logoutBtn.setOnClickListener(v -> {
            showLogoutConfirmDialog();
        });
    }
    
    private void showClearCacheConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.clear_cache);
        builder.setMessage(R.string.clear_cache_confirm_message);
        builder.setPositiveButton(R.string.confirm_action, (dialog, which) -> {
            // 模拟清除缓存操作
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    runOnUiThread(() -> {
                        Toast.makeText(SettingsActivity.this, R.string.cache_cleared, Toast.LENGTH_SHORT).show();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
        builder.setNegativeButton(R.string.cancel_action, null);
        builder.show();
    }
    
    private void showLogoutConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_confirm_message);
        builder.setPositiveButton(R.string.confirm_action, (dialog, which) -> {
            // 执行退出登录操作
            Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton(R.string.cancel_action, null);
        builder.show();
    }
} 