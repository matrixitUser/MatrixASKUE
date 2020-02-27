package com.example.pavlovka;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public class StartStopTimeActivity extends AppCompatActivity {
    TextView tvStartStopTime;
    public Properties properties;
    public Enumeration<String> enumerationStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stop_time);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        tvStartStopTime = findViewById(R.id.tvStartStopTime);

        try {
            properties = Util.getProperties(this, "startStopTime");

            enumerationStr = (Enumeration<String>) properties.propertyNames();

            ArrayList<String> listKeys = new ArrayList<>();

            while (enumerationStr.hasMoreElements()){
                listKeys.add(enumerationStr.nextElement());
            }
            String[] arrTmpForOrder = listKeys.toArray(new String[listKeys.size()]);
            for(int i = 0; i < arrTmpForOrder.length; i++){
                for(int j = 0; j < arrTmpForOrder.length; j++){
                    if(i == j) continue;
                    Date dti = new Date();
                    Date dtj = new Date();
                    String stringTmp;
                    DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
                    try {
                        dti = formatter.parse(arrTmpForOrder[i]);
                        dtj = formatter.parse(arrTmpForOrder[j]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(dti.after(dtj)){
                        stringTmp = arrTmpForOrder[i];
                        arrTmpForOrder[i] = arrTmpForOrder[j];
                        arrTmpForOrder[j] = stringTmp;
                    }
                }
            }
            for(String strTmp : arrTmpForOrder){
                String value = properties.getProperty(strTmp);
                Spannable spans = new SpannableString((strTmp + ": " + value + "\n"));
                int color = Color.BLACK;
                if(value.equals("start")){
                    color = Color.GREEN;
                }
                else if (value.equals("stop")){
                    color = Color.RED;
                }
                spans.setSpan(new ForegroundColorSpan(color), 0, spans.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvStartStopTime.append(spans);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
