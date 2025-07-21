package com.example.shoujixiufu;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageCropActivity extends AppCompatActivity {

    private ImageView imageView;
    private ImageView cropOverlay;
    private Button rotateBtn;
    private Button cropBtn;
    private Button saveBtn;
    private ProgressBar progressBar;
    private Bitmap originalBitmap;
    private Bitmap currentBitmap;
    private float rotationAngle = 0;
    private RectF cropRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        // 初始化视图
        imageView = findViewById(R.id.image_preview);
        cropOverlay = findViewById(R.id.crop_overlay);
        rotateBtn = findViewById(R.id.rotate_btn);
        cropBtn = findViewById(R.id.crop_btn);
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

        // 初始化裁剪区域
        cropRect = new RectF(
                imageView.getLeft() + imageView.getWidth() * 0.2f,
                imageView.getTop() + imageView.getHeight() * 0.2f,
                imageView.getRight() - imageView.getWidth() * 0.2f,
                imageView.getBottom() - imageView.getHeight() * 0.2f
        );

        // 设置旋转按钮
        rotateBtn.setOnClickListener(v -> rotateImage());

        // 设置裁剪按钮
        cropBtn.setOnClickListener(v -> cropImage());

        // 设置保存按钮
        saveBtn.setOnClickListener(v -> saveImage());
    }

    private void rotateImage() {
        rotationAngle += 90;
        if (rotationAngle >= 360) {
            rotationAngle = 0;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        currentBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(currentBitmap);
    }

    private void cropImage() {
        progressBar.setVisibility(View.VISIBLE);
        cropBtn.setEnabled(false);
        rotateBtn.setEnabled(false);

        // 在后台线程中处理裁剪操作
        new Thread(() -> {
            // 计算裁剪区域
            int width = currentBitmap.getWidth();
            int height = currentBitmap.getHeight();
            int cropX = (int) (width * 0.2f);
            int cropY = (int) (height * 0.2f);
            int cropWidth = (int) (width * 0.6f);
            int cropHeight = (int) (height * 0.6f);

            // 执行裁剪
            Bitmap croppedBitmap = Bitmap.createBitmap(currentBitmap, cropX, cropY, cropWidth, cropHeight);
            currentBitmap = croppedBitmap;

            // 在UI线程中更新界面
            runOnUiThread(() -> {
                imageView.setImageBitmap(currentBitmap);
                progressBar.setVisibility(View.GONE);
                cropBtn.setEnabled(true);
                rotateBtn.setEnabled(true);
                Toast.makeText(ImageCropActivity.this, R.string.crop_success, Toast.LENGTH_SHORT).show();
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
                    String fileName = "IMG_CROP_" + sdf.format(new Date()) + ".jpg";
                    
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
                    String fileName = "IMG_CROP_" + sdf.format(new Date()) + ".jpg";
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
                    Toast.makeText(ImageCropActivity.this, R.string.image_saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImageCropActivity.this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onBackPressed() {
        // 如果图片已修改，显示确认对话框
        if (rotationAngle != 0 || currentBitmap != originalBitmap) {
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