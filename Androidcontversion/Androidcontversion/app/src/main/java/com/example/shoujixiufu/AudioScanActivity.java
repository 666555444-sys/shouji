package com.example.shoujixiufu;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Collections;

public class AudioScanActivity extends AppCompatActivity {

    private CardView cardScanProgress;
    private CardView cardAudioSelector;
    private RecyclerView audioList;
    private Button btnBottomAction;
    private FrameLayout scanningScreen;
    private ProgressBar scanProgressBar;
    private TextView tvScanPercentage;
    private TextView tvScanFiles;
    private TextView tvSelectedAudios;
    private Button btnBrowseAudio;
    private Button btnStartScan;
    private View scanCircleOuter;
    private View scanCircleMiddle;
    private View scanCircleInner;

    private boolean scanCompleted = false;
    private boolean modalShown = false;

    private ActivityResultLauncher<String[]> audioPickerLauncher;
    private List<Uri> selectedAudioFiles = new ArrayList<>();
    private List<AudioItem> scannedAudios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_scan);

        // Initialize views
        initViews();
        // Set click listeners
        setClickListeners();
        // Initialize the audio picker launcher
        initAudioPicker();

        // Check for automatic scan start
        if (getIntent().getBooleanExtra("startScan", false)) {
            startScanning();
        }
    }

    private void initViews() {
        try {
            // 基本视图初始化
        cardScanProgress = findViewById(R.id.card_scan_progress);
        cardAudioSelector = findViewById(R.id.card_audio_selector);
        audioList = findViewById(R.id.audio_list);
        btnBottomAction = findViewById(R.id.btn_bottom_action);
        scanningScreen = findViewById(R.id.scanning_screen);
        scanProgressBar = findViewById(R.id.scan_progress_bar);
            
            // 隐藏右上角的目录按钮
            ImageButton btnMenu = findViewById(R.id.btn_menu);
            if (btnMenu != null) {
                btnMenu.setVisibility(View.GONE);
            }
            
            // 扫描相关视图
        tvScanPercentage = findViewById(R.id.tv_scan_percentage);
        tvScanFiles = findViewById(R.id.tv_scan_files);
            tvSelectedAudios = findViewById(R.id.tv_selected_audios);
            btnBrowseAudio = findViewById(R.id.btn_browse_audio);
            btnStartScan = findViewById(R.id.btn_start_scan);
        scanCircleOuter = findViewById(R.id.scan_circle_outer);
        scanCircleMiddle = findViewById(R.id.scan_circle_middle);
        scanCircleInner = findViewById(R.id.scan_circle_inner);
            
            // 设置布局可见性
            scanningScreen.setVisibility(View.GONE);
            cardScanProgress.setVisibility(View.GONE);
            cardAudioSelector.setVisibility(View.VISIBLE);
            
            // 初始化音频选择器
            initAudioPicker();
            
            // 设置时间过滤器点击事件
            TextView filterTime = findViewById(R.id.filter_time);
            if (filterTime != null) {
                filterTime.setOnClickListener(v -> {
                    // 按时间排序音频
                    if (audioList.getAdapter() != null && audioList.getAdapter() instanceof AudioAdapter) {
                        AudioAdapter adapter = (AudioAdapter)audioList.getAdapter();
                        List<AudioItem> sortedByTime = new ArrayList<>(scannedAudios);
                        Collections.sort(sortedByTime, (a1, a2) -> a2.date.compareTo(a1.date));
                        
                        AudioAdapter newAdapter = new AudioAdapter(sortedByTime);
                        audioList.setAdapter(newAdapter);
                        
                        // 更新过滤器样式
                        filterTime.setBackgroundResource(R.drawable.bg_filter_active);
                        filterTime.setTextColor(getResources().getColor(R.color.primary_color));
                        
                        TextView filterSize = findViewById(R.id.filter_size);
                        if (filterSize != null) {
                            filterSize.setBackgroundResource(R.drawable.bg_filter_inactive);
                            filterSize.setTextColor(getResources().getColor(R.color.text_secondary));
                        }
                    }
                });
            }
            
            // 设置大小过滤器点击事件
            TextView filterSize = findViewById(R.id.filter_size);
            if (filterSize != null) {
                filterSize.setOnClickListener(v -> {
                    // 按大小排序音频
                    if (audioList.getAdapter() != null && audioList.getAdapter() instanceof AudioAdapter) {
                        List<AudioItem> sortedBySize = new ArrayList<>(scannedAudios);
                        Collections.sort(sortedBySize, (a1, a2) -> {
                            try {
                                // 从 "100MB" 格式解析
                                String s1 = a1.size.replaceAll("[^0-9.]", "");
                                String s2 = a2.size.replaceAll("[^0-9.]", "");
                                float f1 = Float.parseFloat(s1);
                                float f2 = Float.parseFloat(s2);
                                
                                // 考虑单位
                                if (a1.size.contains("GB")) f1 *= 1024;
                                if (a2.size.contains("GB")) f2 *= 1024;
                                
                                return Float.compare(f2, f1);
                            } catch (Exception e) {
                                return 0;
                            }
                        });
                        
                        AudioAdapter newAdapter = new AudioAdapter(sortedBySize);
                        audioList.setAdapter(newAdapter);
                        
                        // 更新过滤器样式
                        filterSize.setBackgroundResource(R.drawable.bg_filter_active);
                        filterSize.setTextColor(getResources().getColor(R.color.primary_color));
                        
                        TextView timeFilter = findViewById(R.id.filter_time);
                        if (timeFilter != null) {
                            timeFilter.setBackgroundResource(R.drawable.bg_filter_inactive);
                            timeFilter.setTextColor(getResources().getColor(R.color.text_secondary));
                        }
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(this, "视图初始化出错", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setClickListeners() {
        // 返回按钮点击事件
        findViewById(R.id.btn_back).setOnClickListener(v -> showExitConfirmDialog());

        // 选择音频文件按钮点击事件
        btnBrowseAudio.setOnClickListener(v -> {
            // 打开文件选择器
            audioPickerLauncher.launch(new String[]{"audio/*"});
        });
        
        // 开始扫描按钮点击事件
        btnStartScan.setOnClickListener(v -> startScanning());
        
        // 底部操作按钮点击事件
        btnBottomAction.setOnClickListener(v -> {
            showRecoveryDialog();
        });
        
        // 恢复按钮点击事件
        findViewById(R.id.btn_recover).setOnClickListener(v -> {
            showRecoveryDialog();
        });
    }

    private void initAudioPicker() {
        audioPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        selectedAudioFiles.clear();
                        selectedAudioFiles.addAll(uris);

                        StringBuilder audioNames = new StringBuilder();
                        audioNames.append(getString(R.string.selected_audios)).append(":\n");
                        
                        for (Uri uri : uris) {
                            String fileName = getFileNameFromUri(uri);
                            audioNames.append("• ").append(fileName).append("\n");
                        }

                        tvSelectedAudios.setText(audioNames.toString());
                        tvSelectedAudios.setVisibility(View.VISIBLE);
                        btnStartScan.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private String getFileNameFromUri(Uri uri) {
        String result = uri.getPath();
        int cut = result.lastIndexOf('/');
        if (cut != -1) {
            result = result.substring(cut + 1);
        }
        return result;
    }

    private void startScanning() {
        // Show scanning screen and hide selector
        scanningScreen.setVisibility(View.VISIBLE);
        cardAudioSelector.setVisibility(View.GONE);

        // Start scan animations
        startScanAnimation();

        // 扫描本地音频文件
        scanLocalAudio();
    }
    
    // 扫描本地音频文件 - 不再区分目录
    private void scanLocalAudio() {
        new Thread(() -> {
            try {
                // 显示初始进度
                runOnUiThread(() -> {
                    scanProgressBar.setProgress(0);
                    tvScanPercentage.setText("0%");
                    tvScanFiles.setText(getString(R.string.scanning_audio_files));
                    tvScanFiles.setAlpha(1f);
                });
                
                // 先创建音频列表，在扫描过程中填充
                List<AudioItem> audioItems = new ArrayList<>();
                
                // 模拟进度从0%开始，只到10%
                for(int progress = 0; progress <= 10; progress += 2) {
                    final int currentProgress = progress;
                    runOnUiThread(() -> {
                        scanProgressBar.setProgress(currentProgress);
                        tvScanPercentage.setText(currentProgress + "%");
                        
                        // 当进度达到一定程度，更新找到的音频数量
                        if(currentProgress > 5) {
                            int foundCount = audioItems.size() > 0 ? audioItems.size() : 4;
                            tvScanFiles.setText(String.format(getString(R.string.found_files), foundCount));
                        }
                    });
                    
                    // 如果进度到达5%，开始获取音频
                    if(progress == 5) {
                        // 获取部分本地音频
                        List<AudioItem> localAudios = getLocalAudio();
                        if (localAudios.size() > 0) {
                            audioItems.addAll(localAudios.subList(0, Math.min(5, localAudios.size())));
                        }
                    }
                    
                    Thread.sleep(250);
                }
                
                // 扫描到10%后完成
                final List<AudioItem> finalAudios = audioItems.isEmpty() ? createDefaultAudioItems() : audioItems;
                
                runOnUiThread(() -> {
                    scanProgressBar.setProgress(10);
                    tvScanPercentage.setText("10%");
                    int foundCount = finalAudios.size();
                    tvScanFiles.setText(String.format(getString(R.string.found_files), foundCount));
                    
                    // 完成扫描，保存结果
                    scannedAudios = finalAudios;
                    
                    // 延迟500ms后结束扫描动画，显示结果
                    new Handler().postDelayed(() -> finishScanning(finalAudios.size()), 500);
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                // 如果发生错误，使用默认数据
                runOnUiThread(() -> {
                    scannedAudios = createDefaultAudioItems();
                    finishScanning(scannedAudios.size());
                });
            }
        }).start();
    }
    
    // 获取本地音频文件 - 使用MediaStore API
    private List<AudioItem> getLocalAudio() {
        List<AudioItem> audioItems = new ArrayList<>();
        
        try {
            // 查询系统媒体库中的音频文件
            ContentResolver contentResolver = getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            
            // 创建与Android版本兼容的投影
            List<String> projectionList = new ArrayList<>();
            projectionList.add(MediaStore.Audio.Media.DISPLAY_NAME);
            projectionList.add(MediaStore.Audio.Media.SIZE);
            projectionList.add(MediaStore.Audio.Media.DATE_MODIFIED);
            
            // 有条件地添加DURATION (某些旧设备可能不支持)
            boolean hasDuration = false;
            try {
                projectionList.add(MediaStore.Audio.Media.DURATION);
                hasDuration = true;
            } catch (Exception e) {
                // 字段不存在或不可用
            }
            
            String[] projection = projectionList.toArray(new String[0]);
            
            Cursor cursor = contentResolver.query(uri, projection, null, null, null);
            if (cursor != null) {
                // 获取列索引，以防字段顺序不一致
                int nameIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
                int dateIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED);
                int durationIndex = hasDuration ? 
                        cursor.getColumnIndex(MediaStore.Audio.Media.DURATION) : -1;
                
                while (cursor.moveToNext() && audioItems.size() < 20) { // 限制最多20个音频文件
                    try {
                        String name = nameIndex >= 0 ? cursor.getString(nameIndex) : "未知";
                        long size = sizeIndex >= 0 ? cursor.getLong(sizeIndex) : 0;
                        long date = dateIndex >= 0 ? cursor.getLong(dateIndex) * 1000 : 0; // 转换为毫秒
                        
                        // 安全获取时长
                        String duration = "未知";
                        if (durationIndex >= 0) {
                            long durationMs = cursor.getLong(durationIndex);
                            if (durationMs > 0) {
                                duration = formatDuration(durationMs);
                            }
                        }
                        
                        AudioItem item = new AudioItem(
                                name,
                                duration,
                                formatSize(size),
                                formatDate(date)
                        );
                        audioItems.add(item);
                    } catch (Exception e) {
                        // 跳过有问题的条目
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 如果没有找到音频文件，使用默认数据
        if (audioItems.isEmpty()) {
            audioItems = createDefaultAudioItems();
        }
        
        return audioItems;
    }
    
    // 格式化文件大小
    private String formatSize(long size) {
        if (size <= 0) return "0B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + units[digitGroups];
    }
    
    // 格式化时长
    private String formatDuration(long milliseconds) {
        if (milliseconds <= 0) return "0:00";
        
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        seconds %= 60;
        minutes %= 60;
        
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        }
    }
    
    // 格式化日期
    private String formatDate(long timeMillis) {
        Date date = new Date(timeMillis);
        Calendar calendar = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        calendar.setTime(date);
        
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "今天";
        } else if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
            return "昨天";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
            return sdf.format(date);
        }
    }
    
    // 创建默认音频数据
    private List<AudioItem> createDefaultAudioItems() {
        List<AudioItem> items = new ArrayList<>();
        items.add(new AudioItem("歌曲1.mp3", "3:45", "4.2MB", "今天"));
        items.add(new AudioItem("录音文件.wav", "1:30", "2.5MB", "昨天"));
        items.add(new AudioItem("会议录音.m4a", "25:18", "12.7MB", "7-15"));
        return items;
    }

    // 完成扫描
    private void finishScanning(int filesFound) {
        // 隐藏扫描界面
        scanningScreen.setVisibility(View.GONE);
        
        // 显示扫描结果
        cardScanProgress.setVisibility(View.VISIBLE);
        btnBottomAction.setVisibility(View.VISIBLE);
        audioList.setVisibility(View.VISIBLE);
        scanCompleted = true;
        
        // 设置修复按钮点击事件
        findViewById(R.id.btn_recover).setOnClickListener(v -> showRecoveryDialog());
        btnBottomAction.setOnClickListener(v -> showRecoveryDialog());
        
        // 设置适配器，显示音频列表
        AudioAdapter adapter = new AudioAdapter(scannedAudios);
        audioList.setLayoutManager(new LinearLayoutManager(this));
        audioList.setAdapter(adapter);
        
        // 更新进度显示为10%
        TextView progressText = findViewById(R.id.tv_progress_text);
        if (progressText != null) {
            progressText.setText(getString(R.string.scanned_progress).replace("10%", "10%"));
        }
    }

    private void startScanAnimation() {
        // 外圆顺时针旋转
        RotateAnimation outerAnim = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        outerAnim.setDuration(2000);
        outerAnim.setRepeatCount(Animation.INFINITE);
        outerAnim.setInterpolator(new LinearInterpolator());
        scanCircleOuter.startAnimation(outerAnim);
        
        // 中圆逆时针旋转
        RotateAnimation middleAnim = new RotateAnimation(0f, -360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        middleAnim.setDuration(1700);
        middleAnim.setRepeatCount(Animation.INFINITE);
        middleAnim.setInterpolator(new LinearInterpolator());
        scanCircleMiddle.startAnimation(middleAnim);
        
        // 内圆顺时针旋转
        RotateAnimation innerAnim = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        innerAnim.setDuration(1400);
        innerAnim.setRepeatCount(Animation.INFINITE);
        innerAnim.setInterpolator(new LinearInterpolator());
        scanCircleInner.startAnimation(innerAnim);
    }
    
    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        if (scanningScreen.getVisibility() == View.VISIBLE) {
            // 正在扫描中的退出提示
            builder.setTitle(R.string.exit_confirmation)
                    .setMessage(R.string.exit_scan_message)
                    .setPositiveButton(R.string.yes, (dialog, which) -> finish())
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        } else {
            // 扫描完成后的退出提示
            builder.setTitle(R.string.exit_service_title)
                    .setMessage(R.string.exit_service_message)
                    .setPositiveButton(R.string.confirm_exit, (dialog, which) -> finish())
                    .setNegativeButton(R.string.continue_service, (dialog, which) -> dialog.dismiss());
        }
        builder.setCancelable(false).show();
    }

    private void showRecoveryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.recovery_dialog_title)
                .setMessage(R.string.recovery_dialog_content)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.recover, (dialog, which) -> {
                    // 导航到支付页面
                    Intent intent = new Intent(AudioScanActivity.this, PaymentActivity.class);
                    intent.putExtra("returnTo", "audio_scan");
                    startActivity(intent);
                })
                .setCancelable(false)
                .show();
    }

    // Simple data class for audio items
    public static class AudioItem {
        String name;
        String duration;
        String size;
        String date;

        AudioItem(String name, String duration, String size, String date) {
            this.name = name;
            this.duration = duration;
            this.size = size;
            this.date = date;
        }
    }

    // Adapter for the audio list
    private class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {
        private final List<AudioItem> audioItems;

        AudioAdapter(List<AudioItem> audioItems) {
            this.audioItems = audioItems;
        }

        @Override
        public AudioViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_audio, parent, false);
            return new AudioViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AudioViewHolder holder, int position) {
            AudioItem item = audioItems.get(position);
            holder.name.setText(item.name);
            holder.duration.setText(item.duration);
            holder.size.setText(item.size);
            holder.date.setText(item.date);
            
            // Set checkbox click listener
            holder.checkbox.setOnClickListener(v -> {
                // Toggle checked state
                holder.checkbox.setChecked(!holder.checkbox.isChecked());
            });
        }

        @Override
        public int getItemCount() {
            return audioItems.size();
        }

        class AudioViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView duration;
            TextView size;
            TextView date;
            CheckBox checkbox;

            AudioViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tv_audio_title);
                duration = itemView.findViewById(R.id.tv_audio_duration);
                size = itemView.findViewById(R.id.tv_audio_size);
                date = itemView.findViewById(R.id.tv_audio_date);
                checkbox = itemView.findViewById(R.id.checkbox_audio);
            }
        }
    }
} 