package com.example.matrixaskue;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public class NotificationActivity extends AppCompatActivity {

    TextView tvLogs;
    public Properties properties;
    public String ss;
    public Enumeration<String> enumerationStr;


    String stringtomrf = "";
    int roeop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        tvLogs = findViewById(R.id.tvLogs);
        ss = "";

        try {
            properties = Util.getProperties(this, "logs");


            enumerationStr = (Enumeration<String>) properties.propertyNames();

            ArrayList<String> listKeys = new ArrayList<>();

            while (enumerationStr.hasMoreElements()){
                listKeys.add(enumerationStr.nextElement());
            }
            String[] arrTmpForOrder = listKeys.toArray(new String[listKeys.size()]);
            for(int i = 0; i < arrTmpForOrder.length; i++){
                if(arrTmpForOrder[i].split("#").length < 2) continue;
                for(int j = 0; j < arrTmpForOrder.length; j++){
                    stringtomrf = arrTmpForOrder[i];
                    roeop = arrTmpForOrder[i].split("#").length;
                    stringtomrf = arrTmpForOrder[j];
                    roeop = arrTmpForOrder[j].split("#").length;
                    if(arrTmpForOrder[j].split("#").length < 2) continue;
                    if(i == j) continue;
                    Date dti = new Date();
                    Date dtj = new Date();
                    String stringTmp;
                    DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
                    try {
                        dti = formatter.parse(arrTmpForOrder[i].split("#")[1]);
                        dtj = formatter.parse(arrTmpForOrder[j].split("#")[1]);
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
                Spannable spans = new SpannableString((strTmp + ": " + properties.getProperty(strTmp) + "\n"));
                int color = Color.BLACK;
                if(strTmp.split("#").length > 1){
                    if(strTmp.split("#")[0].equals("I")){
                        color = Color.GREEN;
                    }
                    else if (strTmp.split("#")[0].equals("E")){
                        color = Color.RED;
                    }
                }
                spans.setSpan(new ForegroundColorSpan(color), 0, spans.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvLogs.append(spans);
            }
            //Spannable spans = new SpannableString(ss);
            //spans.setSpan(new ForegroundColorSpan(Color.YELLOW), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //tvLogs.setText(spans);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickCleanLogs(View v) throws IOException {
        Util.cleanProperties(this, "logs");
        tvLogs.setText(null);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }




}
