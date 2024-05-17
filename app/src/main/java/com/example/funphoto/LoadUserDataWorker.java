package com.example.funphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoadUserDataWorker extends Worker {

    public LoadUserDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("WorkerCargarDatosUsu", "Entra bien e datos usuario");
        // Obtener los datos de entrada
        Data inputData = getInputData();
        String nombreUsuario = inputData.getString("usuario");

        // URL del archivo PHP en el servidor para obtener los datos del usuario
        String url = "http://34.175.199.167:81/cogerDatosUsuario.php?Usuario=" + nombreUsuario;

        try {
            // Crear la conexión HTTP
            URL serverUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();

            // Configurar la conexión
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Construir los parámetros de la solicitud
            String parametros = "usuario=" + URLEncoder.encode(nombreUsuario, "UTF-8");

            Log.d("Datos:","Param"+parametros);

            // Escribir los datos en el cuerpo de la solicitud
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(parametros);
            writer.flush();
            writer.close();

            // Leer la respuesta del servidor
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            // Obtener la respuesta como cadena JSON
            String jsonResponse = responseBuilder.toString();
            JSONObject jsonObject = new JSONObject(jsonResponse);
            String image64 = jsonObject.getString("pImage");
            byte[] decodedBytes = Base64.decode(image64, Base64.DEFAULT);
            Bitmap imagen = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            String fotoPath = saveImageToStorage(imagen);

            jsonObject.put("pImage",fotoPath);
            String userData = jsonObject.toString();

            Log.d("UserData", "Cadena JSON recibida: " + jsonResponse);

            // Devolver la cadena JSON como resultado
            Data outputData = new Data.Builder()
                    .putString("userData", userData)
                    .build();

            return Result.success(outputData);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private String saveImageToStorage(Bitmap bitmap) {
        String path = getApplicationContext().getFilesDir().getPath() + "/profile_image.png";
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

