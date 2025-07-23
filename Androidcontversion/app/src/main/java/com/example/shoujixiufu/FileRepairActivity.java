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

public class FileRepairActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvFilterTime, tvFilterSize;
    private CardView cardScanProgress;
    private Button btnRecover, btnBottomAction;
    private RecyclerView fileList;
    private TextView tvProgressText;

    private List<FileScanActivity.FileItem> files = new ArrayList<>();
    private boolean sortByTimeDesc = true; // 默认按时间降序
    private boolean sortBySizeDesc = false;
    private int scanPercentage = 10;
    private int filesFound = 5;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_repair);

        // 获取从ScanActivity传递过来的数据
        if (getIntent().hasExtra("scan_percentage")) {
            scanPercentage = getIntent().getIntExtra("scan_percentage", 10);
        }
        if (getIntent().hasExtra("files_found")) {
            filesFound = getIntent().getIntExtra("files_found", 5);
        }

        initViews();
        setupListeners();
        loadFiles();
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

        // 文件列表
        fileList = findViewById(R.id.file_list);
        fileList.setLayoutManager(new LinearLayoutManager(this));

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
            sortAndUpdateFileList();
        });

        tvFilterSize.setOnClickListener(v -> {
            sortBySizeDesc = !sortBySizeDesc;
            sortByTimeDesc = false;
            updateFilterUI();
            sortAndUpdateFileList();
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

    private void loadFiles() {
        new Thread(() -> {
            try {
                // 加载本地文件
                List<FileScanActivity.FileItem> localFiles = getAllLocalFiles();
                if (localFiles.isEmpty()) {
                    localFiles = getDefaultFileItems();
                }
                
                // 保存文件列表
                this.files.clear();
                this.files.addAll(localFiles);
                
                // 排序并更新UI
                handler.post(() -> {
                    sortAndUpdateFileList();
                    
                    // 显示文件列表
                    fileList.setVisibility(View.VISIBLE);
                    btnBottomAction.setVisibility(View.VISIBLE);
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    // 显示默认结果
                    this.files.clear();
                    this.files.addAll(getDefaultFileItems());
                    sortAndUpdateFileList();
                });
            }
        }).start();
    }

    private void sortAndUpdateFileList() {
        if (sortByTimeDesc) {
            // 按时间降序排序
            Collections.sort(files, new Comparator<FileScanActivity.FileItem>() {
                @Override
                public int compare(FileScanActivity.FileItem o1, FileScanActivity.FileItem o2) {
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
            Collections.sort(files, new Comparator<FileScanActivity.FileItem>() {
                @Override
                public int compare(FileScanActivity.FileItem o1, FileScanActivity.FileItem o2) {
                    // 将"1.2MB"、"650KB"等转换为可比较的值
                    return convertSizeToBytes(o2.getSize()) - convertSizeToBytes(o1.getSize());
                }
            });
        }
        
        // 更新文件列表
        FileAdapter adapter = new FileAdapter(this, files);
        fileList.setAdapter(adapter);
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

    private List<FileScanActivity.FileItem> getAllLocalFiles() {
        List<FileScanActivity.FileItem> allFiles = new ArrayList<>();
        
        try {
            // 使用MediaStore获取所有文件类型
            ContentResolver contentResolver = getContentResolver();
            Uri uri = MediaStore.Files.getContentUri("external");
            
            String[] projection = {
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MIME_TYPE
            };
            
            Cursor cursor = contentResolver.query(uri, projection, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext() && allFiles.size() < 30) { // 限制最多显示30个文件
                    try {
                        String name = cursor.getString(0);
                        long size = cursor.getLong(1);
                        long date = cursor.getLong(2) * 1000; // 转换为毫秒
                        String mimeType = cursor.getString(3);
                        
                        String icon = "F"; // 默认文件图标
                        if (mimeType != null) {
                            if (mimeType.contains("image/")) icon = "IMG";
                            else if (mimeType.contains("video/")) icon = "VID";
                            else if (mimeType.contains("audio/")) icon = "AUD";
                            else if (mimeType.contains("text/")) icon = "TXT";
                            else if (mimeType.contains("pdf")) icon = "PDF";
                        }
                        
                        FileScanActivity.FileItem item = new FileScanActivity.FileItem(
                                name,
                                formatFileSize(size),
                                formatDate(date),
                                icon,
                                R.drawable.bg_file_icon_doc
                        );
                        allFiles.add(item);
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
        
        return allFiles;
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

    // 获取默认的文件列表
    private List<FileScanActivity.FileItem> getDefaultFileItems() {
        List<FileScanActivity.FileItem> items = new ArrayList<>();
        
        // 添加一些样本文件
        items.add(new FileScanActivity.FileItem("项目计划书.docx", "1.2MB", "今天", "DOC", R.drawable.bg_file_icon_doc));
        items.add(new FileScanActivity.FileItem("财务报表.xlsx", "650KB", "今天", "XLS", R.drawable.bg_file_icon_doc));
        items.add(new FileScanActivity.FileItem("产品发布会.pptx", "5.7MB", "昨天", "PPT", R.drawable.bg_file_icon_doc));
        items.add(new FileScanActivity.FileItem("合同文件.pdf", "2.4MB", "昨天", "PDF", R.drawable.bg_file_icon_doc));
        items.add(new FileScanActivity.FileItem("程序源代码.zip", "7.5MB", "04-15", "ZIP", R.drawable.bg_file_icon_doc));
        items.add(new FileScanActivity.FileItem("学习笔记.txt", "125KB", "04-12", "TXT", R.drawable.bg_file_icon_doc));
        items.add(new FileScanActivity.FileItem("会议记录.docx", "890KB", "04-10", "DOC", R.drawable.bg_file_icon_doc));
        items.add(new FileScanActivity.FileItem("销售数据.xlsx", "1.5MB", "04-08", "XLS", R.drawable.bg_file_icon_doc));
        
        return items;
    }

    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_service_title)
              .setMessage(R.string.exit_service_message)
              .setNegativeButton(R.string.continue_service, (dialog, which) -> dialog.dismiss())
              .setPositiveButton(R.string.confirm_exit, (dialog, which) -> finish())
              .setCancelable(false)
              .show();
    }

    private void navigateToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("returnTo", "file_repair");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        showExitConfirmDialog();
    }

    // 适配器类
    public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
        private List<FileScanActivity.FileItem> files;
        private android.content.Context context;

        public FileAdapter(android.content.Context context, List<FileScanActivity.FileItem> files) {
            this.context = context;
            this.files = files;
        }

        @Override
        public FileViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_file, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FileViewHolder holder, int position) {
            FileScanActivity.FileItem file = files.get(position);
            
            holder.tvFileName.setText(file.getName());
            holder.tvFileDate.setText(file.getDate());
            holder.tvFileSize.setText(file.getSize());
            holder.tvFileIcon.setText(file.getIcon());
            holder.fileIconContainer.setBackgroundResource(file.getIconBackground());
            holder.checkbox.setChecked(file.isChecked());
            
            holder.checkbox.setOnClickListener(v -> {
                boolean isChecked = holder.checkbox.isChecked();
                file.setChecked(isChecked);
            });
            
            holder.itemView.setOnClickListener(v -> {
                boolean isChecked = !file.isChecked();
                file.setChecked(isChecked);
                holder.checkbox.setChecked(isChecked);
            });
        }

        @Override
        public int getItemCount() {
            return files.size();
        }

        class FileViewHolder extends RecyclerView.ViewHolder {
            TextView tvFileName, tvFileDate, tvFileSize, tvFileIcon;
            View fileIconContainer;
            CheckBox checkbox;

            public FileViewHolder(View itemView) {
                super(itemView);
                tvFileName = itemView.findViewById(R.id.tv_file_name);
                tvFileDate = itemView.findViewById(R.id.tv_file_date);
                tvFileSize = itemView.findViewById(R.id.tv_file_size);
                tvFileIcon = itemView.findViewById(R.id.tv_file_icon);
                fileIconContainer = itemView.findViewById(R.id.file_icon_container);
                checkbox = itemView.findViewById(R.id.checkbox_file);
            }
        }
    }
} 