package com.example.shoujixiufu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.cardview.widget.CardView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.ViewGroup;
import android.content.SharedPreferences;

public class PaymentActivity extends BaseActivity {

    private static final int PAYMENT_DELAY = 2000; // 模拟支付处理时间（毫秒）
    
    private Button payButton;
    private LinearLayout processingLayout;
    private ProgressBar progressBar;
    private CardView permanentPackage;
    private CardView annualPackage;
    private ImageView checkPermanent;
    private ImageView checkAnnual;
    private TextView priceText;
    private TextView originalPriceText;
    private NewsTicker newsTicker;
    private String selectedPackage = "premium"; // 默认选择高级套餐

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // 初始化视图
        initViews();
        
        // 初始化资讯栏
        View newsTickerView = findViewById(R.id.payment_news_ticker);
        if (newsTickerView != null) {
            newsTicker = new NewsTicker(newsTickerView);
            newsTicker.setupNewsContent();
        }
        
        // 设置返回按钮，点击时显示挽留页面
        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> showRecoveryReminder());
        
        // 设置套餐点击事件
        setupPackageSelection();
        
        // 设置支付按钮
        payButton.setOnClickListener(v -> showPaymentConfirmation());
    }
    
    private void initViews() {
        try {
            payButton = findViewById(R.id.pay_button);
            processingLayout = findViewById(R.id.processing_layout);
            progressBar = findViewById(R.id.progress_bar);
            permanentPackage = findViewById(R.id.permanent_package);
            annualPackage = findViewById(R.id.annual_package);
            checkPermanent = findViewById(R.id.check_permanent);
            checkAnnual = findViewById(R.id.check_annual);
            priceText = findViewById(R.id.price_permanent);
            originalPriceText = findViewById(R.id.original_price_permanent);
            
            // 设置服务说明点击事件
            TextView serviceTermsLink = findViewById(R.id.service_terms_link);
            serviceTermsLink.setOnClickListener(v -> showServiceAgreement());
            
            // Add strikethrough to original prices
            TextView originalPricePermanent = findViewById(R.id.original_price_permanent);
            TextView originalPriceAnnual = findViewById(R.id.original_price_annual);
            originalPricePermanent.setPaintFlags(originalPricePermanent.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            originalPriceAnnual.setPaintFlags(originalPriceAnnual.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            
            // 初始状态为高级套餐选中
            checkPermanent.setVisibility(View.VISIBLE);
            checkAnnual.setVisibility(View.GONE);
            permanentPackage.setCardElevation(8f);
            annualPackage.setCardElevation(2f);
            
            // 设置初始状态下的蓝色边框
            permanentPackage.setCardBackgroundColor(getResources().getColor(R.color.white));
            annualPackage.setCardBackgroundColor(getResources().getColor(R.color.white));
            
            // 获取RelativeLayout
            View permanentContent = permanentPackage.getChildAt(0);
            View annualContent = annualPackage.getChildAt(0);
            
            if (permanentContent != null && annualContent != null) {
                permanentContent.setBackgroundResource(R.drawable.card_selected_bg);
                annualContent.setBackground(null);
                
                // 确保超值爆款标签在前面
                try {
                    if (permanentContent instanceof RelativeLayout) {
                        View childView = ((RelativeLayout) permanentContent).getChildAt(0);
                        if (childView instanceof FrameLayout) {
                            FrameLayout badgeContainer = (FrameLayout) childView;
                            badgeContainer.bringToFront();
                        }
                    }
                } catch (Exception e) {
                    // 处理可能的异常
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            // 处理整体初始化异常
            e.printStackTrace();
            Toast.makeText(this, "初始化界面时出现错误", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupPackageSelection() {
        // 设置高级套餐点击事件
        permanentPackage.setOnClickListener(v -> selectPackage("permanent"));
        
        // 设置年度套餐点击事件
        annualPackage.setOnClickListener(v -> selectPackage("annual"));
    }
    
    private void selectPackage(String packageType) {
        try {
            if (packageType.equals("permanent")) {
                checkPermanent.setVisibility(View.VISIBLE);
                checkAnnual.setVisibility(View.GONE);
                permanentPackage.setCardElevation(8f);
                annualPackage.setCardElevation(2f);
                // 添加蓝色边框背景
                permanentPackage.setCardBackgroundColor(getResources().getColor(R.color.white));
                annualPackage.setCardBackgroundColor(getResources().getColor(R.color.white));
                
                // 获取RelativeLayout
                View permanentContent = permanentPackage.getChildAt(0);
                View annualContent = annualPackage.getChildAt(0);
                
                if (permanentContent != null && annualContent != null) {
                    // 设置内容背景为淡蓝色边框背景
                    permanentContent.setBackgroundResource(R.drawable.card_selected_bg);
                    annualContent.setBackground(null);
    
                    // 确保超值爆款标签在前面
                    try {
                        if (permanentContent instanceof RelativeLayout) {
                            View childView = ((RelativeLayout) permanentContent).getChildAt(0);
                            if (childView instanceof FrameLayout) {
                                FrameLayout badgeContainer = (FrameLayout) childView;
                                badgeContainer.bringToFront();
                            }
                        }
                    } catch (Exception e) {
                        // 处理可能的异常
                        e.printStackTrace();
                    }
                }
                
                selectedPackage = "permanent";
            } else {
                checkPermanent.setVisibility(View.GONE);
                checkAnnual.setVisibility(View.VISIBLE);
                permanentPackage.setCardElevation(2f);
                annualPackage.setCardElevation(8f);
                // 添加蓝色边框背景
                permanentPackage.setCardBackgroundColor(getResources().getColor(R.color.white));
                annualPackage.setCardBackgroundColor(getResources().getColor(R.color.white));
                
                // 获取RelativeLayout
                View permanentContent = permanentPackage.getChildAt(0);
                View annualContent = annualPackage.getChildAt(0);
                
                if (permanentContent != null && annualContent != null) {
                    // 设置内容背景为淡蓝色边框背景
                    permanentContent.setBackground(null);
                    annualContent.setBackgroundResource(R.drawable.card_selected_bg);
    
                    // 确保超值爆款标签在前面
                    try {
                        if (permanentContent instanceof RelativeLayout) {
                            View childView = ((RelativeLayout) permanentContent).getChildAt(0);
                            if (childView instanceof FrameLayout) {
                                FrameLayout badgeContainer = (FrameLayout) childView;
                                badgeContainer.bringToFront();
                            }
                        }
                    } catch (Exception e) {
                        // 处理可能的异常
                        e.printStackTrace();
                    }
                }
                
                selectedPackage = "annual";
            }
        } catch (Exception e) {
            // 处理整个方法的异常
            e.printStackTrace();
            Toast.makeText(this, "选择套餐时出现错误", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showPaymentConfirmation() {
        String packageTitle = selectedPackage.equals("permanent") ? "图片清除(高级版)" : "图片清除(一年)";
        String packagePrice = selectedPackage.equals("permanent") ? "¥158" : "¥78";
        
        new AlertDialog.Builder(this)
                .setTitle("确认支付")
                .setMessage("服务：" + packageTitle + "\n价格：" + packagePrice)
                .setPositiveButton("确认支付", (dialog, which) -> processPayment())
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void processPayment() {
        try {
            // 显示处理中状态
            processingLayout.setVisibility(View.VISIBLE);
            payButton.setEnabled(false);
            
            // 模拟支付处理
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    // Set payment success flag in shared preferences
                    SharedPreferences prefs = getSharedPreferences("payment_prefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("payment_success", true).apply();
                    
                    // 支付成功后跳转到支付成功页面
                    Intent successIntent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                    
                    String packageTitle = selectedPackage.equals("permanent") ? "图片清除(高级版)" : "图片清除(一年)";
                    String packagePrice = selectedPackage.equals("permanent") ? "¥158" : "¥78";
                    
                    successIntent.putExtra("service_type", packageTitle);
                    successIntent.putExtra("service_price", packagePrice);
                    
                    // 添加前一个页面信息
                    successIntent = addPreviousActivityInfo(successIntent, PaymentActivity.this.getClass().getName());
                    
                    startActivity(successIntent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(PaymentActivity.this, "跳转失败，请重试", Toast.LENGTH_SHORT).show();
                    processingLayout.setVisibility(View.GONE);
                    payButton.setEnabled(true);
                }
            }, PAYMENT_DELAY);
        } catch (Exception e) {
            Toast.makeText(this, "支付处理失败，请重试", Toast.LENGTH_SHORT).show();
            processingLayout.setVisibility(View.GONE);
            payButton.setEnabled(true);
        }
    }
    
    private void showServiceAgreement() {
        try {
            Intent intent = new Intent(this, ServiceAgreementActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "无法打开服务协议", Toast.LENGTH_SHORT).show();
        }
    }
    

    
    // 添加新方法：导航到订单页面
    private void goToOrderPage() {
        try {
            Log.d("PaymentActivity", "返回订单页面");
            Intent intent = new Intent(this, OrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("PaymentActivity", "导航到订单页面出错", e);
            // 如果发生错误，使用默认返回行为
            super.onBackPressed();
        }
    }
    
    // 重写返回按钮行为
    @Override
    public void onBackPressed() {
        // 无论是否处理支付，都显示"越早修复，几率越高"的提示对话框
        showRecoveryReminder();
    }
    
    // 添加新的退出确认对话框
    private void showExitConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit_service_title)
                .setMessage(R.string.exit_service_message)
                .setPositiveButton(R.string.confirm_exit, (dialog, which) -> {
                    // 确认退出
                    navigateBack();
                })
                .setNegativeButton(R.string.continue_service, (dialog, which) -> {
                    // 继续留在当前页面
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
    
    protected void navigateBack() {
        // 获取上一个页面信息，返回上一个Activity
        Intent intent = null;
        String returnTo = getIntent().getStringExtra("returnTo");
        
        if (returnTo != null) {
            switch (returnTo) {
                case "file_scan":
                    intent = new Intent(this, FileScanActivity.class);
                    break;
                case "video_scan":
                    intent = new Intent(this, VideoScanActivity.class);
                    break;
                case "audio_scan":
                    intent = new Intent(this, AudioScanActivity.class);
                    break;
                case "file_repair":
                    intent = new Intent(this, FileRepairActivity.class);
                    intent.putExtra("scan_percentage", 8);
                    break;
                case "video_repair":
                    intent = new Intent(this, VideoRepairActivity.class);
                    intent.putExtra("scan_percentage", 9);
                    break;
                case "audio_repair":
                    intent = new Intent(this, AudioRepairActivity.class);
                    intent.putExtra("scan_percentage", 10);
                    break;
                case "vip_membership":
                    intent = new Intent(this, VipMembershipActivity.class);
                    break;
                default:
                    intent = new Intent(this, MainActivity.class);
                    break;
            }
            // 需要重新扫描
            if (returnTo.contains("scan")) {
                intent.putExtra("startScan", true);
            }
            startActivity(intent);
        } else {
            // 如果没有指定返回页面，返回主页
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        finish();
    }
    
    private void showRecoveryReminder() {
        try {
            // 创建自定义对话框
            Dialog reminderDialog = new Dialog(this, R.style.FullScreenDialogStyle);
            reminderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            reminderDialog.setContentView(R.layout.dialog_recovery_reminder);
            reminderDialog.setCancelable(false);
            
            // 设置窗口背景透明
            if (reminderDialog.getWindow() != null) {
                reminderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reminderDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 
                        ViewGroup.LayoutParams.MATCH_PARENT);
                reminderDialog.getWindow().setGravity(android.view.Gravity.CENTER);
            }

            // 设置关闭按钮 - 点击返回订单页面
            ImageButton closeButton = reminderDialog.findViewById(R.id.close_button);
            if (closeButton != null) {
                closeButton.setOnClickListener(v -> {
                    reminderDialog.dismiss();
                    goToOrderPage();
                });
            }

            // 设置立即修复按钮
            Button repairButton = reminderDialog.findViewById(R.id.repair_now_button);
            if (repairButton != null) {
                repairButton.setOnClickListener(v -> {
                    reminderDialog.dismiss();
                    // 继续留在当前页面，不做任何操作
                });
            }

            // 显示对话框
            reminderDialog.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            // 如果对话框显示失败，返回上一页面
            navigateBack();
        }
    }
}

