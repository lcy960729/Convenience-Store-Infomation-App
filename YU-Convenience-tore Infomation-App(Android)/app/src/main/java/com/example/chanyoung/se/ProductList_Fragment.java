package com.example.chanyoung.se;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class ProductList_Fragment extends Fragment {
    Spinner classificationSp, sortSp;
    private TabLayout tabLayout;
    GridView productList_gridview;

    String setCname;
    String setType;

    String setSort;
    Product_ItemGridViewAdpter adapter;

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
        adapter = new Product_ItemGridViewAdpter(user);
        getProductlist(page);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_productlist_fragment, container, false);

        setCname = "전체";

        classificationSp = (Spinner) view.findViewById(R.id.productListFragment_classification_Spinner);
        classificationSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter = new Product_ItemGridViewAdpter(user);
                setType = String.valueOf(adapterView.getItemAtPosition(i));
                page = 1;
                getProductlist(page);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sortSp = (Spinner) view.findViewById(R.id.productListFragment_sort_Spinner);
        sortSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter = new Product_ItemGridViewAdpter(user);
                setSort = String.valueOf(adapterView.getItemAtPosition(i));
                page = 1;
                getProductlist(page);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        productList_gridview = (GridView) view.findViewById(R.id.productlistGridView);
        tabLayout = (TabLayout) view.findViewById(R.id.Convenience_tab_layout);

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adapter = new Product_ItemGridViewAdpter(user);
                setCname = String.valueOf(tab.getText());
                setCname = setCname.replaceAll(" ", "");
                page = 1;
                getProductlist(page);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        final boolean mLockListView = false;
        final boolean[] lastItemVisibleFlag = {false};

        productList_gridview.setOnScrollListener(new ListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag[0] && mLockListView == false) {
                    getProductlist(page);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItemVisibleFlag[0] = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });

        return view;
    }

    public void getProductlist(int getpage) {
        try {
            JSONArray getGoodsList = null;

            getGoodsList = new JSONArray(new DBHelper().execute("getGoodsList", "&conv=" + setCname + "&type=" + setType + "&sort=" + setSort + "&page=" + getpage).get());

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

            if (page == 1)
                productList_gridview.setAdapter(adapter);
            else
                adapter.notifyDataSetChanged();

            if (getGoodsList.length() != 0) {
                page++;
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
