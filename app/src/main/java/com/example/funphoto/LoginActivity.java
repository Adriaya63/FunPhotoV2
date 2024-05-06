package com.example.funphoto;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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
                // Aquí puedes agregar la lógica para el inicio de sesión
                // Por ahora, solo mostraremos un mensaje de inicio de sesión exitoso
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String message = "Inicio de sesión exitoso para el usuario: " + username;
                // Puedes reemplazar este mensaje con la lógica real de inicio de sesión
                // como verificación de credenciales, acceso a la base de datos, etc.
                // Por ahora, solo mostraremos un mensaje de éxito.
                // También puedes redirigir a la actividad principal aquí si el inicio de sesión es exitoso.
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                // Redirigir a MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Cerrar LoginActivity para que no se pueda volver atrás
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
