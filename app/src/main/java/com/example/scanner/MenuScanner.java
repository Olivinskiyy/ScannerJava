package com.example.scanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MenuScanner extends AppCompatActivity implements View.OnClickListener {

    private static final String PREFS_NAME = "QRCodeHistory";
    private static final String HISTORY_KEY = "history";

    ImageView scanBtn;
    ImageView historyButton;
    ImageView generatebutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_scanner);
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


        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);

        historyButton = findViewById(R.id.historybutton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistory();
            }
        });
        generatebutton = findViewById(R.id.generatebutton);
        generatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuScanner.this,  qrcodecreate.class);
                startActivityWithAnimation(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        scanCode();
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning barcode");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                saveToHistory(result.getContents());
                showResultDialog(result.getContents());
            } else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void saveToHistory(String result) {
        // Получаем сохраненную историю
        Set<String> history = getHistory();

        // Добавляем новый результат
        history.add(result);

        // Сохраняем обновленную историю
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(HISTORY_KEY, history);
        editor.apply();
    }

    private Set<String> getHistory() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getStringSet(HISTORY_KEY, new HashSet<String>());
    }

    private void showResultDialog(String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(result);
        builder.setTitle("Scanning Result");
        builder.setPositiveButton("Scan again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scanCode();
            }
        }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MenuScanner.this, MenuScanner.class));
            }
        }).setNeutralButton("Copy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                copyToClipboard(result);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showHistory() {
        Set<String> history = getHistory();
        final ArrayList<String> historyList = new ArrayList<>(history);

        if (historyList.isEmpty()) {
            Toast.makeText(this, "History is empty", Toast.LENGTH_SHORT).show();
        } else {
            // Показываем историю в диалоговом окне
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Scanning History");
            builder.setItems(historyList.toArray(new String[0]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showResultDialog(historyList.get(which));
                }
            });
            builder.setNegativeButton("Close", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Scanned Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }
    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
