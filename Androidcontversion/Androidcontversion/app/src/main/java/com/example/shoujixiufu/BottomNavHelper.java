package com.example.shoujixiufu;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 底部导航栏帮助类，确保统一的导航视觉效果和交互
 */
public class BottomNavHelper {

    /**
     * 设置底部导航栏
     * @param context 上下文
     * @param navHome 首页导航视图
     * @param navCases 案例导航视图
     * @param navOrder 订单导航视图
     * @param navProfile 个人中心导航视图
     * @param currentTab 当前选中的标签页
     */
    public static void setupBottomNavigation(Context context, 
                                             LinearLayout navHome, 
                                             LinearLayout navCases, 
                                             LinearLayout navOrder, 
                                             LinearLayout navProfile, 
                                             String currentTab) {
        // 重置所有导航项的状态
        resetNavigationState(context, navHome, navCases, navOrder, navProfile);
        
        // 根据当前页面设置选中状态
        switch (currentTab) {
            case "home":
                selectHomeTab(context, navHome);
                break;
            case "cases":
                selectCasesTab(context, navCases);
                break;
            case "order":
                selectOrderTab(context, navOrder);
                break;
            case "profile":
                selectProfileTab(context, navProfile);
                break;
        }
        
        // 设置点击事件
        setupClickListeners(context, navHome, navCases, navOrder, navProfile);
    }
    
    /**
     * 重置所有导航项的状态为未选中
     */
    private static void resetNavigationState(Context context, 
                                           LinearLayout navHome, 
                                           LinearLayout navCases, 
                                           LinearLayout navOrder, 
                                           LinearLayout navProfile) {
        // 重置首页
        if (navHome != null) {
            ImageView icon = navHome.findViewById(R.id.nav_home_icon);
            TextView text = navHome.findViewById(R.id.nav_home_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_home);
            if (text != null) text.setTextColor(context.getResources().getColor(R.color.text_secondary));
            navHome.setBackground(null);
        }
        
        // 重置案例
        if (navCases != null) {
            ImageView icon = navCases.findViewById(R.id.nav_cases_icon);
            TextView text = navCases.findViewById(R.id.nav_cases_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_cases);
            if (text != null) text.setTextColor(context.getResources().getColor(R.color.text_secondary));
            navCases.setBackground(null);
        }
        
        // 重置订单
        if (navOrder != null) {
            ImageView icon = navOrder.findViewById(R.id.nav_order_icon);
            TextView text = navOrder.findViewById(R.id.nav_order_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_order);
            if (text != null) text.setTextColor(context.getResources().getColor(R.color.text_secondary));
            navOrder.setBackground(null);
        }
        
        // 重置个人中心
        if (navProfile != null) {
            ImageView icon = navProfile.findViewById(R.id.nav_profile_icon);
            TextView text = navProfile.findViewById(R.id.nav_profile_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_profile);
            if (text != null) text.setTextColor(context.getResources().getColor(R.color.text_secondary));
            navProfile.setBackground(null);
        }
    }
    
    /**
     * 设置首页为选中状态
     */
    private static void selectHomeTab(Context context, LinearLayout navHome) {
        if (navHome != null) {
            ImageView icon = navHome.findViewById(R.id.nav_home_icon);
            TextView text = navHome.findViewById(R.id.nav_home_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_home_selected);
            if (text != null) text.setTextColor(context.getResources().getColor(R.color.primary_color));
            navHome.setBackgroundResource(R.drawable.bottom_nav_item_background);
        }
    }
    
    /**
     * 设置案例为选中状态
     */
    private static void selectCasesTab(Context context, LinearLayout navCases) {
        if (navCases != null) {
            ImageView icon = navCases.findViewById(R.id.nav_cases_icon);
            TextView text = navCases.findViewById(R.id.nav_cases_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_cases_selected);
            if (text != null) text.setTextColor(context.getResources().getColor(R.color.primary_color));
            navCases.setBackgroundResource(R.drawable.bottom_nav_item_background);
        }
    }
    
    /**
     * 设置订单为选中状态
     */
    private static void selectOrderTab(Context context, LinearLayout navOrder) {
        if (navOrder != null) {
            ImageView icon = navOrder.findViewById(R.id.nav_order_icon);
            TextView text = navOrder.findViewById(R.id.nav_order_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_order_selected);
            if (text != null) text.setTextColor(context.getResources().getColor(R.color.primary_color));
            navOrder.setBackgroundResource(R.drawable.bottom_nav_item_background);
        }
    }
    
    /**
     * 设置个人中心为选中状态
     */
    private static void selectProfileTab(Context context, LinearLayout navProfile) {
        if (navProfile != null) {
            ImageView icon = navProfile.findViewById(R.id.nav_profile_icon);
            TextView text = navProfile.findViewById(R.id.nav_profile_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_profile_selected);
            if (text != null) text.setTextColor(context.getResources().getColor(R.color.primary_color));
            navProfile.setBackgroundResource(R.drawable.bottom_nav_item_background);
        }
    }
    
    /**
     * 设置底部导航点击事件
     */
    private static void setupClickListeners(Context context,
                                          LinearLayout navHome,
                                          LinearLayout navCases,
                                          LinearLayout navOrder,
                                          LinearLayout navProfile) {
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                // 如果当前不是MainActivity，则跳转到MainActivity并关闭当前页面
                if (!(context instanceof MainActivity)) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    
                    // 关闭当前Activity
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).finish();
                    }
                }
            });
        }
        
        if (navCases != null) {
            navCases.setOnClickListener(v -> {
                // 如果当前不是CasesActivity，则跳转到CasesActivity
                if (!(context instanceof CasesActivity)) {
                    Intent intent = new Intent(context, CasesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    
                    // 关闭当前Activity
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).finish();
                    }
                }
            });
        }
        
        if (navOrder != null) {
            navOrder.setOnClickListener(v -> {
                // 如果当前不是OrderActivity，则跳转到OrderActivity
                if (!(context instanceof OrderActivity)) {
                    Intent intent = new Intent(context, OrderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    
                    // 关闭当前Activity
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).finish();
                    }
                }
            });
        }
        
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                // 如果当前不是ProfileActivity，则跳转到ProfileActivity
                if (!(context instanceof ProfileActivity)) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    
                    // 关闭当前Activity
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).finish();
                    }
                }
            });
        }
    }
} 