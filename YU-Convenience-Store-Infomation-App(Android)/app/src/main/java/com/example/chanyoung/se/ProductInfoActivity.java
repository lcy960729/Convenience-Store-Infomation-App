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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ProductInfoActivity extends AppCompatActivity {
    ImageButton exitBtn;
    LayoutInflater inflater;

    ImageView favoriteBtn,imageIv;
    TextView gnameTv, updateTv, loveTv, hateTv, priceTv, cnameTv, typeTv, eventTv;

    EditText commentEt;
    ImageButton commentEditBtn;

    ArrayList<CommentItem> commentList = new ArrayList<CommentItem>();
    ProductItem productitem;
    CommentItem selectedComment;
    LinearLayout product_comment_layout;
    User user;
    boolean isLove, isHate;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.comment_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().toString().equals("친구 추가")) {
            try {
                String insert_friends = new DBHelper().execute("insert_friends", "&userid=" + user.id + "&fid=" + selectedComment.cwriter).get();

                if (insert_friends.equals("true")) {
                    Toast.makeText(getApplicationContext(), "친구 추가 완료", Toast.LENGTH_SHORT).show();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (item.getTitle().toString().equals("댓글 삭제")){
            try {
                String insert_friends = new DBHelper().execute("delete_pcomment", "&pno=" + productitem.pno + "&cwriter=" + user.id + "&contents=" + selectedComment.ccontent).get();
                System.out.println(insert_friends);
                if (insert_friends.equals("true")) {
                    Toast.makeText(getApplicationContext(), "댓글 삭제 완료", Toast.LENGTH_SHORT).show();
                    product_comment_layout.removeAllViews();
                    getComments();
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
        setContentView(R.layout.productinfo_interface);

        final Intent intent = getIntent();

        user = (User) intent.getSerializableExtra("user");
        productitem = (ProductItem) intent.getSerializableExtra("product");

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        exitBtn = (ImageButton) findViewById(R.id.productinfo_exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageIv = (ImageView)findViewById(R.id.productinfo_productImage_Iv);
        gnameTv = (TextView) findViewById(R.id.productinfo_title_Tv);
        updateTv = (TextView) findViewById(R.id.productinfo_update_Tv);
        priceTv = (TextView) findViewById(R.id.productinfo_price_Tv);
        loveTv = (TextView) findViewById(R.id.productinfo_love_Tv);
        hateTv = (TextView) findViewById(R.id.productinfo_hate_Tv);
        cnameTv = (TextView) findViewById(R.id.productinfo_cname_Tv);
        typeTv = (TextView) findViewById(R.id.productinfo_type_Tv);
        eventTv = (TextView)findViewById(R.id.productinfo_event_Tv);

        new DownloadImageTask(imageIv)
                .execute(productitem.imageurl);

        gnameTv.setText(productitem.gname);
        updateTv.setText(productitem.date);
        loveTv.setText(productitem.glove);
        hateTv.setText(productitem.ghate);
        priceTv.setText(productitem.price);
        cnameTv.setText(productitem.cname);
        typeTv.setText(productitem.gtype);
        eventTv.setText(productitem.event);

        try {
            JSONObject getuser = new JSONObject(new DBHelper().execute("get_GLOVEHATE", "&userid=" + user.id + "&pno=" + productitem.pno).get());

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


        LinearLayout loveBtn = (LinearLayout) findViewById(R.id.productinfo_love_Btn);
        LinearLayout hateBtn = (LinearLayout) findViewById(R.id.productinfo_hate_Btn);

        loveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((isLove==false && isHate == true) || (isLove == false && isHate == false)) {
                    System.out.println();
                    try {
                        String insertcomment = null;
                        if (isLove == false && isHate == true){
                            insertcomment = new DBHelper().execute("gLove", "&type=u&userid=" + user.id + "&pno=" + productitem.pno).get();
                            loveTv.setText(String.valueOf(Integer.parseInt(loveTv.getText().toString()) +1));
                            hateTv.setText(String.valueOf(Integer.parseInt(hateTv.getText().toString()) -1));
                        }
                        else {
                            insertcomment = new DBHelper().execute("gLove", "&type=i&userid=" + user.id + "&pno=" + productitem.pno).get();
                            loveTv.setText(String.valueOf(Integer.parseInt(loveTv.getText().toString()) +1));
                        }
                        if (insertcomment.equals("true")) {
                            Toast.makeText(getApplicationContext(), "LOVE", Toast.LENGTH_SHORT).show();
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
                        String insertcomment = new DBHelper().execute("gLoveHate", "&type=love&userid=" + user.id + "&pno=" + productitem.pno).get();
                        loveTv.setText(String.valueOf(Integer.parseInt(loveTv.getText().toString()) -1));
                        if (insertcomment.equals("true")) {
                            Toast.makeText(getApplicationContext(), "OO", Toast.LENGTH_SHORT).show();
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
                            insertcomment = new DBHelper().execute("gHate", "&type=u&userid=" + user.id + "&pno=" + productitem.pno).get();
                            loveTv.setText(String.valueOf(Integer.parseInt(loveTv.getText().toString()) - 1));
                            hateTv.setText(String.valueOf(Integer.parseInt(hateTv.getText().toString()) + 1));
                        }
                        else {
                            insertcomment = new DBHelper().execute("gHate", "&type=i&userid=" + user.id + "&pno=" + productitem.pno).get();
                            hateTv.setText(String.valueOf(Integer.parseInt(hateTv.getText().toString()) +1));
                        }

                        if (insertcomment.equals("true")) {
                            Toast.makeText(getApplicationContext(), "HATE", Toast.LENGTH_SHORT).show();
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
                        String insertcomment = new DBHelper().execute("gLoveHate", "&type=hate&userid=" + user.id + "&pno=" + productitem.pno).get();
                        hateTv.setText(String.valueOf(Integer.parseInt(hateTv.getText().toString()) -1));
                        if (insertcomment.equals("true")) {
                            Toast.makeText(getApplicationContext(), "OO", Toast.LENGTH_SHORT).show();
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

        favoriteBtn = (ImageView) findViewById(R.id.productinfo_favoriteProduct_btn);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productitem.isFavorite) {
                    favoriteBtn.setImageResource(R.drawable.star_btn);
                    try {
                        String sendMsg = "&userid=" + user.id + "&pid=" + productitem.gname.replaceAll("%", "%25");
                        ;
                        String login_result = new DBHelper().execute("delete_favoriteProduct", sendMsg).get();

                        if (login_result.equals("true")) {
                        } else {
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
                    favoriteBtn.setImageResource(R.drawable.starcheck_btn);
                    try {
                        String sendMsg = "&userid=" + user.id + "&pid=" + productitem.gname.replaceAll("%", "%25");
                        ;
                        String login_result = new DBHelper().execute("inset_favoriteProduct", sendMsg).get();

                        if (login_result.equals("true")) {
                        } else {
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (productitem.isFavorite)
            favoriteBtn.setImageResource(R.drawable.starcheck_btn);
        else
            favoriteBtn.setImageResource(R.drawable.star_btn);

        product_comment_layout = (LinearLayout) findViewById(R.id.productinfo_commnet_layout);
        getComments();

        commentEt = (EditText) findViewById(R.id.productinfo_comment_Et);
        commentEditBtn = (ImageButton) findViewById(R.id.productinfo_commentEdit_btn);
        commentEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 댓글을 작성하는 부분
                try {
                    String insertcomment = new DBHelper().execute("insert_pcomment", "&pno=" + productitem.pno + "&cwriter=" + user.id + "&contents=" + commentEt.getText().toString()).get();

                    if (insertcomment.equals("true")) {
                        Toast.makeText(getApplicationContext(), "댓글 입력 완료", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        product_comment_layout.removeAllViews();
                        getComments();
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
            JSONArray get_comments = new JSONArray(new DBHelper().execute("get_pcomments", "&pno=" + productitem.pno).get());

            for (int i = 0; i < get_comments.length(); i++) {
                String id = get_comments.getJSONObject(i).getString("id");
                String pno = get_comments.getJSONObject(i).getString("pno");
                String cwriter = get_comments.getJSONObject(i).getString("cwriter");
                String contents = get_comments.getJSONObject(i).getString("contents");
                String cdate = get_comments.getJSONObject(i).getString("cdate");


                final LinearLayout comment_layout = (LinearLayout) inflater.inflate(R.layout.comment_interface, null);
                product_comment_layout.addView(comment_layout);
                TextView cwriterTv = (TextView) comment_layout.findViewById(R.id.comment_writer_Tv);
                TextView ccontentTv = (TextView) comment_layout.findViewById(R.id.comment_contents_Tv);
                TextView cdateTv = (TextView) comment_layout.findViewById(R.id.comment_date_Tv);
                comment_layout.setTag(i);

                commentList.add(new CommentItem(pno, id, cwriter, contents, cdate));
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
