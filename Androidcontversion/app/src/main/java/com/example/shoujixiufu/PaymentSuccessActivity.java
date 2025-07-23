package com.example.shoujixiufu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class PaymentSuccessActivity extends BaseActivity {

    // 修改手机号验证模式：必须是11位数字，以1开头
    private static final String PHONE_PATTERN = "^1\\d{10}$";
    private EditText phoneInput;
    private Button submitButton;
    private Dialog successDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // 初始化视图
        initViews();
        
        // 设置提交按钮点击事件
        submitButton.setOnClickListener(v -> submitPhoneNumber());
        
        // 动画效果（简单实现）
        animateSuccessIcon();
        
        // 初始化成功对话框
        initSuccessDialog();
    }
    
    private void initViews() {
        try {
            phoneInput = findViewById(R.id.phone_input);
            submitButton = findViewById(R.id.submit_btn);
            
            // 自动聚焦输入框
            if (phoneInput != null) {
                phoneInput.requestFocus();
            }
        } catch (Exception e) {
            Log.e("PaymentSuccessActivity", "初始化视图失败", e);
            e.printStackTrace();
            Toast.makeText(this, "初始化页面失败，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void animateSuccessIcon() {
        try {
        ImageView successIcon = findViewById(R.id.success_icon);
            
            // 淡入动画
        successIcon.setAlpha(0f);
            successIcon.setScaleX(0.5f);
            successIcon.setScaleY(0.5f);
        successIcon.animate()
                .alpha(1f)
                            .scaleX(1f)
                            .scaleY(1f)
                    .setDuration(500)
                            .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void submitPhoneNumber() {
        try {
            String phone = phoneInput.getText().toString().trim();
            
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 验证手机号必须是11位数字且以1开头
            if (!Pattern.matches(PHONE_PATTERN, phone)) {
                Toast.makeText(this, "请输入正确的11位手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 显示提交成功对话框
            showSuccessDialog();
            
            // 记录手机号（实际应用中可能会发送到服务器）
            Log.d("PaymentSuccessActivity", "用户提交的手机号: " + phone);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "提交手机号码时发生错误", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initSuccessDialog() {
        try {
            successDialog = new Dialog(this);
            successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            successDialog.setContentView(R.layout.dialog_submit_success);
            successDialog.setCancelable(false); // 不允许点击外部关闭
            
            // 设置返回首页按钮
            Button returnHomeButton = successDialog.findViewById(R.id.return_home_btn);
            returnHomeButton.setOnClickListener(v -> {
                successDialog.dismiss();
                safeGoToHomePage();
            });
            
            // 设置关闭按钮
            View closeButton = successDialog.findViewById(R.id.close_btn);
            closeButton.setOnClickListener(v -> {
                successDialog.dismiss();
                safeGoToHomePage();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showSuccessDialog() {
        try {
            if (successDialog != null && !successDialog.isShowing()) {
                successDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "显示成功对话框时发生错误", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void safeGoToOrderPage() {
        try {
            Log.d("PaymentSuccessActivity", "准备返回订单页面，previousActivity=" + previousActivity);
            
            // 不再依赖previousActivity，直接跳转到OrderActivity
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("paymentSuccess", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("PaymentSuccessActivity", "返回订单页面失败", e);
            e.printStackTrace();
            Toast.makeText(this, "返回订单页面失败，请重试", Toast.LENGTH_SHORT).show();
            
            // 备用方案：如果无法跳转到OrderActivity，尝试回到MainActivity
            try {
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                finish();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    private void safeGoToHomePage() {
        try {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "返回首页失败，请重试", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    @Override
    public void onBackPressed() {
        // 此页面不再提供返回功能，用户必须提交手机号
        // 或者点击对话框中的返回首页按钮
        Toast.makeText(this, "请输入手机号码并提交", Toast.LENGTH_SHORT).show();
    }
} 