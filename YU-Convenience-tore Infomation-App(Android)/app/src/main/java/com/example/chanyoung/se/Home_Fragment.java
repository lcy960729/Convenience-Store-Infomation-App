
package com.example.chanyoung.se;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class Home_Fragment extends Fragment {

    ListView border_listview = null;
    ViewPager Product_ViewPager = null;

    View view;
    User user;

    @Override
    public void onResume() {
        super.onResume();
        init_BorderList();
        init_ItemList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() != null && getActivity() instanceof MainActivity){
            user = ((User)((MainActivity) getActivity()).getData());
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tab_home_fragment, container, false);
        init_BorderList();
        init_ItemList();

        return view;
    }

    public void init_ItemList() {

        ProductItem_ViewPagerAdapter adapter = new ProductItem_ViewPagerAdapter(user);

        Product_ViewPager = (ViewPager) view.findViewById(R.id.homeFragment_ViewPager);

        try {
            JSONArray getGoodsList = new JSONArray(new DBHelper().execute("getGoodsList", "&conv=전체&type=상품 분류 - 전체&sort=인기순&page=1").get());

            for (int i = 0; i < (getGoodsList.length() > 5 ? 5 : getGoodsList.length()); i++) {
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

                adapter.addItem(user, pno, gname, cname, imageurl, price, event, gtype, ghate, glike, date);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Product_ViewPager.setAdapter(adapter);
    }

    public void init_BorderList() {
        final BorderItem_ListViewAdpter adapter = new BorderItem_ListViewAdpter();

        border_listview = (ListView) view.findViewById(R.id.athome_border_listView);
        border_listview.setAdapter(adapter);

        try {
            JSONArray getBoardList = new JSONArray(new DBHelper().execute("getBoardList", "&setSort=BLIKE&page=1").get());

            for (int i = 0; i < getBoardList.length(); i++) {
                String bid = getBoardList.getJSONObject(i).getString("bid");
                String id = getBoardList.getJSONObject(i).getString("id");
                String title = getBoardList.getJSONObject(i).getString("title");
                String writer = getBoardList.getJSONObject(i).getString("writer");
                String content = getBoardList.getJSONObject(i).getString("content");
                String wdate = getBoardList.getJSONObject(i).getString("wdate");
                String bhate = getBoardList.getJSONObject(i).getString("bhate");
                String blike = getBoardList.getJSONObject(i).getString("blike");
                adapter.addItem(bid, id, writer, title, content, wdate, bhate, blike);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        border_listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                final Intent postActivtiy_intent = new Intent(getActivity(), PostActivity.class);
                postActivtiy_intent.putExtra("post", (BorderItem) adapter.getItem(position));
                postActivtiy_intent.putExtra("user", user);
                startActivity(postActivtiy_intent);
            }
        });
    }
}