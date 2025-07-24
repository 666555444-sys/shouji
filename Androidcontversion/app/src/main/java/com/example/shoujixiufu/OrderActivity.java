package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements OrderAdapter.OrderItemClickListener {

    private RecyclerView orderRecyclerView;
    private LinearLayout navHome, navCases, navOrder, navProfile;
    private LinearLayout emptyOrderState;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        initViews();
        setupBottomNavigation();
        setupOrderList();
        
        // 检查是否从支付成功页面返回
        checkPaymentSuccessParam();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        
        // 检查是否从支付成功页面返回
        checkPaymentSuccessParam();
    }
    
    // 检查是否有支付成功的标志
    private void checkPaymentSuccessParam() {
        try {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("paymentSuccess")) {
                boolean paymentSuccess = intent.getBooleanExtra("paymentSuccess", false);
                Log.d("OrderActivity", "支付成功参数: " + paymentSuccess);
                
                if (paymentSuccess) {
                    // 如果支付成功，更新第一个待支付订单状态为处理中
                    updateFirstPendingOrderStatus();
                }
            }
        } catch (Exception e) {
            Log.e("OrderActivity", "处理支付成功参数时出错", e);
        }
    }
    
    // 更新第一个待支付订单的状态
    private void updateFirstPendingOrderStatus() {
        try {
            if (orderList != null && !orderList.isEmpty() && orderAdapter != null) {
                for (int i = 0; i < orderList.size(); i++) {
                    Order order = orderList.get(i);
                    if ("待支付".equals(order.getStatus())) {
                        // 找到第一个待支付订单，更新为已完成
                        order.setStatus("已完成");
                        orderAdapter.notifyItemChanged(i);
                        Toast.makeText(this, "订单支付成功", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("OrderActivity", "更新订单状态时出错", e);
        }
    }

    private void initViews() {
        // 初始化订单列表
        orderRecyclerView = findViewById(R.id.order_recycler_view);
        emptyOrderState = findViewById(R.id.empty_order_state);
        
        // 初始化底部导航
        navHome = findViewById(R.id.nav_home);
        navCases = findViewById(R.id.nav_cases);
        navOrder = findViewById(R.id.nav_order);
        navProfile = findViewById(R.id.nav_profile);
    }

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

    private void updateNavigationState() {
        // 重置所有导航项的状态
        resetNavigationState();
        
        // 设置订单页面为选中状态
        if (navOrder != null) {
            ImageView icon = navOrder.findViewById(R.id.nav_order_icon);
            TextView text = navOrder.findViewById(R.id.nav_order_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_order_selected);
            if (text != null) text.setTextColor(getResources().getColor(R.color.primary_color));
            
            // 添加底部边框指示器
            navOrder.setBackgroundResource(R.drawable.bottom_nav_item_background);
        }
    }

    private void resetNavigationState() {
        // 重置首页
        if (navHome != null) {
            ImageView icon = navHome.findViewById(R.id.nav_home_icon);
            TextView text = navHome.findViewById(R.id.nav_home_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_home);
            if (text != null) text.setTextColor(getResources().getColor(R.color.text_secondary));
            navHome.setBackground(null);
        }
        
        // 重置案例
        if (navCases != null) {
            ImageView icon = navCases.findViewById(R.id.nav_cases_icon);
            TextView text = navCases.findViewById(R.id.nav_cases_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_cases);
            if (text != null) text.setTextColor(getResources().getColor(R.color.text_secondary));
            navCases.setBackground(null);
        }
        
        // 重置订单
        navOrder.setBackground(null);
        
        // 重置个人中心
        if (navProfile != null) {
            ImageView icon = navProfile.findViewById(R.id.nav_profile_icon);
            TextView text = navProfile.findViewById(R.id.nav_profile_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_profile);
            if (text != null) text.setTextColor(getResources().getColor(R.color.text_secondary));
            navProfile.setBackground(null);
        }
    }

    private void setupOrderList() {
        // 设置订单列表
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // 获取订单数据
        orderList = getOrderList();
        
        // 如果订单列表为空，显示空状态
        if (orderList.isEmpty()) {
            orderRecyclerView.setVisibility(View.GONE);
            emptyOrderState.setVisibility(View.VISIBLE);
        } else {
            orderRecyclerView.setVisibility(View.VISIBLE);
            emptyOrderState.setVisibility(View.GONE);
            
            // 设置适配器
            orderAdapter = new OrderAdapter(orderList, this);
            orderAdapter.setOrderItemClickListener(this);
            orderRecyclerView.setAdapter(orderAdapter);
        }
    }
    
    // 模拟获取订单数据
    private List<Order> getOrderList() {
        List<Order> orders = new ArrayList<>();
        
        // 只保留第一个订单
        orders.add(new Order("1", "手机数据恢复服务", "待支付", "SJ20230615001", "2023-06-15 14:30", 159.00, "数据恢复", "iPhone 13"));
        
        return orders;
    }

    @Override
    public void onOrderDetailClick(Order order) {
        // 跳转到支付页面
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("service_type", order.getTitle());
        intent.putExtra("service_price", "¥" + order.getAmount());
        startActivity(intent);
    }

    @Override
    public void onCancelOrderClick(Order order, int position) {
        // 弹出确认对话框
        new AlertDialog.Builder(this)
            .setTitle("取消订单")
            .setMessage("确定要取消此订单吗？")
            .setPositiveButton("确定", (dialog, which) -> {
                // 执行取消订单操作
                cancelOrder(order, position);
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    // 执行取消订单操作
    private void cancelOrder(Order order, int position) {
        // 更新订单状态为"已取消"
        order.setStatus("已取消");
        
        // 通知适配器更新界面
        orderAdapter.notifyItemChanged(position);
        
        // 显示取消成功提示
        Toast.makeText(this, "订单已取消", Toast.LENGTH_SHORT).show();
        
        // 如果所有订单都被取消，可以显示空状态
        boolean allCancelled = true;
        for (Order o : orderList) {
            if (!"已取消".equals(o.getStatus()) && !"已完成".equals(o.getStatus())) {
                allCancelled = false;
                break;
            }
        }
        
        if (allCancelled && orderList.size() == 0) {
            orderRecyclerView.setVisibility(View.GONE);
            emptyOrderState.setVisibility(View.VISIBLE);
        }
    }
    
    // 处理返回按钮点击
    public void onBackPressed(View view) {
        finish();
    }
} 