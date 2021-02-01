package com.example.chanyoung.se;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class Friends_Fragment extends Fragment {

    User user;
    ListView friends_listview;
    View view;

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
        init_FriendsList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_friends_fragment, container,false);

        init_FriendsList();
        return view;
    }

    public void init_FriendsList() {
        Friends_ItemListViewAdpter adapter = new Friends_ItemListViewAdpter(user,this);

        friends_listview = (ListView) view.findViewById(R.id.atFriends_friends_listView);
        friends_listview.setAdapter(adapter);

        try {
            JSONArray getFriendsList = null;
            System.out.println("get_friends"+"&userid=" + user.id);
            getFriendsList = new JSONArray(new DBHelper().execute("get_friends", "&userid=" + user.id).get());

            for (int i = 0; i < getFriendsList.length(); i++) {
                String id = getFriendsList.getJSONObject(i).getString("id");
                adapter.addItem(id);
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
