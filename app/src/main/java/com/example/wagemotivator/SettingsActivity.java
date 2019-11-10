package com.example.wagemotivator;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Objects;


public class SettingsActivity extends AppCompatActivity {

    private EditText wageEditText;
    private TextView tvStartingHour;
    private TextView tvStartingMinute;
    private TextView tvFinishingHour;
    private TextView tvFinishingMinute;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");


        // Assign attributes
        wageEditText = findViewById(R.id.wageEditText);
        tvStartingHour = findViewById(R.id.tvStartingHour);
        tvStartingMinute = findViewById(R.id.tvStartingMinute);
        tvFinishingHour = findViewById(R.id.tvFinishingHour);
        tvFinishingMinute = findViewById(R.id.tvFinishingMinute);
        sharedPreferences = getSharedPreferences(SharedConst.SHARED_PREFS, MODE_PRIVATE);


        // Initialize text views
        wageEditText.setText(sharedPreferences.getString(SharedConst.DAILY_WAGE, "0"));
        tvStartingHour.setText(sharedPreferences.getString(SharedConst.STARTING_HOUR, "9"));
        tvStartingMinute.setText(sharedPreferences.getString(SharedConst.STARTING_MINUTE, "00"));
        tvFinishingHour.setText(sharedPreferences.getString(SharedConst.FINISHING_HOUR, "18"));
        tvFinishingMinute.setText(sharedPreferences.getString(SharedConst.FINISHING_MINUTE, "00"));


        // Edit starting hour
        Button btnEditStartingHour = findViewById(R.id.btnEditStartingHour);
        btnEditStartingHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener timePickerListener1 = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        tvStartingHour.setText(Integer.toString(hour));
                        tvStartingMinute.setText(String.format("%02d", minute));
                    }
                };
                TimePickerDialog mTimePicker = new TimePickerDialog(SettingsActivity.this,
                        timePickerListener1, 9,0, true);
                mTimePicker.show();
            }
        });


        // Edit finishing hour
        Button btnEditFinishingHour = findViewById(R.id.btnEditFinishingHour);
        btnEditFinishingHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener timePickerListener2 = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        tvFinishingHour.setText(Integer.toString(hour));
                        tvFinishingMinute.setText(String.format("%02d", minute));
                    }
                };
                TimePickerDialog mTimePicker = new TimePickerDialog(SettingsActivity.this,
                        timePickerListener2, 18,0, true);
                mTimePicker.show();
            }
        });


        // Save button
        Button saveButton = findViewById(R.id.saveSettingsButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(SharedConst.DAILY_WAGE, wageEditText.getText().toString());
                editor.putString(SharedConst.STARTING_HOUR, tvStartingHour.getText().toString());
                editor.putString(SharedConst.STARTING_MINUTE, tvStartingMinute.getText().toString());
                editor.putString(SharedConst.FINISHING_HOUR, tvFinishingHour.getText().toString());
                editor.putString(SharedConst.FINISHING_MINUTE, tvFinishingMinute.getText().toString());

                editor.apply();

                Toast.makeText(getApplicationContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });
    }

}
