package com.example.matrixaskue;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class PersonalSettingActivity extends AppCompatActivity {
    EditText settingName, settingLastName, settingEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);

        settingEmail = findViewById(R.id.settingEmail);
        settingLastName = findViewById(R.id.settingLastName);
        settingName = findViewById(R.id.settingName);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void onClickSave(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalSettingActivity.this);
        builder.setTitle("")
                .setMessage("Вы действительно хотите изменить данные?")
                .setCancelable(false)
                .setNegativeButton("Нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        })
                .setNeutralButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String email = settingEmail.getText().toString();
                                String lastName = settingLastName.getText().toString();
                                String name = settingName.getText().toString();
                                try {
                                    Util.setPropertyConfig("email", email, PersonalSettingActivity.this);
                                    Util.setPropertyConfig("lastName", lastName, PersonalSettingActivity.this);
                                    Util.setPropertyConfig("name", name, PersonalSettingActivity.this);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Данные сохранены",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();


    }
}
