package com.example.wagemotivator;

import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.*;
import com.example.wagemotivator.util.MyBaseActivity;
import com.example.wagemotivator.util.SharedConst;
import java.util.Date;
import java.util.Random;


public class MainActivity extends MyBaseActivity {

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


        initialize();

        runTimer();


        ConstraintLayout cl = findViewById(R.id.clProgressBar);
        cl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.animate().rotation(0).setDuration(420);
                return true;
            }
        });

    }


    private void initialize() {
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
    }


    private void runTimer() {

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



    public void checkDay() { // check if weekend

        SharedPreferences sp = getSharedPreferences(SharedConst.SHARED_PREFS, MODE_PRIVATE);
        boolean workingWeekend = Boolean.parseBoolean(sp.getString(SharedConst.WORKING_WEEKEND, "false"));

        if(!workingWeekend) {

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
                finish();
                startActivity(new Intent(MainActivity.this, WeekendActivity.class));
            }
        }

    }


    public void rotateProgressBar(View view) {
        int randomRotation = new Random().nextInt(720) -360;
        int randomDuration = new Random().nextInt(1900) + 100;

        view.animate().rotationBy(randomRotation).setDuration(randomDuration);

    }
}
