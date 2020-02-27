package com.example.pavlovka;

import android.content.Context;

import com.example.pavlovka.Classes.Auth.AuthBySession.AuthBySession;
import com.example.pavlovka.Classes.EditGetRow.EditGetRow;
import com.example.pavlovka.Classes.EditGetRow.RecordFromEditGetRow;
import com.example.pavlovka.Classes.FoldersGet.Children;
import com.example.pavlovka.Classes.FoldersGet.Data;
import com.example.pavlovka.Classes.FoldersGet.Folder;
import com.example.pavlovka.Classes.FoldersGet.FoldersGet;
import com.example.pavlovka.Classes.GetSessionidd.BodyFromSession;
import com.example.pavlovka.Classes.GetSessionidd.SessionJson;
import com.example.pavlovka.Classes.GetSessionidd.UserFromBodySession;
import com.example.pavlovka.Classes.Message;
import com.example.pavlovka.Classes.Poll;
import com.example.pavlovka.Classes.QueryFromDatabase.QueryDB;
import com.example.pavlovka.Classes.QueryFromDatabase.RecordsFromQueryDB;
import com.example.pavlovka.Classes.RowCache2And3.RowCache;
import com.example.pavlovka.Classes.RowCache2And3.RowFromRowCache;
import com.example.pavlovka.Classes.SignalBind;
import com.example.pavlovka.Classes.WaterTower.ExportWaterTower;
import com.example.pavlovka.Classes.WaterTower.NodeWaterTower;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

public class ApiQuery {

    private static ApiQuery instance = new ApiQuery();

    public static ApiQuery Instance() {
        return instance;
    }
    public Gson gson = new Gson();
    public String gSessionId = "";
    public String gFolderId = "";
    public String goObjectIdUpp = "";
    ArrayList<String> logsList = new ArrayList<>();
    boolean isAdmin = false;
    public String AuthByLogin(Context context, String login, String password){
        SessionJson sessionJson = new SessionJson();
        sessionJson.setWhat("auth-by-login");
        sessionJson.setLoginPassword(login, Helper.getMd5(password));

        String json = gson.toJson(sessionJson);
        Call<SessionJson> call = NetworkService.Instance().getMxApi().getSessionId(json);
        try {
            Response<SessionJson> response = call.execute();
            if(!response.isSuccessful()){
                return "";
            }
            BodyFromSession post = response.body().getBody();
            if(post == null) return "";
            UserFromBodySession userFromBodySession = post.getUser();
            if(userFromBodySession != null){
                String strIsAdmin = userFromBodySession.getIsAdmin();
                if(strIsAdmin == null || strIsAdmin.equals("false")){
                    isAdmin = false;
                }
                else {
                    isAdmin = true;
                }
            }
            gSessionId = post.getSessionId();
            Util.setPropertyConfig("sessionId", gSessionId, context);
            Util.setPropertyConfig("isAdmin", Boolean.toString(isAdmin), context);
            RowCache(context);
            return post.getSessionId();
        } catch (IOException e) {
            if(logsReplay(e.getMessage())){
                Util.logsException(e.getMessage(),context);
            }
        }
       return "";
    }
    public boolean logsReplay(String value){
        for(int i = 0; i < logsList.size(); i++){
            if(logsList.get(i).equals(value)){
                return false;
            }
        }
        if(logsList.size() > 4){
            logsList.remove(0);
        }
        logsList.add(value);
        return true;
    }
    public Message MessageExecute (String what, Object body, Context context){
        Message message = new Message();
        if(gSessionId == null || gSessionId == ""){
            isGetSession(context);
        }
        message.setHead(what, gSessionId);
        message.setBody(body);
        String json = gson.toJson(message);
        Message answerMessage = new Message();
        Call<Message> call = NetworkService.Instance().getMxApi().message(json);
        try {
            Response<Message> response = call.execute();
            if(!response.isSuccessful()) return null;
            answerMessage = response.body();
        } catch (IOException e) {
            if(logsReplay(e.getMessage())){
                Util.logsException(e.getMessage(),context);
            }
        }
        return answerMessage;
    }

    public void SignalBind(String connectionId, Context context){
        SignalBind signalBind = new SignalBind();
        signalBind.setConnectionId(connectionId);
        MessageExecute("signal-bin", signalBind, context);
    }

    public Boolean Poll(String objectId, String cmd, String components, Context context){
        if(!isAdmin){
            try {
                isAdmin = Boolean.parseBoolean(Util.getProperty("isAdmin", context));
            } catch (IOException exc) {
                Util.logsException(exc.getMessage(), context);
            }
        }
        if(!isAdmin) return true;
        try{
            Poll poll = new Poll();
            poll.setPoll1(new String[]{objectId}, cmd, components);
            Message answer = MessageExecute("poll", poll, context);
            String what = answer.getHead().getWhat();
            if(what.equals("poll-accepted")){
                try {
                    Thread.sleep(1000 * 3);
                    return true;
                } catch (InterruptedException exc) {
                    if(logsReplay(exc.getMessage())){
                        Util.logsException(exc.getMessage(),context);
                    }
                }
            }
        }catch (Exception ex){
            ex.fillInStackTrace();
        }
        return false;
    }

    public String isGetSession(Context context) {
        String login = "", password = "";
        try {
            login = Util.getProperty("login", context);
            password = Util.getProperty("password", context);
            gSessionId = Util.getProperty("sessionId", context);
        } catch (IOException exc) {
            Util.logsException(exc.getMessage(), context);
        }
        if(gSessionId == null || gSessionId.equals("")) return AuthByLogin(context, login, password);
        if(AuthBySession(context)) return gSessionId;
        return AuthByLogin(context, login, password);
    }

