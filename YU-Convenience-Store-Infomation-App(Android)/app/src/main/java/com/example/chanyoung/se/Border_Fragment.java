package com.example.chanyoung.se;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class Border_Fragment extends Fragment {

    ListView border_listview = null;
    LinearLayout writeBtn;
    BorderItem_ListViewAdpter adapter;
    ImageButton menuBtn;
    View view;
    User user;

    int page;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            user = ((User) ((MainActivity) getActivity()).getData());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        adapter = new BorderItem_ListViewAdpter();
        init_BorderList(page);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_border_fragment, container, false);

        border_listview = (ListView) view.findViewById(R.id.atCommunity_border_listView);
        adapter = new BorderItem_ListViewAdpter();

        page = 1;
        init_BorderList(page);

        writeBtn = (LinearLayout)view.findViewById(R.id.communityFragment_write_btn);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent writePostActivtiy_intent = new Intent(getActivity(), WritePostActivity.class);
                writePostActivtiy_intent.putExtra("user",user);
                writePostActivtiy_intent.putExtra("type","insert");
                startActivity(writePostActivtiy_intent);
            }
        });



        final boolean mLockListView = false;
        final boolean[] lastItemVisibleFlag = {false};

        border_listview.setOnScrollListener(new ListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag[0] && mLockListView == false) {
                    init_BorderList(page);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItemVisibleFlag[0] = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }

        });
        return view;
    }

    public void init_BorderList(int getpage) {
        try {
            JSONArray getBoardList = new JSONArray(new DBHelper().execute("getBoardList", "&setSort=bid&page=" + getpage).get());

            for (int i = 0; i < getBoardList.length(); i++) {
                String bid = getBoardList.getJSONObject(i).getString("bid");
                String id = getBoardList.getJSONObject(i).getString("id");
                String title = getBoardList.getJSONObject(i).getString("title");
                String content = getBoardList.getJSONObject(i).getString("content");
                String wdate = getBoardList.getJSONObject(i).getString("wdate");
                String bhate = getBoardList.getJSONObject(i).getString("bhate");
                String blike = getBoardList.getJSONObject(i).getString("blike");
                String writer = getBoardList.getJSONObject(i).getString("writer");
                adapter.addItem(bid, id, writer, title, content, wdate, bhate, blike);
            }

            if (page == 1)
                border_listview.setAdapter(adapter);
            else
                adapter.notifyDataSetChanged();

            if (getBoardList.length() != 0) {
                page++;
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
                postActivtiy_intent.putExtra("post", (BorderItem)adapter.getItem(position));
                postActivtiy_intent.putExtra("user", user);
                startActivity(postActivtiy_intent);
            }
        });
    }
}
