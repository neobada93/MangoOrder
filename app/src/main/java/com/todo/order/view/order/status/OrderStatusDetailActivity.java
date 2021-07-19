package com.todo.order.view.order.status;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.util.Etc;
import com.todo.order.view.common.BasicActivity;
import com.todo.order.view.order.OrderStatusDetailAdapter;
import com.todo.order.view.order.OrderStatusDetailItem;

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

import static com.todo.order.config.MangoPreferences.GBN_ORDER_STATUS_DETAIL;
import static com.todo.order.config.MangoPreferences.GBN_ORDER_STATUS_LIST;
import static com.todo.order.config.ServerAddress.API_ACTION;

public class OrderStatusDetailActivity extends BasicActivity implements View.OnClickListener {

    private Context mContext;
    private MangoPreferences pref;
    private ImageButton ib_order_status_detail_arrow_left;
    private TextView tv_order_status_detail_title, tv_order_status_detail_date, tv_order_status_detail_total_price;
    private Etc etc;
    private OrderStatusDetailTask orderStatusDetailTask;
    private String result_txt, uid;
    private ArrayList<OrderStatusDetailItem> mOrderStatusDetailItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_detail);

        etc = new Etc();
        pref = new MangoPreferences(this);
        uid = pref.getString("uid", "");

        tv_order_status_detail_title = (TextView) findViewById(R.id.tv_order_status_detail_title);
        tv_order_status_detail_date = (TextView) findViewById(R.id.tv_order_status_detail_date);
        tv_order_status_detail_total_price = (TextView) findViewById(R.id.tv_order_status_detail_total_price);

        Bundle b = getIntent().getExtras();
        String ordGbn = "";
        String ordDt = "";
        String totalPrice = "";
        String title = "주문상세";
        if (b != null) {
            ordGbn = b.getString("ordGbn");
            ordDt = b.getString("ordDt");
            totalPrice = b.getString("totalPrice");
            if(ordGbn.equals("2")) {
                title = "출고상세";
            }
        }

        tv_order_status_detail_title.setText(title);
        tv_order_status_detail_date.setText("일자 : " + etc.dateFormat(ordDt, "yyyyMMdd", "yyyy-MM-dd"));
        tv_order_status_detail_total_price.setText(etc.commaDouble(totalPrice));

        ib_order_status_detail_arrow_left = (ImageButton) findViewById(R.id.ib_order_status_detail_arrow_left);

        ib_order_status_detail_arrow_left.setOnClickListener(this);

        orderStatusDetailTask = new OrderStatusDetailTask();
        orderStatusDetailTask.execute(API_ACTION, GBN_ORDER_STATUS_DETAIL, uid, ordGbn, ordDt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_order_status_detail_arrow_left:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class OrderStatusDetailTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            String ordGbn = params[3];
            String ordDt = params[4];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("ordGbn", ordGbn));
                pairs.add(new BasicNameValuePair("ordDt", ordDt));

                post.setEntity(new UrlEncodedFormEntity(pairs,"utf-8"));
                HttpResponse response = client.execute(post);

                HttpEntity ent = response.getEntity();
                result_txt = EntityUtils.toString(ent);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.e("result :: ", result.toString());
            mOrderStatusDetailItems = new ArrayList<OrderStatusDetailItem>();

            try {
                JSONArray array = new JSONArray(result_txt);
                for(int i = 0; i < array.length(); i++) {
                    JSONObject listObj = array.getJSONObject(i);
                    OrderStatusDetailItem orderStatusDetailItem = new OrderStatusDetailItem();

                    orderStatusDetailItem.setLarNm(listObj.has("larNm") ? listObj.get("larNm").toString() : "");
                    orderStatusDetailItem.setCnt(listObj.has("cnt") ? listObj.get("cnt").toString() : "");
                    orderStatusDetailItem.setPrice(listObj.has("price") ? listObj.get("price").toString() : "");

                    mOrderStatusDetailItems.add(orderStatusDetailItem);
                }

                OrderStatusDetailAdapter orderStatusDetailAdapter = new OrderStatusDetailAdapter(mContext, mOrderStatusDetailItems);
                RecyclerView target = findViewById(R.id.rv_order_status_detail);
                target.setLayoutManager(new LinearLayoutManager(mContext));
                target.setAdapter(orderStatusDetailAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}