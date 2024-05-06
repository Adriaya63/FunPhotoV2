package com.example.funphoto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ImageView imageViewProfile;
    private Button buttonTakePhoto;
    private Button buttonSignUp;

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

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para tomar una foto de perfil
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí deberías agregar la lógica para registrar al nuevo usuario
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
}
