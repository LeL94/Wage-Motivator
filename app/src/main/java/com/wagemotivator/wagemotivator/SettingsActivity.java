package com.wagemotivator.wagemotivator;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wagemotivator.wagemotivator.util.BaseActivity;
import com.wagemotivator.wagemotivator.util.SharedConst;

import java.util.Objects;


public class SettingsActivity extends BaseActivity {

    private EditText etWage;
    private TextView tvStartingHour;
    private TextView tvStartingMinute;
    private TextView tvFinishingHour;
    private TextView tvFinishingMinute;
    private Switch swWeekend;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");

        initialize();
    }


    private void initialize() {

        // Assign attributes
        etWage = findViewById(R.id.etWage);
        tvStartingHour = findViewById(R.id.tvStartingHour);
        tvStartingMinute = findViewById(R.id.tvStartingMinute);
        tvFinishingHour = findViewById(R.id.tvFinishingHour);
        tvFinishingMinute = findViewById(R.id.tvFinishingMinute);
        swWeekend = findViewById(R.id.swWeekend);
        sp = getSharedPreferences(SharedConst.SHARED_PREFS, MODE_PRIVATE);

        // Initialize text views
        etWage.setText(sp.getString(SharedConst.DAILY_WAGE, "0"));
        tvStartingHour.setText(sp.getString(SharedConst.STARTING_HOUR, "9"));
        tvStartingMinute.setText(sp.getString(SharedConst.STARTING_MINUTE, "00"));
        tvFinishingHour.setText(sp.getString(SharedConst.FINISHING_HOUR, "18"));
        tvFinishingMinute.setText(sp.getString(SharedConst.FINISHING_MINUTE, "00"));
        swWeekend.setChecked(Boolean.parseBoolean(sp.getString(SharedConst.WORKING_WEEKEND, "false")));

    }


    // Button edit starting time
    public void editStartingTime(View view) {
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


    // Button edit finishing time
    public void editFinishingTime(View view) {
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


    // Save button
    public void saveSettings(View view) {
        SharedPreferences.Editor editor = sp.edit();

        // avoid crash if wage is empty
        if (etWage.getText().toString().trim().equals("")) {
            editor.putString(SharedConst.DAILY_WAGE, "0");
        } else {
            editor.putString(SharedConst.DAILY_WAGE, etWage.getText().toString());
        }

        editor.putString(SharedConst.STARTING_HOUR, tvStartingHour.getText().toString());
        editor.putString(SharedConst.STARTING_MINUTE, tvStartingMinute.getText().toString());
        editor.putString(SharedConst.FINISHING_HOUR, tvFinishingHour.getText().toString());
        editor.putString(SharedConst.FINISHING_MINUTE, tvFinishingMinute.getText().toString());
        editor.putString(SharedConst.WORKING_WEEKEND, Boolean.toString(swWeekend.isChecked()));

        editor.apply();

        Toast.makeText(getApplicationContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

}
