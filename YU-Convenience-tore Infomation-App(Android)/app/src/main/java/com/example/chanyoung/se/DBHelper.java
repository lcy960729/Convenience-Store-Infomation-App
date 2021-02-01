package com.example.chanyoung.se;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

class DBHelper extends AsyncTask<String, Void, String> {
    String sendMsg;
    @Override
    protected String doInBackground(String... strings) {
        StringBuilder temp = new StringBuilder();
        try {

            // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
            URL url = new URL("http://165.229.125.132:8080/SE/db_helper.jsp");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

            sendMsg = "header=" + strings[0] + strings[1];

            osw.write(sendMsg);
            osw.flush();

            //jsp와 통신 성공 시 수행
            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader input = new InputStreamReader(conn.getInputStream(), "UTF-8");
                Scanner sc = new Scanner(input);
                while (sc.hasNext()){
                    temp.append(sc.nextLine() + "\n");
                }
            } else {
                // 통신 실패
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //jsp로부터 받은 리턴 값
        return temp.toString().replaceAll("\\P{Print}","").trim();
    }

}


