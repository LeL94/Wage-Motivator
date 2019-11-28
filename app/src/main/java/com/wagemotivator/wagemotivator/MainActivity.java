package com.wagemotivator.wagemotivator;

import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.*;

import com.wagemotivator.wagemotivator.util.ActivityWithSettings;
import com.wagemotivator.wagemotivator.util.Config;
import com.wagemotivator.wagemotivator.util.SharedConst;
import java.util.Date;
import java.util.Random;


public class MainActivity extends ActivityWithSettings {

    private TextView tvRemainingTime;
    private TextView tvRemainingTimeShadow;
    private TextView tvGainText;
    private TextView tvGainTextShadow;
    private ProgressBar progressBar;
    private TextView tvPercentage;
    private TextView tvBrokenPb;
    private ImageView ivPlanet;
    private SharedPreferences sp;

    private double dailyWage;
    private int startingHour;
    private int startingMinute;
    private int finishingHour;
    private int finishingMinute;
    private int totalWorkingTimeInSeconds;

    private static int clicksOnProgressBar = 0;
    private static boolean brokenProgressBar = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initialize();
        runTimer();


        // set long click on progress bar
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
        sp = getSharedPreferences(SharedConst.SHARED_PREFS, MODE_PRIVATE);
        tvRemainingTime = findViewById(R.id.tvRemainingTime);
        tvRemainingTimeShadow = findViewById(R.id.tvRemainingTimeShadow);
        tvGainText = findViewById(R.id.gainText);
        tvGainTextShadow = findViewById(R.id.gainTextShadow);
        progressBar = findViewById(R.id.progressBar);
        tvPercentage = findViewById(R.id.tvPercentage);
        ivPlanet = findViewById(R.id.ivPlanet);
        sp = getSharedPreferences(SharedConst.SHARED_PREFS, MODE_PRIVATE);

        // Initialize
        dailyWage = Double.parseDouble(sp.getString(SharedConst.DAILY_WAGE, "0"));
        startingHour = Integer.parseInt(sp.getString(SharedConst.STARTING_HOUR, "9"));
        startingMinute = Integer.parseInt(sp.getString(SharedConst.STARTING_MINUTE, "0"));
        finishingHour = Integer.parseInt(sp.getString(SharedConst.FINISHING_HOUR, "18"));
        finishingMinute = Integer.parseInt(sp.getString(SharedConst.FINISHING_MINUTE, "0"));
        // (finishingHour*3600 + finishingMinute*60) - (startingHour*3600 + startingMinute*60);
        totalWorkingTimeInSeconds = (finishingHour-startingHour)*3600 + (finishingMinute-startingMinute)*60;

