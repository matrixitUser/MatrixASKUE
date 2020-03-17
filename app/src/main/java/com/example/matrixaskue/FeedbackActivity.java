package com.example.matrixaskue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class FeedbackActivity extends AppCompatActivity {
    EditText textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        textEmail = findViewById(R.id.textEmail);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void onClickSend(View view) {
        String text = textEmail.getText().toString();
        /* Создаем интент с экшеном на отправку */
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        /* Заполняем данными: тип текста, адрес, сабж и собственно текст письма */
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"8.elpa.mary.8@gmail.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Запрос");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);

        /* Отправляем на выбор!*/
        FeedbackActivity.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));


    }
}
