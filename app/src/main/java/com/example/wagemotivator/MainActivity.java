package com.example.wagemotivator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Calendar;
import android.os.*;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String DAILY_NET = "dailyNet";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkDay();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Load wage with shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        final double dailyNet = Double.parseDouble(sharedPreferences.getString(DAILY_NET, Double.toString(0)));


        // Variables
        final TextView remainingTimeText = findViewById(R.id.remainingTimeText);
        final TextView gainText = findViewById(R.id.gainText);
        final DecimalFormat df2 = new DecimalFormat("##.###");


        // Timer
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){

                setTimeAndGain(remainingTimeText, gainText, df2, dailyNet);

                handler.postDelayed(this, 1000);
            }
        }, 0);

    }


    public void setTimeAndGain(TextView remainingTimeText, TextView gainText, DecimalFormat df2, double dailyNet) {
        // Get time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int totSecond = (hour-9)*3600 + minute*60 + second;

        // Remaining time
        int remainingHour = 17 - hour;
        int remainingMinute = 59 - minute;
        int remainingSecond = 59 - second;
        remainingTimeText.setText(remainingHour + " : " + remainingMinute + " : " + remainingSecond);

        // Guadagno
        double gain = 0;
        if (hour == 13) // pausa pranzo
            gain = dailyNet/28800 * 14400;

        else if (hour >= 9 && hour < 13) // mattino
            gain = dailyNet/28800 * totSecond;

        else if (hour > 13 && hour < 18) // pomeriggio
            gain = dailyNet/28800 * (totSecond-3600);

        else if (hour >= 18 && hour < 24) { // sera
            remainingTimeText.setText("00 : 00 : 00");
            gain = dailyNet;
        }

        else { // da mezzanotte alle 9
            remainingTimeText.setText("08 : 00 : 00");
            gain = 0;
        }

        gainText.setText(df2.format(gain));
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
            case R.id.item1:
                //Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.item2:
                Toast.makeText(this, "...work in progress...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                Toast.makeText(this, "...work in progress...", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void checkDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == calendar.SATURDAY || day == calendar.SUNDAY) {
            startActivity(new Intent(MainActivity.this, WeekendActivity.class));
        }
    }
}
