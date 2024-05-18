package com.example.funphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class UploadFotoWorker extends Worker {

    public UploadFotoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String usuario = inputData.getString("usuario");
        String pie = inputData.getString("pie");
        String imagenPath = inputData.getString("imagenPath");

        String url = "http://34.175.199.167:81/add_photo.php";

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imagenPath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            URL serverUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String parametros = "usuario=" + URLEncoder.encode(usuario, "UTF-8") +
                    "&pie=" + URLEncoder.encode(pie, "UTF-8") +
                    "&image=" + URLEncoder.encode(encodedImage, "UTF-8");

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
///