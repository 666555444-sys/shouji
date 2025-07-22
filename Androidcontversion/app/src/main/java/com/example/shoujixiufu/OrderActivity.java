package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        
        // 添加一些测试数据
        orders.add(new Order("1", "手机数据恢复服务", "处理中", "SJ20230615001", "2023-06-15 14:30", 299.00, "数据恢复", "iPhone 13"));
        orders.add(new Order("2", "手机屏幕维修", "已完成", "SJ20230610002", "2023-06-10 09:15", 499.00, "硬件维修", "华为 P40"));
        orders.add(new Order("3", "系统故障排查", "待支付", "SJ20230605003", "2023-06-05 16:45", 199.00, "系统维护", "小米 12"));
        
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
    public void onContactServiceClick(Order order) {
        // 跳转到联系客服页面
        try {
            Intent intent = new Intent(this, ContactServiceActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "打开客服页面失败，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }
    
    // 处理返回按钮点击
    public void onBackPressed(View view) {
        finish();
    }
} 