package com.example.chanyoung.se;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Product_ItemGridViewAdpter extends BaseAdapter {
    User user;
    final ArrayList<String> favoirteProduct;

    Product_ItemGridViewAdpter(User user){
        //관심 상품 목록 불러오기
        favoirteProduct = new ArrayList<String>();
        try {
            JSONArray getGoodsList = null;

            getGoodsList = new JSONArray(new DBHelper().execute("favorgoods", "&userid=" + user.id).get());

            for (int i = 0; i < getGoodsList.length(); i++) {
                String gname = getGoodsList.getJSONObject(i).getString("gname");

                favoirteProduct.add(gname);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.user = user;
    }

    private ArrayList<ProductItem> productItem_ArrayList = new ArrayList<ProductItem>();

    @Override
    public int getCount() {
        return productItem_ArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return productItem_ArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        final ProductItem gridViewItem = (ProductItem)getItem(pos);


        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.product_list_item, parent, false);
        }


        final View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent productInfoActivtiy_intent = new Intent(finalConvertView.getContext(), ProductInfoActivity.class);
                productInfoActivtiy_intent.putExtra("product", (ProductItem) getItem(pos));
                productInfoActivtiy_intent.putExtra("user",user);
                productInfoActivtiy_intent.putExtra("isFavorite",gridViewItem.isFavorite);
                context.startActivity(productInfoActivtiy_intent);
            }
        });

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView itemImageIv = convertView.findViewById(R.id.productListItem_image_Iv);
        final TextView itemGNameTv = convertView.findViewById(R.id.productListItem_gname_Tv);
        TextView itemCNameTv = convertView.findViewById(R.id.productListItem_cname_Tv);
        TextView itemPriceTv = convertView.findViewById(R.id.productListItem_price_Tv);
        TextView itemTypeTv = convertView.findViewById(R.id.productListItem_type_Tv);
        TextView itemEventTv = convertView.findViewById(R.id.productListitem_event_Tv);
        TextView itemLoveTv = convertView.findViewById(R.id.productListItem_love_Tv);
        TextView itemhateTv = convertView.findViewById(R.id.productListItem_hate_Tv);

        //웹상에서 이미지 가져옴
        new DownloadImageTask(itemImageIv)
                .execute(gridViewItem.imageurl);

        // 아이템 내 각 위젯에 데이터 반영 ( Test 중 )
        itemGNameTv.setText(gridViewItem.gname);
        itemCNameTv.setText(gridViewItem.cname);
        itemPriceTv.setText(gridViewItem.price);
        itemTypeTv.setText(gridViewItem.gtype);
        itemGNameTv.setText(gridViewItem.gname);
        itemEventTv.setText(gridViewItem.event);
        itemLoveTv.setText(gridViewItem.glove);
        itemhateTv.setText(gridViewItem.ghate);

        //관심 상품인지 체크
        gridViewItem.isFavorite = favoirteProduct.contains(gridViewItem.gname);

        final ImageButton favoriteBtn = (ImageButton) convertView.findViewById(R.id.productListItem_start_Btn);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gridViewItem.isFavorite) {
                    favoriteBtn.setImageResource(R.drawable.star_btn);
                    favoirteProduct.remove(gridViewItem.gname);
                    try {
                        String sendMsg = "&userid=" + user.id + "&pid=" + gridViewItem.gname.replaceAll("%","%25");
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
                    favoirteProduct.add(gridViewItem.gname);
                    try {
                        String sendMsg = "&userid=" + user.id + "&pid=" + gridViewItem.gname.replaceAll("%","%25");
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
                gridViewItem.isFavorite = favoirteProduct.contains(gridViewItem.gname);
            }
        });

        if (gridViewItem.isFavorite)
            favoriteBtn.setImageResource(R.drawable.starcheck_btn);
        else
            favoriteBtn.setImageResource(R.drawable.star_btn);

        return convertView;
    }

    public void addItem(String pno, String gname, String cname, String imageurl, String price, String event, String gtype, String ghate, String glove, String date) {
        ProductItem item = new ProductItem(pno, gname, cname, imageurl, price, event, gtype, ghate, glove, date);
        this.user = user;
        productItem_ArrayList.add(item);
    }

}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
