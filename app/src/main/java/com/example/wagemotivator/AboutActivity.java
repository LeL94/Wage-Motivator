package com.example.wagemotivator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Objects.requireNonNull(getSupportActionBar()).setTitle("About");


        WebView wbAbout = findViewById(R.id.wbAbout);
        wbAbout.loadUrl("file:///android_asset/about1.html");

//        TextView tvAbout = findViewById(R.id.tvAbout);
//        Spanned aboutText = Html.fromHtml("<h1>Wage Motivator</h1>" + getString(R.string.about_text));
//        tvAbout.setText(aboutText);
    }
}
