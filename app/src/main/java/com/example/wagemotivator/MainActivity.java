package com.example.wagemotivator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.*;
import android.widget.Toast;

import java.util.Date;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkDay();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final SharedPreferences sharedPreferences = getSharedPreferences(SharedConst.SHARED_PREFS, MODE_PRIVATE);


        // Variables
        final TextView tvRemainingTime = findViewById(R.id.tvRemainingTime);
        final TextView gainText = findViewById(R.id.gainText);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final TextView tvPercentage = findViewById(R.id.tvPercentage);

        // Timer
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){

                try {
                    setTimeAndGain(sharedPreferences, tvRemainingTime, gainText, progressBar, tvPercentage);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                handler.postDelayed(this, 1000);
            }
        }, 0);

    }


    @SuppressLint("DefaultLocale")
    public void setTimeAndGain(SharedPreferences sharedPreferences, TextView tvRemainingTime, TextView gainText,
                               ProgressBar progressBar, TextView tvPercentage) throws ParseException {

        double dailyWage = Double.parseDouble(sharedPreferences.getString(SharedConst.DAILY_WAGE, "0"));
        int startingHour = Integer.parseInt(sharedPreferences.getString(SharedConst.STARTING_HOUR, "9"));
        int startingMinute = Integer.parseInt(sharedPreferences.getString(SharedConst.STARTING_MINUTE, "0"));
        int finishingHour = Integer.parseInt(sharedPreferences.getString(SharedConst.FINISHING_HOUR, "18"));
        int finishingMinute = Integer.parseInt(sharedPreferences.getString(SharedConst.FINISHING_MINUTE, "0"));
        //int totalWorkingTimeInSeconds = (finishingHour*3600 + finishingMinute*60) - (startingHour*3600 + startingMinute*60);
        int totalWorkingTimeInSeconds = (finishingHour-startingHour)*3600 + (finishingMinute-startingMinute)*60;

        // Get time
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentSecond = calendar.get(Calendar.SECOND);
        int elapsedTimeInSeconds = (currentHour-startingHour)*3600 + currentMinute*60 + currentSecond;

        // Progress bar counter
        int counter = 100*elapsedTimeInSeconds/totalWorkingTimeInSeconds;

        // Remaining time
        int remainingHour = finishingHour - currentHour;
        int remainingMinute = 59 - currentMinute;
        int remainingSecond = 59 - currentSecond;
        @SuppressLint("DefaultLocale")
        String remainingTime = remainingHour + " : " + String.format("%02d", remainingMinute) + " : " + String.format("%02d", remainingSecond);
        tvRemainingTime.setText(remainingTime);

        // Timer
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.format(date);

        Date currentTimeFormatted = dateFormat.parse(dateFormat.format(date));
        @SuppressLint("DefaultLocale")
        Date startingTimeFormatted = dateFormat.parse(String.format("%02d", startingHour) + ":" + String.format("%02d", startingMinute));
        @SuppressLint("DefaultLocale")
        Date finishingTimeFormatted = dateFormat.parse(String.format("%02d", finishingHour) + ":" + String.format("%02d", finishingMinute));

        double gain;

        // if it is working time
        assert currentTimeFormatted != null;
        if(currentTimeFormatted.after(startingTimeFormatted)
                & currentTimeFormatted.before(finishingTimeFormatted)) {
            gain = dailyWage/totalWorkingTimeInSeconds * elapsedTimeInSeconds;
        }

        // if work is over
        else if (currentTimeFormatted.after(finishingTimeFormatted)) {
            tvRemainingTime.setText("00 : 00 : 00");
            gain = dailyWage;
            counter = 100;
        }

        // if work is to begin
        else {
            tvRemainingTime.setText("09 : 00 : 00");
            gain = 0;
            counter = 0;
        }

        gainText.setText(String.format("%.3f", gain));
        progressBar.setProgress(counter);
        tvPercentage.setText(counter+"%");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;

            case R.id.item_share:
                Toast.makeText(this, "...work in progress...", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.item_about_us:
                Toast.makeText(this, "...working on it...", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.item_contact_us:
                Toast.makeText(this, "...coming soon...", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.item_report_bug:
                Toast.makeText(this, "This app is bugless, NYEH NYEH", Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void checkDay() { // check if weekend
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            startActivity(new Intent(MainActivity.this, WeekendActivity.class));
        }
    }
}
