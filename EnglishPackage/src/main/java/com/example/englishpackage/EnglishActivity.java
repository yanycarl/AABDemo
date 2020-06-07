package com.example.englishpackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.aabtest.MainActivity;
import  com.example.englishpackage.R;

import java.util.Locale;

public class EnglishActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_english);

        findViewById(R.id.button3).setOnClickListener(this);

        Button button = findViewById(R.id.button2);
        button.setText(R.string.the_text);
    }

    @Override
    public void onClick(View v) {
        getResources().getConfiguration().setLocale(new Locale("ja-rJP"));
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }
}
