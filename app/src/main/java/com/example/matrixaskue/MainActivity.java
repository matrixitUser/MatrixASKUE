package com.example.matrixaskue;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matrixaskue.Classes.QueryFromDatabase.RecordsFromQueryDB;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executor;


public class MainActivity extends AppCompatActivity {
    LinearLayout layoutName, layoutPName, layoutValue;
    public Gson gson = new Gson();

    public Properties properties;

    Handler handler = new Handler();
    public  MyService myService;
    ArrayList<String> listNames = new ArrayList<>();
    ArrayList<String> listPNames = new ArrayList<>();
    ArrayList<String> listValues = new ArrayList<>();
    ArrayList<String> listIds = new ArrayList<>();
    ArrayList <Integer> listIdName = new ArrayList<>();
    ArrayList <Integer> listIdPName = new ArrayList<>();
    ArrayList <Integer> listIdValues = new ArrayList<>();
    boolean funtionStart = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutName = findViewById(R.id.layoutName);
        layoutPName = findViewById(R.id.layoutPName);
        layoutValue = findViewById(R.id.layoutValue);

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
       //myService.stopForeground(true);
        stopService(intent);
        myService.stopSelf();
        myService.IsActivity(true);
        startService(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                TcpIpWls();
            }
        }).start();
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
        else if (resultCode == Const.Success) { // TODO Берем данные с my servise
            listNames = data.getStringArrayListExtra("listNames");
            listPNames = data.getStringArrayListExtra("listPNames");
            listValues = data.getStringArrayListExtra("listValues");
            listIds = data.getStringArrayListExtra("listIds");
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

    public Socket socket;
    // while this is true, the server will continue running
    // used to send messages
    private DataOutputStream mBufferOut;
    // used to read messages from the server
    public BufferedReader mBufferIn;
    // sends message received notifications

    Executor es;

    public void TcpIpWls(){
        if(!Util.isConnectionInternet(this)) return;
        int receiveLen;
        byte[] buffer = new byte[20];
        try {
            socket = new Socket(InetAddress.getByName(Const.IpAddressWls), Const.wlsPort);
            mBufferOut = new DataOutputStream(socket.getOutputStream());

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        } catch (Exception exc) {
            Util.logsException(exc.getMessage(),this);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                Util.logsException(e.getMessage(),this);
            }
        }
    }

    public void MainFunction(){
        for (int i = 0; i < listNames.size(); i++){
            int id = 1000 + i;
            listIdName.add(id);
            TextView textView = new TextView(MainActivity.this);
            textView.setText(listNames.get(i));
            textView.setId(id);
            textView.isClickable();
            textView.setOnClickListener(viewOCL);
            layoutName.addView(textView);
        }
        for (int i = 0; i < listPNames.size(); i++){
            int id = 2000 + i;
            listIdPName.add(id);
            TextView textView = new TextView(MainActivity.this);
            textView.setText(listPNames.get(i));
            textView.setId(id);
            textView.isClickable();
            textView.setOnClickListener(viewOCL);
            layoutPName.addView(textView);
        }
        for (int i = 0; i < listValues.size(); i++){
            int id = 3000 + i;
            listIdValues.add(id);
            TextView textView = new TextView(MainActivity.this);
            textView.setText(listValues.get(i));
            textView.setId(id);
            textView.isClickable();
            textView.setOnClickListener(viewOCL);
            layoutValue.addView(textView);
        }
        funtionStart = true;
    }

    public void updateValue(){
        for (int i = 0; i < listValues.size(); i++){
            TextView textView = findViewById(listIdValues.get(i));
            textView.setText(listValues.get(i));
        }
    }


    View.OnClickListener viewOCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            String objectId = "";
            if (viewId < 2000) {
                for (int i = 0; i < listIdName.size(); i++) {
                    if (viewId == listIdName.get(i)) {
                        objectId = listIds.get(i);
                        Intent intent = new Intent(MainActivity.this, FullDateActivity.class);
                        intent.putExtra("objectId", objectId);
                        setResult(Const.CustomId, intent);
                        startActivity(intent);
                    }
                }
            } else if (viewId < 3000) {
                for (int i = 0; i < listIdPName.size(); i++) {
                    if (viewId == listIdPName.get(i)) {
                        objectId = listIds.get(i);
                        Intent intent = new Intent(MainActivity.this, FullDateActivity.class);
                        intent.putExtra("objectId", objectId);
                        setResult(Const.CustomId, intent);
                        startActivity(intent);
                    }
                }
            } else if (viewId >= 3000) {
                for (int i = 0; i < listIdValues.size(); i++) {
                    if (viewId == listIdValues.get(i)) {
                        objectId = listIds.get(i);
                        RecordsFromQueryDB[] records = ApiQuery.Instance().QueryFromDatabase(MainActivity.this, objectId);
                        Intent intent = new Intent(MainActivity.this, FullDateActivity.class);
                        intent.putExtra("objectId", objectId);
                        setResult(Const.CustomId, intent);
                        startActivity(intent);
                    }
                }
            }
        }
    };
}
