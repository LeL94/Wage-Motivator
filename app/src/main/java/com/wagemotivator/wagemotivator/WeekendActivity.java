package com.wagemotivator.wagemotivator;

import android.os.Bundle;

import com.wagemotivator.wagemotivator.util.BaseActivity;

public class WeekendActivity extends BaseActivity {

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
