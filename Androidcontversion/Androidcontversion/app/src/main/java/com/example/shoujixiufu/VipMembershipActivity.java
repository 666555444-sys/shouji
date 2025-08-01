package com.example.shoujixiufu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class VipMembershipActivity extends AppCompatActivity {
    
    private CardView permanentPackage;
    private CardView annualPackage;
    private ImageView checkPermanent;
    private ImageView checkAnnual;
    private TextView permanentPrice;
    private TextView annualPrice;
    private TextView termsText;
    private Button activateButton;
    private String selectedPackage = "premium"; // 默认选择高级套餐
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_membership);
        
        // 初始化视图
        initViews();
        
        // 设置监听器
        setupListeners();
    }
    
    private void initViews() {
        // 获取返回按钮
        ImageButton backBtn = findViewById(R.id.backBtn);
        
        // 获取套餐选择相关视图
        permanentPackage = findViewById(R.id.permanent_package);
        annualPackage = findViewById(R.id.annual_package);
        checkPermanent = findViewById(R.id.check_permanent);
        checkAnnual = findViewById(R.id.check_annual);
        permanentPrice = findViewById(R.id.permanent_price);
        annualPrice = findViewById(R.id.annual_price);
        
        // 获取服务条款和激活按钮
        termsText = findViewById(R.id.terms_text);
        activateButton = findViewById(R.id.btn_activate_vip);
        
        // 默认选中高级套餐
        selectPackage("permanent");
    }
    
    private void setupListeners() {
        // 返回按钮点击事件
        findViewById(R.id.backBtn).setOnClickListener(v -> onBackPressed());
        
        // 高级套餐点击事件
        permanentPackage.setOnClickListener(v -> selectPackage("permanent"));
        
        // 年度套餐点击事件
        annualPackage.setOnClickListener(v -> selectPackage("annual"));
        
        // 服务条款点击事件
        termsText.setOnClickListener(v -> showServiceAgreement());
        
        // 激活按钮点击事件
        activateButton.setOnClickListener(v -> showPaymentConfirmation());
    }
    
    private void selectPackage(String packageType) {
        try {
            // 更新选中状态
            this.selectedPackage = packageType;
            
            // 更新UI
            if (packageType.equals("permanent")) {
                checkPermanent.setVisibility(View.VISIBLE);
                checkAnnual.setVisibility(View.INVISIBLE);
                permanentPackage.setCardBackgroundColor(getResources().getColor(R.color.white));
                annualPackage.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
            } else {
                checkPermanent.setVisibility(View.INVISIBLE);
                checkAnnual.setVisibility(View.VISIBLE);
                permanentPackage.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                annualPackage.setCardBackgroundColor(getResources().getColor(R.color.white));
            }
        } catch (Exception e) {
            // 处理整个方法的异常
            e.printStackTrace();
            Toast.makeText(this, "选择套餐时出现错误", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showServiceAgreement() {
        try {
            // 跳转到服务协议页面
            Intent intent = new Intent(this, ServiceAgreementActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "无法打开服务协议", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showPaymentConfirmation() {
        String packageTitle = selectedPackage.equals("permanent") ? "高级会员" : "年度会员";
        String packagePrice = selectedPackage.equals("permanent") ? "¥158" : "¥78";
        
        new AlertDialog.Builder(this)
                .setTitle("确认支付")
                .setMessage("服务：" + packageTitle + "\n价格：" + packagePrice)
                .setPositiveButton("确认支付", (dialog, which) -> navigateToPayment())
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void navigateToPayment() {
        try {
            // 跳转到支付页面
            Intent intent = new Intent(this, PaymentActivity.class);
            
            // 传递套餐信息
            String packageTitle = selectedPackage.equals("permanent") ? "高级会员" : "年度会员";
            String packagePrice = selectedPackage.equals("permanent") ? "¥158" : "¥78";
            
            intent.putExtra("service_title", packageTitle);
            intent.putExtra("service_price", packagePrice);
            intent.putExtra("returnTo", "vip_membership"); // 添加返回标识
            
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "跳转支付页面失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onBackPressed() {
        finish();
    }
} 