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
        Data inputData = getInputData();
        String usuario = inputData.getString("usuario");
        String nombre = inputData.getString("nombre");
        String contrasena = inputData.getString("contrasena");
        String email = inputData.getString("email");
        String imagenPath = inputData.getString("imagenPath");

        String url = "http://34.175.199.167:81/add_user.php";

        try {
            URL serverUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String parametros = "usuario=" + URLEncoder.encode(usuario, "UTF-8") +
                    "&nombre=" + URLEncoder.encode(nombre, "UTF-8") +
                    "&contrasena=" + URLEncoder.encode(contrasena, "UTF-8") +
                    "&email=" + URLEncoder.encode(email, "UTF-8") +
                    "&imagenPath=" + URLEncoder.encode(imagenPath, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(parametros);
            writer.flush();
            writer.close();

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            connection.disconnect();

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
