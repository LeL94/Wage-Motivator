package com.example.wagemotivator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Calendar;
import android.os.*;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkDay();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final SharedPreferences sharedPreferences = getSharedPreferences(SharedConst.SHARED_PREFS, MODE_PRIVATE);


        // Variables
        final TextView remainingTimeText = findViewById(R.id.remainingTimeText);
        final TextView gainText = findViewById(R.id.gainText);
        final DecimalFormat df2 = new DecimalFormat("##.###");
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final TextView tvPercentage = findViewById(R.id.tvPercentage);

        // Timer
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){

                setTimeAndGain(sharedPreferences, remainingTimeText, gainText, df2, progressBar, tvPercentage);

                handler.postDelayed(this, 1000);
            }
        }, 0);

    }


    public void setTimeAndGain(SharedPreferences sharedPreferences, TextView remainingTimeText, TextView gainText,
                               DecimalFormat df2, ProgressBar progressBar, TextView tvPercentage) {

        double dailyWage = Double.parseDouble(sharedPreferences.getString(SharedConst.DAILY_WAGE, "0"));
        int startingHour = Integer.parseInt(sharedPreferences.getString(SharedConst.STARTING_HOUR, "9"));
        int finishingHour = Integer.parseInt(sharedPreferences.getString(SharedConst.FINISHING_HOUR, "18"));
        int lunchBreakStart = Integer.parseInt(sharedPreferences.getString(SharedConst.LUNCH_BREAK_START, "13"));
        int lunchBreakFinish = Integer.parseInt(sharedPreferences.getString(SharedConst.LUNCH_BREAK_FINISH, "14"));
        int workingTimeInSeconds = (finishingHour - startingHour - (lunchBreakFinish-lunchBreakStart)) * 3600;
        int lunchBreakInSeconds = (lunchBreakFinish-lunchBreakStart) * 3600;

        // Get time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int elapsedTimeInSeconds = (hour-startingHour)*3600 + minute*60 + second;

        // Progress bar counter
        int counter = 100*elapsedTimeInSeconds/(workingTimeInSeconds+lunchBreakInSeconds);

        // Remaining time
        int remainingHour = (finishingHour-1) - hour;
        int remainingMinute = 59 - minute;
        int remainingSecond = 59 - second;
        remainingTimeText.setText(remainingHour + " : " + remainingMinute + " : " + remainingSecond);

        // Gain
        double gain;
        if (hour >= lunchBreakStart && hour < lunchBreakFinish) // lunch break
            gain = dailyWage/workingTimeInSeconds * (lunchBreakStart-startingHour)*3600;

        else if (hour >= startingHour && hour < lunchBreakStart) // morning
            gain = dailyWage/workingTimeInSeconds * elapsedTimeInSeconds;

        else if (hour >= lunchBreakFinish && hour < finishingHour) // afternoon
            gain = dailyWage/workingTimeInSeconds * (elapsedTimeInSeconds-lunchBreakInSeconds);

        else if (hour >= finishingHour) { // evening
            remainingTimeText.setText("00 : 00 : 00");
            gain = dailyWage;
            counter = 100;
        }

        else { // midnight to 9
            remainingTimeText.setText("08 : 00 : 00");
            gain = 0;
            counter = 0;
        }

        gainText.setText(df2.format(gain));
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
