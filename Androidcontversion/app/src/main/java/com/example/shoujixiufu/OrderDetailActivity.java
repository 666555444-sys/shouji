package com.example.shoujixiufu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER = "extra_order";

    private Order order;

    // UI elements
    private TextView tvOrderStatus;
    private TextView tvServiceName;
    private TextView tvOrderNumber;
    private TextView tvPurchaseTime;
    private TextView tvPrice;
    private TextView tvPaymentSuccessBadge;
    private Button btnViewTutorial;
    private LinearLayout navHome, navCases, navOrder, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Get order data
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ORDER)) {
            order = (Order) intent.getSerializableExtra(EXTRA_ORDER);
        }

        initViews();
        setupBottomNavigation();
        displayOrderInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (order == null) return;
        
        // 1. 检查是否支付成功
        SharedPreferences paymentPrefs = getSharedPreferences("payment_prefs", MODE_PRIVATE);
        boolean paymentSuccess = paymentPrefs.getBoolean("payment_success", false);
        
        // 2. 检查订单状态是否在其他地方被更改
        SharedPreferences orderPrefs = getSharedPreferences("order_prefs", MODE_PRIVATE);
        String savedStatus = orderPrefs.getString("order_" + order.getOrderNumber() + "_status", null);
        
        // 支付成功的情况
        if (paymentSuccess && "待支付".equals(order.getStatus())) {
            // 更新订单状态
            order.setStatus("已完成");
            
            // 确保按钮容器不可见
            View actionButtons = findViewById(R.id.action_buttons);
            if (actionButtons != null) {
                actionButtons.setVisibility(View.GONE);
            }
            
            // 直接隐藏支付按钮，以防万一
            Button payButton = findViewById(R.id.pay_btn);
            if (payButton != null) {
                payButton.setVisibility(View.GONE);
            }
            
            // 刷新整个UI
            displayOrderInfo();
            
            // 清除标记
            paymentPrefs.edit().putBoolean("payment_success", false).apply();
            
            // 保存订单状态
            orderPrefs.edit().putString("order_" + order.getOrderNumber() + "_status", "已完成").apply();
        }
        // 订单状态被其他地方更改的情况
        else if (savedStatus != null && !savedStatus.equals(order.getStatus())) {
            order.setStatus(savedStatus);
            displayOrderInfo();
        }
    }

    private void initViews() {
        tvOrderStatus = findViewById(R.id.order_status_title);
        tvServiceName = findViewById(R.id.service_name);
        tvOrderNumber = findViewById(R.id.order_number);
        tvPurchaseTime = findViewById(R.id.purchase_time);
        tvPrice = findViewById(R.id.price_text);
        tvPaymentSuccessBadge = findViewById(R.id.payment_success_badge);
        btnViewTutorial = findViewById(R.id.view_tutorial_btn);

        Button btnCopy = findViewById(R.id.copy_order_id_btn);
        Button btnCancel = findViewById(R.id.cancel_order_btn);
        Button btnPay = findViewById(R.id.pay_btn);
        ImageButton backBtn = findViewById(R.id.back_btn);

        // 修改返回按钮点击事件，直接跳转到订单页面
        backBtn.setOnClickListener(v -> goToOrderPage());

        btnCopy.setOnClickListener(v -> copyOrderId());
        btnCancel.setOnClickListener(v -> cancelOrder());
        btnPay.setOnClickListener(v -> goToPay());
        
        // 设置查看教程按钮点击事件
        btnViewTutorial.setOnClickListener(v -> goToTutorial());

        navHome = findViewById(R.id.nav_home);
        navCases = findViewById(R.id.nav_cases);
        navOrder = findViewById(R.id.nav_order);
        navProfile = findViewById(R.id.nav_profile);
    }

    private void displayOrderInfo() {
        if (order == null) return;

        tvServiceName.setText(order.getTitle());
        tvOrderNumber.setText(order.getOrderNumber());
        tvPurchaseTime.setText(order.getOrderTime());
        tvPrice.setText(String.format("¥%.2f", order.getAmount()));

        // Set status
        if ("已完成".equals(order.getStatus()) || "支付成功".equals(order.getStatus())) {
            showPaymentSuccess();
        } else if ("待支付".equals(order.getStatus())) {
            tvOrderStatus.setText(R.string.order_pending_payment);
        } else {
            tvOrderStatus.setText(order.getStatus());
        }
    }

    private void showPaymentSuccess() {
        tvOrderStatus.setText(R.string.payment_success);
        tvOrderStatus.setTextColor(getResources().getColor(R.color.success_color));
        
        if (tvPaymentSuccessBadge != null) {
            tvPaymentSuccessBadge.setText(getString(R.string.payment_success));
            tvPaymentSuccessBadge.setVisibility(View.VISIBLE);
        }
        
        // 隐藏操作按钮容器
        View actionButtons = findViewById(R.id.action_buttons);
        if (actionButtons != null) {
            actionButtons.setVisibility(View.GONE);
        }
        
        // 单独确保立即付款按钮隐藏
        Button payButton = findViewById(R.id.pay_btn);
        if (payButton != null) {
            payButton.setVisibility(View.GONE);
        }
        
        // 显示查看教程按钮
        if (btnViewTutorial != null) {
            btnViewTutorial.setVisibility(View.VISIBLE);
        }
        
        // 保存订单状态
        if (order != null) {
            SharedPreferences orderPrefs = getSharedPreferences("order_prefs", MODE_PRIVATE);
            orderPrefs.edit().putString("order_" + order.getOrderNumber() + "_status", "已完成").apply();
        }
    }

    private void copyOrderId() {
        if (order == null) return;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("order_id", order.getOrderNumber());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.order_id_copied, Toast.LENGTH_SHORT).show();
    }

    private void cancelOrder() {
        Toast.makeText(this, R.string.order_cancelled, Toast.LENGTH_SHORT).show();
        
        // Update the order status and refresh the UI
        if (order != null) {
            order.setStatus("已取消");  // 设置为"已取消"状态
            
            // 更新UI显示
            tvOrderStatus.setText("已取消");
            tvOrderStatus.setTextColor(getResources().getColor(R.color.text_tertiary));
            
            // 确保按钮容器不可见
            View actionButtons = findViewById(R.id.action_buttons);
            if (actionButtons != null) {
                actionButtons.setVisibility(View.GONE);
            }
            
            // 隐藏立即付款按钮
            Button payButton = findViewById(R.id.pay_btn);
            if (payButton != null) {
                payButton.setVisibility(View.GONE);
            }
            
            // 显示"已取消"状态标签
            if (tvPaymentSuccessBadge != null) {
                tvPaymentSuccessBadge.setText("已取消");
                tvPaymentSuccessBadge.setBackgroundResource(R.drawable.bg_cancel_badge);
                tvPaymentSuccessBadge.setVisibility(View.VISIBLE);
            }
            
            // 保存取消状态到SharedPreferences，确保返回订单列表后状态一致
            SharedPreferences prefs = getSharedPreferences("order_prefs", MODE_PRIVATE);
            prefs.edit().putString("order_" + order.getOrderNumber() + "_status", "已取消").apply();
        }
    }

    private void goToPay() {
        try {
            Intent intent = new Intent(this, PaymentActivity.class);
            if (order != null) {
                intent.putExtra("service_title", order.getTitle());
                intent.putExtra("service_price", order.getAmount());
                intent.putExtra("order_number", order.getOrderNumber());
            }
            startActivity(intent);
        } catch (Exception e) {
            Log.e("OrderDetailActivity", "跳转到支付页面失败", e);
            Toast.makeText(this, "无法跳转到支付页面，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }
    
    // 添加新方法：导航到订单页面
    private void goToOrderPage() {
        try {
            Log.d("OrderDetailActivity", "返回订单页面");
            Intent intent = new Intent(this, OrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("OrderDetailActivity", "导航到订单页面出错", e);
            // 如果发生错误，使用默认返回行为
            super.onBackPressed();
        }
    }
    
    // 重写返回按钮行为
    @Override
    public void onBackPressed() {
        goToOrderPage();
    }

    // Bottom navigation setup
    private void setupBottomNavigation() {
        // 使用底部导航帮助类设置底部导航
        BottomNavHelper.setupBottomNavigation(
                this, 
                navHome, 
                navCases, 
                navOrder, 
                navProfile, 
                "order"
        );
    }
    
    // 添加跳转到教程页面的方法
    private void goToTutorial() {
        try {
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("OrderDetailActivity", "打开教程页面失败", e);
            Toast.makeText(this, "无法打开教程，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }
} 