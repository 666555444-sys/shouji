package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 本类用于示范如何在MainActivity中使用PermissionManager
 * 
 * 使用方法：
 * 1. 在MainActivity中添加PermissionManager成员变量
 * 2. 在onCreate中初始化PermissionManager
 * 3. 调用permissionManager的方法代替原有的权限请求方法
 * 4. 移除原有的权限请求相关代码
 */
public class PermissionUsageExample extends AppCompatActivity {

    private Intent pendingIntent;
    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化权限管理器
        permissionManager = new PermissionManager(this);
        
        // 请求必要的存储权限
        requestStoragePermissions();
        
        // 其他初始化代码...
    }
    
    // 请求存储权限的新方法
    private void requestStoragePermissions() {
        permissionManager.requestStoragePermissions(new PermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                // 权限被授予，可以进行后续操作
                Toast.makeText(PermissionUsageExample.this, 
                        R.string.permission_granted, Toast.LENGTH_SHORT).show();
                
                // 如果有待处理的意图，可以执行它
                if (pendingIntent != null) {
                    startActivity(pendingIntent);
                    pendingIntent = null;
                }
            }

            @Override
            public void onPermissionDenied() {
                // 权限被拒绝，可以给用户一些提示
                Toast.makeText(PermissionUsageExample.this, 
                        R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // 示例：在onServiceClick方法中使用权限管理器
    public void onServiceClickExample(ServiceModel service) {
        try {
            String serviceTitle = service.getTitle();
            Intent intent = null;
            
            // 根据不同的服务类型，准备相应的Intent
            if (serviceTitle.equals(getString(R.string.file_repair))) {
                intent = new Intent(this, ScanActivity.class);
                intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_FILE);
            } else if (serviceTitle.equals(getString(R.string.video_repair))) {
                intent = new Intent(this, ScanActivity.class);
                intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_VIDEO);
            } else if (serviceTitle.equals(getString(R.string.audio_repair))) {
                intent = new Intent(this, ScanActivity.class);
                intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_AUDIO);
            } else {
                // 其他服务仍需要开通会员
                // showPremiumDialog();
                return;
            }
            
            // 检查权限，如有必要则请求权限
            if (intent != null) {
                final Intent finalIntent = intent;
                if (permissionManager.hasPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // 已有权限，直接启动Activity
                    startActivity(finalIntent);
                } else {
                    // 没有权限，保存Intent并请求权限
                    pendingIntent = finalIntent;
                    permissionManager.requestStoragePermissions(new PermissionManager.PermissionCallback() {
                        @Override
                        public void onPermissionGranted() {
                            startActivity(finalIntent);
                            pendingIntent = null;
                        }

                        @Override
                        public void onPermissionDenied() {
                            Toast.makeText(PermissionUsageExample.this, 
                                    "需要存储权限才能访问文件", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "页面跳转失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }
} 