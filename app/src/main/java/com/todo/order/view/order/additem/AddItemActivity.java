package com.todo.order.view.order.additem;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
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
import com.todo.order.view.order.OrderItem;
import com.todo.order.view.common.BasicActivity;
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
import java.util.Iterator;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.todo.order.config.MangoPreferences.GBN_ITEMS;
import static com.todo.order.config.ServerAddress.API_ACTION;

public class AddItemActivity extends BasicActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager vpContent;
    private ViewPagerAdapter viewPagerAdapter;
    private ArrayList<Fragment> arrayFragments;
    private ArrayList<String> arrayTitles;
    private TextView tv_add_item_count;
    private TextView tv_item_choice;

    private String result_txt;
    private String uid;
    private String cate;
    private String srch;

    private Context mContext;
    private ImageButton btnClose;
    private ImageButton ibSearch;
    private ItemTask itemTask;
    private EditText etSearch;

    RecyclerView rvAddItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

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
        tv_add_item_count = (TextView) findViewById(R.id.tv_add_item_count);
        tv_item_choice = (TextView) findViewById(R.id.tv_item_choice);

        rvAddItem = findViewById(R.id.rvAddItem);

        tv_item_choice.setOnClickListener(this);

        ibSearch = (ImageButton) findViewById(R.id.ib_search_2);
        ibSearch.setOnClickListener(this);

        etSearch = (EditText) findViewById(R.id.et_search);

        itemTask = new ItemTask();
        itemTask.execute(API_ACTION, GBN_ITEMS, uid, "", "");



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
            case R.id.tv_item_choice:
                intent = new Intent();
                intent.putExtra("EXTRA_SELECTED_ITEM", mOrderItemSelected);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.ib_search_2:
                itemTask = new ItemTask();
                srch = etSearch.getText().toString();
                itemTask.execute(API_ACTION, GBN_ITEMS, uid, "", srch);
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

//                        AddItemAdapter itemAdapter = new AddItemAdapter(mContext, mOrderItems);
//                        RecyclerView target = findViewById(R.id.rvAddItemFragment);
//                        target.setLayoutManager(new LinearLayoutManager(mContext));
//                        target.setAdapter(itemAdapter);



                        arrayFragments.add(AddItemFragment.newInstance(uid, "", obj.get(key).toString()));


                        Log.e("tab :: ", obj.get(key).toString());
                    }

                    viewPagerEnTabLayout();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}