        float savedRotation = Float.parseFloat(sp.getString(SharedConst.PLANET_ROTATION, "0"));
        ivPlanet.setRotation(savedRotation);
    }


    private void runTimer() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                try {
                    checkDay();
                    setTimeAndGain();
                    processPlanetRotation();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 1000);
            }
        }, 0);
    }


    @SuppressLint("DefaultLocale")
    private void setTimeAndGain() throws ParseException {

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
        tvRemainingTimeShadow.setText(remainingTime);

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


        // if it is working time
        if(compareStarting >0 & compareFinishing < 0) {
            gain = dailyWage/totalWorkingTimeInSeconds * elapsedTimeInSeconds;
        }

        // if work is over
        else if (compareFinishing >= 0) {
            tvRemainingTime.setText("00 : 00 : 00");
            tvRemainingTimeShadow.setText("00 : 00 : 00");
            gain = dailyWage;
            counter = 100;
        }

        // if work is to begin
        else {
            remainingHour = totalWorkingTimeInSeconds / 3600;
            remainingMinute = (totalWorkingTimeInSeconds % 3600) / 60;
            String temp = String.format("%02d", remainingHour) + " : " + String.format("%02d", remainingMinute) + " : 00";
            tvRemainingTime.setText(temp);
            tvRemainingTimeShadow.setText(temp);
            gain = 0;
            counter = 0;
        }

        tvGainText.setText(String.format("%.3f", gain));
        tvGainTextShadow.setText(String.format("%.3f", gain));
        progressBar.setProgress(counter);
        tvPercentage.setText(counter+"%");
    }


    private void checkDay() { // check if weekend

        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);


        switch (today) {
            case Calendar.MONDAY:
                ivPlanet.setImageResource(R.drawable.planet_moon);
                break;

            case Calendar.TUESDAY:
                ivPlanet.setImageResource(R.drawable.planet_mars);
                break;

            case Calendar.WEDNESDAY:
                ivPlanet.setImageResource(R.drawable.planet_mercury);
                break;

            case Calendar.THURSDAY:
                // TODO bug with my huawei bar lags when rotating
                ivPlanet.setImageResource(R.drawable.planet_jupiter);
                break;

            case Calendar.FRIDAY:
                ivPlanet.setImageResource(R.drawable.planet_venus);
                break;

            case Calendar.SATURDAY:
                ivPlanet.setImageResource(R.drawable.planet_saturn);
                break;

            case Calendar.SUNDAY:
                ivPlanet.setImageResource(R.drawable.planet_sun);
                break;
        }


        SharedPreferences sp = getSharedPreferences(SharedConst.SHARED_PREFS, MODE_PRIVATE);
        boolean workingWeekend = Boolean.parseBoolean(sp.getString(SharedConst.WORKING_WEEKEND, "false"));

        if(!workingWeekend) {

            if (today == Calendar.SATURDAY || today == Calendar.SUNDAY) {
                finish();
                startActivity(new Intent(MainActivity.this, WeekendActivity.class));
            }
        }

    }


    public void rotateProgressBar(View view) {
        int randomRotation = new Random().nextInt(630) + 90;
        long randomDuration = randomRotation;

        if (Math.random() >= 0.5) // 50% to have negative rotation
            randomRotation *= -1;

        view.animate().rotationBy(randomRotation).setDuration(randomDuration);

        clicksOnProgressBar++;

        if (clicksOnProgressBar >= Config.CLICKS_TO_BREAK_PROGRESS_BAR) {
            trollProgressBar(view);
        }
//        else if (clicksOnProgressBar >= 15) {
//            Toast.makeText(this, String.valueOf(clicksOnProgressBar), Toast.LENGTH_SHORT).show();
//        }
    }


    private void trollProgressBar(View view) {
        view.animate().rotationBy(720).setDuration(300);
        view.animate().translationYBy(-2000).setDuration(300);

        tvBrokenPb = findViewById(R.id.tvBrokenPb);
        tvBrokenPb.animate().alpha(1).setDuration(2000);
        brokenProgressBar = true;
    }


    @Override
    protected void onResume() {
        super.onResume();

        float savedRotation = Float.parseFloat(sp.getString(SharedConst.PLANET_ROTATION, "0"));
        ivPlanet.setRotation(savedRotation);

        // if progress bar is broken, update UI
        clicksOnProgressBar = 0;
        if (brokenProgressBar) {

            ConstraintLayout clProgressBar = findViewById(R.id.clProgressBar);
            tvBrokenPb = findViewById(R.id.tvBrokenPb);

            clProgressBar.setTranslationY(-2000);
            tvBrokenPb.setAlpha(1);
        }
    }


    public void processPlanetRotation() {
        ivPlanet.animate().rotationBy((float) 0.1).setDuration(1000);

        // save rotation value to shared preferences
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SharedConst.PLANET_ROTATION, Float.toString(ivPlanet.getRotation()));
        editor.apply();

        //System.out.println(ivPlanet.getRotation());
        //ivPlanet.animate().translationY(-210);
        //int[] location = new int[2];
        //ivPlanet.getLocationInWindow(location);
        //System.out.println(location[0] + ", " + location[1]);
    }
}
