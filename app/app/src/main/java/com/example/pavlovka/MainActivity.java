package com.example.pavlovka;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pavlovka.Classes.EditGetRow.RecordFromEditGetRow;
import com.example.pavlovka.Classes.QueryFromDatabase.RecordsFromQueryDB;
import com.example.pavlovka.Classes.WaterTower.ExportWaterTower;
import com.google.gson.Gson;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;


public class MainActivity extends AppCompatActivity {
    TextView tvMotor, tvUPPMain, tvUPPSecondary, tvHeightWaters, tvLastStartTime, tvLastStopTime, tvLastUpdate, tvUpp, tvTimeDataWls,tvMonitoringWls, textHight;
    public Gson gson = new Gson();
    private RangeSeekBar rangeSeekBar;
    private float rightSeekBar;
    private float leftSeekBar;
    private float rightSeekBarPrev;
    private float leftSeekBarPrev;
    private int controlMode = 2;

    private double height;
    private int motorStatus;
    private int pumpStopTry = 0;
    private int pumpStartTry = 0;
    private  float SeekBarMax; //TODO: должно приходить с сервера
    private  float SeekBarMin;//TODO: должно приходить с сервера
    private float interval = 1;
    Timer timer = new Timer();

    private ImageView drawingImageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint = new Paint();
    Paint paintl = new Paint();

    public Properties properties;
    public Enumeration<String> enumerationStr;

    float xPoint = 0;
    float yPoint;
    int imageViewHeight = 100;
    int imageViewWidht = 1000;
    final int maxPoint = 100;  //125
    final int middlePoint = 50; //50
    float  y[] = new float[maxPoint+5];

    ArrayList<Float> arrayHeight = new ArrayList<>();
    ArrayList<Date> arrayHeightDate = new ArrayList<>();

    int strokeWidth = 8;
    int indexXY = 0;
    int w;
    int wR;




    Handler handler = new Handler();
    public  MyService myService;
    public ProgressBar myProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textHight = findViewById(R.id.strHight);
        myProgressBar = findViewById(R.id.pbWatersLevel);
        tvMotor = findViewById(R.id.tvMotor);
        tvUPPMain = findViewById(R.id.tvUPPMain);
        tvLastStartTime = findViewById(R.id.tvLastStartTime);
        tvLastStopTime = findViewById(R.id.tvLastStopTime);
        tvUPPSecondary = findViewById(R.id.tvUPPSecondary);
        tvHeightWaters = findViewById(R.id.tvWls);
        tvLastUpdate = findViewById(R.id.tvLastUpdate);
        tvUpp = findViewById(R.id.tvUpp);
        tvTimeDataWls = findViewById(R.id.tvTimeDataWls);
        tvMonitoringWls = findViewById(R.id.tvMonitoringWls);
        drawingImageView = findViewById(R.id.drawingImageView);

        rangeSeekBar = findViewById(R.id.sb_vertical_8);
        rangeSeekBar.getLeftSeekBar().setIndicatorTextDecimalFormat("0.0");
        rangeSeekBar.getRightSeekBar().setIndicatorTextDecimalFormat("0.0");




