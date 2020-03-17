package com.example.matrixaskue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.matrixaskue.Classes.EditGetRow.RecordFromEditGetRow;
import com.example.matrixaskue.Classes.QueryFromDatabase.RecordsFromQueryDB;

public class FullDateActivity extends AppCompatActivity {
    String objectId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulldate);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        getObjectId();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void getObjectId() {
        Intent intent = getIntent();
        objectId = intent.getStringExtra("objectId");
        new Thread(new Runnable() {
            @Override
            public void run() {
                RecordsFromQueryDB[] recordsFromQueryDB = ApiQuery.Instance().QueryFromDatabase(FullDateActivity.this,objectId);
            }
        }).start();

    }
}
