package com.example.shoujixiufu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileSendActivity extends AppCompatActivity {

    private RecyclerView fileListRecyclerView;
    private Button addFileBtn;
    private Button sendFileBtn;
    private ProgressBar sendProgressBar;
    private TextView emptyStateText;
    private LinearLayout sendingLayout;
    private TextView sendingProgressText;
    private List<FileItem> fileItems;
    private FileListAdapter adapter;
    private int totalFiles = 0;
    private int sentFiles = 0;
    private Handler handler;
    private boolean isSending = false;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        addFileFromUri(uri);
                    } else if (result.getData().getClipData() != null) {
                        // 处理多选文件
                        for (int i = 0; i < result.getData().getClipData().getItemCount(); i++) {
                            Uri fileUri = result.getData().getClipData().getItemAt(i).getUri();
                            addFileFromUri(fileUri);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_send);

        // 初始化视图
        fileListRecyclerView = findViewById(R.id.file_list);
        addFileBtn = findViewById(R.id.add_file_btn);
        sendFileBtn = findViewById(R.id.send_file_btn);
        sendProgressBar = findViewById(R.id.send_progress);
        emptyStateText = findViewById(R.id.empty_state_text);
        sendingLayout = findViewById(R.id.sending_layout);
        sendingProgressText = findViewById(R.id.sending_progress_text);

        // 设置返回按钮
        ImageButton backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> onBackPressed());

        // 初始化文件列表
        fileItems = new ArrayList<>();
        adapter = new FileListAdapter(fileItems, this::removeFile);
        fileListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileListRecyclerView.setAdapter(adapter);

        // 设置添加文件按钮
        addFileBtn.setOnClickListener(v -> openFilePicker());

        // 设置发送按钮
        sendFileBtn.setOnClickListener(v -> {
            if (fileItems.isEmpty()) {
                Toast.makeText(this, R.string.please_select_files, Toast.LENGTH_SHORT).show();
            } else {
                startSendingFiles();
            }
        });

        // 初始化Handler
        handler = new Handler(Looper.getMainLooper());

        // 更新UI状态
        updateUIState();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        filePickerLauncher.launch(intent);
    }

    private void addFileFromUri(Uri uri) {
        String fileName = getFileNameFromUri(uri);
        String fileType = getContentResolver().getType(uri);
        long fileSize = getFileSizeFromUri(uri);
        
        // 确定文件图标
        int iconResId;
        if (fileType != null) {
            if (fileType.startsWith("image/")) {
                iconResId = R.drawable.ic_file_image;
            } else if (fileType.startsWith("video/")) {
                iconResId = R.drawable.ic_file_video;
            } else if (fileType.startsWith("application/pdf")) {
                iconResId = R.drawable.ic_file_pdf;
            } else if (fileType.contains("spreadsheet") || fileType.contains("excel")) {
                iconResId = R.drawable.ic_file_excel;
            } else if (fileType.contains("document") || fileType.contains("word")) {
                iconResId = R.drawable.ic_file_doc;
            } else {
                iconResId = R.drawable.ic_file;
            }
        } else {
            iconResId = R.drawable.ic_file;
        }

        // 创建文件项并添加到列表
        FileItem fileItem = new FileItem(uri, fileName, formatFileSize(fileSize), iconResId);
        fileItems.add(fileItem);
        adapter.notifyItemInserted(fileItems.size() - 1);
        
        // 更新UI状态
        updateUIState();
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private long getFileSizeFromUri(Uri uri) {
        long size = 0;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (sizeIndex != -1) {
                        size = cursor.getLong(sizeIndex);
                    }
                }
            }
        }
        if (size == 0) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    size = inputStream.available();
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void removeFile(int position) {
        if (position >= 0 && position < fileItems.size()) {
            fileItems.remove(position);
            adapter.notifyItemRemoved(position);
            updateUIState();
        }
    }

    private void updateUIState() {
        if (fileItems.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            fileListRecyclerView.setVisibility(View.GONE);
            sendFileBtn.setEnabled(false);
        } else {
            emptyStateText.setVisibility(View.GONE);
            fileListRecyclerView.setVisibility(View.VISIBLE);
            sendFileBtn.setEnabled(!isSending);
        }
        
        addFileBtn.setEnabled(!isSending);
        sendingLayout.setVisibility(isSending ? View.VISIBLE : View.GONE);
    }

    private void startSendingFiles() {
        isSending = true;
        totalFiles = fileItems.size();
        sentFiles = 0;
        sendProgressBar.setProgress(0);
        updateUIState();

        // 模拟发送文件过程
        simulateFileSending();
    }

    private void simulateFileSending() {
        // 模拟文件发送进度
        new Thread(() -> {
            for (int i = 0; i <= 100; i += 5) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                final int progress = i;
                handler.post(() -> {
                    sendProgressBar.setProgress(progress);
                    sendingProgressText.setText(getString(R.string.sending_progress, progress));
                    
                    // 更新已发送文件数
                    if (progress > 0 && progress % 20 == 0 && sentFiles < totalFiles) {
                        sentFiles++;
                    }
                });
            }
            
            // 发送完成
            handler.post(() -> {
                isSending = false;
                updateUIState();
                showSendCompleteDialog();
            });
        }).start();
    }

    private void showSendCompleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.send_complete)
                .setMessage(R.string.files_sent_success)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    // 清空文件列表
                    fileItems.clear();
                    adapter.notifyDataSetChanged();
                    updateUIState();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (isSending) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.cancel)
                    .setMessage("正在发送文件，确定要取消吗？")
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        isSending = false;
                        updateUIState();
                        super.onBackPressed();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    // 文件项数据类
    public static class FileItem {
        private Uri uri;
        private String name;
        private String size;
        private int iconResId;

        public FileItem(Uri uri, String name, String size, int iconResId) {
            this.uri = uri;
            this.name = name;
            this.size = size;
            this.iconResId = iconResId;
        }

        public Uri getUri() {
            return uri;
        }

        public String getName() {
            return name;
        }

        public String getSize() {
            return size;
        }

        public int getIconResId() {
            return iconResId;
        }
    }

    // 文件列表适配器接口
    public interface FileListAdapterCallback {
        void onRemoveFile(int position);
    }
} 