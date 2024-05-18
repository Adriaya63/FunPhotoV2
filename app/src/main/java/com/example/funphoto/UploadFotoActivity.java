package com.example.funphoto;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UploadFotoActivity extends AppCompatActivity {

    private TextView titleTextView;
    private Button openCameraButton;
    private Button openGalleryButton;
    private Button uploadButton;
    private Button cancelButton;
    private ImageView imageToUpload;
    private EditText editFotoDesc;
    private String username;
    private String fotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_foto);

        username = getIntent().getStringExtra("username");


        // Instanciar cada elemento del XML
        titleTextView = findViewById(R.id.titleTextView);
        openCameraButton = findViewById(R.id.openCameraButton);
        openGalleryButton = findViewById(R.id.openGalleryButton);
        uploadButton = findViewById(R.id.uploadButton);
        cancelButton = findViewById(R.id.cancelButton);
        imageToUpload = findViewById(R.id.imageToUpload);
        editFotoDesc = findViewById(R.id.editFotoDesc);

        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent elIntentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureLauncher.launch(elIntentFoto);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadFotoActivity.this, MainActivity.class);
                intent.putExtra("username", username); // Agregar el nombre de usuario como extra
                startActivity(intent);
                finish();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFotoDesc = editFotoDesc.getText().toString();

                if (fotoPath != null) {
                    saveNewFoto(username, fotoPath, newFotoDesc);
                    String message = "Foto subida correctamente";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(UploadFotoActivity.this, MainActivity.class);
                    intent.putExtra("username", username); // Agregar el nombre de usuario como extra
                    startActivity(intent);
                    // Luego de registrar, puedes regresar a LoginActivity
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Por favor, toma una foto antes de registrarte", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new
                    ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap laminiatura = (Bitmap) bundle.get("data");
                    imageToUpload.setImageBitmap(laminiatura);
                    fotoPath = saveImageToStorage(laminiatura);
                } else {
                    Log.d("TakenPicture", "No se ha tomado ninguna foto");
                }
            });

    private String saveImageToStorage(Bitmap bitmap) {
        String path = getApplicationContext().getFilesDir().getPath() + "/profile_image.png";
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveNewFoto(String usuario,String fotoPath,String pie) {
        Data inputData = new Data.Builder()
                .putString("usuario", usuario)
                .putString("imagenPath", fotoPath)
                .putString("pie",pie)
                .build();

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest sendDataWorkRequest = new OneTimeWorkRequest.Builder(UploadFotoWorker.class)
                .setInputData(inputData)
                .build();
        workManager.enqueue(sendDataWorkRequest);
    }
}
