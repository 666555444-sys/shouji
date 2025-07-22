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

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER = "extra_order";

    private Order order;

    // Views
    private TextView tvOrderStatus;
    private TextView tvServiceName;
    private TextView tvOrderNumber;
    private TextView tvPurchaseTime;
    private TextView tvPrice;
    private TextView tvPaymentSuccessBadge;
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

    private void initViews() {
        tvOrderStatus = findViewById(R.id.order_status_title);
        tvServiceName = findViewById(R.id.service_name);
        tvOrderNumber = findViewById(R.id.order_number);
        tvPurchaseTime = findViewById(R.id.purchase_time);
        tvPrice = findViewById(R.id.price_text);
        tvPaymentSuccessBadge = findViewById(R.id.payment_success_badge);

        Button btnCopy = findViewById(R.id.copy_order_id_btn);
        Button btnCancel = findViewById(R.id.cancel_order_btn);
        Button btnPay = findViewById(R.id.pay_btn);
        ImageButton backBtn = findViewById(R.id.back_btn);

        // 修改返回按钮点击事件，直接跳转到订单页面
        backBtn.setOnClickListener(v -> goToOrderPage());

        btnCopy.setOnClickListener(v -> copyOrderId());
        btnCancel.setOnClickListener(v -> cancelOrder());
        btnPay.setOnClickListener(v -> goToPay());

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
        tvPaymentSuccessBadge.setVisibility(View.VISIBLE);
        findViewById(R.id.action_buttons).setVisibility(View.GONE);
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
        // Here you can implement actual cancellation logic
    }

    private void goToPay() {
        Toast.makeText(this, R.string.go_to_pay, Toast.LENGTH_SHORT).show();
        // Here you can implement jumping to payment activity
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
} 