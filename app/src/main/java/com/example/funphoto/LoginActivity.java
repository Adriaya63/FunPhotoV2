package com.example.funphoto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonSignUp;
    private String idioma = "es";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        Intent intent = getIntent();

        //Cambiar idioma al darle a un boton
        if (extras != null) {
            idioma = extras.getString("IDIOMA");
        }
        Locale nuevaloc = new Locale(idioma);
        Locale.setDefault(nuevaloc);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);
        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        setContentView(R.layout.activity_login);

        // Asociar los elementos de la interfaz con las variables
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        // Inicializar los botones idiomas
        // Detecta el click en el boton español y cambia el idioma de la app
        Button cambiarIdioma_a_es = findViewById(R.id.buttonEspañol);
        cambiarIdioma_a_es.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idioma = "es";
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                intent.putExtra("IDIOMA", idioma);
                finish(); // Finaliza la actividad actual
                startActivity(intent); // Inicia la nueva actividad con el nuevo idioma
                //Toast para indicar el cambio a ingles
                Toast.makeText(getApplicationContext(), "Cambiando idioma a español", Toast.LENGTH_LONG).show();
            }
        });

        // Detecta el click en el boton ingles y cambia el idioma de la app
        Button cambiarIdioma_a_in = findViewById(R.id.buttonIngles);
        cambiarIdioma_a_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idioma = "en";
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                intent.putExtra("IDIOMA", idioma);
                finish(); // Finaliza la actividad actual
                startActivity(intent); // Inicia la nueva actividad con el nuevo idioma
                //Toast para indicar el cambio a ingles
                Toast.makeText(getApplicationContext(), "\n" + "Changing language to English", Toast.LENGTH_LONG).show();
            }
        });

        // Detecta el click en el boton euskera y cambia el idioma de la app
        Button cambiarIdioma_a_eus = findViewById(R.id.buttonEuskera);
        cambiarIdioma_a_eus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idioma = "eu";
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                intent.putExtra("IDIOMA", idioma);
                finish(); // Finaliza la actividad actual
                startActivity(intent); // Inicia la nueva actividad con el nuevo idioma
                //Toast para indicar el cambio a ingles
                Toast.makeText(getApplicationContext(), "Hizkuntza aldatzea euskarara", Toast.LENGTH_LONG).show();
            }
        });

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
