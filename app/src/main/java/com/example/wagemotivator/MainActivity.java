package com.example.wagemotivator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

    private TextView tvRemainingTime;
    private TextView gainText;
    private ProgressBar progressBar;
    private TextView tvPercentage;

    private double dailyWage;
    private int startingHour;
    private int startingMinute;
    private int finishingHour;
    private int finishingMinute;
    private int totalWorkingTimeInSeconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkDay();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Get references from layout
        SharedPreferences sharedPreferences = getSharedPreferences(SharedConst.SHARED_PREFS, MODE_PRIVATE);
        tvRemainingTime = findViewById(R.id.tvRemainingTime);
        gainText = findViewById(R.id.gainText);
        progressBar = findViewById(R.id.progressBar);
        tvPercentage = findViewById(R.id.tvPercentage);

        // Initialize
        dailyWage = Double.parseDouble(sharedPreferences.getString(SharedConst.DAILY_WAGE, "0"));
        startingHour = Integer.parseInt(sharedPreferences.getString(SharedConst.STARTING_HOUR, "9"));
        startingMinute = Integer.parseInt(sharedPreferences.getString(SharedConst.STARTING_MINUTE, "0"));
        finishingHour = Integer.parseInt(sharedPreferences.getString(SharedConst.FINISHING_HOUR, "18"));
        finishingMinute = Integer.parseInt(sharedPreferences.getString(SharedConst.FINISHING_MINUTE, "0"));
        // (finishingHour*3600 + finishingMinute*60) - (startingHour*3600 + startingMinute*60);
        totalWorkingTimeInSeconds = (finishingHour-startingHour)*3600 + (finishingMinute-startingMinute)*60;


        // Timer
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                try {
                    setTimeAndGain();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 1000);
            }
        }, 0);
    }


    @SuppressLint("DefaultLocale")
    public void setTimeAndGain() throws ParseException {


        // Get time
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentSecond = calendar.get(Calendar.SECOND);
        int elapsedTimeInSeconds = (currentHour-startingHour)*3600 + (currentMinute-startingMinute)*60 + currentSecond;

        // Progress bar counter
        int counter = 100*elapsedTimeInSeconds/totalWorkingTimeInSeconds;

        // Remaining time
        int remainingTimeInSeconds = totalWorkingTimeInSeconds - elapsedTimeInSeconds;
        int remainingHour = remainingTimeInSeconds / 3600;
        int remainingMinute = (remainingTimeInSeconds % 3600) / 60;
        int remainingSecond = (remainingTimeInSeconds % 3600) % 60;


        @SuppressLint("DefaultLocale")
        String remainingTime = remainingHour + " : " + String.format("%02d", remainingMinute) + " : " + String.format("%02d", remainingSecond);
        tvRemainingTime.setText(remainingTime);

        // Timer
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.format(date);

        Date currentTimeFormatted = dateFormat.parse(dateFormat.format(date));
        @SuppressLint("DefaultLocale")
        Date startingTimeFormatted = dateFormat.parse(String.format("%02d", startingHour) + ":" +String.format("%02d", startingMinute) + ": 00");
        @SuppressLint("DefaultLocale")
        Date finishingTimeFormatted = dateFormat.parse(String.format("%02d", finishingHour) + ":" + String.format("%02d", finishingMinute) + ": 00");

        double gain;
        int compareStarting = currentTimeFormatted.compareTo(startingTimeFormatted); // compareStarting < 0 if currentTime < startingTime
        int compareFinishing = currentTimeFormatted.compareTo(finishingTimeFormatted);

        //Log.d("MainActivity", "compareStarting: " + compareStarting);
        //Log.d("MainActivity", "compareFinishing: " + compareFinishing);

        // if it is working time
        if(compareStarting >0 & compareFinishing < 0) {
            gain = dailyWage/totalWorkingTimeInSeconds * elapsedTimeInSeconds;
        }

        // if work is over
        else if (compareFinishing >= 0) {
            tvRemainingTime.setText("00 : 00 : 00");
            gain = dailyWage;
            counter = 100;
        }

        // if work is to begin
        else {
            remainingHour = totalWorkingTimeInSeconds / 3600;
            remainingMinute = (totalWorkingTimeInSeconds % 3600) / 60;
            tvRemainingTime.setText(
                    String.format("%02d", remainingHour) + " : " +
                    String.format("%02d", remainingMinute) + " : 00");
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
            // SETTINGS
            case R.id.item_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;

            // SHARE
            case R.id.item_share:
                Toast.makeText(this, "...work in progress...", Toast.LENGTH_SHORT).show();
                return true;

            // CONTACT US
            case R.id.item_contact_us:
                //Toast.makeText(this, "...coming soon...", Toast.LENGTH_SHORT).show();
                contactUs();
                return true;

            // REPORT BUG
            case R.id.item_report_bug:
                Toast.makeText(this, "This app is bugless, NYEH NYEH", Toast.LENGTH_LONG).show();
                return true;

            // ABOUT US
            case R.id.item_about:
                Toast.makeText(this, "...working on it...", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void contactUs() {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:wage.motivator@gmail.com"));

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }


    public void checkDay() { // check if weekend
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            startActivity(new Intent(MainActivity.this, WeekendActivity.class));
        }
    }
}
