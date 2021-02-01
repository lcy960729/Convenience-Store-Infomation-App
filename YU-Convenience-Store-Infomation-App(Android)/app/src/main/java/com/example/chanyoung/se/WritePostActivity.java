package com.example.chanyoung.se;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.ExecutionException;

public class WritePostActivity extends AppCompatActivity {

    ImageButton exitBtn, commitBtn;
    EditText titleEt, contentEt;

    BorderItem post;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writepost_interface);

        titleEt = (EditText) findViewById(R.id.writepost_title_Et);
        contentEt = (EditText) findViewById(R.id.writepost_content_Et);

        final Intent intent = getIntent();
        final String type = intent.getStringExtra("type");

        if (type.equals("insert")){
            user = (User) intent.getSerializableExtra("user");
        }
        else if (type.equals("update")){
            post = (BorderItem) intent.getSerializableExtra("post");
            titleEt.setText(post.title);
            contentEt.setText(post.content);
        }


        exitBtn = (ImageButton) findViewById(R.id.writepost_exit_Btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        commitBtn = (ImageButton) findViewById(R.id.writepost_commit_Btn);
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String sendMsg = null;
                    String write_result = null;
                    if (type.equals("insert")) {
                        sendMsg = "&writer=" + user.id + "&title=" + titleEt.getText().toString() + "&content=" + contentEt.getText().toString();
                        write_result = new DBHelper().execute("writePost", sendMsg).get();

                    } else if (type.equals("update")) {
                        sendMsg = "&bid=" + post.bid + "&title=" + titleEt.getText().toString() + "&content=" + contentEt.getText().toString();
                        write_result = new DBHelper().execute("updatePost", sendMsg).get();
                    }

                    if (write_result.equals("true")) {
                        Toast.makeText(getApplicationContext(), "글이 작성되었습니다!", Toast.LENGTH_SHORT).show();
                        intent.putExtra("title",titleEt.getText().toString());
                        intent.putExtra("content", contentEt.getText().toString());
                        setResult(RESULT_OK, intent);
                        FirebaseMessaging.getInstance().subscribeToTopic(titleEt.getText().toString().hashCode()+"~" +user.id );
                        finish();

                    } else Toast.makeText(getApplicationContext(), "빈칸없이 입력 해주세요", Toast.LENGTH_SHORT).show();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
