package com.example.shoujixiufu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private static final int PAYMENT_DELAY = 2000; // 模拟支付处理时间（毫秒）
    
    private RadioButton wechatPayRadio;
    private RadioButton alipayRadio;
    private Button payButton;
    private LinearLayout paymentMethodsLayout;
    private LinearLayout processingLayout;
    private ProgressBar progressBar;
    private TextView serviceNameText;
    private TextView servicePriceText;
    private String serviceType;
    private String servicePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // 初始化视图
        initViews();
        
        // 设置返回按钮
        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> onBackPressed());
        
        // 获取传递的服务类型
        Intent intent = getIntent();
        if (intent != null) {
            serviceType = intent.getStringExtra("service_type");
            servicePrice = intent.getStringExtra("service_price");
            
            if (serviceType == null) {
                serviceType = getString(R.string.deep_scan_service);
            }
            
            if (servicePrice == null) {
                servicePrice = getString(R.string.service_price);
            }
            
            // 设置服务名称和价格
            serviceNameText.setText(serviceType);
            servicePriceText.setText(servicePrice);
        }
        
        // 设置支付方式选择
        setupPaymentMethods();
        
        // 设置支付按钮
        payButton.setOnClickListener(v -> processPayment());
        
        // 设置服务条款和隐私政策点击事件
        TextView termsText = findViewById(R.id.terms_text);
        termsText.setOnClickListener(v -> showTermsAndPrivacy());
    }
    
    private void initViews() {
        wechatPayRadio = findViewById(R.id.wechat_pay_radio);
        alipayRadio = findViewById(R.id.alipay_radio);
        payButton = findViewById(R.id.pay_button);
        paymentMethodsLayout = findViewById(R.id.payment_methods_layout);
        processingLayout = findViewById(R.id.processing_layout);
        progressBar = findViewById(R.id.progress_bar);
        serviceNameText = findViewById(R.id.service_name);
        servicePriceText = findViewById(R.id.service_price);
    }
    
    private void setupPaymentMethods() {
        // 默认选择微信支付
        wechatPayRadio.setChecked(true);
        
        // 设置微信支付点击事件
        LinearLayout wechatPayLayout = findViewById(R.id.wechat_pay_layout);
        wechatPayLayout.setOnClickListener(v -> {
            wechatPayRadio.setChecked(true);
            alipayRadio.setChecked(false);
        });
        
        // 设置支付宝点击事件
        LinearLayout alipayLayout = findViewById(R.id.alipay_layout);
        alipayLayout.setOnClickListener(v -> {
            wechatPayRadio.setChecked(false);
            alipayRadio.setChecked(true);
        });
    }
    
    private void processPayment() {
        // 显示处理中状态
        paymentMethodsLayout.setVisibility(View.GONE);
        processingLayout.setVisibility(View.VISIBLE);
        payButton.setEnabled(false);
        
        // 模拟支付处理
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // 支付成功后跳转到支付成功页面
            Intent successIntent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
            successIntent.putExtra("service_type", serviceType);
            startActivity(successIntent);
            finish();
        }, PAYMENT_DELAY);
    }
    
    private void showTermsAndPrivacy() {
        // 显示服务条款和隐私政策对话框
        new AlertDialog.Builder(this)
                .setTitle("服务条款和隐私政策")
                .setMessage("点击\"立即支付\"，即表示您同意我们的服务条款和隐私政策。\n\n" +
                        "服务条款包括但不限于：服务内容、使用限制、退款政策等。\n\n" +
                        "隐私政策包括但不限于：信息收集、使用和保护等。")
                .setPositiveButton("查看服务条款", (dialog, which) -> {
                    Intent intent = new Intent(PaymentActivity.this, ServiceAgreementActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("查看隐私政策", (dialog, which) -> {
                    Intent intent = new Intent(PaymentActivity.this, PrivacyPolicyActivity.class);
                    startActivity(intent);
                })
                .setNeutralButton("关闭", null)
                .show();
    }
    
    @Override
    public void onBackPressed() {
        // 如果正在处理支付，显示确认对话框
        if (processingLayout.getVisibility() == View.VISIBLE) {
            new AlertDialog.Builder(this)
                    .setTitle("取消支付")
                    .setMessage("确定要取消本次支付吗？")
                    .setPositiveButton("确定", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("继续支付", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}

