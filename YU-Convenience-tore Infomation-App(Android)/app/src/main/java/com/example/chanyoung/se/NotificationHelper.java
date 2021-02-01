package com.example.chanyoung.se;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Scanner;

public class NotificationHelper extends AsyncTask<String, Void, String> {
    StringBuilder temp = new StringBuilder();
    @Override
    protected String doInBackground(String... strings) {
        try
        {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json; UTF-8");
            conn.setRequestProperty("Authorization","key=AIzaSyB9ZNpHAK-hzZ5I5CIf2bRysKRu6RTdMFw");

            JSONObject json = new JSONObject();
            System.out.println(strings[0]);
            json.put("to","/topics/" + strings[0] );

            JSONObject info = new JSONObject();
            info.put("title", "YU 편의점");
            info.put("body",  "내가 쓴 글("+ strings[1] + ")에 새로운 댓글이 달렸습니다 !!");

            json.put("notification", info);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();


            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader input = new InputStreamReader(conn.getInputStream(), "UTF-8");
                Scanner sc = new Scanner(input);
                while (sc.hasNext()){
                    temp.append(sc.nextLine() + "\n");
                }
            } else {
                // 통신 실패
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return temp.toString().replaceAll("\\P{Print}","").trim();
    }
}
