package com.example.matrixaskue;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


public class MainActivity extends AppCompatActivity {
    LinearLayout  layoutValue;
    public Gson gson = new Gson();

    public Properties properties;

    Handler handler = new Handler();
    public  MyService myService;
    ArrayList<String> listNames = new ArrayList<>();
    ArrayList<String> listPNames = new ArrayList<>();
    ArrayList<String> listValues = new ArrayList<>();
    ArrayList<String> listValue = new ArrayList<>();
    ArrayList<String> listIds = new ArrayList<>();
    ArrayList <Integer> listIdResourse = new ArrayList<>();
    ArrayList <Integer> listIdName = new ArrayList<>();
    ArrayList <Integer> listIdValues = new ArrayList<>();
    ArrayList <Integer> listIdDates = new ArrayList<>();
    ArrayList<String> listDates = new ArrayList<>();
    ArrayList<String> listResourse = new ArrayList<>();
    boolean funtionStart = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // setTitle("Газизова");

        layoutValue = findViewById(R.id.layoutValue);
        layoutValue.setPadding(10,10,10,10);

        String login = "", password = "";
        try {
            login = Util.getProperty("login", this);
            password = Util.getProperty("password", this);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(login == null || password == null || login.equals("") || password.equals("")){
            Intent intentSignin = new Intent(this, SigninActivity.class);
            startActivityForResult(intentSignin, Const.Session);

        }
        else{
            if(!Util.isConnectionInternet(this)){
                Util.logsError(Const.notConnectionToInternet,this);
                return;
            }

            FunctionAtStart();

        }

    }

