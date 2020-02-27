package com.example.pavlovka;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class CustomizableOptionsActivity extends AppCompatActivity {
    private EditText etWLSmin2, etWLSmax2, etProc1, etMaxTimeStop, etProc2;
    private Switch swhAutoQueryByDiscrepancy, swhWlsLessThenWlsminAndStop, swhWlsMoreThenWlsmaxAndStart,
            swhWlsLessThenWLsmin2AndStart, swhWlsMoreThenWlsmax2AndStart, swhProc1, swhMaxTimeStop, swhProc2,swhDataNull,swhNotConnection;
    private String[] aItemsMethod = {"Только контроллер", "Сервер и контроллер", "Только сервер"};

    private String password = "111";
    private String verificationPassword;

    private int controlMode = -1;
    private float rightSeekBar;
    private float leftSeekBar;
    private int itemCurrentPrev;
    Spinner spinner;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customizable_options);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        etWLSmin2 = findViewById(R.id.etWLSmin2);
        etWLSmax2 = findViewById(R.id.etWLSmax2);
        etProc1 = findViewById(R.id.etProc1);
        etMaxTimeStop = findViewById(R.id.etMaxTimeStop);
        etProc2 = findViewById(R.id.etProc2);

        swhAutoQueryByDiscrepancy = findViewById(R.id.swhAutoQueryByDiscrepancy);
        getControlMod();

        swhWlsLessThenWlsminAndStop = findViewById(R.id.swhWlsLessThenWLsminAndStop);
        swhWlsMoreThenWlsmaxAndStart = findViewById(R.id.swhWlsMoreThenWlsmaxAndStart);
        swhWlsLessThenWLsmin2AndStart = findViewById(R.id.swhWlsLessThenWlsmin2AndStart);
        swhWlsMoreThenWlsmax2AndStart = findViewById(R.id.swhWlsMoreThenWlsmax2AndStart);
        swhProc1 = findViewById(R.id.swhProc1);
        swhMaxTimeStop = findViewById(R.id.swhMaxTimeStop);
        swhProc2 = findViewById(R.id.swhProc2);
        swhDataNull = findViewById(R.id.swhDataNull);
        swhNotConnection = findViewById(R.id.swhNotConnection);

        try {
            swhWlsLessThenWlsminAndStop.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isWlsLessThenWlsminAndStop", "false",this)));
            swhWlsMoreThenWlsmaxAndStart.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isWlsMoreThenWlsmaxAndStart", "false",this)));
            swhWlsLessThenWLsmin2AndStart.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isWlsLessThenWLsmin2AndStart", "false",this)));
            swhWlsMoreThenWlsmax2AndStart.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isWlsMoreThenWlsmax2AndStart", "false",this)));
            swhProc1.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isProc1", "false",this)));
            swhMaxTimeStop.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isMaxTimeStop", "false",this)));
            swhProc2.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isProc2", "false",this)));
            swhDataNull.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isDataNull", "false",this)));
            swhNotConnection.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isNotConnection", "false",this)));

            swhAutoQueryByDiscrepancy.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("AutoQueryByDiscrepancy", "false",this)));
            etWLSmin2.setText(Util.getPropertyOrSetDefaultValue("WLSmin2", "9",this));
            etWLSmax2.setText(Util.getPropertyOrSetDefaultValue("WLSmax2", "13.75",this));
            etProc1.setText(Util.getPropertyOrSetDefaultValue("Proc1", "10",this));
            etMaxTimeStop.setText(Util.getPropertyOrSetDefaultValue("maxTimeStop", "30",this));
            etProc2.setText(Util.getPropertyOrSetDefaultValue("Proc2", "20",this));

          //  getControlMod();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, aItemsMethod);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner =  findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // выделяем элемент
        spinner.setSelection(controlMode);
        itemCurrentPrev = controlMode;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                ((TextView) parent.getChildAt(0)).setTextSize(14);
                if (itemCurrentPrev == id) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomizableOptionsActivity.this);
                builder.setTitle("Предупреждение")
                        .setMessage("Вы действительно хотите изменить вариант управления")
                        .setCancelable(false)
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        spinner.setSelection(itemCurrentPrev);
                                        dialog.cancel();

                                    }
                                })
                        .setNeutralButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        singin();

                                        dialog.cancel();

                                    }
                                });


                AlertDialog alert = builder.create();
                alert.show();

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp(){
        setControlMod();
        finish();
        return true;
    }
    public void onDestroy(){
        super.onDestroy();
        try {
            Util.setPropertyConfig("isWlsLessThenWlsminAndStop", Boolean.toString(swhWlsLessThenWlsminAndStop.isChecked()), this);
            Util.setPropertyConfig("isWlsMoreThenWlsmaxAndStart", Boolean.toString(swhWlsMoreThenWlsmaxAndStart.isChecked()), this);
            Util.setPropertyConfig("isWlsLessThenWLsmin2AndStart", Boolean.toString(swhWlsLessThenWLsmin2AndStart.isChecked()), this);
            Util.setPropertyConfig("isWlsMoreThenWlsmax2AndStart", Boolean.toString(swhWlsMoreThenWlsmax2AndStart.isChecked()), this);
            Util.setPropertyConfig("isProc1", Boolean.toString(swhProc1.isChecked()), this);
            Util.setPropertyConfig("isMaxTimeStop", Boolean.toString(swhMaxTimeStop.isChecked()), this);
            Util.setPropertyConfig("isProc2", Boolean.toString(swhProc2.isChecked()), this);
            Util.setPropertyConfig("isDataNull", Boolean.toString(swhDataNull.isChecked()), this);
            Util.setPropertyConfig("isNotConnection", Boolean.toString(swhNotConnection.isChecked()), this);

            Util.setPropertyConfig("AutoQueryByDiscrepancy", Boolean.toString(swhAutoQueryByDiscrepancy.isChecked()), this);
            Util.setPropertyConfig("WLSmin2", etWLSmin2.getText().toString(),this);
            Util.setPropertyConfig("WLSmax2", etWLSmax2.getText().toString(),this);
            Util.setPropertyConfig("Proc1", etProc1.getText().toString(),this);
            Util.setPropertyConfig("maxTimeStop", etMaxTimeStop.getText().toString(),this);
            Util.setPropertyConfig("Proc2", etProc2.getText().toString(),this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void singin()
    {

        LayoutInflater li = LayoutInflater.from(this);
        View dialog_signinView = li.inflate(R.layout.dialog_signin, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(dialog_signinView);
        final EditText userInput = (EditText) dialog_signinView.findViewById(R.id.inputText);
        mDialogBuilder.setCancelable(false);
        mDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int id) {
                        //Вводим текст и отображаем в строке ввода на основном экране:
                        verificationPassword = userInput.getText().toString();
                        if (verificationPassword.equals(password)) {
                            controlMode = spinner.getSelectedItemPosition();
                            itemCurrentPrev = controlMode;
                            ApiQuery.Instance().NodeWatertower(rightSeekBar,leftSeekBar,controlMode,CustomizableOptionsActivity.this);
                            setControlMod();
                            dialog.cancel();
                            //TODO Ильмир
                        } else {
                            Toast.makeText(getBaseContext(), "Неверный пароль", Toast.LENGTH_SHORT).show();
                            spinner.setSelection((int) itemCurrentPrev);

                        }
                    }
                });
        mDialogBuilder.setNegativeButton("Отмена",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();


    }

    public void  getControlMod(){
        Intent intent = getIntent();
        controlMode = intent.getIntExtra("controlMode", 0);
        rightSeekBar = intent.getFloatExtra("rightSeekBar",0);
        leftSeekBar = intent.getFloatExtra("leftSeekBar",0);
    }

    public void  setControlMod(){
        Intent answerIntent = new Intent();
        answerIntent.putExtra("controlMode", controlMode);
        setResult(Const.CustomOptions, answerIntent);

    }

}
