package com.example.matrixaskue;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SendMetersActivity extends AppCompatActivity {
    private ArrayList<String> aItemsMethod;
    private ArrayList<String> listValue = new ArrayList<>();
    private final int Pick_image = 1;
    private static final int CAMERA_REQUEST = 0;
    Spinner spinner;
    TextView getValue;
    CheckBox verification;
    EditText metersForSend;
    Uri imageUri;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_meters);
        assert getSupportActionBar() != null;   //null check
        getItemsMethod();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, aItemsMethod);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getValue = findViewById(R.id.getValue);
        spinner =  findViewById(R.id.spinner);
        verification = findViewById(R.id.verification);
        metersForSend = findViewById(R.id.metersForSend);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                getValue.setText(listValue.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void getItemsMethod() {
        Intent intent = getIntent();
        aItemsMethod = intent.getStringArrayListExtra("listNames");
        listValue = intent.getStringArrayListExtra("listValue");
    }

    public void onClickSendMeters(View view) {
        if (verification.isChecked()){
            String meters = metersForSend.getText().toString();
            if (!meters.equals("")){
                if (imageUri == null){
                    /* Создаем интент с экшеном на отправку */
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                    /* Заполняем данными: тип текста, адрес, сабж и собственно текст письма */
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"info@matrixit.ru"});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Корректировка показаний");
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, meters);

                    /* Отправляем на выбор!*/
                    startActivity(emailIntent);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Сообщение отправлено",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);

                    /* Заполняем данными: тип текста, адрес, сабж и собственно текст письма */
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"8.elpa.mary.8@gmail.com"});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Корректировка показаний");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, meters);
                    emailIntent.setType("message/rfc822");


                    /* Отправляем на выбор!*/
                    startActivity(Intent.createChooser(emailIntent, "Выбор меил агента"));
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Сообщение отправлено",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            else if (imageUri == null){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Введите показания для отправки",
                            Toast.LENGTH_SHORT);
                    toast.show();
            }
            else if (imageUri != null){
                meters = "Показания во вложении";
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                /* Заполняем данными: тип текста, адрес, сабж и собственно текст письма */
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"8.elpa.mary.8@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Корректировка показаний");
                emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, meters);
                emailIntent.setType("message/rfc822");


                /* Отправляем на выбор!*/
                startActivity(Intent.createChooser(emailIntent, "Выбор меил агента"));
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Сообщение отправлено",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Подтвердите правильность данных",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onClickPhotoFromCamera(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // проверяем, что есть приложение способное обработать интент
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // создать файл для фотографии
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // ошибка, возникшая в процессе создания файла

            }

            // если файл создан, запускаем приложение камеры
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }

    public void onClickPhotoFromGallery(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, Pick_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case Pick_image:
                if(resultCode == RESULT_OK){

                    //Получаем URI изображения
                    imageUri = data.getData();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Изображение прикреплено",
                            Toast.LENGTH_SHORT);
                    toast.show();

                }
            case CAMERA_REQUEST:
                if(resultCode == RESULT_OK){

                    // Фотка сделана, извлекаем картинку
                    imageUri = data.getData();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Изображение прикреплено",
                            Toast.LENGTH_SHORT);
                    toast.show();

                }
        }
    }

    private File createImageFile() throws IOException {

        // создание файла с уникальным именем
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* префикс */
                ".jpg",         /* расширение */
                storageDir      /* директория */
        );

        // сохраняем пусть для использования с интентом ACTION_VIEW
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}
