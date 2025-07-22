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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class PaymentSuccessActivity extends BaseActivity {

    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";
    private EditText phoneInput;
    private Button submitButton;
    private ImageButton backButton;
    private Dialog successDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // 初始化视图
        initViews();
        
        // 设置返回按钮点击事件 - 确保这里正确绑定点击事件
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                safeGoToOrderPage();
            });
        }
        
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
            backButton = findViewById(R.id.back_to_order_btn);
            
            // 直接在这里设置点击事件，不再依赖onCreate中的设置
            if (backButton != null) {
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        safeGoToOrderPage();
                    }
                });
            } else {
                Log.e("PaymentSuccessActivity", "返回按钮未找到");
            }
            
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
            
            if (!Pattern.matches(PHONE_PATTERN, phone)) {
                Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 模拟提交
            // 在实际应用中，这里可以添加网络请求来提交手机号码
            showSuccessDialog();
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
            successDialog.setCancelable(true);
            
            // 设置返回首页按钮
            Button returnHomeButton = successDialog.findViewById(R.id.return_home_btn);
            returnHomeButton.setOnClickListener(v -> safeGoToHomePage());
            
            // 设置关闭按钮
            View closeButton = successDialog.findViewById(R.id.close_btn);
            closeButton.setOnClickListener(v -> successDialog.dismiss());
            
            // 点击对话框外部关闭
            successDialog.setOnCancelListener(dialog -> successDialog.dismiss());
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
            // 如果有previousActivity，则返回到那个页面
            if (previousActivity != null) {
                navigateBack();
                return;
            }
            
            // 备用方案：跳转到OrderActivity
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("paymentSuccess", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
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
        // 用户必须提交手机号或者点击返回按钮
        safeGoToOrderPage();
    }
} 