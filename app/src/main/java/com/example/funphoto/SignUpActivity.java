package com.example.funphoto;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ImageView imageViewProfile;
    private Button buttonTakePhoto;
    private Button buttonSignUp;
    private Button buttonCancel;
    private String fotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonCancel = findViewById(R.id.buttonCancel);

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent elIntentFoto= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureLauncher.launch(elIntentFoto);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Registro de usuario cancelado";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                // Luego de registrar, puedes regresar a LoginActivity
                finish();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editTextUsername.getText().toString();
                String newName = editTextName.getText().toString();
                String newPassword = editTextPassword.getText().toString();
                String newEmail = editTextEmail.getText().toString();

                if (fotoPath != null) {
                    saveNewUser(newUsername,newName, newPassword, newEmail, fotoPath);
                    String message = "Usuario registrado correctamente";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
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
                    imageViewProfile.setImageBitmap(laminiatura);
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

    private void saveNewUser(String usuario,String nombre, String contrasena, String email, String fotoPath) {
        Data inputData = new Data.Builder()
                .putString("usuario", usuario)
                .putString("nombre", nombre)
                .putString("contrasena", contrasena)
                .putString("email", email)
                .putString("imagenPath", fotoPath)
                .build();

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest sendDataWorkRequest = new OneTimeWorkRequest.Builder(SendUserDataWorker.class)
                .setInputData(inputData)
                .build();
        workManager.enqueue(sendDataWorkRequest);
    }

}