    private void FunctionAtStart(){
        myService = new MyService();
        PendingIntent pendingIntent = createPendingResult(1, new Intent(),0  );
        Intent intent = new Intent(this, MyService.class).putExtra("pendingIntent", pendingIntent);
        stopService(intent);
        myService.stopSelf();
        myService.IsActivity(true);
        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.Report:
                // В разработке
                return true;
            case R.id.Send_meters:
                Intent intentSend = new Intent(this, SendMetersActivity.class)
                        .putExtra("listNames",listNames)
                        .putExtra("listValue",listValue);
                //TODO сюда ещё кидануть данные
                startActivity(intentSend);
                return true;
            case R.id.Notif:
             // В разработке
                //   Intent intentNotif = new Intent(this, SendMetersActivity.class);
               // startActivity(intentNotif);
                return true;
            case R.id.Settings:
                Intent intentS = new Intent(this, PersonalSettingActivity.class);
                startActivity(intentS);
                return true;
            case R.id.Setting_notif:
                // В разработке
              //  Intent intentSn = new Intent(this, PersonalSettingActivity.class);
             //   startActivity(intentSn);
                return true;
            case R.id.Feedback:
                Intent intentF = new Intent(this, FeedbackActivity.class);
                startActivity(intentF);
                return true;
            case R.id.Profile:
                try {
                    Util.cleanProperties(this, "config");
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            case R.id.Exit:
                PendingIntent pendingIntent = createPendingResult(1, new Intent(),0  );
                Intent intentExit = new Intent(this, MyService.class).putExtra("pendingIntent", pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    finishAndRemoveTask();
                    stopService(intentExit);
                } else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        finishAffinity();
                        stopService(intentExit);
                    } else
                    {
                        finish();
                        stopService(intentExit);
                    }
                }
                android.os.Process.killProcess(android.os.Process.myPid());

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void onClickWlsCardView(View view){

    }
    public void onClickPollCurrent(View view){ // TODO id Точек учета
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(ApiQuery.Instance().Poll(Const.objectIdUpp,"","Current", MainActivity.this)){

                    }
                }
                catch (Exception ex){
                    ex.fillInStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Const.Error) {

        }
        else if (resultCode == Const.Success) { //  Берем данные с my servise
            listNames = data.getStringArrayListExtra("listPNames");
            listValue = data.getStringArrayListExtra("listValue");
            listIds = data.getStringArrayListExtra("listIds");
           // listDates = data.getStringArrayListExtra("listDates");
            listResourse = data.getStringArrayListExtra("listResourse");
            if (funtionStart == true) updateValue();
            else MainFunction();
        }
        else if(resultCode == Const.Session){
            FunctionAtStart();
        }
        else if(resultCode == Const.Exit){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                finishAndRemoveTask();
            } else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    finishAffinity();
                } else
                {
                    finish();
                }
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        else if(resultCode == Const.ClosedService){

        }
    }


    public void MainFunction(){
        TextView textView = new TextView(MainActivity.this);
        layoutValue.addView(textView);
        for (int i = 0; i < listValue.size(); i++){
            int idResourse = 1000 + i;
            listIdResourse.add(idResourse);
            int idName = 2000 + i;
            listIdName.add(idName);
            int idValue = 3000 + i;
            listIdValues.add(idValue);
            int idDate = 4000 + i;
            listIdDates.add(idDate);
            CardView cardView = new CardView(MainActivity.this);
            cardView.setCardBackgroundColor(0x632182);
           // cardView.setPadding(100,100,100,100);
            cardView.setRadius(70);
            cardView.setCardElevation(8);
            cardView.setContentPadding(20, 20, 20, 20);
            LinearLayout linearLayout = new LinearLayout(MainActivity.this);
            linearLayout.setPadding(10,10,10,10);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            // Ресурс
            TextView textViewResourse = new TextView(MainActivity.this);
            textViewResourse.setText(listResourse.get(i));
            textViewResourse.setGravity(Gravity.CENTER);
            textViewResourse.setTextSize(15);

            textViewResourse.setId(idResourse);
            textViewResourse.isClickable();
            textViewResourse.setOnClickListener(viewOCL);
            linearLayout.addView(textViewResourse);
            // Название
            TextView textViewName = new TextView(MainActivity.this);
            textViewName.setText(listNames.get(i));
            textViewName.setGravity(Gravity.CENTER);
            textViewName.setTextSize(20);
            textViewName.setId(idName);
            textViewName.isClickable();
            textViewName.setOnClickListener(viewOCL);
            linearLayout.addView(textViewName);
            // Показания
            TextView textViewValue = new TextView(MainActivity.this);
            textViewValue.setText(listValue.get(i));
            textViewValue.setGravity(Gravity.CENTER);
            textViewValue.setTextSize(30);
            textViewValue.setId(idValue);
            textViewValue.isClickable();
            textViewValue.setOnClickListener(viewOCL);
            linearLayout.addView(textViewValue);
            // Дата
           /* TextView textViewDate = new TextView(MainActivity.this);
            textViewDate.setText(listDates.get(i));
            textViewDate.setGravity(Gravity.CENTER);
            textViewDate.setTextSize(10);
            textViewDate.setId(idDate);
            textViewDate.isClickable();
            textViewDate.setOnClickListener(viewOCL);
            linearLayout.addView(textViewDate);*/

            cardView.addView(linearLayout);
            layoutValue.addView(cardView);
            TextView textView1 = new TextView(MainActivity.this);
            layoutValue.addView(textView1);
        }

        funtionStart = true;
    }

    public void updateValue(){
        for (int i = 0; i < listValue.size(); i++){
            TextView textView = findViewById(listIdValues.get(i));
            textView.setText(listValue.get(i));
        }
    }


    View.OnClickListener viewOCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            String objectId = "";
            String resourse = "";
            if (viewId >= 1000) {
                for (int i = 0; i < listIdResourse.size(); i++) {
                    if (viewId == listIdResourse.get(i)) {
                        objectId = listIds.get(i);
                        resourse = listResourse.get(i);
                        Intent intent = new Intent(MainActivity.this, FullDateActivity.class);
                        intent
                                .putExtra("objectId", objectId)
                                .putExtra("resourse", resourse);
                        setResult(Const.CustomId, intent);
                        startActivity(intent);
                    }
                }
            }
            if (viewId >= 2000) {
                for (int i = 0; i < listIdName.size(); i++) {
                    if (viewId == listIdName.get(i)) {
                        objectId = listIds.get(i);
                        resourse = listResourse.get(i);
                        Intent intent = new Intent(MainActivity.this, FullDateActivity.class);
                        intent
                                .putExtra("objectId", objectId)
                                .putExtra("resourse", resourse);
                        setResult(Const.CustomId, intent);
                        startActivity(intent);
                    }
                }
            }
            if (viewId >= 3000) {
                for (int i = 0; i < listIdValues.size(); i++) {
                    if (viewId == listIdValues.get(i)) {
                        objectId = listIds.get(i);
                        resourse = listResourse.get(i);
                        Intent intent = new Intent(MainActivity.this, FullDateActivity.class);
                        intent
                                .putExtra("objectId", objectId)
                                .putExtra("resourse", resourse);
                        setResult(Const.CustomId, intent);
                        startActivity(intent);
                    }
                }
            }
           /* if (viewId >= 4000) {
                for (int i = 0; i < listIdDates.size(); i++) {
                    if (viewId == listIdDates.get(i)) {
                        objectId = listIds.get(i);
                        Intent intent = new Intent(MainActivity.this, FullDateActivity.class);
                        intent.putExtra("objectId", objectId);
                        startActivity(intent);
                    }
                }
            }*/
        }
    };
}
