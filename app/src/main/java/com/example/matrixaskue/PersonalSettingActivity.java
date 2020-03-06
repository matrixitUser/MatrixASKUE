package com.example.matrixaskue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PersonalSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
