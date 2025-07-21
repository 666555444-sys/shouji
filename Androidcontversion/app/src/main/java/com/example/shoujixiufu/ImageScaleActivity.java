package com.example.shoujixiufu;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageScaleActivity extends AppCompatActivity {

    private ImageView imageView;
    private SeekBar scaleSeekBar;
    private TextView scalePercentText;
    private Button applyBtn;
    private Button saveBtn;
    private ProgressBar progressBar;
    private Bitmap originalBitmap;
    private Bitmap currentBitmap;
    private int scalePercent = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_scale);

        // 初始化视图
        imageView = findViewById(R.id.image_preview);
        scaleSeekBar = findViewById(R.id.scale_seekbar);
        scalePercentText = findViewById(R.id.scale_percent_text);
        applyBtn = findViewById(R.id.apply_btn);
        saveBtn = findViewById(R.id.save_btn);
        progressBar = findViewById(R.id.progress_bar);

        // 设置返回按钮
        ImageButton backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> onBackPressed());

        // 加载示例图片
        // 实际应用中，应该从相册或相机获取图片
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_image);
        currentBitmap = originalBitmap;
        imageView.setImageBitmap(currentBitmap);

        // 设置缩放滑块
        setupScaleSeekBar();

        // 设置应用按钮
        applyBtn.setOnClickListener(v -> applyScale());

        // 设置保存按钮
        saveBtn.setOnClickListener(v -> saveImage());
    }

    private void setupScaleSeekBar() {
        scaleSeekBar.setMax(200);  // 20% - 200%
        scaleSeekBar.setProgress(100);  // 默认100%
        updateScaleText(100);

        scaleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 确保最小缩放为20%
                int actualProgress = Math.max(20, progress);
                if (fromUser && actualProgress != progress) {
                    seekBar.setProgress(actualProgress);
                    return;
                }
                
                scalePercent = actualProgress;
                updateScaleText(actualProgress);
                
                // 实时预览缩放效果
                float scale = actualProgress / 100f;
                imageView.setScaleX(scale);
                imageView.setScaleY(scale);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 不需要实现
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 不需要实现
            }
        });
    }

    private void updateScaleText(int percent) {
        scalePercentText.setText(getString(R.string.scale_percent, percent));
    }

    private void applyScale() {
        progressBar.setVisibility(View.VISIBLE);
        applyBtn.setEnabled(false);
        scaleSeekBar.setEnabled(false);

        // 在后台线程中处理缩放操作
        new Thread(() -> {
            // 计算缩放比例
            float scale = scalePercent / 100f;
            
            // 创建缩放矩阵
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            
            // 执行缩放
            Bitmap scaledBitmap = Bitmap.createBitmap(
                    originalBitmap, 
                    0, 0, 
                    originalBitmap.getWidth(), 
                    originalBitmap.getHeight(), 
                    matrix, 
                    true);
            
            currentBitmap = scaledBitmap;

            // 在UI线程中更新界面
            runOnUiThread(() -> {
                imageView.setScaleX(1f);
                imageView.setScaleY(1f);
                imageView.setImageBitmap(currentBitmap);
                progressBar.setVisibility(View.GONE);
                applyBtn.setEnabled(true);
                scaleSeekBar.setEnabled(true);
                Toast.makeText(ImageScaleActivity.this, R.string.scale_applied, Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void saveImage() {
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);

        new Thread(() -> {
            boolean success = false;
            Uri imageUri = null;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10及以上使用MediaStore API
                    ContentResolver resolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String fileName = "IMG_SCALE_" + sdf.format(new Date()) + ".jpg";
                    
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                    
                    imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    if (imageUri != null) {
                        OutputStream outputStream = resolver.openOutputStream(imageUri);
                        if (outputStream != null) {
                            success = currentBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                            outputStream.close();
                        }
                    }
                } else {
                    // Android 9及以下使用传统File API
                    File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String fileName = "IMG_SCALE_" + sdf.format(new Date()) + ".jpg";
                    File imageFile = new File(imagesDir, fileName);
                    
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    success = currentBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();
                    
                    // 通知媒体库更新
                    MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getAbsolutePath(), fileName, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            final boolean finalSuccess = success;
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                saveBtn.setEnabled(true);
                if (finalSuccess) {
                    Toast.makeText(ImageScaleActivity.this, R.string.image_saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImageScaleActivity.this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onBackPressed() {
        // 如果图片已修改，显示确认对话框
        if (currentBitmap != originalBitmap) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.discard_changes)
                    .setMessage(R.string.discard_changes_message)
                    .setPositiveButton(R.string.yes, (dialog, which) -> super.onBackPressed())
                    .setNegativeButton(R.string.no, null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
} 