package com.example.scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity {


    private static final int REQUEST_PERMISSION_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageView nizWelcome = findViewById(R.id.nizwelcome);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        nizWelcome.startAnimation(slideUpAnimation);

        ImageView verhWelcome = findViewById(R.id.verhwelcome);
        Animation slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        verhWelcome.startAnimation(slideDownAnimation);

        ImageView welcomebutton = findViewById(R.id.welcomebtn);
        verhWelcome.startAnimation(slideDownAnimation);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            // Если разрешение не предоставлено, запросите у пользователя разрешение
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        } else {
            // Разрешение уже предоставлено
            // Ваш код для работы с памятью устройства здесь
        }



        ImageView welcomebtn = findViewById(R.id.welcomebtn);
        welcomebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MenuScanner.class);
                startActivityWithAnimation(intent);
            }
        });
    }
    private void startActivityWithAnimation(Intent intent) {
        // Запускаем новую активити с анимацией
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            // Проверяем, предоставлено ли разрешение
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, выполняйте ваш код для работы с памятью устройства
            } else {
                // Разрешение не предоставлено, обработайте ситуацию
            }
        }
    }

}