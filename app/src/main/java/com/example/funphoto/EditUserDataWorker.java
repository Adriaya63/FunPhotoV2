package com.example.funphoto;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class EditUserDataWorker extends Worker {

    public EditUserDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String usuario = inputData.getString("usuario");
        String bio = inputData.getString("bio");


        String url = "http://34.175.199.167:81/update_bio.php";

        try {
            URL serverUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String parametros = "usuario=" + URLEncoder.encode(usuario, "UTF-8") +
                    "&bio=" + URLEncoder.encode(bio, "UTF-8");

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

