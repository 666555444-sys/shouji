package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class VideoRepairActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextView menuBtn;
    private CardView featureItem1, featureItem2, featureItem3, featureItem4;
    private Button startRepairBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_repair);
        
        // 初始化视图
        initViews();
        
        // 设置点击事件
        setupClickListeners();
        
        // 动画效果
        animateFeatures();
    }
    
    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        menuBtn = findViewById(R.id.menuBtn);
        featureItem1 = findViewById(R.id.featureItem1);
        featureItem2 = findViewById(R.id.featureItem2);
        featureItem3 = findViewById(R.id.featureItem3);
        featureItem4 = findViewById(R.id.featureItem4);
        startRepairBtn = findViewById(R.id.startRepairBtn);
    }
    
    private void setupClickListeners() {
        // 返回按钮
        backBtn.setOnClickListener(v -> {
            showExitConfirmDialog();
        });
        
        // 菜单按钮
        menuBtn.setOnClickListener(v -> {
            // 这里可以实现菜单功能
        });
        
        // 开始修复按钮
        startRepairBtn.setOnClickListener(v -> {
            goToVideoScan();
        });
    }
    
    private void animateFeatures() {
        // 设置动画延迟
        animateFeatureItem(featureItem1, 100);
        animateFeatureItem(featureItem2, 200);
        animateFeatureItem(featureItem3, 300);
        animateFeatureItem(featureItem4, 400);
    }
    
    private void animateFeatureItem(View view, long delay) {
        AnimationSet animSet = new AnimationSet(true);
        
        // 透明度动画
        AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(500);
        
        // 平移动画
        TranslateAnimation translateAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.2f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnim.setDuration(500);
        
        // 添加动画到集合
        animSet.addAnimation(alphaAnim);
        animSet.addAnimation(translateAnim);
        animSet.setStartOffset(delay);
        
        // 启动动画
        view.startAnimation(animSet);
    }
    
    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.are_you_sure_return);
        builder.setMessage(R.string.return_rescan_note);
        builder.setPositiveButton(R.string.confirm_action, (dialog, which) -> {
            finish();
        });
        builder.setNegativeButton(R.string.cancel_action, null);
        builder.show();
    }
    
    private void goToVideoScan() {
        // 添加参数表示直接开始扫描
        Intent intent = new Intent(this, VideoScanActivity.class);
        intent.putExtra("startScan", true);
        startActivity(intent);
    }
    
    @Override
    public void onBackPressed() {
        showExitConfirmDialog();
    }
} 