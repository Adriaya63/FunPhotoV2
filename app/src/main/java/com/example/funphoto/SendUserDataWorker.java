package com.example.funphoto;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SendUserDataWorker extends Worker {

    public SendUserDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Obtener los datos de entrada
        Data inputData = getInputData();
        String usuario = inputData.getString("usuario");
        String contrasena = inputData.getString("contrasena");
        String email = inputData.getString("email");
        String imagenChunk = inputData.getString("imagenChunk");
        boolean isLastChunk = inputData.getBoolean("isLastChunk", false);

        // URL de tu archivo PHP en el servidor
        String url = "http://34.175.200.65:81/add_user.php";

        try {
            // Crear la conexión HTTP
            URL serverUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();

            // Configurar la conexión
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Construir los parámetros de la solicitud
            String parametros = "usuario=" + URLEncoder.encode(usuario, "UTF-8") +
                    "&contrasena=" + URLEncoder.encode(contrasena, "UTF-8") +
                    "&email=" + URLEncoder.encode(email, "UTF-8") +
                    "&imagenChunk=" + URLEncoder.encode(imagenChunk, "UTF-8");

            // Escribir los datos en el cuerpo de la solicitud
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(parametros);
            writer.flush();
            writer.close();

            // Obtener la respuesta del servidor (opcional)
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            // Log.d("SendUserDataWorker", "Response Code: " + responseCode);
            // Log.d("SendUserDataWorker", "Response Message: " + responseMessage);

            // Cerrar la conexión
            connection.disconnect();

            if (isLastChunk) {
                // Esto es la última parte de la imagen, puedes procesarla completamente ahora
            }

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
