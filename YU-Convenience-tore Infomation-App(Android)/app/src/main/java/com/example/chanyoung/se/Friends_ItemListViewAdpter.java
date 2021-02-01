package com.example.chanyoung.se;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

class ListViewFriendsItem {
    User user;

    ListViewFriendsItem(String id) {
        user = new User(id);
    }
}

public class Friends_ItemListViewAdpter extends BaseAdapter {
    private ArrayList<ListViewFriendsItem> listViewFriendsItem = new ArrayList<ListViewFriendsItem>();
    User user;
    Friends_Fragment ffragment;
    Friends_ItemListViewAdpter(User user,Friends_Fragment ffragment) {
        this.user = user;
        this.ffragment = ffragment;
    }

    @Override
    public int getCount() {
        return listViewFriendsItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewFriendsItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friends_list_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView nameTv = (TextView) convertView.findViewById(R.id.friends_name_Tv);
        TextView emailTv = (TextView) convertView.findViewById(R.id.friends_email_Tv);
        ImageView imageIv = (ImageView) convertView.findViewById(R.id.friends_image_Iv);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListViewFriendsItem listViewItem = listViewFriendsItem.get(position);

        // 아이템 내 각 위젯에 데이터 반영 ( Test 중 )
        nameTv.setText(listViewItem.user.name);
        emailTv.setText(listViewItem.user.mail);
        //imageIv.setBackground(listViewItem.image);

        TextView firendFavoriteProductBtn = (TextView) convertView.findViewById(R.id.friends_favorite_Btn);
        firendFavoriteProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent favoiteProductActivity_intent = new Intent(parent.getContext(), FavoriteProductActivity.class);
                favoiteProductActivity_intent.putExtra("user", listViewItem.user);
                parent.getContext().startActivity(favoiteProductActivity_intent);
            }
        });

        TextView deleteFriendBtn = (TextView) convertView.findViewById(R.id.friends_delete_Btn);
        deleteFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String deleteFriend = new DBHelper().execute("delete_friend", "&userid=" + user.id + "&fid=" + listViewItem.user.name).get();
                    System.out.println(deleteFriend);
                    if (deleteFriend.equals("true")){
                        Toast.makeText(parent.getContext(), "친구 삭제 완료",Toast.LENGTH_SHORT).show();
                        ffragment.init_FriendsList();
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    public void addItem(String id) {
        ListViewFriendsItem item = new ListViewFriendsItem(id);

        listViewFriendsItem.add(item);
    }

}
