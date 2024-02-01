package com.example.scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class qrcodecreate extends AppCompatActivity {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    // List to store QR code history
    private List<String> qrCodeHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcodecreate);

        EditText editText = findViewById(R.id.edit_text);
        ImageView generatebuttonn = findViewById(R.id.generatebutton);
        Button generateButton = findViewById(R.id.button);
        ImageView scanBtn = findViewById(R.id.scanBtn);
        ImageView downloadButton = findViewById(R.id.download_button);
        ImageView historyCreateButton = findViewById(R.id.historycreatebtn);
        ImageView imageView = findViewById(R.id.qr_code);

        generatebuttonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(qrcodecreate.this, "You already on this page!", Toast.LENGTH_SHORT).show();
            }
        });
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(qrcodecreate.this, MenuScanner.class);
                startActivityWithAnimation(intent);
            }
        });

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWritePermissionAndDownload();
            }
        });

        // Button to show QR code history
        historyCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQRCodeHistory();
            }
        });
    }

    private void generateQRCode() {
        EditText editText = findViewById(R.id.edit_text);
        ImageView imageView = findViewById(R.id.qr_code);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(editText.getText().toString(), BarcodeFormat.QR_CODE, 300, 300);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private void startActivityWithAnimation(Intent intent) {
        // Запускаем новую активити с анимацией
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void requestWritePermissionAndDownload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                downloadQRCode();
            }
        } else {
            downloadQRCode();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("DownloadQRCode", "Write permission granted");
                downloadQRCode();
            } else {
                Toast.makeText(this, "Write permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadQRCode() {
        EditText editText = findViewById(R.id.edit_text);
        ImageView imageView = findViewById(R.id.qr_code);

        // Get the drawable from the ImageView
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

        // Check if the drawable is not null
        if (drawable != null) {
            // Get the bitmap from the drawable
            Bitmap bitmap = drawable.getBitmap();

            // Verify Bitmap Validity
            if (bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
                // Get the external files directory
                File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

                // Check External Storage State
                if (path != null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // Construct the file path using user-entered text
                    String fileName = editText.getText().toString().trim() + ".png";
                    File file = new File(path, fileName);

                    Log.d("DownloadQRCode", "File path: " + file.getAbsolutePath());

                    // Add filename to QR code history
                    qrCodeHistory.add(fileName);

                    try {
                        FileOutputStream stream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        stream.flush();
                        stream.close();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            addImageToGallery(file);
                        }

                        Toast.makeText(this, "QR Code saved (Android/data/com.example.scanner)", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("DownloadQRCode", "Error: " + e.getMessage());
                        Toast.makeText(this, "Error saving QR Code", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("DownloadQRCode", "External files directory is null or external storage not writable");
                    Toast.makeText(this, "Error saving QR Code. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("DownloadQRCode", "Invalid Bitmap");
                Toast.makeText(this, "Error saving QR Code. Invalid Bitmap", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where the drawable is null
            Toast.makeText(this, "No QR Code to download", Toast.LENGTH_SHORT).show();
        }
    }

    private void addImageToGallery(File file) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        ContentResolver contentResolver = getContentResolver();
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            if (imageUri != null) {
                OutputStream imageOut = contentResolver.openOutputStream(imageUri);
                imageOut.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("DownloadQRCode", "Error adding image to gallery: " + e.getMessage());
        }
    }

    private void showQRCodeHistory() {
        // Create a StringBuilder to concatenate the history
        StringBuilder historyStringBuilder = new StringBuilder();

        // Iterate through the QR code history and append each item to the StringBuilder
        for (String qrCode : qrCodeHistory) {
            historyStringBuilder.append(qrCode).append("\n");
        }

        // Display the history (you can use a TextView or a Toast, adjust as needed)
        Toast.makeText(this, "QR Code History:\n" + historyStringBuilder.toString(), Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Choose file provider", Toast.LENGTH_LONG).show();

        // Open file explorer to the specified directory
        File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (path != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(path.getAbsolutePath());
            intent.setDataAndType(uri, "*/*");
            startActivity(intent);
        }
    }


}
