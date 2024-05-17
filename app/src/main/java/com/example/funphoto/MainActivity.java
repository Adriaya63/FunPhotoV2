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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements EditBioDialog.EditBioDialogListener {
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener el username del intent
        username = getIntent().getStringExtra("username");

        // Llamar al método para cargar los datos del usuario
        cargarDatosUsuarios(username);

        // Buscar los ImageButtons por su ID
        ImageButton imageButtonSearch = findViewById(R.id.imageButton);
        ImageButton imageButtonGallery = findViewById(R.id.imageButton2);
        ImageButton imageButtonUser = findViewById(R.id.imageButton3);
        Button closeSessionButton = findViewById(R.id.closeSessionButton);
        Button editButton = findViewById(R.id.editButton);


        // Asignar OnClickListener a cada botón
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de búsqueda
                Toast.makeText(MainActivity.this, "Botón de búsqueda clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("username", username); // Agregar el nombre de usuario como extra
                startActivity(intent);
                finish();
            }
        });

        imageButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de la galería
                Toast.makeText(MainActivity.this, "Botón de galería clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                intent.putExtra("username", username); // Agregar el nombre de usuario como extra
                startActivity(intent);
                finish();
            }
        });

        imageButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de usuario
                Toast.makeText(MainActivity.this, "Botón de usuario clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("username", username); // Agregar el nombre de usuario como extra
                startActivity(intent);
                finish();
                // No es necesario iniciar una nueva actividad aquí, ya estamos en MainActivity
            }
        });

        // Agregar OnClickListener al botón de cerrar sesión
        closeSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de cerrar sesión
                // Iniciar la actividad LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Cerrar la actividad actual para evitar que el usuario regrese presionando el botón de retroceso
            }
        });

        // Agregar OnClickListener al botón de editar
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de editar
                // Mostrar el cuadro de diálogo para editar la biografía
                openEditBioDialog();

            }
        });
    }

    private void cargarDatosUsuarios(String username) {
        // Imprimir el nombre de usuario para verificar si funciona
        Log.d("Usuario_Main", "**************Username: " + username);

        // Crear un objeto Data con los parámetros
        Data inputData = new Data.Builder()
                .putString("usuario", username)
                .build();

        Log.d("MainCargarDatUsu1", "Entra bien en Main");

        // Crear una instancia de WorkManager y programar la tarea para carga   r los datos del usuario
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest cargarDatosUserRequest = new OneTimeWorkRequest.Builder(LoadUserDataWorker.class)
                .setInputData(inputData)
                .build();
        workManager.enqueue(cargarDatosUserRequest);

        // Observar el resultado de la tarea
        Log.d("MainCargarDatUsu2", "Entra bien en imprimir archivos");
        workManager.getWorkInfoByIdLiveData(cargarDatosUserRequest.getId()).observe(MainActivity.this, workInfo -> {
            if (workInfo != null && workInfo.getState().isFinished()) {
                // La tarea ha finalizado
                if (workInfo.getState() == androidx.work.WorkInfo.State.SUCCEEDED) {
                    // Obtener los datos del usuario del resultado
                    String userData = workInfo.getOutputData().getString("userData");
                    Log.d("json222", "Valor de userData: " + userData);

                    try {
                        // Convertir la cadena JSON a JSONObject
                        JSONObject jsonObject = new JSONObject(userData);

                        // Mostrar los datos del usuario en el log
                        Log.d("UserData_Main", "Datos del usuario - Nombre: " + jsonObject.getString("Nombre") +
                                ", Email: " + jsonObject.getString("Email") +
                                ", Bio: " + jsonObject.getString("Bio"));

                        TextView nombreUsuarioTextView = findViewById(R.id.profileName);
                        nombreUsuarioTextView.setText(jsonObject.getString("Nombre"));

                        TextView bioUsuarioTextView = findViewById(R.id.profileDescription);
                        bioUsuarioTextView.setText(jsonObject.getString("Bio"));



                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("UserData", "Error al convertir la cadena JSON a JSONObject: " + e.getMessage());
                    }

                } else {
                    // La tarea falló
                    String message = "Inicio de sesión fallido. Verifica tus credenciales.";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openEditBioDialog() {
        EditBioDialog editBioDialog = new EditBioDialog();
        editBioDialog.show(getSupportFragmentManager(), "edit bio dialog");

    }

    @Override
    public void onSaveClicked(String newBio) {
        // Aquí puedes enviar el nuevo texto de la biografía y el nombre de usuario al Worker
        sendDataToEditWorker(newBio, username);
    }

    private void sendDataToEditWorker(String newBio, String username) {
        // Crear un objeto Data con los parámetros
        Data inputData = new Data.Builder()
                .putString("usuario", username)
                .putString("bio", newBio)
                .build();

        // Crear una instancia de WorkManager y programar la tarea para editar los datos del usuario
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest editUserDataRequest = new OneTimeWorkRequest.Builder(EditUserDataWorker.class)
                .setInputData(inputData)
                .build();
        workManager.enqueue(editUserDataRequest);

        // Observar el resultado de la tarea, si es necesario
    }
}
