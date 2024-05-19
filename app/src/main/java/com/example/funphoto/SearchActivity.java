package com.example.funphoto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText editTextSeach;
    private Button buttonSearch;
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileDescription;
    private Button btnFollow;
    private Button btnUnfollow;
    private RecyclerView photosRecyclerView;
    String username = "";
    String follows = "";
    String searchText = "";

    private PubliAdapter taskAdapter;
    private List<Publicacion> pubList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Obtener el username del intent
        username = getIntent().getStringExtra("username");
        follows = getIntent().getStringExtra("follows");
        Log.d("SearchRecived",follows);
        Log.d("SearchUser",username);

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
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar todos los elementos del XML
                searchText = editTextSeach.getText().toString();
                cargarDatosUsuarios(searchText);
                pubList = cargarFotosUsuario(searchText);
                taskAdapter = new PubliAdapter(pubList);
                photosRecyclerView.setAdapter(taskAdapter);

            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarFollow(searchText,username);
                Toast.makeText(SearchActivity.this, "Has seguido a "+searchText, Toast.LENGTH_SHORT).show();

            }
        });

        btnUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarFollow(searchText,username);
                Toast.makeText(SearchActivity.this, "Has dejado de seguir a "+searchText, Toast.LENGTH_SHORT).show();

            }
        });

        // Asignar OnClickListener a cada botón
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción cuando se hace clic en el botón de búsqueda
                Toast.makeText(SearchActivity.this, "Botón de búsqueda clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
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
                Toast.makeText(SearchActivity.this, "Botón de galería clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchActivity.this, GalleryActivity.class);
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
                Toast.makeText(SearchActivity.this, "Botón de usuario clickeado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("username", username); // Agregar el nombre de usuario como extra
                intent.putExtra("follows", follows);
                startActivity(intent);
                finish();
            }
        });
    }

    private void cargarDatosUsuarios(String usuario) {
        // Imprimir el nombre de usuario para verificar si funciona
        Log.d("Usuario_Main", "**************Username: " + usuario);

        // Crear un objeto Data con los parámetros
        Data inputData = new Data.Builder()
                .putString("usuario", usuario)
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

                            Bitmap imagen = BitmapFactory.decodeFile(jsonObject.getString("pImage"));
                            profileImage.setImageBitmap(imagen);

                            Bitmap circularBitmap = ImageHelper.getCircularBitmap(imagen);
                            profileImage.setImageBitmap(circularBitmap);

                            // Mostrar los datos del usuario en los TextViews
                            profileImage.setVisibility(View.VISIBLE);
                            profileName.setVisibility(View.VISIBLE);
                            profileDescription.setVisibility(View.VISIBLE);
                            btnFollow.setVisibility(View.VISIBLE);
                            Log.d("Pruebas",follows);
                            Log.d("Pruebas1",searchText);
                            Log.d("Pruebas2",username);
                            if(searchText.equals(username)){
                                btnFollow.setVisibility(View.INVISIBLE);
                                btnUnfollow.setVisibility(View.INVISIBLE);
                            }else if (buscarUser(follows,searchText)){
                                btnFollow.setVisibility(View.INVISIBLE);
                                btnUnfollow.setVisibility(View.VISIBLE);
                            }else{
                                btnFollow.setVisibility(View.VISIBLE);
                                btnUnfollow.setVisibility(View.INVISIBLE);
                            }
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

    private List<Publicacion> cargarFotosUsuario(String username) {
        // Crear un objeto Data con los parámetros
        List<Publicacion> publicaciones = new ArrayList<>();
        Data inputData = new Data.Builder()
                .putString("usuario", username)
                .build();

        // Crear una instancia de WorkManager y programar la tarea para cargar las fotos del usuario
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest cargarFotosUsuarioRequest = new OneTimeWorkRequest.Builder(GetUserImagesWorker.class)
                .setInputData(inputData)
                .build();
        workManager.enqueue(cargarFotosUsuarioRequest);

        // Observar el resultado de la tarea
        workManager.getWorkInfoByIdLiveData(cargarFotosUsuarioRequest.getId()).observe(SearchActivity.this, workInfo -> {
            if (workInfo != null && workInfo.getState().isFinished()) {
                // La tarea ha finalizado
                if (workInfo.getState() == androidx.work.WorkInfo.State.SUCCEEDED) {
                    // Obtener las fotos del usuario del resultado
                    String userPhotos = workInfo.getOutputData().getString("userData");
                    Log.d("FotosUsuario", "Fotos del usuario: " + userPhotos);

                    try {
                        // Convertir la cadena JSON a JSONArray
                        JSONArray jsonArray = new JSONArray(userPhotos);

                        // Limpiar la lista de publicaciones antes de agregar nuevas imágenes
                        publicaciones.clear();

                        // Recorrer el JSONArray y crear objetos Imagen para cada elemento JSON
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonImage = jsonArray.getJSONObject(i);
                            Log.d("Json",jsonImage.toString());
                            String user = jsonImage.getString("usuario");
                            String fotoPath = jsonImage.getString("foto");
                            String pieFoto = jsonImage.getString("pie");

                            // Crear un nuevo objeto Imagen y agregarlo a la lista pubList
                            Publicacion imagen = new Publicacion(user,fotoPath, pieFoto);
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
        return publicaciones;
    }

    private void insertarFollow(String seguido,String seguidor) {
        Data inputData = new Data.Builder()
                .putString("seguido", seguido)
                .putString("seguidor", seguidor)
                .build();

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest sendDataWorkRequest = new OneTimeWorkRequest.Builder(InsertFollowWorker.class)
                .setInputData(inputData)
                .build();
        workManager.enqueue(sendDataWorkRequest);
    }
    private void eliminarFollow(String seguido,String seguidor) {
        Data inputData = new Data.Builder()
                .putString("seguido", seguido)
                .putString("seguidor", seguidor)
                .build();

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest sendDataWorkRequest = new OneTimeWorkRequest.Builder(DeleteFollowWorker.class)
                .setInputData(inputData)
                .build();
        workManager.enqueue(sendDataWorkRequest);
    }
    private boolean buscarUser(String follows, String name) {
        boolean sigue = false;

        try {
            // Convertir la cadena follows en un JSONArray
            JSONArray followsArray = new JSONArray(follows);

            // Iterar sobre los elementos del JSONArray
            for (int i = 0; i < followsArray.length(); i++) {
                // Obtener el objeto JSON en la posición i
                JSONObject jsonObject = followsArray.getJSONObject(i);
                if(jsonObject.getString("seguido").equals(searchText)){
                    sigue = true;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Manejar la excepción si ocurre algún error al procesar el JSON
        }

        return sigue;
    }

}
