package com.example.chanyoung.se;

import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    TabPagerAdapter pagerAdapter;
    ListView listview = null;
    User user;

    public Object getData() {
        return user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_interface);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        TextView nameTv = (TextView) findViewById(R.id.main_name_Tv);
        nameTv.setText(user.name);

        TextView logoutTv = (TextView) findViewById(R.id.main_logout_Tv);
        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Creating TabPagerAdapter adapter
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        final String[] items = {"내 관심 상품", "계정 삭제"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);


        listview = (ListView) findViewById(R.id.drawer_menulist);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                switch (position) {
                    case 0:
                        Intent favoiteProductActivity_intent = new Intent(MainActivity.this, FavoriteProductActivity.class);
                        favoiteProductActivity_intent.putExtra("user", user);
                        startActivity(favoiteProductActivity_intent);
                        break;
                    case 1:
                        final EditText edittext = new EditText(getApplicationContext());
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("계정 삭제")
                                .setMessage("회원탈퇴를 하시겠습니까? \n회원탈퇴를 위해선 아래에 '회원탈퇴'를 입력해주세요")
                                .setView(edittext)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (edittext.getText().toString().equals("회원탈퇴")) {

                                            try {
                                                String insertcomment = new DBHelper().execute("deleteUser", "&userid=" + user.id).get();
                                                if (insertcomment.equals("true")) {
                                                    Toast.makeText(getApplicationContext(), "계정이 삭제 되었습니다", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            } catch (ExecutionException e) {
                                                e.printStackTrace();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "정확하게 입력해주세요", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                        break;
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
