package com.todo.order.view.order.status;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.util.Etc;
import com.todo.order.view.common.BasicActivity;
import com.todo.order.view.favoirtes.FavoritesActivity;
import com.todo.order.view.order.OrderItem;
import com.todo.order.view.order.OrderOutputAdapter;
import com.todo.order.view.order.OrderOutputItem;
import com.todo.order.view.order.OrderStatusAdapter;
import com.todo.order.view.order.OrderStatusItem;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.todo.order.config.MangoPreferences.GBN_FAVORITES_LIST;
import static com.todo.order.config.MangoPreferences.GBN_ORDER_STATUS_LIST;
import static com.todo.order.config.ServerAddress.API_ACTION;

public class OrderStatusActivity extends BasicActivity implements View.OnClickListener {

    private Context mContext;

    private MangoPreferences pref;

    private TabLayout tl_order_status;

    private ViewPager vp_order_status;

    private OrderStatusFragment orderStatusFragment;
    private OrderOutputFragment orderOutputFragment;

    private ItemTask itemTask;
    private ItemTaskOutput itemTaskOutput;

    private ImageButton ib_status_home;

    private String result_txt, result_txt_output, uid;

    private ArrayList<OrderStatusItem> mOrderStatusItems;
    private ArrayList<OrderOutputItem> mOrderOutputItems;
    private Etc etc;

    private TextView tv_order_status_stdt, tv_order_status_eddt, tv_order_output_stdt, tv_order_output_eddt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        pref = new MangoPreferences(this);
        uid = pref.getString("uid", "");
        etc = new Etc();

        tl_order_status = (TabLayout) findViewById(R.id.tl_order_status);
        vp_order_status = (ViewPager) findViewById(R.id.vp_order_status);

        orderStatusFragment = new OrderStatusFragment();
        orderOutputFragment = new OrderOutputFragment();

        tl_order_status.setupWithViewPager(vp_order_status);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(orderStatusFragment, "주문");
        viewPagerAdapter.addFragment(orderOutputFragment, "출고");
        vp_order_status.setAdapter(viewPagerAdapter);

        ib_status_home = (ImageButton) findViewById(R.id.ib_status_home);
        ib_status_home.setOnClickListener(this);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String strFirstDay = String.valueOf(year) + "-" + String.format("%02d", (month+1)) + "-" + "01";
        String strLastDay = String.valueOf(year) + "-" + String.format("%02d", (month+1)) + "-" + c.getActualMaximum(Calendar.DAY_OF_MONTH);

        String orderGbn = "1";
        String stDt = strFirstDay.replace("-", "");
        String edDt = strLastDay.replace("-", "");
        itemTask = new ItemTask();
        itemTask.execute(API_ACTION, GBN_ORDER_STATUS_LIST, uid, orderGbn, stDt, edDt);

