package com.example.pavlovka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class SigninActivity extends AppCompatActivity {

    public EditText etPassword, etLogin;
    private TextView tvMessageSignin;
    public String sessionId;

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        tvMessageSignin = findViewById(R.id.tvMessageSignin);
        if(!Util.isConnectionInternet(this)){
            Util.logsError(Const.notConnectionToInternet,this);
            tvMessageSignin.setText(Const.notConnectionToInternet);
            return;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logs, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.LogsActivityMenu:
                Intent intent1 = new Intent(this, LogsActivity.class);
                startActivity(intent1);
                return true;
            case R.id.ExitSignin:

                Intent answerIntent = new Intent();
                answerIntent.putExtra("Exit", true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    setResult(Const.Exit, answerIntent);
                    finishAndRemoveTask();
                } else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        setResult(Const.Exit, answerIntent);
                        finishAffinity();
                    } else
                    {
                        setResult(Const.Exit, answerIntent);
                        finish();
                    }
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void onClickEntry(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String login = etLogin.getText().toString(), password = etPassword.getText().toString();
                if(login.equals(""))
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvMessageSignin.setText("введите логин");
                        }
                    });
                }
                else if(password.equals("")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvMessageSignin.setText("введите пароль");
                        }
                    });
                }
                else {
                    sessionId = ApiQuery.Instance().AuthByLogin(SigninActivity.this, login, password);
                    if(sessionId == null || sessionId == ""){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvMessageSignin.setText("неверный логин или пароль");
                            }
                        });
                    }
                    else{
                        try {
                            Util.setPropertyConfig("login", login, SigninActivity.this);
                            Util.setPropertyConfig("password", password, SigninActivity.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent answerIntent = new Intent();
                        answerIntent.putExtra("isLoginAndPassword", true);
                        setResult(Const.Session, answerIntent);
                        finish();
                    }
                }
            }
        }).start();
    }
}
