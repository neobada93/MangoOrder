package com.todo.order.view.favoirtes;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.util.ViewPagerAdapter;
import com.todo.order.view.common.BasicActivity;
import com.todo.order.view.order.OrderItem;
import com.google.android.material.tabs.TabLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.todo.order.config.MangoPreferences.GBN_FAVORITES_LIST;
import static com.todo.order.config.MangoPreferences.GBN_FAVORITES_SAVE;
import static com.todo.order.config.ServerAddress.API_ACTION;

public class FavoritesActivity extends BasicActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager vpContent;
    private ViewPagerAdapter viewPagerAdapter;
    private ArrayList<Fragment> arrayFragments;
    private ArrayList<String> arrayTitles;
    private TextView tv_favorites_count, tv_save_favorites;

    private String result_txt;
    private String uid;
    private String cate;
    private String srch;

    private Context mContext;
    private ImageButton btnClose;
    private ImageButton ibSearch;
    private FavoritesActivity.ItemTask itemTask;
    private EditText etSearch;

    RecyclerView rvFavorites;
    SaveFavoritesTask saveFavoritesTask;

    private MangoPreferences pref = new MangoPreferences(this);

    public ArrayList<OrderItem> getmOrderItemSelected() {
        return mOrderItemSelected;
    }

    public void setmOrderItemSelected(ArrayList<OrderItem> mOrderItemSelected) {
        this.mOrderItemSelected = mOrderItemSelected;
    }

    public void addmOrderItemSelected(OrderItem orderItem) {
        this.mOrderItemSelected.add(orderItem);
    }

    private ArrayList<OrderItem> mOrderItemSelected = new ArrayList<OrderItem>();
    private ArrayList<HashMap<String, String>> mChangedItem = new ArrayList<HashMap<String, String>>();

    public void addChangedItem(HashMap orderItem) {
        this.mChangedItem.add(orderItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        uid = pref.getString("uid", "");

        btnClose = findViewById(R.id.ib_close_2);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tlTab_2);
        vpContent = (ViewPager) findViewById(R.id.vpContent);
        vpContent.setOffscreenPageLimit(100);
        tv_favorites_count = (TextView) findViewById(R.id.tv_favorites_count);
        tv_save_favorites = (TextView) findViewById(R.id.tv_save_favorites);

        rvFavorites = findViewById(R.id.rvFavorites);

        tv_save_favorites.setOnClickListener(this);

        ibSearch = (ImageButton) findViewById(R.id.ib_search_2);
        ibSearch.setOnClickListener(this);

        etSearch = (EditText) findViewById(R.id.et_search);

        itemTask = new FavoritesActivity.ItemTask();
        itemTask.execute(API_ACTION, GBN_FAVORITES_LIST, uid, "", "");



    }

    private void viewPagerEnTabLayout() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, arrayFragments, arrayTitles);
        vpContent.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(vpContent);
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {
            case R.id.tv_save_favorites:

                saveFavoritesTask = new SaveFavoritesTask();
                String strItemcd;
                String strQty;
                StringBuilder sb = new StringBuilder();
                StringBuilder sbCd = new StringBuilder();
                for(int i=0; i<mChangedItem.size(); i++) {

                    String key = mChangedItem.get(i).keySet().toString().replace("[", "").replace("]", "");
                    String val =mChangedItem.get(i).get(key);
                    sbCd.append(key);
                    sb.append(val);

                    if(i != (mChangedItem.size()-1)) {
                        sb.append(",");
                        sbCd.append(",");
                    }

                    Log.e("sb ::: ", sb.toString());
                    Log.e("sbCd ::: ", sbCd.toString());
                }

                saveFavoritesTask.execute(API_ACTION, GBN_FAVORITES_SAVE, uid, sbCd.toString(), sb.toString());

                break;
            case R.id.ib_search_2:
                itemTask = new FavoritesActivity.ItemTask();
                srch = etSearch.getText().toString();
                itemTask.execute(API_ACTION, GBN_FAVORITES_LIST, uid, "", srch);
                break;
        }
    }

    private class ItemTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            String cate = params[3];
            String keyword = params[4];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("cate", cate));
                pairs.add(new BasicNameValuePair("srch", keyword));

                post.setEntity(new UrlEncodedFormEntity(pairs,"utf-8"));
                HttpResponse response = client.execute(post);

                HttpEntity ent = response.getEntity();
                result_txt = EntityUtils.toString(ent);

                return true;
            } catch(Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.e("result :: ", result.toString());
            if(result) {
                try {
                    arrayTitles = new ArrayList<>();
                    arrayFragments = new ArrayList<>();
                    JSONObject obj = new JSONObject(result_txt);
                    Iterator<String> temp = obj.keys();
                    while (temp.hasNext()) {
                        String key = temp.next();
                        JSONArray array = new JSONArray(obj.get(key).toString());
                        arrayTitles.add(key);

//                        for(int i = 0; i < array.length(); i++) {
//                            JSONObject listObj = array.getJSONObject(i);
//                            OrderItem orderItem = new OrderItem();
//
//                            orderItem.setName(listObj.has("cate") ? listObj.get("cate").toString() : "");
//                            orderItem.setName(listObj.has("item") ? listObj.get("item").toString() : "");
//                            orderItem.setUnit(listObj.has("unit") ? listObj.get("unit").toString() : "");
//                            orderItem.setPrice(listObj.has("price") ? listObj.get("price").toString() : "");
//
//                            mOrderItems.add(orderItem);
//                        }

//                        FavoritesAdapter itemAdapter = new FavoritesAdapter(mContext, mOrderItems);
//                        RecyclerView target = findViewById(R.id.rvFavoritesFragment);
//                        target.setLayoutManager(new LinearLayoutManager(mContext));
//                        target.setAdapter(itemAdapter);



                        arrayFragments.add(FavoritesFragment.newInstance(uid, "", obj.get(key).toString()));


                        Log.e("tab :: ", obj.get(key).toString());
                    }

                    viewPagerEnTabLayout();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SaveFavoritesTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            String itemcd = params[3];
            String chk = params[4];

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("itemcd", itemcd));
                pairs.add(new BasicNameValuePair("chk", chk));

                post.setEntity(new UrlEncodedFormEntity(pairs,"utf-8"));
                HttpResponse response = client.execute(post);

                HttpEntity ent = response.getEntity();
                result_txt = EntityUtils.toString(ent);

                return true;
            } catch(Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                try{
                    JSONArray array = new JSONArray(result_txt);

                    Log.e("result", array.toString());

                    for(int i = 0; i < array.length(); i++) {
                        JSONObject listObj = array.getJSONObject(i);

                        String msg = listObj.get("msg").toString();
                        String code = listObj.get("code").toString();

                        switch (code) {
                            case "200":
                                msg = "저장 되었습니다.";
                                break;
                            case "400":
                                msg = "저장에 실패했습니다.";
                                break;
                        }

                        show(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if ((FavoritesActivity.this != null) && (!FavoritesActivity.this.isFinishing())) {
                    new android.app.AlertDialog.Builder(FavoritesActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }

    public void show(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("즐겨찾기 저장");
        builder.setMessage(message);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}