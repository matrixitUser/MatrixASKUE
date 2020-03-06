package com.example.matrixaskue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SendMetersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_meters);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
