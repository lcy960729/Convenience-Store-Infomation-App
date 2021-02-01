package com.example.chanyoung.se;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PostActivity extends AppCompatActivity {
    User user;
    BorderItem post;
    ArrayList<CommentItem> commentList = new ArrayList<CommentItem>();

    CommentItem selectedComment;

    LinearLayout post_comment_layout;
    LayoutInflater inflater;
    ImageButton menuBtn;
    ImageButton exitBtn;

    ImageButton commentEditBtn;

    TextView titleTv, contentsTv, dateTv, loveTv, hateTv, writerTv;

    EditText commentEt;

    boolean isLove, isHate;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    titleTv.setText(data.getStringExtra("title"));
                    contentsTv.setText(data.getStringExtra("content"));
                }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        if (v.getId() == R.id.post_menu_btn) {
            inflater.inflate(R.menu.post_menu, menu);
        } else inflater.inflate(R.menu.comment_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("친구 추가")) {

            if (user.id.equals(selectedComment.id)) {
                Toast.makeText(getApplicationContext(), "자기 자신과는 친구가 될 수 없습니다....", Toast.LENGTH_SHORT).show();
                return false;
            }

            try {
                String insertcomment = new DBHelper().execute("insert_friends", "&userid=" + user.id + "&fid=" + selectedComment.cwriter).get();

                if (insertcomment.equals("true")) {
                    Toast.makeText(getApplicationContext(), "친구 추가 완료", Toast.LENGTH_SHORT).show();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (item.getTitle().toString().equals("댓글 삭제")) {
            try {
                String insert_friends = new DBHelper().execute("delete_comment", "&bid=" + post.bid + "&cwriter=" + user.id + "&contents=" + selectedComment.ccontent).get();
                System.out.println(insert_friends);
                if (insert_friends.equals("true")) {
                    Toast.makeText(getApplicationContext(), "댓글 삭제 완료", Toast.LENGTH_SHORT).show();
                    post_comment_layout.removeAllViews();
                    getComments();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (item.getTitle().toString().equals("글 수정")) {

            if (user.id.equals(post.id)) {
                final Intent writePostActivtiy_intent = new Intent(PostActivity.this, WritePostActivity.class);
                writePostActivtiy_intent.putExtra("post", post);
                writePostActivtiy_intent.putExtra("type", "update");
                startActivityForResult(writePostActivtiy_intent, 10);
            } else {
                Toast.makeText(getApplicationContext(), "자신의 글만 삭제할 수 있습니다", Toast.LENGTH_SHORT).show();
            }
        } else if (item.getTitle().toString().equals("글 삭제")) {
            try {
                String insert_friends = new DBHelper().execute("deletePost", "&bid=" + post.bid + "&writer=" + user.id).get();
                System.out.println(insert_friends);
                if (insert_friends.equals("true")) {
                    Toast.makeText(getApplicationContext(), "글 삭제 완료", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_interface);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        menuBtn = (ImageButton) findViewById(R.id.post_menu_btn);
        registerForContextMenu(menuBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostActivity.this.openContextMenu(view);
            }
        });

        exitBtn = (ImageButton) findViewById(R.id.post_exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        titleTv = (TextView) findViewById(R.id.post_title_Tv);
        contentsTv = (TextView) findViewById(R.id.post_contents_Tv);
        dateTv = (TextView) findViewById(R.id.post_date_Tv);
        loveTv = (TextView) findViewById(R.id.post_love_Tv);
        hateTv = (TextView) findViewById(R.id.post_hate_Tv);
        writerTv = (TextView) findViewById(R.id.post_writer_Tv);

        commentEt = (EditText) findViewById(R.id.post_comment_Et);
        commentEditBtn = (ImageButton) findViewById(R.id.post_commentEdit_btn);

        post = (BorderItem) intent.getSerializableExtra("post");

        titleTv.setText(post.title);
        contentsTv.setText(post.content);
        dateTv.setText(post.date);
        loveTv.setText(post.like);
        hateTv.setText(post.hate);
        writerTv.setText(post.writer);

        try {
            JSONObject getuser = new JSONObject(new DBHelper().execute("get_PLOVEHATE", "&userid=" + user.id + "&bid=" + post.bid).get());

            String loveBoolean = getuser.getString("love");
            String hateBoolean = getuser.getString("hate");

            isLove = Boolean.parseBoolean(loveBoolean);
            isHate = Boolean.parseBoolean(hateBoolean);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        LinearLayout loveBtn = (LinearLayout) findViewById(R.id.post_love_Btn);
        LinearLayout hateBtn = (LinearLayout) findViewById(R.id.post_hate_Btn);

        final TextView loveTv = (TextView) findViewById(R.id.post_love_Tv);
        final TextView hateTv = (TextView) findViewById(R.id.post_hate_Tv);

        loveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((isLove == false && isHate == true) || (isLove == false && isHate == false)) {
                    System.out.println();
                    try {
                        String insertcomment = null;
                        if (isLove == false && isHate == true) {
                            insertcomment = new DBHelper().execute("pLove", "&type=u&userid=" + user.id + "&bid=" + post.bid).get();
                            loveTv.setText(String.valueOf(Integer.parseInt(loveTv.getText().toString()) + 1));
                            hateTv.setText(String.valueOf(Integer.parseInt(hateTv.getText().toString()) - 1));
                        } else {
                            insertcomment = new DBHelper().execute("pLove", "&type=i&userid=" + user.id + "&bid=" + post.bid).get();
                            loveTv.setText(String.valueOf(Integer.parseInt(loveTv.getText().toString()) + 1));
                        }
                        if (insertcomment.equals("true")) {
                            Toast.makeText(getApplicationContext(), "LOVE!!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isLove = true;
                    isHate = false;
                } else if (isHate == false && isLove == true) {
                    try {
                        String insertcomment = new DBHelper().execute("dLoveHate", "&type=love&userid=" + user.id + "&bid=" + post.bid).get();
                        loveTv.setText(String.valueOf(Integer.parseInt(loveTv.getText().toString()) - 1));
                        if (insertcomment.equals("true")) {
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isHate = false;
                    isLove = false;
                }
                System.out.println(isHate + " + " + isLove);
            }
        });

        hateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((isLove == true && isHate == false) || (isLove == false && isHate == false)) {
                    System.out.println();
                    try {
                        String insertcomment = null;
                        if (isLove == true && isHate == false) {
                            insertcomment = new DBHelper().execute("pHate", "&type=u&userid=" + user.id + "&bid=" + post.bid).get();
                            loveTv.setText(String.valueOf(Integer.parseInt(loveTv.getText().toString()) - 1));
                            hateTv.setText(String.valueOf(Integer.parseInt(hateTv.getText().toString()) + 1));
                        } else {
                            insertcomment = new DBHelper().execute("pHate", "&type=i&userid=" + user.id + "&bid=" + post.bid).get();
                            hateTv.setText(String.valueOf(Integer.parseInt(hateTv.getText().toString()) + 1));
                        }

                        if (insertcomment.equals("true")) {
                            Toast.makeText(getApplicationContext(), "HATE!!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isHate = true;
                    isLove = false;
                } else if (isHate == true && isLove == false) {
                    try {
                        String insertcomment = new DBHelper().execute("dLoveHate", "&type=hate&userid=" + user.id + "&bid=" + post.bid).get();
                        hateTv.setText(String.valueOf(Integer.parseInt(hateTv.getText().toString()) - 1));
                        if (insertcomment.equals("true")) {
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isHate = false;
                    isLove = false;
                }
                System.out.println(isHate + " + " + isLove);
            }
        });

        post_comment_layout = (LinearLayout) findViewById(R.id.post_comment_layout);
        getComments();

        commentEt = (EditText) findViewById(R.id.post_comment_Et);
        commentEditBtn = (ImageButton) findViewById(R.id.post_commentEdit_btn);
        commentEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 댓글을 작성하는 부분
                try {
                    String insertcomment = new DBHelper().execute("insert_comment", "&bid=" + post.bid + "&cwriter=" + user.id + "&contents=" + commentEt.getText().toString()).get();

                    if (insertcomment.equals("true")) {
                        Toast.makeText(getApplicationContext(), "댓글 입력 완료", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        post_comment_layout.removeAllViews();
                        getComments();
                        if (!post.id.equals(user.id))
                            new NotificationHelper().execute(post.title.hashCode() + "~" + post.id, post.title);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getComments() {
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        commentList.clear();

        try {
            JSONArray get_comments = new JSONArray(new DBHelper().execute("get_comments", "&bid=" + post.bid).get());

            for (int i = 0; i < get_comments.length(); i++) {
                String id = get_comments.getJSONObject(i).getString("id");
                String bid = get_comments.getJSONObject(i).getString("bid");
                String cwriter = get_comments.getJSONObject(i).getString("cwriter");
                String contents = get_comments.getJSONObject(i).getString("contents");
                String cdate = get_comments.getJSONObject(i).getString("cdate");

                final LinearLayout comment_layout = (LinearLayout) inflater.inflate(R.layout.comment_interface, null);
                post_comment_layout.addView(comment_layout);
                TextView cwriterTv = (TextView) comment_layout.findViewById(R.id.comment_writer_Tv);
                TextView ccontentTv = (TextView) comment_layout.findViewById(R.id.comment_contents_Tv);
                TextView cdateTv = (TextView) comment_layout.findViewById(R.id.comment_date_Tv);
                comment_layout.setTag(i);

                commentList.add(new CommentItem(bid, id, cwriter, contents, cdate));
                registerForContextMenu(comment_layout);

                comment_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i = (int) v.getTag();
                        selectedComment = commentList.get(i);
                        openContextMenu(comment_layout);
                    }
                });

                cwriterTv.setText(cwriter);
                ccontentTv.setText(contents);
                cdateTv.setText(cdate);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
