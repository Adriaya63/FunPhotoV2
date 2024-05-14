package com.example.funphoto;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginUserWorker extends Worker {

    public LoginUserWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Obtener los datos de entrada
        Data inputData = getInputData();
        String nombreUsuario = inputData.getString("usuario");
        String contrasena = inputData.getString("contrasena");

        Log.d("Data", "Datos:"+nombreUsuario+" "+contrasena);
        // URL del archivo PHP en el servidor
        String url = "http://34.175.199.167:81/verificar_login.php";

        try {
            // Crear la conexión HTTP
            URL serverUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();

            // Configurar la conexión
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Construir los parámetros de la solicitud
            String parametros = "usuario=" + URLEncoder.encode(nombreUsuario, "UTF-8") +
                    "&contrasena=" + URLEncoder.encode(contrasena, "UTF-8");

            Log.d("Datos:","Param"+parametros);

            // Escribir los datos en el cuerpo de la solicitud
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(parametros);
            writer.flush();
            writer.close();

            // Leer la respuesta del servidor
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();
            reader.close();

            // Comprobar la respuesta del servidor
            if (response != null && response.equals("Autenticado")) {
                // Si el usuario está autenticado, regresar Result.success()
                return Result.success();
            } else {
                // Si no está autenticado, regresar Result.failure()
                return Result.failure();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
