package com.example.matrixaskue;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.matrixaskue.Classes.QueryFromDatabase.RecordsFromQueryDB;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Helper {
    public static String getMetaData(Context context, String name) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }
    public static RecordsFromQueryDB GetLastRecordByType(RecordsFromQueryDB[] records, String type){
        ArrayList<RecordsFromQueryDB> recordsLocal= new ArrayList<>();

        try{
            for(RecordsFromQueryDB rec : records){
                if(rec.s1.equals(type)){
                    recordsLocal.add(rec);
                }
            }
            return recordsLocal.get(recordsLocal.size() - 1);
        }
        catch(Exception ex){}
        return  null;
    }
    public static ArrayList<RecordsFromQueryDB> GetRecordsByType(RecordsFromQueryDB[] records, String type){
        ArrayList<RecordsFromQueryDB> recordsLocal= new ArrayList<>();
        try{
            for(RecordsFromQueryDB rec : records){
                if(rec.s1.equals(type)){
                    recordsLocal.add(rec);
                }
            }
            return recordsLocal;
        }
        catch (Exception ex){}
        return null;
    }
    public static String GetS2FromRec(RecordsFromQueryDB record){
        try{
            return record.getS2();
        }
        catch (Exception ex){}
        return "";
    }
    public static String GetD1FromRec(RecordsFromQueryDB record){
        try{
            return record.getD1s();
        }
        catch (Exception ex){}
        return "";
    }
    public static String getMd5(String input)
    {
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getDateDiff(Date dtOld , Date dtNew, TimeUnit timeUnit) {
        long diffInMillies = dtNew.getTime() - dtOld.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}
