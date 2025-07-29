package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 基础Activity，提供返回按钮处理和其他通用功能
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected String previousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 获取上一个Activity的名称（如果有）
        if (getIntent().hasExtra("previous_activity")) {
            previousActivity = getIntent().getStringExtra("previous_activity");
        }
    }

    /**
     * 添加前一个Activity信息到Intent中
     * @param intent 目标Intent
     * @param previousActivityName 前一个Activity名称
     * @return 添加了信息的Intent
     */
    protected Intent addPreviousActivityInfo(Intent intent, String previousActivityName) {
        intent.putExtra("previous_activity", previousActivityName);
        return intent;
    }

    /**
     * 返回到前一个页面
     */
    protected void navigateBack() {
        if (previousActivity != null) {
            try {
                // 尝试获取类并创建Intent
                Class<?> targetClass = Class.forName(previousActivity);
                Intent intent = new Intent(this, targetClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        // 默认行为：结束当前Activity
        finish();
    }
    
    @Override
    public void onBackPressed() {
        navigateBack();
    }
} 