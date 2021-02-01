package com.example.chanyoung.se;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

class User implements Serializable {
    String id;
    String name;
    String cellphone;
    String mail;

    User(String id) {
        this.id = id;

        try {
            JSONObject getuser = new JSONObject(new DBHelper().execute("getUser", "&id=" + id).get());

            String name = getuser.getString("name");
            String cellphone = getuser.getString("cellphone");
            String mail = getuser.getString("mail");

            this.name = name;
            this.cellphone = cellphone;
            this.mail = mail;

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class LoginActivity extends AppCompatActivity {

    TextView idpwFindBtn;
    TextView signUpBtn;

    Button loginBtn;
    Button socialLoginBtn;

    EditText idEt;
    EditText pwEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);

        idEt = (EditText) findViewById(R.id.login_id_Et);
        pwEt = (EditText) findViewById(R.id.login_pw_Et);

        loginBtn = (Button) findViewById(R.id.login_login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String sendMsg = "&id=" + idEt.getText().toString() + "&pwd=" + pwEt.getText().toString();
                    String login_result = new DBHelper().execute("login", sendMsg).get();

                    if (login_result.equals("true")) {
                        Intent mainActivtiy_intent = new Intent(LoginActivity.this, MainActivity.class);
                        User user = new User(idEt.getText().toString());

                        mainActivtiy_intent.putExtra("user", user);
                        startActivity(mainActivtiy_intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "로그인 실패 : 아이디 비밀번호를 확인해주세요!!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        signUpBtn = (TextView) findViewById(R.id.login_signUp_Btn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUpActivtiy_intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(SignUpActivtiy_intent);
            }
        });

        idpwFindBtn = (TextView) findViewById(R.id.login_idpwfind_Btn);
        idpwFindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText phnum_Et = new EditText(LoginActivity.this);
                final AlertDialog.Builder findIdPwDialog = new AlertDialog.Builder(LoginActivity.this);
                findIdPwDialog.setTitle("아이디 비밀번호 찾기");
                findIdPwDialog.setMessage("회원가입시 작성한 \n휴대폰 번호를 입력 해주세요");
                findIdPwDialog.setView(phnum_Et);
                findIdPwDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        try {
                            String sendMsg = "&phonenum=" + phnum_Et.getText().toString();
                            String findIdPw_result = new DBHelper().execute("findIdPw", sendMsg).get();
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setTitle("알림")
                                    .setMessage(findIdPw_result).show();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                findIdPwDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                findIdPwDialog.show();
            }
        });

        socialLoginBtn = (Button) findViewById(R.id.login_slogin_btn);
        socialLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "준비중입니다 ㅜ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

