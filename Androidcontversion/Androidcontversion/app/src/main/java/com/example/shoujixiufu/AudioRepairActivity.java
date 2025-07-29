package com.example.shoujixiufu;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AudioRepairActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvFilterTime, tvFilterSize;
    private CardView cardScanProgress;
    private Button btnRecover, btnBottomAction;
    private RecyclerView audioList;
    private TextView tvProgressText;

    private List<AudioItem> audios = new ArrayList<>();
    private boolean sortByTimeDesc = true; // 默认按时间降序
    private boolean sortBySizeDesc = false;
    private int scanPercentage = 10;
    private int filesFound = 5;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_repair);

        // 获取从ScanActivity传递过来的数据
        if (getIntent().hasExtra("scan_percentage")) {
            scanPercentage = getIntent().getIntExtra("scan_percentage", 10);
        }
        if (getIntent().hasExtra("files_found")) {
            filesFound = getIntent().getIntExtra("files_found", 5);
        }

        initViews();
        setupListeners();
        loadAudios();
    }

    private void initViews() {
        // 工具栏
        btnBack = findViewById(R.id.btn_back);

        // 过滤器
        tvFilterTime = findViewById(R.id.filter_time);
        tvFilterSize = findViewById(R.id.filter_size);

        // 卡片和容器
        cardScanProgress = findViewById(R.id.card_scan_progress);

        // 按钮
        btnRecover = findViewById(R.id.btn_recover);
        btnBottomAction = findViewById(R.id.btn_bottom_action);

        // 音频列表
        audioList = findViewById(R.id.audio_list);
        audioList.setLayoutManager(new LinearLayoutManager(this));

        // 进度文本
        tvProgressText = findViewById(R.id.tv_progress_text);
        tvProgressText.setText(getString(R.string.scanned_progress).replace("10%", scanPercentage + "%"));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> showExitConfirmDialog());

        // 过滤器监听器
        tvFilterTime.setOnClickListener(v -> {
            sortByTimeDesc = !sortByTimeDesc;
            sortBySizeDesc = false;
            updateFilterUI();
            sortAndUpdateAudioList();
        });

        tvFilterSize.setOnClickListener(v -> {
            sortBySizeDesc = !sortBySizeDesc;
            sortByTimeDesc = false;
            updateFilterUI();
            sortAndUpdateAudioList();
        });

        // 按钮监听器
        btnRecover.setOnClickListener(v -> navigateToPayment());
        btnBottomAction.setOnClickListener(v -> navigateToPayment());
    }

    private void updateFilterUI() {
        // 更新时间过滤器UI
        tvFilterTime.setBackgroundResource(sortByTimeDesc || sortByTimeDesc == false && sortBySizeDesc == false
                ? R.drawable.bg_filter_active
                : R.drawable.bg_filter_inactive);
        tvFilterTime.setTextColor(getResources().getColor(
                sortByTimeDesc || sortByTimeDesc == false && sortBySizeDesc == false
                        ? R.color.primary_color
                        : R.color.text_secondary));

        // 更新大小过滤器UI
        tvFilterSize.setBackgroundResource(sortBySizeDesc
                ? R.drawable.bg_filter_active
                : R.drawable.bg_filter_inactive);
        tvFilterSize.setTextColor(getResources().getColor(
                sortBySizeDesc
                        ? R.color.primary_color
                        : R.color.text_secondary));
    }

    private void loadAudios() {
        new Thread(() -> {
            try {
                // 加载本地音频文件
                List<AudioItem> localAudios = getAllLocalAudios();
                if (localAudios.isEmpty()) {
                    localAudios = getDefaultAudioItems();
                }
                
                // 保存音频列表
                this.audios.clear();
                this.audios.addAll(localAudios);
                
                // 排序并更新UI
                handler.post(() -> {
                    sortAndUpdateAudioList();
                    
                    // 显示音频列表
                    audioList.setVisibility(View.VISIBLE);
                    btnBottomAction.setVisibility(View.VISIBLE);
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    // 显示默认结果
                    this.audios.clear();
                    this.audios.addAll(getDefaultAudioItems());
                    sortAndUpdateAudioList();
                });
            }
        }).start();
    }

    private void sortAndUpdateAudioList() {
        if (sortByTimeDesc) {
            // 按时间降序排序
            Collections.sort(audios, new Comparator<AudioItem>() {
                @Override
                public int compare(AudioItem o1, AudioItem o2) {
                    // 将"今天"、"昨天"等转换为可比较的值
                    String date1 = o1.getDate();
                    String date2 = o2.getDate();
                    
                    if (date1.equals("今天") && !date2.equals("今天")) return -1;
                    if (!date1.equals("今天") && date2.equals("今天")) return 1;
                    if (date1.equals("昨天") && !date2.equals("昨天")) return -1;
                    if (!date1.equals("昨天") && date2.equals("昨天")) return 1;
                    
                    // 其他情况按字母顺序排序（MM-dd格式，越大越新）
                    return date2.compareTo(date1);
                }
            });
        } else if (sortBySizeDesc) {
            // 按大小降序排序
            Collections.sort(audios, new Comparator<AudioItem>() {
                @Override
                public int compare(AudioItem o1, AudioItem o2) {
                    // 将"1.2MB"、"650KB"等转换为可比较的值
                    return convertSizeToBytes(o2.getSize()) - convertSizeToBytes(o1.getSize());
                }
            });
        }
        
        // 更新音频列表
        AudioAdapter adapter = new AudioAdapter(audios);
        audioList.setAdapter(adapter);
    }
    
    // 将文件大小字符串转换为字节大小以便比较
    private int convertSizeToBytes(String sizeStr) {
        try {
            sizeStr = sizeStr.trim().toUpperCase();
            float size = Float.parseFloat(sizeStr.replaceAll("[^\\d.]", ""));
            
            if (sizeStr.contains("KB")) {
                return (int)(size * 1024);
            } else if (sizeStr.contains("MB")) {
                return (int)(size * 1024 * 1024);
            } else if (sizeStr.contains("GB")) {
                return (int)(size * 1024 * 1024 * 1024);
            } else {
                return (int)size; // 字节
            }
        } catch (Exception e) {
            return 0;
        }
    }

    private List<AudioItem> getAllLocalAudios() {
        List<AudioItem> allAudios = new ArrayList<>();
        
        try {
            // 使用MediaStore获取音频文件
            ContentResolver contentResolver = getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            
            String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST
            };
            
            Cursor cursor = contentResolver.query(uri, projection, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext() && allAudios.size() < 30) { // 限制最多显示30个音频文件
                    try {
                        String title = cursor.getString(0);
                        long size = cursor.getLong(1);
                        long date = cursor.getLong(2) * 1000; // 转换为毫秒
                        long duration = cursor.getLong(3);
                        String artist = cursor.getString(4);
                        
                        AudioItem item = new AudioItem(
                                title,
                                formatFileSize(size),
                                formatDate(date),
                                formatDuration(duration),
                                artist == null || artist.isEmpty() ? "未知艺术家" : artist,
                                false
                        );
                        allAudios.add(item);
                    } catch (Exception e) {
                        // 跳过有问题的文件
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return allAudios;
    }

    // 格式化文件大小
    private String formatFileSize(long size) {
        if (size <= 0)
            return "0 B";
        
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
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
    
    // 格式化时长
    private String formatDuration(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    // 获取默认的音频列表
    private List<AudioItem> getDefaultAudioItems() {
        List<AudioItem> items = new ArrayList<>();
        
        // 添加一些样本音频文件
        items.add(new AudioItem("流行音乐1", "4.5MB", "今天", "3:45", "流行歌手", false));
        items.add(new AudioItem("古典音乐集锦", "12.8MB", "今天", "8:20", "莫扎特", false));
        items.add(new AudioItem("录音笔记", "1.7MB", "昨天", "2:15", "我", false));
        items.add(new AudioItem("语音消息", "850KB", "昨天", "1:05", "张三", false));
        items.add(new AudioItem("会议录音", "6.2MB", "04-12", "5:30", "未知艺术家", false));
        items.add(new AudioItem("播客节目", "18.5MB", "04-10", "15:20", "知识FM", false));
        items.add(new AudioItem("英语听力", "5.3MB", "04-08", "4:45", "英语学习", false));
        items.add(new AudioItem("钢琴曲", "8.7MB", "04-05", "7:12", "贝多芬", false));
        
        return items;
    }

    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_service_title)
              .setMessage(R.string.exit_service_message)
              .setNegativeButton(R.string.continue_service, (dialog, which) -> dialog.dismiss())
              .setPositiveButton(R.string.confirm_exit, (dialog, which) -> {
                  // 返回到MainActivity
                  Intent intent = new Intent(this, MainActivity.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                  startActivity(intent);
                  finish();
              })
              .setCancelable(false)
              .show();
    }

    private void navigateToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("returnTo", "audio_repair");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        showExitConfirmDialog();
        // 不需要调用super.onBackPressed()，因为我们想阻止默认的后退行为，显示确认对话框
    }

    // 音频项类
    public static class AudioItem {
        private String title;
        private String size;
        private String date;
        private String duration;
        private String artist;
        private boolean isChecked;

        public AudioItem(String title, String size, String date, String duration, String artist, boolean isChecked) {
            this.title = title;
            this.size = size;
            this.date = date;
            this.duration = duration;
            this.artist = artist;
            this.isChecked = isChecked;
        }

        public String getTitle() {
            return title;
        }

        public String getSize() {
            return size;
        }

        public String getDate() {
            return date;
        }

        public String getDuration() {
            return duration;
        }

        public String getArtist() {
            return artist;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    // 适配器类
    public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {
        private List<AudioItem> audios;

        public AudioAdapter(List<AudioItem> audios) {
            this.audios = audios;
        }

        @Override
        public AudioViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_audio, parent, false);
            return new AudioViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AudioViewHolder holder, int position) {
            AudioItem audio = audios.get(position);
            
            holder.tvTitle.setText(audio.getTitle());
            holder.tvDate.setText(audio.getDate());
            holder.tvSize.setText(audio.getSize());
            holder.tvDuration.setText(audio.getDuration());
            holder.tvArtist.setText(audio.getArtist());
            holder.checkbox.setChecked(audio.isChecked());
            
            holder.checkbox.setOnClickListener(v -> {
                boolean isChecked = holder.checkbox.isChecked();
                audio.setChecked(isChecked);
            });
            
            holder.itemView.setOnClickListener(v -> {
                boolean isChecked = !audio.isChecked();
                audio.setChecked(isChecked);
                holder.checkbox.setChecked(isChecked);
            });
        }

        @Override
        public int getItemCount() {
            return audios.size();
        }

        class AudioViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDate, tvSize, tvDuration, tvArtist;
            CheckBox checkbox;

            public AudioViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_audio_title);
                tvDate = itemView.findViewById(R.id.tv_audio_date);
                tvSize = itemView.findViewById(R.id.tv_audio_size);
                tvDuration = itemView.findViewById(R.id.tv_audio_duration);
                tvArtist = itemView.findViewById(R.id.tv_audio_artist);
                checkbox = itemView.findViewById(R.id.checkbox_audio);
            }
        }
    }
} 