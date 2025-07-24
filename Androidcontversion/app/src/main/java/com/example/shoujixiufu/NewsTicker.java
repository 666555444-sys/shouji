package com.example.shoujixiufu;

import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 管理资讯栏目的工具类
 */
public class NewsTicker {
    private ViewFlipper viewFlipper;
    private List<String> phoneNumbers;
    private List<String> newsTexts;
    private Random random;

    /**
     * 构造函数
     * @param rootView 包含ViewFlipper的根视图
     */
    public NewsTicker(View rootView) {
        viewFlipper = rootView.findViewById(R.id.news_flipper);
        random = new Random();
        initializeLists();
    }
    
    /**
     * 初始化电话号码和新闻文本列表
     */
    private void initializeLists() {
        // 生成随机电话号码列表
        phoneNumbers = new ArrayList<>();
        String[] prefixes = {"130", "131", "132", "133", "134", "135", "136", "137", "138", "139", 
                             "150", "151", "152", "155", "156", "157", "158", "159", 
                             "176", "177", "178", "179", "186", "187", "188"};
        
        for (int i = 0; i < 10; i++) {
            String prefix = prefixes[random.nextInt(prefixes.length)];
            String maskedNumber = prefix + "****" + String.format("%04d", random.nextInt(10000));
            phoneNumbers.add(maskedNumber);
        }
        
        // 新闻文本列表
        newsTexts = new ArrayList<>();
        newsTexts.add("刚刚成功恢复了微信数据");
        newsTexts.add("10分钟前恢复了相册照片");
        newsTexts.add("刚刚购买了图片清除服务");
        newsTexts.add("成功清理了手机内存");
        newsTexts.add("使用垃圾清理功能提速30%");
        newsTexts.add("一键恢复了已删除的视频");
        newsTexts.add("刚刚完成了微信聊天记录备份");
        newsTexts.add("5分钟前购买了高级服务");
    }
    
    /**
     * 设置自定义资讯内容
     */
    public void setupNewsContent() {
        if (viewFlipper == null) return;
        
        // 清除现有的所有视图
        viewFlipper.removeAllViews();
        
        // 添加随机生成的资讯
        for (int i = 0; i < 6; i++) {
            String phone = phoneNumbers.get(random.nextInt(phoneNumbers.size()));
            String news = newsTexts.get(random.nextInt(newsTexts.size()));
            
            TextView textView = new TextView(viewFlipper.getContext());
            textView.setLayoutParams(new ViewFlipper.LayoutParams(
                    ViewFlipper.LayoutParams.MATCH_PARENT,
                    ViewFlipper.LayoutParams.WRAP_CONTENT));
            textView.setText(phone + " " + news);
            textView.setTextColor(viewFlipper.getContext().getResources().getColor(R.color.text_primary));
            textView.setTextSize(14);
            textView.setSingleLine(true);
            textView.setEllipsize(android.text.TextUtils.TruncateAt.END);
            
            viewFlipper.addView(textView);
        }
        
        // 设置动画间隔为10秒
        viewFlipper.setFlipInterval(10000);
        viewFlipper.startFlipping();
    }
} 