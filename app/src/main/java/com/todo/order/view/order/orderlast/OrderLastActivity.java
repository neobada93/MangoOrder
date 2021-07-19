package com.todo.order.view.order.orderlast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.util.Etc;
import com.todo.order.view.common.BasicActivity;

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

import static com.todo.order.config.MangoPreferences.GBN_ORDER_LAST;
import static com.todo.order.config.ServerAddress.API_ACTION_ORDER;

public class OrderLastActivity extends BasicActivity implements View.OnClickListener {

    private ImageButton btnClose;
    private String result_txt;
    private MangoPreferences pref = new MangoPreferences(this);
    private ArrayList<ItemOrderLast> mItemOrderLast;
    OrderLastTask orderLastTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_last);

        btnClose = findViewById(R.id.ib_close_activity_order_last);
        btnClose.setOnClickListener(this);

        mItemOrderLast = new ArrayList<>();

        orderLastTask = new OrderLastTask();

        String uid = pref.getString("uid", "");
        String udt = pref.getString("udt", "20201026");
        orderLastTask.execute(API_ACTION_ORDER, GBN_ORDER_LAST, uid);
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch(view.getId()) {
            case R.id.ib_close_activity_order_last:
                finish();
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private class OrderLastTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));

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
                    JSONArray array = new JSONArray(result_txt);

                    for(int i = 0; i < array.length(); i++) {
                        JSONObject listObj = array.getJSONObject(i);

                        ItemOrderLast itemOrderLast = new ItemOrderLast();

                        Etc etc = new Etc();
                        String d = listObj.has("udt") ? listObj.get("udt").toString() : "";
                        if (!d.equals("")) {
                            d = etc.dateFormat(d, "yyyyMMdd", "yyyy-MM-dd");
                        }

                        itemOrderLast.setOrderDate(d);
                        itemOrderLast.setItemName(listObj.has("larnm") ? listObj.get("larnm").toString() : "");

                        mItemOrderLast.add(itemOrderLast);
                    }

                    OrderLastAdapter orderLastAdapter = new OrderLastAdapter(OrderLastActivity.this, mItemOrderLast, OrderLastActivity.this);
                    RecyclerView target = findViewById(R.id.rv_order_last);
                    target.setLayoutManager(new LinearLayoutManager(OrderLastActivity.this));
                    target.setAdapter(orderLastAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}