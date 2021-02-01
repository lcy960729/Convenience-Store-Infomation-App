package com.example.chanyoung.se;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class FavoriteProductActivity extends AppCompatActivity {

    User user;

    ImageButton exitBtn, menuBtn;

    Product_ItemGridViewAdpter adapter;
    GridView productList_gridview;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorite_product_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favoriteproduct_interface);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        TextView titleTv = (TextView)findViewById(R.id.favoriteproduct_title_Tv);

        titleTv.setText(user.name + "님의 관심상품 목록");

        exitBtn = (ImageButton) findViewById(R.id.favoriteproduct_exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        adapter = new Product_ItemGridViewAdpter(user);

        productList_gridview = (GridView) findViewById(R.id.favoriteproduct_productlistGridView);
        productList_gridview.setAdapter(adapter);

        try {
            JSONArray getGoodsList = null;

            getGoodsList = new JSONArray(new DBHelper().execute("favorgoods", "&userid=" + user.id).get());

            for (int i = 0; i < getGoodsList.length(); i++) {
                String pno = getGoodsList.getJSONObject(i).getString("pno");
                String gname = getGoodsList.getJSONObject(i).getString("gname");
                String cname = getGoodsList.getJSONObject(i).getString("cname");
                String imageurl = getGoodsList.getJSONObject(i).getString("imageurl");
                String price = getGoodsList.getJSONObject(i).getString("price");
                String event = getGoodsList.getJSONObject(i).getString("event");
                String gtype = getGoodsList.getJSONObject(i).getString("gtype");
                String ghate = getGoodsList.getJSONObject(i).getString("ghate");
                String glike = getGoodsList.getJSONObject(i).getString("glike");
                String date = getGoodsList.getJSONObject(i).getString("date");

                adapter.addItem(pno, gname, cname, imageurl, price, event, gtype, ghate, glike, date);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initfavoriteProduct() {

        adapter = new Product_ItemGridViewAdpter(user);

        productList_gridview.setAdapter(adapter);
        try {
            JSONArray getGoodsList = null;

            getGoodsList = new JSONArray(new DBHelper().execute("favorgoods", "&userid=" + user.id).get());

            for (int i = 0; i < getGoodsList.length(); i++) {
                String pno = getGoodsList.getJSONObject(i).getString("pno");
                String gname = getGoodsList.getJSONObject(i).getString("gname");
                String cname = getGoodsList.getJSONObject(i).getString("cname");
                String imageurl = getGoodsList.getJSONObject(i).getString("imageurl");
                String price = getGoodsList.getJSONObject(i).getString("price");
                String event = getGoodsList.getJSONObject(i).getString("event");
                String gtype = getGoodsList.getJSONObject(i).getString("gtype");
                String ghate = getGoodsList.getJSONObject(i).getString("ghate");
                String glike = getGoodsList.getJSONObject(i).getString("glike");
                String date = getGoodsList.getJSONObject(i).getString("date");

                adapter.addItem(pno, gname, cname, imageurl, price, event, gtype, ghate, glike, date);
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