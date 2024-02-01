package com.example.scanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int DELAY_MILLISECONDS = 2000;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView TextView = findViewById(R.id.olivinskiy);
        TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://tapy.me/olivinskiy";

                // Создаем Intent с ACTION_VIEW и передаем в него Uri ссылки
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                // Запускаем Intent
                startActivity(intent);
            }
        });

        // Поставим задачу в очередь для перехода через 2 секунды
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Проверяем, впервые ли пользователь заходит в приложение
                if (isFirstTime()) {
                    // Если впервые, переходим на WelcomeActivity
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivityWithAnimation(intent);
                } else {
                    // Если не впервые, переходим на MenuActivity
                    Intent intent = new Intent(MainActivity.this, MenuScanner.class);
                    startActivityWithAnimation(intent);
                }

                // Закрываем MainActivity, чтобы пользователь не мог вернуться назад
                finish();
            }
        }, DELAY_MILLISECONDS);
    }

    private boolean isFirstTime() {
        // Используем SharedPreferences для сохранения информации о том, впервые ли пользователь заходит
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstTime = preferences.getBoolean("is_first_time", true);

        // Если впервые, сохраняем информацию об этом
        if (isFirstTime) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("is_first_time", false);
            editor.apply();
        }

        return isFirstTime;
    }
    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