    public boolean AuthBySession(Context context)
    {
        if (gSessionId == null || gSessionId.equals("")) return false;
        AuthBySession authBySession = new AuthBySession();
        authBySession.setSessionId(gSessionId);
        try {
            Message message = MessageExecute("auth-by-session", authBySession, context);
            if(message == null || message.getHead() == null) return false;
            if (!message.getHead().getWhat().equals("auth-success")) return false;
            String json = gson.toJson(message.getBody());
            AuthBySession authBySession1 = gson.fromJson(json, AuthBySession.class) ;
            gSessionId = authBySession1.getSessionId();
            Util.setPropertyConfig("sessionId", gSessionId, context);
            return true;
        } catch (Exception e) {
            if(logsReplay(e.getMessage())){
                Util.logsException(e.getMessage(),context);
            }
        }
        return false;
    }

    public String FoldersGet (Context context){ // folderId папки

        try {
            Message message = MessageExecute("folders-get",null, context);
            String json1 = gson.toJson(message.getBody());
            FoldersGet foldersGet = gson.fromJson(json1, FoldersGet.class);
            Folder f = foldersGet.getRoot();
            Children[] ch = f.getChildren();
            for (int i = 0; i < ch.length; i++){
                Data d = ch[i].getData();
                String name = d.getName();
                if (name.equals("Павловка"))gFolderId = d.getId();
            }



        } catch (Exception e) {
            if(logsReplay(e.getMessage())){
                Util.logsException(e.getMessage(),context);
            }
        }
        return null;
    }

    public String RowCache(Context context){
        FoldersGet(context);
        RowCache rowCache = new RowCache();
        rowCache.setFilter(gFolderId, null, 10, 0, null);
        Message message = MessageExecute("rows-get-2", rowCache, context);
        String json1 = gson.toJson(message.getBody());
        RowCache rowCache1 = gson.fromJson(json1, RowCache.class);
        RowFromRowCache[] row = rowCache1.getRows();

        for (int i = 0; i < row.length; i++) {
        String name = row[i].getPname();
        if (name.equals("УПП<->сервер")) goObjectIdUpp = row[i].getId();
        }
        return goObjectIdUpp;
    }

    public RecordsFromQueryDB[] QueryFromDatabase(Context context){
        Calendar calStart = Calendar.getInstance();
        calStart.add(Calendar.MINUTE,-25);//-25
        Date dtStart = calStart.getTime();
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.add(Calendar.MINUTE, 20);
        Date dtNow = calendarNow.getTime();
        QueryDB queryDB = new QueryDB();
        queryDB.setQueryDB(new String[]{goObjectIdUpp}, dtStart, dtNow,"Current");
        try {
            Message message = MessageExecute("records-get1", queryDB, context);
            if(message == null || message.getBody() == null) return null;
            String json1 = gson.toJson(message.getBody());
            QueryDB queryDB1 = gson.fromJson(json1, QueryDB.class) ;
            return queryDB1.getRecords();
        } catch (Exception e) {
            if(logsReplay(e.getMessage())){
                Util.logsException(e.getMessage(),context);
            }
        }
        return null;
    }

    public RecordsFromQueryDB[] QueryFromDatabaseWls(Context context, String idWls){
        Calendar calStart = Calendar.getInstance();
        calStart.add(Calendar.MINUTE,-300);//-25
        Date dtStart = calStart.getTime();
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.add(Calendar.MINUTE, 20);
        Date dtNow = calendarNow.getTime();
        QueryDB queryDB = new QueryDB();
        queryDB.setQueryDB(new String[]{idWls}, dtStart, dtNow,"Current");
        try {
            Message message = MessageExecute("records-get1", queryDB, context);
            if(message == null || message.getBody() == null) return null;
            String json1 = gson.toJson(message.getBody());
            QueryDB queryDB1 = gson.fromJson(json1, QueryDB.class) ;
            return queryDB1.getRecords();
        } catch (Exception e) {
            if(logsReplay(e.getMessage())){
                Util.logsException(e.getMessage(),context);
            }
        }
        return null;
    }

    public ExportWaterTower ExportWatertower(Context context){
        if (goObjectIdUpp.equals("")) RowCache(context);
        ExportWaterTower exportWaterTower = new ExportWaterTower();
        exportWaterTower.setWaterTower(goObjectIdUpp);
        Message message = MessageExecute( "export-watertower", exportWaterTower, context);
        String json1 = gson.toJson(message.getBody());
        ExportWaterTower exportWaterTower1 = gson.fromJson(json1, ExportWaterTower.class);
        return exportWaterTower1;
    }

    public void NodeWatertower(final Float max, final Float min, final int controlMode, final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeWaterTower nodeWaterTower = new NodeWaterTower();
                nodeWaterTower.setNodeWaterTower(goObjectIdUpp, max, min, controlMode);
                Message message = MessageExecute( "node-watertower", nodeWaterTower, context);
                String json1 = gson.toJson(message.getBody());
            }
        }).start();

    }
    public RecordFromEditGetRow EditGetRow(Context context){
        EditGetRow editGetRow = new EditGetRow();
        editGetRow.setEditGetRow(false, goObjectIdUpp );
        Message message = MessageExecute( "edit-get-row", editGetRow, context);
        String json1 = gson.toJson(message.getBody());
        EditGetRow editGetRow1 = gson.fromJson(json1, EditGetRow.class);
        return editGetRow1.getTube();
    }


}
