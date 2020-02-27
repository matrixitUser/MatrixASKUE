package com.example.pavlovka;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
public class Util {
    public static int listKeysSize = 50;

    public static String getProperty(String key, Context context) throws IOException {
        return getProperties(context,"config").getProperty(key);
    }
    public static String getPropertyOrSetDefaultValue(String key, String value, Context context) throws IOException {
        String valueTmp = getProperty(key, context);
        if(valueTmp == null){
            setPropertyConfig(key, value, context);
            valueTmp = getProperty(key, context);
        }
        return valueTmp;
    }
    public static Properties getProperties(Context context, String fileName) throws IOException{
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(context.getFilesDir()+"/"+ fileName + ".properties");
        properties.load(fileInputStream);
        return properties;
    }

    public static void cleanProperties(Context context, String fileName) throws IOException{
        FileWriter   fileInputStream = new FileWriter (context.getFilesDir()+"/"+ fileName + ".properties", false);
        fileInputStream.write("");
        fileInputStream.flush();
        fileInputStream.close();
    }
    public static void setProperty(String key, String value, Context context, String fileName) throws IOException {
        if(value == null) return;
        Properties properties = new Properties();
        if (fileName == "heightOnDraw") listKeysSize = 100;
        else listKeysSize = 50;
        ArrayList<String> listKeys = new ArrayList<>();
        try {
            Properties properties1 = getProperties(context, fileName);
            Enumeration<String> enumerationStr = (Enumeration<String>) properties1.propertyNames();
            while (enumerationStr.hasMoreElements()){
                listKeys.add(enumerationStr.nextElement());
            }
            properties = properties1;
        } catch (IOException e) {
            e.printStackTrace();
            new File(context.getFilesDir(), fileName + ".properties");
        }
        if(listKeys.size() < listKeysSize){
            properties.setProperty(key,value);
        }
        else{
            Date dtTmp = new Date();
            String strKey = "";
            for(String keyTmp : listKeys){
                Date dtKey = new Date();
                DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
                if(fileName.equals("logs")){
                    try {
                        if(keyTmp.split("#").length > 1)
                        {
                            dtKey = formatter.parse(keyTmp.split("#")[1]);
                        }
                        else{
                            properties.remove(keyTmp);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        dtKey = formatter.parse(keyTmp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if(dtTmp.after(dtKey)){
                    strKey = keyTmp;
                    dtTmp = dtKey;
                }
            }
            properties.remove(strKey);
            properties.setProperty(key,value);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(context.getFilesDir()+"/"+ fileName + ".properties");
        properties.store(fileOutputStream,null);
    }

    public static void setPropertyConfig(String key, String value, Context context) throws IOException {
        if(value == null) return;
        Properties properties = new Properties();
        try {
            Properties properties1 = getProperties(context, "config");
            properties = properties1;
        } catch (IOException e) {
            e.printStackTrace();
            new File(context.getFilesDir(), "config.properties");
        }
        properties.setProperty(key,value);
        FileOutputStream fileOutputStream = new FileOutputStream(context.getFilesDir()+"/config.properties");
        properties.store(fileOutputStream,null);
    }

    public static void logsInfo(String value, Context context){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        String key = simpleDateFormat.format(new Date());
        try {
            setProperty("I#" + key, value, context, "logs");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void logsError(String value, Context context){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        String key = simpleDateFormat.format(new Date());
        try {
            setProperty("E#" + key, value, context, "logs");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void logsException(String value, Context context){
        Throwable t = new Throwable();
        StackTraceElement trace[] = t.getStackTrace();
        // Глубина стэка должна быть больше 1-го, поскольку интересующий
        // нас элемент стэка находится под индексом 1 массива элементов
        // Элемент с индексом 0 - это текущий метод, т.е. log
        String tmp = "";
        if (trace.length > 1)
        {
            StackTraceElement element = trace[1];
            tmp = element.getClassName() + "." + element.getMethodName() + " [" + element.getLineNumber() + "] ";
        }
        logsError(tmp + value, context);
    }
    public static boolean isConnectionInternet(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }
}