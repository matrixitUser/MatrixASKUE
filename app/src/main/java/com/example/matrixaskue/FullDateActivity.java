package com.example.matrixaskue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class FullDateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulldate);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        //RecordsFromQueryDB[] records = ApiQuery.Instance().QueryFromDatabase(this);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