          //  canvas.drawLine(0, 50, 1000, 50, paint);



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
                tvHeightWaters.setText(Const.notConnectionToInternet);
                return;
            }
            FunctionAtStart();

            w = getWindowManager().getDefaultDisplay().getWidth();
            wR = (int) (w*0.20);
            bitmap = Bitmap.createBitmap(w, 100, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            drawingImageView.setImageBitmap(bitmap);

            waterTower();


        }
        //waterTower1();
       // onDraw();
       // RecordFromEditGetRow editGetRow = ApiQuery.Instance().EditGetRow(MainActivity.this);
      //  String idWls = editGetRow.getIdWls();
     //   RecordsFromQueryDB[] hh = ApiQuery.Instance().QueryFromDatabaseWls(MainActivity.this, idWls );


        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                boolean isLeft=false;
                if(leftValue != leftSeekBar) isLeft=true;
                if(isLeft) {
                    if (rangeSeekBar.getLeftSeekBar().getProgress() < SeekBarMin)
                        rangeSeekBar.setProgress(SeekBarMin, rightSeekBar);
                    if (rangeSeekBar.getLeftSeekBar().getProgress() > (rightSeekBar-interval))
                        rangeSeekBar.setProgress(rightSeekBar-interval, rightSeekBar);
                }
                else
                {
                    if (rangeSeekBar.getRightSeekBar().getProgress() > SeekBarMax)
                        rangeSeekBar.setProgress(leftSeekBar, SeekBarMax);
                    if (rangeSeekBar.getRightSeekBar().getProgress() < (leftSeekBar+interval))
                        rangeSeekBar.setProgress(leftSeekBar, leftSeekBar+interval);
                    if ((rightSeekBar < height) && (motorStatus == 1))
                        pumpStopTry = 1;
                    else pumpStopTry = 0;
                    if ((leftSeekBar > height) && (motorStatus == 0))
                        pumpStartTry = 0;
                    else pumpStartTry = 1;
                }
                rightSeekBar =  rangeSeekBar.getRightSeekBar().getProgress();
                leftSeekBar = rangeSeekBar.getLeftSeekBar().getProgress();
            }
            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                String alertMessage = "Вы пытаетесь установить максимальный уровень наполнения башни ниже текущего уровня. Насос будет остановлен.";
                String btnMessage = "Остановить насос";
                // "Вы действительно желаете изменить величину максимального уровня заполнения башни?"
                if (!isLeft)
                {
                    if ((pumpStopTry == 0) || (motorStatus == 0))
                    {
                        alertMessage = "Вы действительно желаете изменить величину максимального уровня заполнения башни?";
                        btnMessage = "Применить изменения";
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Предупреждение")
                                .setMessage(alertMessage)
                                .setCancelable(false)
                                .setNegativeButton("Отмена",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                rightSeekBar = rightSeekBarPrev;
                                                rangeSeekBar.setProgress(leftSeekBar, rightSeekBarPrev);
                                                dialog.cancel();

                                            }
                                        })
                                .setNeutralButton(btnMessage,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                rightSeekBarPrev = rightSeekBar;
                                                //TODO:  Ильмир
                                                ApiQuery.Instance().NodeWatertower(rightSeekBar,leftSeekBar,controlMode,MainActivity.this);
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                }
                if (isLeft)
                {
                    if ((pumpStartTry == 1) || (motorStatus == 1))
                    {
                        alertMessage = "Вы действительно желаете изменить величину минимального уровня заполнения башни?";
                        btnMessage = "Применить изменения";
                    }
                    else {
                        alertMessage = "Вы пытаетесь установить минимальный уровень наполнения башни выше текущего уровня. Насос будет включен.";
                        btnMessage = "Включить насос";
                    }
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle("Предупреждение")
                            .setMessage(alertMessage)
                            .setCancelable(false)
                            .setNegativeButton("Отмена",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog1, int id) {
                                            leftSeekBar = leftSeekBarPrev;
                                            rangeSeekBar.setProgress(leftSeekBarPrev, rightSeekBar);
                                            dialog1.cancel();
                                        }
                                    })
                            .setNeutralButton(btnMessage,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            leftSeekBarPrev = leftSeekBar;
                                            //TODO:  Ильмир
                                            ApiQuery.Instance().NodeWatertower(rightSeekBar,leftSeekBar,controlMode,MainActivity.this);
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder1.create();
                    alert.show();
                }
            }
        });
        textHight.setText(Const.strHight);
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
            case R.id.CustomizableOptionsActivity:
                Intent intentCustomizableOptionsActivity = new Intent(this, CustomizableOptionsActivity.class)
                .putExtra("controlMode", controlMode)
                .putExtra("rightSeekBar", rightSeekBar)
                .putExtra("leftSeekBar", leftSeekBar);

                startActivityForResult(intentCustomizableOptionsActivity, Const.CustomOptions);
                return true;
            case R.id.LogsActivity:
                Intent intentLogsActivity = new Intent(this, LogsActivity.class);
                startActivity(intentLogsActivity);
                return true;
            case R.id.StartStopTimeActivity:
                Intent intentStartStopTimeActivity = new Intent(this, StartStopTimeActivity.class);
                startActivity(intentStartStopTimeActivity);
                return true;
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
        sendMessage();
    }
    public void onClickPollCurrent(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(ApiQuery.Instance().Poll(Const.objectIdUpp,"","Current", MainActivity.this)){
                        MainFunction();
                    }
                    else{
                        tvHeightWaters.setText(Const.notConnectionToServer);
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
            tvHeightWaters.setText(data.getStringExtra("tvHeightWaters"));
        }
        else if (resultCode == Const.Success) {
            tvHeightWaters.setText(data.getStringExtra("tvHeightWaters"));
            tvLastStartTime.setText(data.getStringExtra("tvLastStartTime"));
            tvLastStopTime.setText(data.getStringExtra("tvLastStopTime"));
            tvMotor.setText(data.getStringExtra("tvMotor"));
            tvUPPMain.setText(data.getStringExtra("tvUPPMain"));
            tvUPPSecondary.setText(data.getStringExtra("tvUPPSecondary"));
            tvLastUpdate.setText(data.getStringExtra("tvLastUpdate"));
            myProgressBar.setProgress(data.getIntExtra("myProgressBar",0));
            tvTimeDataWls.setText(data.getStringExtra("tvTimeDataWls"));
            height = data.getDoubleExtra("height",0);
            motorStatus = data.getIntExtra("motorStatus",-1);
            drawHight();
        }
        else if(resultCode == Const.Session){
            FunctionAtStart();
        }
        else if(resultCode == Const.CustomOptions){
            controlMode = data.getIntExtra("controlMode",-1);
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
    public void sendMessage() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int[] bytesSendHeight = new int[]{0x10,0x04,0x0B,0x00,0x00,0x02,0x70,0xAE};
                try {
                    for(int i = 0; i < bytesSendHeight.length; i++){
                        try{
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvMonitoringWls.setText("!");
                                }
                            });
                            mBufferOut.writeByte(bytesSendHeight[i]);
                        }catch (Exception ex){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvMonitoringWls.setText("?");
                                }
                            });
                            ex.printStackTrace();
                        }
                    }
                    try{
                        mBufferOut.flush();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int[] bytesSendWls = new int[]{0x10,0x04,0x01,0x00,0x00,0x01,0x33,0x77};
                try {
                    for(int i = 0; i < bytesSendWls.length; i++){
                        try{
                            mBufferOut.writeByte(bytesSendWls[i]);
                        }catch (Exception ex){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvMonitoringWls.setText("?");
                                }
                            });
                        }
                    }
                    try{
                        mBufferOut.flush();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void TcpIpWls(){
        if(!Util.isConnectionInternet(this)) return;
        int receiveLen;
        byte[] buffer = new byte[20];
        try {
            socket = new Socket(InetAddress.getByName(Const.IpAddressWls), Const.wlsPort);
            mBufferOut = new DataOutputStream(socket.getOutputStream());

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            float height, pressure;

            int wls;
            NumberFormat formatDouble = new DecimalFormat("#00.00");
            String strTmp = "";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            while (true){
                try{
                    receiveLen = dataInputStream.read(buffer);
                    if (receiveLen > 5 && buffer[0] == 0x10 && buffer[1] == 0x04) {
                        if(buffer[2] == 0x04){
                            Date dtNow = new Date();
                            pressure = ByteBuffer.wrap(new byte[]{buffer[5],buffer[6],buffer[3],buffer[4]}).getFloat();
                            height = (float) (pressure*1.03);
                            strTmp = simpleDateFormat.format(dtNow) + "->" + formatDouble.format(height);
                        }
                        if(buffer[2] == 0x02){
                            wls = (int)buffer[4];
                            strTmp += ":" + Integer.toBinaryString(wls);
                        }
                        final String tmp1 = strTmp;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvMonitoringWls.setText(tmp1);
                            }
                        });
                    }
                }
                catch (Exception exc){
                    Util.logsException(exc.getMessage(),this);
                }
            }

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
        RecordsFromQueryDB[] records = ApiQuery.Instance().QueryFromDatabase(this);
        if(records == null || records.length == 0){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tvHeightWaters.setText("\n\n" + Const.ifDataNull);
                }
            });
            //requestSmsPermission();
            return;
        }
        RecordsFromQueryDB recordsUpp = Helper.GetLastRecordByType(records,"upp");
        RecordsFromQueryDB recordsWls = Helper.GetLastRecordByType(records, "wls");
        RecordsFromQueryDB recordsCurrentPhaseMax = Helper.GetLastRecordByType(records, "currentPhaseMax");
        RecordsFromQueryDB recordsHeight = Helper.GetLastRecordByType(records, "высота");
        RecordsFromQueryDB recordsLastStartTime = Helper.GetLastRecordByType(records, "lastStartTime");
        RecordsFromQueryDB recordsLastStopTime = Helper.GetLastRecordByType(records, "lastStopTime");
        RecordsFromQueryDB recordsMotorCurrent = Helper.GetLastRecordByType(records, "motorCurrent");

        if(recordsUpp == null || recordsWls == null || recordsHeight == null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tvHeightWaters.setText("\n\n" + Const.ifDataNull);
                }
            });
            return;
        }

        String strMotor = "насос\n", strUppSecondary = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String strLastUpdate = "время обновления: " + simpleDateFormat.format(recordsUpp.getDateDt());
        final String strTimeDataWls = "время опроса башни: " + recordsWls.getS2();

        final String strLastStartTime = recordsLastStartTime != null ? ("время запуска: "  + recordsLastStartTime.getS2()) : "undefined";
        final String strlastStopTime = recordsLastStopTime != null ? ("время останова: " + recordsLastStopTime.getS2()) : "undefined";

        String[] arrUpp = recordsUpp.getS2().split("; ");
        for(String uppTmp : arrUpp){
            String[] arrTmp = uppTmp.split("=");
            switch (arrTmp[0]){
                case "мотор":
                    if(arrTmp[1].equals("START")){
                        strMotor += "СТАРТ";//"ЗАПУЩЕН";
                    }
                    else {
                        strMotor += "СТОП";// "ОСТ-ЛЕН";
                    }
                    break;
                case "Auto mode":
                    strUppSecondary += "Auto mode: " + arrTmp[1] + "; ";
                    break;
                case "Fault":
                    strUppSecondary += "Fault: " + arrTmp[1] + "; ";
                    break;
                case "TOR":
                    strUppSecondary += "TOR: " + arrTmp[1] + "; ";
                    break;
                case "ReadySS":
                    strUppSecondary += "ReadySS: " + arrTmp[1] + "; ";
                    break;
                case "DI":
                    strUppSecondary += "DI: " + arrTmp[1];
                    break;
            }
        }
        NumberFormat formatDouble = new DecimalFormat("#00.00");

        String strUppMain = recordsCurrentPhaseMax != null ? ("Макс.ток фаз,A:\n" + recordsCurrentPhaseMax.getD1s() + "\n") : ("undefined\n");
        strUppMain += recordsMotorCurrent != null ? ("% от номинала:\n" + recordsMotorCurrent.getD1s()) : "undefined";

       double height = recordsHeight.getD1d();


        String strHeight = "\n" + formatDouble.format(height)+"\n";

        double percent = height*100/14;
        strHeight += "\n" + formatDouble.format(percent)+"\n";

        int wls = (int)(recordsWls.getD1d());
        strHeight += "\n" +  Integer.toBinaryString(wls);

        final double height_f = height;
        final String strHeight_f = strHeight, strMotor_f = strMotor, strUppMain_f = strUppMain, strUppSecondary_f = strUppSecondary;
        handler.post(new Runnable() {
            @Override
            public void run() {
                tvHeightWaters.setText(strHeight_f);
                tvLastStartTime.setText(strLastStartTime);
                tvLastStopTime.setText(strlastStopTime);
                tvMotor.setText(strMotor_f);
                tvUPPMain.setText(strUppMain_f);
                tvUPPSecondary.setText(strUppSecondary_f);
                tvLastUpdate.setText(strLastUpdate);
                myProgressBar.setProgress((int)(height_f*100));
                tvTimeDataWls.setText(strTimeDataWls);
            }
        });

    }

    public void waterTower(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ExportWaterTower exportWaterTower = ApiQuery.Instance().ExportWatertower(MainActivity.this);

                            rightSeekBar = Float.parseFloat(exportWaterTower.getMax());
                            leftSeekBar = Float.parseFloat(exportWaterTower.getMin());
                            rightSeekBarPrev = rightSeekBar;
                            leftSeekBarPrev = leftSeekBar;
                            SeekBarMin = Float.parseFloat(exportWaterTower.getCriticalMin());
                            SeekBarMax = Float.parseFloat(exportWaterTower.getCriticalMax());
                            interval = Float.parseFloat(exportWaterTower.getInterval());
                            controlMode = (int) Float.parseFloat(exportWaterTower.getControlMode());


                        if (arrayHeight.size()<40){

                            waterTower1();
                            onDraw();
                        }
                        try{
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    rangeSeekBar.setProgress(leftSeekBar, rightSeekBar);
                                }
                            });
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                     //   RecordFromEditGetRow editGetRow = ApiQuery.Instance().EditGetRow(MainActivity.this);
                    }
                },0, 1000 * 60* 1);
            }
        }).start();

    }
    public void waterTower1(){
      //  new Thread(new Runnable() {
         //   @Override
          //  public void run() {
                        RecordFromEditGetRow editGetRow = ApiQuery.Instance().EditGetRow(MainActivity.this);
                        String idWls = editGetRow.getIdWls();
                        RecordsFromQueryDB[] arrayRecord = ApiQuery.Instance().QueryFromDatabaseWls(MainActivity.this, idWls );
                        for (int i = 0; i< arrayRecord.length; i++)
                        {
                            if(arrayRecord[i].getS1().equals("высота")){
                               arrayHeight.add(Float.valueOf(arrayRecord[i].getD1s()));
                               arrayHeightDate.add(arrayRecord[i].getDateDt());

                            }
                      }
          //  }
      //  }).start();

    }

    public void  getControlMod(){
        Intent intent = getIntent();
        controlMode = intent.getIntExtra("controlMode", -2);
    }


    public void onDraw() {

        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(2);
        paint.setTextSize(35);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0,0, w-wR, 100, paint);
        paintl.setColor(Color.WHITE);
        float minH = leftSeekBar;
        float maxH = rightSeekBar;
        float kH = (maxH - minH)/imageViewHeight;

        for (int i = 0; i< arrayHeight.size()-0; i++){
            if (i>0) {
                float stopH = arrayHeight.get(i);
                float startH = arrayHeight.get(i-1);
                indexXY = i;
                drawHeightPoint(kH, minH, (indexXY - 1) * strokeWidth, startH,indexXY * strokeWidth, stopH);
                y[indexXY]=stopH;
            }

        }
        indexXY++;


    }

    public void drawHight(){
        y[indexXY] = (float) height;
        float minH = leftSeekBar;
        float maxH = rightSeekBar;

        float kH = (maxH - minH)/imageViewHeight;

        if (indexXY == 0) {indexXY++; return;}
        if (indexXY < maxPoint) {
            if (y[indexXY]>= y[indexXY-1]) paint.setColor(Color.BLUE);
            else paint.setColor(Color.RED);

            drawHeightPoint(kH, minH, (indexXY - 1) * strokeWidth, y[indexXY - 1],indexXY * strokeWidth, y[indexXY]);

        }
        else
        {
            canvas.drawColor(Color.WHITE);
            indexXY = middlePoint;int j=0;
            j=1;

            for (int i = maxPoint-middlePoint; i < maxPoint; i++){
                drawHeightPoint(kH, minH, (j - 1) * strokeWidth, y[i - 1],j * strokeWidth, y[i]);
                j++;
            }
        }
        indexXY++;
    }

    public void drawHeightPoint(float kH, float minH, float startX, double startH,  float stopX, double stopH){
        float startY = imageViewHeight - (float) ((startH-minH)/kH);
        float stopY = imageViewHeight - (float) ((stopH-minH)/kH);

       // yPoint = imageViewHeight - (float) ((height-minH)/kH);
      //  canvas.drawPoint(xPoint, yPoint, paint);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        String timeS = "!!!";

        double deltaH = (y[indexXY]- y[indexXY-1]);
        double timeD=0;

        if (deltaH > 0 )
        {
            timeD = (rightSeekBar - height)/deltaH*5;
            timeS = ((int) (timeD/60) + "h " + (int) (timeD%60) + "m");
        }
        else
        if (deltaH < 0 )
        {
            timeD = (leftSeekBar - height)/deltaH*5;
            timeS = ((int) (timeD/60) + "h " + (int) (timeD%60) + "m");
        }

        canvas.drawRect(w - wR,0, w, 100, paintl);
        canvas.drawText(timeS, w - wR+40, 50, paint);

    }
/*
    public String phone = "89174242238";
    public String message = "0000;RESET";
    private static final int PERMISSION_SEND_SMS = 123;

    public void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        } else {
            // permission already granted run sms send
            sendSms(phone, message);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    sendSms(phone, message);
                } else {
                    // permission denied
                }
                return;
            }
        }
    }

    private void sendSms(String phoneNumber, String message){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }*/
}