        String orderGbnOutput = "2";
        String stDtOutput = strFirstDay.replace("-", "");
        String edDtOutput = strLastDay.replace("-", "");
        itemTaskOutput = new ItemTaskOutput();
        itemTaskOutput.execute(API_ACTION, GBN_ORDER_STATUS_LIST, uid, orderGbnOutput, stDtOutput, edDtOutput);

    }

    public String getUid() {
        return uid;
    }

    public void getData(String action, String gbn, String uid, String orderGbn, String stDt, String edDt) {
        ItemTask itemTask = new ItemTask();
        itemTask.execute(action, gbn, uid, orderGbn, stDt, edDt);
    }

    public void getDataOutput(String action, String gbn, String uid, String orderGbn, String stDt, String edDt) {
        ItemTaskOutput itemTaskOutput = new ItemTaskOutput();
        itemTaskOutput.execute(action, gbn, uid, orderGbn, stDt, edDt);
    }

    public String getDate(String d) {
        return etc.dateFormat(d, "yyyyMMdd", "yyyy-MM-dd");
    }

    public String getPrice(String p) {
        return etc.commaDouble(p);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.ib_status_home:
                finish();
                break;
        }
    }

    private void viewPagerEnTabLayout() {

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }

    private class ItemTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            String ordGbn = params[3];
            String stDt = params[4];
            String edDt = params[5];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("ordGbn", ordGbn));
                pairs.add(new BasicNameValuePair("stDt", stDt));
                pairs.add(new BasicNameValuePair("edDt", edDt));

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

            if (result) {
                mOrderStatusItems = new ArrayList<OrderStatusItem>();
                try {
                    JSONArray array = new JSONArray(result_txt);
                    Log.e("array :: ", array.toString());
                    Double totalAmount = 0.0;
                    for(int i=0; i<array.length(); i++) {
                        Double price = 0.0;
                        JSONObject listObj = array.getJSONObject(i);
                        OrderStatusItem orderStatusItem = new OrderStatusItem();

                        orderStatusItem.setOrdDt(listObj.has("ordDt") ? listObj.get("ordDt").toString() : "");
                        orderStatusItem.setPrice(listObj.has("price") ? listObj.get("price").toString() : "0");

                        if(listObj.has("price")) {
                            price = Double.parseDouble(listObj.get("price").toString());
                        }

                        totalAmount += price;

                        mOrderStatusItems.add(orderStatusItem);
                    }

                    OrderStatusAdapter orderStatusAdapter = new OrderStatusAdapter(mContext, mOrderStatusItems);
                    RecyclerView target = findViewById(R.id.rv_order_status);
                    target.setLayoutManager(new LinearLayoutManager(mContext));
                    target.setAdapter(orderStatusAdapter);

                    TextView tv_order_status_total_amount = findViewById(R.id.tv_order_status_total_amount);

                    DecimalFormat df = new DecimalFormat("#");
                    df.setMaximumFractionDigits(0);

                    tv_order_status_total_amount.setText(etc.commaDouble(df.format(totalAmount)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private class ItemTaskOutput extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            String ordGbn = params[3];
            String stDt = params[4];
            String edDt = params[5];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("ordGbn", ordGbn));
                pairs.add(new BasicNameValuePair("stDt", stDt));
                pairs.add(new BasicNameValuePair("edDt", edDt));

                post.setEntity(new UrlEncodedFormEntity(pairs,"utf-8"));
                HttpResponse response = client.execute(post);

                HttpEntity ent = response.getEntity();
                result_txt_output = EntityUtils.toString(ent);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.e("result :: ", result.toString());

            if (result) {
                mOrderOutputItems = new ArrayList<OrderOutputItem>();
                try {
                    JSONArray array = new JSONArray(result_txt_output);
                    Log.e("array :: ", array.toString());
                    Double totalAmount = 0.0;
                    for(int i=0; i<array.length(); i++) {
                        Double price = 0.0;
                        JSONObject listObj = array.getJSONObject(i);
                        OrderOutputItem orderOutputItem = new OrderOutputItem();

                        orderOutputItem.setOrdDt(listObj.has("ordDt") ? listObj.get("ordDt").toString() : "");
                        orderOutputItem.setPrice(listObj.has("price") ? listObj.get("price").toString() : "0");

                        if(listObj.has("price")) {
                            price = Double.parseDouble(listObj.get("price").toString());
                        }

                        totalAmount += price;

                        mOrderOutputItems.add(orderOutputItem);
                    }

                    OrderOutputAdapter orderOutputAdapter = new OrderOutputAdapter(mContext, mOrderOutputItems, OrderStatusActivity.this);
                    RecyclerView target = findViewById(R.id.rv_order_status_output);
                    target.setLayoutManager(new LinearLayoutManager(mContext));
                    target.setAdapter(orderOutputAdapter);

                    TextView tv_order_status_total_amount_output = findViewById(R.id.tv_order_status_total_amount_output);

                    DecimalFormat df = new DecimalFormat("#");
                    df.setMaximumFractionDigits(0);

                    tv_order_status_total_amount_output.setText(etc.commaDouble(df.format(totalAmount)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}