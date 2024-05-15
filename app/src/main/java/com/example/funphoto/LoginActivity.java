package com.example.funphoto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Asociar los elementos de la interfaz con las variables
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        // Acción cuando se presiona el botón de login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el nombre de usuario y contraseña
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                Log.d("Data",username+" "+password);

                // Crear un objeto Data con los parámetros
                Data inputData = new Data.Builder()
                        .putString("usuario", username)
                        .putString("contrasena", password)
                        .build();

                // Crear una instancia de WorkManager y programar la tarea para verificar el inicio de sesión
                WorkManager workManager = WorkManager.getInstance(getApplicationContext());
                OneTimeWorkRequest loginUserWorkRequest = new OneTimeWorkRequest.Builder(LoginUserWorker.class)
                        .setInputData(inputData)
                        .build();
                workManager.enqueue(loginUserWorkRequest);

                // Observar el resultado de la tarea
                workManager.getWorkInfoByIdLiveData(loginUserWorkRequest.getId()).observe(LoginActivity.this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        // La tarea ha finalizado
                        if (workInfo.getState() == androidx.work.WorkInfo.State.SUCCEEDED) {
                            // La autenticación fue exitosa
                            String message = "Inicio de sesión exitoso para el usuario: " + username;
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            // Redirigir a MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", username); // Aquí se añade el parámetro username al intent
                            startActivity(intent);
                            finish(); // Cerrar LoginActivity para que no se pueda volver atrás
                        } else {
                            // La autenticación falló
                            String message = "Inicio de sesión fallido. Verifica tus credenciales.";
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
