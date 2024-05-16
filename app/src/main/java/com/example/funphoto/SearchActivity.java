package com.example.funphoto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity {

    private EditText editTextSeach;
    private Button buttonSearch;
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileDescription;
    private Button btnFollow;
    private Button btnUnfollow;
    private RecyclerView photosRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Buscar los ImageButtons por su ID
        ImageButton imageButtonSearch = findViewById(R.id.imageButton);
        ImageButton imageButtonGallery = findViewById(R.id.imageButton2);
        ImageButton imageButtonUser = findViewById(R.id.imageButton3);

        editTextSeach = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        profileDescription = findViewById(R.id.profileDescription);
        btnFollow = findViewById(R.id.btnFollow);
        btnUnfollow = findViewById(R.id.btnUnfollow);
        photosRecyclerView = findViewById(R.id.photosRecyclerView);

        // Configurar el OnClickListener del include

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar todos los elementos del XML
                String searchText = editTextSeach.getText().toString();
                cargarDatosUsuarios(searchText);

            }
        });

        // Asignar OnClickListener a cada botón
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de búsqueda
                Toast.makeText(SearchActivity.this, "Botón de búsqueda clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de la galería
                Toast.makeText(SearchActivity.this, "Botón de galería clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchActivity.this, GalleryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de usuario
                Toast.makeText(SearchActivity.this, "Botón de usuario clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void cargarDatosUsuarios(String username) {
        boolean rdo = false;
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
        workManager.getWorkInfoByIdLiveData(cargarDatosUserRequest.getId()).observe(SearchActivity.this, workInfo -> {
            if (workInfo != null && workInfo.getState().isFinished()) {
                // La tarea ha finalizado
                if (workInfo.getState() == androidx.work.WorkInfo.State.SUCCEEDED) {
                    // Obtener los datos del usuario del resultado
                    String userData = workInfo.getOutputData().getString("userData");
                    Log.d("json222", "Valor de userData: " + userData);
                    if (userData.equals("Usuario no encontrado")){
                        String message = "Usuario no encontrado";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                        profileImage.setVisibility(View.INVISIBLE);
                        profileName.setVisibility(View.INVISIBLE);
                        profileDescription.setVisibility(View.INVISIBLE);
                        btnFollow.setVisibility(View.INVISIBLE);
                        btnUnfollow.setVisibility(View.INVISIBLE);
                        photosRecyclerView.setVisibility(View.INVISIBLE);
                    }else {

                        try {
                            // Convertir la cadena JSON a JSONObject
                            JSONObject jsonObject = new JSONObject(userData);

                            // Mostrar los datos del usuario en el log
                            Log.d("UserData_Main", "Datos del usuario - Nombre: " + jsonObject.getString("Nombre") +
                                    ", Email: " + jsonObject.getString("Email") +
                                    ", Bio: " + jsonObject.getString("Bio"));

                            profileName.setText(jsonObject.getString("Usuario"));
                            profileDescription.setText(jsonObject.getString("Bio"));

                            // Mostrar los datos del usuario en los TextViews
                            profileImage.setVisibility(View.VISIBLE);
                            profileName.setVisibility(View.VISIBLE);
                            profileDescription.setVisibility(View.VISIBLE);
                            btnFollow.setVisibility(View.VISIBLE);
                            btnUnfollow.setVisibility(View.VISIBLE);
                            photosRecyclerView.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("UserData", "Error al convertir la cadena JSON a JSONObject: " + e.getMessage());
                        }
                    }
                } else {
                    // La tarea falló
                    String message = "Inicio de sesión fallido. Verifica tus credenciales.";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
