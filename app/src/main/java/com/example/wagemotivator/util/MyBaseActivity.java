package com.example.wagemotivator.util;

import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.wagemotivator.R;
import com.example.wagemotivator.SettingsActivity;


public class MyBaseActivity extends AppCompatActivity {

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
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
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
            Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }

}
