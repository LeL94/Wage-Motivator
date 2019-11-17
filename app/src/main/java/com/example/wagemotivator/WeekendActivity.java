package com.example.wagemotivator;

import android.os.Bundle;

import com.example.wagemotivator.util.MyBaseActivity;

public class WeekendActivity extends MyBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekend);
    }


    @Override
    public void onBackPressed() {
        // do nothing
    }
}
