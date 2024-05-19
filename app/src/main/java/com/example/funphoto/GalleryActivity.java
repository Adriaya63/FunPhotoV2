package com.example.funphoto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView recyclerViewTasks;
    private PubliAdapter taskAdapter;
    private List<Publicacion> pubList;
    String username = "";
    String follows = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Recuperar el Intent que inició esta actividad
        username = getIntent().getStringExtra("username");
        follows = getIntent().getStringExtra("follows");

        pubList = cargarFotosSeguidos(follows);
        pubList = ordenarPorFecha(pubList);

        recyclerViewTasks = findViewById(R.id.photosRecyclerView);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new PubliAdapter(pubList);
        recyclerViewTasks.setAdapter(taskAdapter);

        // Buscar los ImageButtons por su ID
        ImageButton imageButtonSearch = findViewById(R.id.imageButton);
        ImageButton imageButtonGallery = findViewById(R.id.imageButton2);
        ImageButton imageButtonUser = findViewById(R.id.imageButton3);

        // Asignar OnClickListener a cada botón
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de búsqueda
                Toast.makeText(GalleryActivity.this, "Botón de búsqueda clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GalleryActivity.this, SearchActivity.class);
                intent.putExtra("username", username); // Agregar el nombre de usuario como extra
                intent.putExtra("follows", follows);
                startActivity(intent);
                finish();
            }
        });

        imageButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de la galería
                Toast.makeText(GalleryActivity.this, "Botón de galería clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GalleryActivity.this, GalleryActivity.class);
                intent.putExtra("username", username); // Agregar el nombre de usuario como extra
                intent.putExtra("follows", follows);
                startActivity(intent);
                finish();
            }
        });

        imageButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de usuario
                Toast.makeText(GalleryActivity.this, "Botón de usuario clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
                intent.putExtra("username", username); // Agregar el nombre de usuario como extra
                intent.putExtra("follows", follows);
                startActivity(intent);
                finish();
            }
        });
    }
    private List<Publicacion> cargarFotosSeguidos(String follows) {
        List<Publicacion> publicaciones = new ArrayList<>();
        try {
            // Convertir la cadena follows en un JSONArray
            JSONArray followsArray = new JSONArray(follows);

            // Iterar sobre los elementos del JSONArray
            for (int i = 0; i < followsArray.length(); i++) {
                // Obtener el objeto JSON en la posición i
                JSONObject jsonObject = followsArray.getJSONObject(i);
                String usuario = jsonObject.getString("seguido");

                Data inputData = new Data.Builder()
                        .putString("usuario", usuario)
                        .build();

                // Crear una instancia de WorkManager y programar la tarea para cargar las fotos del usuario
                WorkManager workManager = WorkManager.getInstance(getApplicationContext());
                OneTimeWorkRequest cargarFotosUsuarioRequest = new OneTimeWorkRequest.Builder(GetUserImagesWorker.class)
                        .setInputData(inputData)
                        .build();
                workManager.enqueue(cargarFotosUsuarioRequest);

                // Observar el resultado de la tarea
                workManager.getWorkInfoByIdLiveData(cargarFotosUsuarioRequest.getId()).observe(GalleryActivity.this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        // La tarea ha finalizado
                        if (workInfo.getState() == androidx.work.WorkInfo.State.SUCCEEDED) {
                            // Obtener las fotos del usuario del resultado
                            String userPhotos = workInfo.getOutputData().getString("userData");
                            Log.d("FotosUsuario", "Fotos del usuario: " + userPhotos);

                            try {
                                // Convertir la cadena JSON a JSONArray
                                JSONArray jsonArray = new JSONArray(userPhotos);


                                // Recorrer el JSONArray y crear objetos Imagen para cada elemento JSON
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsonImage = jsonArray.getJSONObject(j);
                                    Log.d("Json",jsonImage.toString());
                                    String user = jsonImage.getString("usuario");
                                    String fotoPath = jsonImage.getString("foto");
                                    String pieFoto = jsonImage.getString("pie");
                                    String date = jsonImage.getString("fecha");

                                    // Crear un nuevo objeto Imagen y agregarlo a la lista pubList
                                    Publicacion imagen = new Publicacion(user,fotoPath, pieFoto,date);
                                    publicaciones.add(imagen);
                                }

                                // Notificar al adaptador que los datos han cambiado
                                taskAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("FotosUsuario", "Error al procesar el JSON: " + e.getMessage());
                            }

                        } else {
                            // La tarea falló
                            Log.e("FotosUsuario", "Error al cargar las fotos del usuario.");
                        }
                    }

                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Manejar la excepción si ocurre algún error al procesar el JSON
        }
        // Crear un objeto Data con los parámetros

        return publicaciones;
    }

    private List<Publicacion> ordenarPorFecha(List<Publicacion> pubs){
        Collections.sort(pubs, new Comparator<Publicacion>() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            @Override
            public int compare(Publicacion p1, Publicacion p2) {
                try {
                    Date date1 = sdf.parse(p1.getDate());
                    Date date2 = sdf.parse(p2.getDate());
                    // Ordenar de manera descendente (de más reciente a más antigua)
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        return pubs;
    }
}
