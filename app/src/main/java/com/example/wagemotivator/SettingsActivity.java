package com.example.wagemotivator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String DAILY_NET = "dailyNet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");




        Button button = findViewById(R.id.updateWageButton);
        final EditText dailyNetText = findViewById(R.id.dailyNetText);

        // Load wage with shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        dailyNetText.setText(sharedPreferences.getString(DAILY_NET, Double.toString(0)));
        //Toast.makeText(getApplicationContext(), "Wage loaded: " + sharedPreferences.getString(DAILY_NET, Double.toString(dailyNet)), Toast.LENGTH_SHORT).show();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Save wage with shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(DAILY_NET, dailyNetText.getText().toString());
                editor.apply();
                Toast.makeText(getApplicationContext(), "Wage updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
