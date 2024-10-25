package com.example.listycity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class ShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);


        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("City Details");
        }


        Intent intent = getIntent();
        String cityName = intent.getStringExtra("CITY_NAME");


        TextView cityNameTextView = findViewById(R.id.city_name_text_view);
        cityNameTextView.setText(cityName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
