package com.example.matrixaskue;

public class Const {

    public static final String MX_URL = "http://system.matrixit.ru";
    public static final String MX_URL_local = "http://system.matrixit.ru:8081";
    public static final String IpAddressWls = "77.94.120.165";
    public static final Integer wlsPort = 5001;

    public final static String objectIdUpp = "facfda23-daaf-4491-819a-84de67a71579";//"facfda23-daaf-4491-819a-84de67a71579"
    public final static String objectIdWLS = "8e7162dc-eb4a-4da0-ba8d-18d440356d89";
    public final static Integer Error = 0;
    public final static Integer Success = 1;
    public final static Integer Session = 2;
    public final static Integer ClosedService = 3;
    public final static Integer Exit = 4;
    public final static Integer CustomId = 5;

    public final static Integer ActivDestroy = 77;
    public final static Integer ActivCreate = 2;

    public final static String ifDataNull = "НЕТ ДАННЫХ, ОБНОВИТЕ";
    public final static String notConnectionToServer = "Нет соединения с сервером";
    public final static String notConnectionToInternet = "Нет подключения к интернету";

    public final static String strHight = "\n"+"Высота, м:" +"\n" +"\n" + "Заполн., %:" +"\n"+"\n" + "WLS:"+"\n" +"\n";
    public final static String strCurr = "1";


    public final static Integer notifNotConnecion = 0;
    public final static Integer notifDataNull = 1;
    public final static Integer notifWlsLessThanWlsmin_Stop = 2;
    public final static Integer notifWlsMoreThanWlsmax_Start = 3;
    public final static Integer notifWlsLessThanWlsmin2_Start = 4;
    public final static Integer notifWlsMoreThanWlsmax2_Start = 5;
    public final static Integer notifMismatchIdWlsAndHeightMoreThanProc = 6;
    public final static Integer notifMaxTimeStop = 7;
    public final static Integer notifTokLessThanProc = 8;


}
