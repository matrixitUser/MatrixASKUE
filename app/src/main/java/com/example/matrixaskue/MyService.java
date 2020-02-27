package com.example.matrixaskue;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.matrixaskue.Classes.BodyForSignalR.ListUpdateBody;
import com.example.matrixaskue.Classes.Message;
import com.example.matrixaskue.Classes.QueryFromDatabase.RecordsFromQueryDB;
import com.example.matrixaskue.Classes.RowCache2And3.RowFromRowCache;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import microsoft.aspnet.signalr.client.Connection;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;

public class MyService extends Service {
    public Gson gson = new Gson();
    NotificationManager notificationManager;
    private String gSessionId = "";
    public PendingIntent pendingIntent;
    ExecutorService es;
    Timer timer = new Timer();
    Timer timer1 = new Timer();
    static boolean isActivity = false;
    public boolean isEnterInFunc  =false;
    public Connection  connection = null;
    public int countVisit = 0;
    String name = ""; String pName = ""; String values = "";

    ArrayList<String> listIds = new ArrayList<>();
    ArrayList<String> listNames = new ArrayList<>();
    ArrayList<String> listPNames = new ArrayList<>();
    ArrayList<String> listValues = new ArrayList<>();

    int index;
    private boolean isPingOk = false;
    private Date dtUppStop;
    private int motorStatus = -1;

    private NumberFormat formatDouble = new DecimalFormat("#00.00");
    private double gHeight = -1;
    private int gWls = -1;
    private boolean gIsMotorStart = false;
    private boolean gIsNotConnected = true;
    public boolean gIsAdmin = false, gIsAutoQueryByDiscrepancy = false;
    private int lastSendNotifId = -1;
    public int IdService;
    public MyService() {
    }
    public void onCreate() {
        super.onCreate();
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        es = Executors.newFixedThreadPool(1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        pendingIntent = intent.getParcelableExtra("pendingIntent");
        if(pendingIntent != null){
            MyRun mr = new MyRun();
            es.execute(mr);
            startForeground(6000, "Service is running background");
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void startForeground(int color, String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        String CHANNEL_ID = "channel_00";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID) // don't forget create a notification channel first
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setColor(color)
                .setSound(null)
                .build();
        startForeground(1, notification);
    }



    class MyRun implements Runnable {
        public void run() {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    gSessionId = ApiQuery.Instance().isGetSession(MyService.this);
                    if(gSessionId == null || gSessionId == ""){
                        Intent intent = new Intent().putExtra("tvHeightWaters", Const.notConnectionToServer);
                        try {
                            pendingIntent.send(MyService.this,Const.Error,intent);
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                        boolean isNotConnection = false;
                        try{
                            isNotConnection = Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isNotConnection", "false",MyService.this));
                        }
                        catch (Exception ex){
                            ex.fillInStackTrace();
                        }
                        sendNotif(Const.notConnectionToServer, "", Const.notifNotConnecion, isNotConnection);
                        gIsNotConnected = true;
                    }else{
                        if ((connection == null) || (connection.getState() != ConnectionState.Connected)||(!isPingOk))
                        {
                            Subscribe();
                        }
                        isPingOk = false;
                        countVisit = 0;
                        gIsNotConnected = false;
                        MainFunction();
                    }
                }
            },0, 1000 * 60* 5);

            timer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    isEnterInFunc = false;
                }
            },1000, 1000 * 40);
        }
    }
    void sendNotif(String contentText, String contentInfo, int notfiId, boolean isNotifOn) {
        if(!isNotifOn) return;
        if(lastSendNotifId == notfiId) return;
        lastSendNotifId = notfiId;
        Util.logsError(contentText,this);
        String CHANNEL_ID = "channel_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .setContentText(contentText);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(2, builder.build());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent().putExtra("tvHeightWaters", Const.notConnectionToServer);
        try {
            pendingIntent.send(MyService.this, Const.ClosedService, intent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        timer.cancel();
        timer1.cancel();
        stopForeground(true);
        stopSelf();
    }
    void IsActivity(boolean isact){
        isActivity = isact;
    }

    public void Subscribe()
    {
        try{
            if (connection == null)
            {
                connection = new Connection(Const.MX_URL + "/messageacceptor");

                connection.reconnected(new Runnable() {
                    @Override
                    public void run() {
                        ApiQuery.Instance().SignalBind(connection.getConnectionId(),MyService.this);
                    }
                });

                connection.closed(new Runnable() {
                    @Override
                    public void run() {
                        connection.stop();
                    }
                });
                connection.error(new ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                        connection.stop();
                    }
                });
                connection.received(new MessageReceivedHandler() {
                    @Override
                    public void onMessageReceived(final JsonElement jsonElement) {
                        ReceivedSignalR(jsonElement);
                    }
                });
            }
            TryToConnect();
        }
        catch (Exception ex){
            Util.logsException(ex.getMessage(),this);
        }
    }
    public void ReceivedSignalR(JsonElement e) //TODO Сюда нужен ID точки учета?
    {
        Message message = gson.fromJson(e, Message.class);;
        Object body = message.getBody();
        String what = message.getHead().getWhat();
        if(what.equals("ping")){
            isPingOk = true;
        }
        else if(what.equals("ListUpdate")){
            String tmp1 = gson.toJson(body);
            ListUpdateBody listUpdateBody = gson.fromJson(tmp1, ListUpdateBody.class) ;
            String[] ids = listUpdateBody.getIds();
            for(String id : ids){
                if(id.equals(Const.objectIdUpp)){
                    try {
                        Thread.sleep(1000 * 3);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    MainFunction();
                }
            }
        }
    }
    private void TryToConnect()
    {
        try{
            if (connection.getState() == ConnectionState.Connected)
            {
                connection.stop();
            }
            else
            {
                connection.connected(new Runnable() {
                    @Override
                    public void run() {
                        ApiQuery.Instance().SignalBind(connection.getConnectionId(), MyService.this);
                    }
                });
                connection.start();
            }
        }
        catch (Exception ex){
            Util.logsException(ex.getMessage(),this);
        }
    }
    public void MainFunction(){ //TODO Здесь должны быть нужные мне данные
        if(isEnterInFunc || countVisit > 2) return;
        countVisit++;
        isEnterInFunc = true;
        //RecordsFromQueryDB[] records = ApiQuery.Instance().QueryFromDatabase(this);
        listIds.clear();
        listNames.clear();
        listPNames.clear();
        listValues.clear();
        RowFromRowCache[] recordsName = ApiQuery.Instance().RowCache(this);

        for (int i = 0; i < recordsName.length; i++) {
            String tmp1 = recordsName[i].getId();
            listIds.add(tmp1);
            String name = recordsName[i].getName();
            listNames.add(name);
            String pName = recordsName[i].getPname();
            listPNames.add(pName);
            float value = recordsName[i].getValue();
            String valueUnit = recordsName[i].getvalueUnitMeasurement();
            String values = value +" " + valueUnit;
            listValues.add(values);
        }



         sendInActive(recordsName);

        countVisit--;
    }
    public void sendInActive(RowFromRowCache[] records){ //TODO То что отправим в main



        Intent intent = new Intent()
                .putExtra("listNames", listNames)
                .putExtra("listPNames",listPNames)
                .putExtra("listValues",listValues)
                .putExtra("listIds",listIds)
                ;
        try {
            pendingIntent.send(MyService.this, Const.Success, intent);
        } catch (PendingIntent.CanceledException e) {
            Util.logsException(e.getMessage(),this);
        }


    }

    public void VerificationMainFunction(){


    }


}
