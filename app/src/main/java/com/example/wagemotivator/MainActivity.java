package com.example.wagemotivator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Calendar;
import android.os.*;
import android.widget.Toast;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String DAILY_NET = "dailyNet";
    private double dailyNet = 83.1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button = findViewById(R.id.updateWageButton);
        final EditText dailyNetText = findViewById(R.id.dailyNetText);


        // Load wage with shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        dailyNetText.setText(sharedPreferences.getString(DAILY_NET, Double.toString(dailyNet)));
        //Toast.makeText(getApplicationContext(), "Wage loaded: " + sharedPreferences.getString(DAILY_NET, Double.toString(dailyNet)), Toast.LENGTH_SHORT).show();


        // Variables
        final TextView remainingTimeText = findViewById(R.id.remainingTimeText);
        final TextView gainText = findViewById(R.id.gainText);
        final DecimalFormat df2 = new DecimalFormat("##.###");


        // Initialize


        // Timer
        final Handler handler = new Handler();
        int delay = 1000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){

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
                    remainingTimeText.setText(0 + " : " + 0 + " : " + 0);
                    gain = dailyNet;
                }

                else { // da mezzanotte alle 9
                    remainingTimeText.setText(8 + " : " + 0 + " : " + 0);
                    gain = 0;
                }

                gainText.setText(df2.format(gain));

                handler.postDelayed(this, 1000);
            }
        }, delay);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Save wage with shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(DAILY_NET, dailyNetText.getText().toString());
                editor.apply();
                dailyNet = Double.parseDouble(dailyNetText.getText().toString());
                Toast.makeText(getApplicationContext(), "Wage updated", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
