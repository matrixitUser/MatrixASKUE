package com.example.matrixaskue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.matrixaskue.Classes.EditGetRow.RecordFromEditGetRow;
import com.example.matrixaskue.Classes.QueryFromDatabase.RecordsFromQueryDB;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class FullDateActivity extends AppCompatActivity {
    String objectId, resourse, stringNa, stringT1, stringT2, stringTall, stringP, stringI, stringU;
    Double consumption;
    TextView strNa, strParam, strTall, strT1, strT2, type, strSplit;
    LinearLayout graphLayout;
    ArrayList<Double> listDatas = new ArrayList<>();
    ArrayList<Double> listConumptions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulldate);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        strNa = findViewById(R.id.strNa);
        strParam = findViewById(R.id.strParam);
        strTall = findViewById(R.id.strTall);
        strT1 = findViewById(R.id.strT1);
        strT2 = findViewById(R.id.strT2);
        strSplit = findViewById(R.id.strSplit);
        type = findViewById(R.id.type);
        graphLayout = findViewById(R.id.graphLayout);

        getParam();
        if (resourse.equals("Электроэнергия")) setDateEnergy();
        if (resourse.equals("Вода")) setDateWater();


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    public void getParam(){
        Intent intent = getIntent();
        objectId = intent.getStringExtra("objectId");
        resourse = intent.getStringExtra("resourse");
    }
    public void setDateWater(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                type.setText("Тип прибора: Бетар СГВ-15Д");
                strNa.setText(stringNa);
                strTall.setText(stringTall);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                RecordFromEditGetRow records = ApiQuery.Instance().EditGetRow(objectId,FullDateActivity.this);
                String parametrs = records.getParameters();
                String[] splitParametrs = parametrs.split(";");
                listDatas.clear();
                int channel = Integer.parseInt(splitParametrs[0]);
                stringNa = "Заводской номер: " + splitParametrs[5];
                String strChannel;
                switch (channel){
                    case 1:
                        strChannel = "Пар.0040";
                        break;
                    case 2:
                        strChannel = "Пар.0042";
                        break;
                    case 3:
                        strChannel = "Пар.0044";
                        break;
                    case 4:
                        strChannel = "Пар.0046";
                        break;
                    case 5:
                        strChannel = "Пар.0048";
                        break;
                    case 6:
                        strChannel = "Пар.004A";
                        break;
                    case 7:
                        strChannel = "Пар.004C";
                        break;
                    case 8:
                        strChannel = "Пар.004E";
                        break;
                    case 9:
                        strChannel = "Пар.0050";
                        break;
                    case 10:
                        strChannel = "Пар.0052";
                        break;
                    case 11:
                        strChannel = "Пар.0054";
                        break;
                    case 12:
                        strChannel = "Пар.0056";
                        break;
                    case 13:
                        strChannel = "Пар.0058";
                        break;
                    case 14:
                        strChannel = "Пар.005A";
                        break;
                    case 15:
                        strChannel = "Пар.005C";
                        break;
                    case 16:
                        strChannel = "Пар.005E";
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + channel);
                }

                RecordsFromQueryDB[] recordsFromQueryDBCurr = ApiQuery.Instance().QueryFromDatabase(FullDateActivity.this, objectId, "Current");
                for (int i = 0; i < recordsFromQueryDBCurr.length; i++){
                    if(recordsFromQueryDBCurr[i].getS1().equals(strChannel)){
                        stringTall = recordsFromQueryDBCurr[i].getD1s() + " м3";
                        double tAll = recordsFromQueryDBCurr[i].getD1d();
                        listDatas.add(tAll);
                    }
                }
                if (listDatas.size() > 3) {
                    for (int i = 1; i < listDatas.size(); i++){
                        consumption = listDatas.get(i) - listDatas.get(i-1);
                        listConumptions.add(consumption);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }).start();

    }

    public void setDateEnergy() {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                type.setText("Тип прибора: Меркурий 206");
                strNa.setText(stringNa);
                strTall.setText(stringTall);
                strT1.setText(stringT1);
                strT2.setText(stringT2);
                strParam.setText("P: " + stringP + "\n" + "I: " + stringI + "\n" + "U: " + stringU);
                strSplit.setText("|");

                if (listDatas.size() > 3) {
                    GraphView graph = new GraphView(FullDateActivity.this);
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(generateData());
                    graph.addSeries(series);
                    graphLayout.addView(graph);
                }
                else {
                    TextView textView = new TextView(FullDateActivity.this);
                    textView.setGravity(Gravity.CENTER);
                    textView.setText("Не хватает данных для графика");
                    graphLayout.addView(textView);
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                RecordsFromQueryDB[] recordsFromQueryDBConst = ApiQuery.Instance().QueryFromDatabase(FullDateActivity.this, objectId, "Constant");
               // RecordFromEditGetRow records = ApiQuery.Instance().EditGetRow(objectId,FullDateActivity.this);
                for (int i = 0; i < recordsFromQueryDBConst.length; i++){
                    if(recordsFromQueryDBConst[i].getS1().equals("Серийный номер")){
                        stringNa = "Заводской номер: " + recordsFromQueryDBConst[i].getS2();
                    }
                }
                RecordsFromQueryDB[] recordsFromQueryDBCurr = ApiQuery.Instance().QueryFromDatabase(FullDateActivity.this, objectId, "Current");
                double t1 = 0;
                double t2 = 0;
                for (int i = 0; i < recordsFromQueryDBCurr.length; i++){
                    if(recordsFromQueryDBCurr[i].getS1().equals("Активная мощность по тарифу 1")){
                        stringT1 = "T1: " + recordsFromQueryDBCurr[i].getD1s() + " " + recordsFromQueryDBCurr[i].getS2();
                        t1 = recordsFromQueryDBCurr[i].getD1d();
                    }
                    if(recordsFromQueryDBCurr[i].getS1().equals("Активная мощность по тарифу 2")){
                        stringT2 = "T2: " + recordsFromQueryDBCurr[i].getD1s() + " " + recordsFromQueryDBCurr[i].getS2();
                        t2 = recordsFromQueryDBCurr[i].getD1d();
                    }
                    if(recordsFromQueryDBCurr[i].getS1().equals("P")){
                        stringP = recordsFromQueryDBCurr[i].getD1s() + " " + recordsFromQueryDBCurr[i].getS2();
                    }
                    if(recordsFromQueryDBCurr[i].getS1().equals("I")){
                        stringI = recordsFromQueryDBCurr[i].getD1s() + " " + recordsFromQueryDBCurr[i].getS2();
                    }
                    if(recordsFromQueryDBCurr[i].getS1().equals("U")){
                        stringU = recordsFromQueryDBCurr[i].getD1s() + " " + recordsFromQueryDBCurr[i].getS2();
                    }

                    double tAll = t1 + t2;
                    listDatas.add(tAll);
                }
                if (listDatas.size() > 3) {
                    for (int i = 1; i < listDatas.size(); i++){
                        consumption = listDatas.get(i) - listDatas.get(i-1);
                        listConumptions.add(consumption);

                    }
                }
                double tAll = t1 + t2;
                stringTall = String.valueOf(tAll) + "Вт";
                handler.sendEmptyMessage(0);
            }
        }).start();

    }
    private DataPoint[] generateData() {
        int count = listConumptions.size();
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double y = listConumptions.get(i);
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

}
