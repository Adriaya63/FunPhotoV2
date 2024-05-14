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
import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ImageView imageViewProfile;
    private Button buttonTakePhoto;
    private Button buttonSignUp;
    private Button buttonCancel;
    private String fotoEnBase64;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextUsername = findViewById(R.id.editTextUsername);
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
                // Aquí deberías agregar la lógica para registrar al nuevo usuario
                String newUsername = editTextUsername.getText().toString();
                String newPassword = editTextPassword.getText().toString();
                String newEmail = editTextEmail.getText().toString();
                saveNewUser(newUsername,newPassword,newEmail,fotoEnBase64);
                // Por ahora, solo mostraremos un mensaje de usuario registrado correctamente
                String message = "Usuario registrado correctamente";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                // Luego de registrar, puedes regresar a LoginActivity
                finish();
            }
        });
    }
    private ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new
                    ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK &&
                        result.getData()!= null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap laminiatura = (Bitmap) bundle.get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    laminiatura.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] fototransformada = stream.toByteArray();
                    fotoEnBase64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);

                    imageViewProfile.setImageBitmap(laminiatura);
                } else {
                    Log.d("TakenPicture", "No photo taken");
                }
            });

    private void saveNewUser(String usuario, String contrasena,String email, String foto) {
        int chunkSize = 1024; // Tamaño del fragmento, ajusta según sea necesario
        List<String> chunks = splitBase64Image(foto, chunkSize);

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            // Crear un Data object con los parámetros
            Data inputData = new Data.Builder()
                    .putString("usuario", usuario)
                    .putString("contrasena", contrasena)
                    .putString("email", email)
                    .putString("imagenChunk", chunk)
                    .putBoolean("isLastChunk", i == chunks.size() - 1) // Marcar la última parte
                    .build();

            // Crear una instancia de WorkManager y programar la tarea
            WorkManager workManager = WorkManager.getInstance(getApplicationContext());
            OneTimeWorkRequest sendDataWorkRequest = new OneTimeWorkRequest.Builder(SendUserDataWorker.class)
                    .setInputData(inputData)
                    .build();
            workManager.enqueue(sendDataWorkRequest);
        }
    }

    private List<String> splitBase64Image(String base64Image, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int offset = 0;
        while (offset < base64Image.length()) {
            int length = Math.min(chunkSize, base64Image.length() - offset);
            String chunk = base64Image.substring(offset, offset + length);
            chunks.add(chunk);
            offset += length;
        }
        return chunks;
    }
}
