package com.example.funphoto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class GalleryActivity extends AppCompatActivity {

    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Recuperar el Intent que inició esta actividad
        username = getIntent().getStringExtra("username");

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
                startActivity(intent);
                finish();
            }
        });
    }
}
