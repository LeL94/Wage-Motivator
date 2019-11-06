package com.example.wagemotivator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");


        SharedPreferences sharedPreferences = getSharedPreferences(SharedConst.SHARED_PREFS, MODE_PRIVATE);


        // Initialize
        final EditText wageEditText = findViewById(R.id.wageEditText);
        final EditText startingHourText = findViewById(R.id.startingEditText);
        final EditText finishingHourText = findViewById(R.id.finishingEditText);
        final EditText lunchBreakStartText = findViewById(R.id.lunchBreakStartEditText);
        final EditText lunchBreakFinishText = findViewById(R.id.lunchBreakFinishEditText);

        wageEditText.setText(sharedPreferences.getString(SharedConst.DAILY_WAGE, "0"));
        startingHourText.setText(sharedPreferences.getString(SharedConst.STARTING_HOUR, "9"));
        finishingHourText.setText(sharedPreferences.getString(SharedConst.FINISHING_HOUR, "18"));
        lunchBreakStartText.setText(sharedPreferences.getString(SharedConst.LUNCH_BREAK_START, "13"));
        lunchBreakFinishText.setText(sharedPreferences.getString(SharedConst.LUNCH_BREAK_FINISH, "14"));


        // Save button
        Button saveButton = findViewById(R.id.saveSettingsButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO check if legal input

                SharedPreferences sharedPreferences = getSharedPreferences(SharedConst.SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(SharedConst.DAILY_WAGE, wageEditText.getText().toString());
                editor.putString(SharedConst.STARTING_HOUR, startingHourText.getText().toString());
                editor.putString(SharedConst.FINISHING_HOUR, finishingHourText.getText().toString());
                editor.putString(SharedConst.LUNCH_BREAK_START, lunchBreakStartText.getText().toString());
                editor.putString(SharedConst.LUNCH_BREAK_FINISH, lunchBreakFinishText.getText().toString());

                editor.apply();

                Toast.makeText(getApplicationContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });
    }
}
