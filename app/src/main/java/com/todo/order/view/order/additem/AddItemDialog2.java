package com.todo.order.view.order.additem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.view.order.OrderItem;
import com.todo.order.util.MyTab;
import com.todo.order.util.RecyclerTouchListener;
import com.todo.order.util.TabAdapter;

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

import static com.todo.order.config.MangoPreferences.GBN_ITEMS;
import static com.todo.order.config.ServerAddress.API_ACTION;

public class AddItemDialog2 extends Dialog {

    private String result_txt;
    private String uid;
    private Context mContext;
    private Activity mActivity;
    private ImageButton btnClose;
    RecyclerView rvAddItem, rvTab;
    ArrayList<OrderItem> mOrderItems = new ArrayList<>();
    ArrayList<MyTab> mMyTabs = new ArrayList<>();
    private MangoPreferences pref = new MangoPreferences(getContext());

    ArrayList<RecyclerView> targets = new ArrayList<RecyclerView>();

    public AddItemDialog2(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_item_add);

        uid = pref.getString("uid", "");

        rvAddItem = findViewById(R.id.rvAddItem);
        rvTab = findViewById(R.id.rv_tab);

        AddItemDialog2.ItemsTask itemsTask = new AddItemDialog2.ItemsTask();
        itemsTask.execute(API_ACTION, GBN_ITEMS, uid, "");

        btnClose = findViewById(R.id.ib_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    private class ItemsTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            String cate = params[3];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("cate", cate));

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
                    JSONObject obj = new JSONObject(result_txt);
                    Iterator<String> temp = obj.keys();
                    while (temp.hasNext()) {
                        String key = temp.next();
                        JSONArray array = new JSONArray(obj.get(key).toString());
                        MyTab myTab = new MyTab(key);
                        mMyTabs.add(myTab);

                        for(int i = 0; i < array.length(); i++) {
                            JSONObject listObj = array.getJSONObject(i);
                            OrderItem orderItem = new OrderItem();

                            orderItem.setName(listObj.has("cate") ? listObj.get("cate").toString() : "");
                            orderItem.setName(listObj.has("item") ? listObj.get("item").toString() : "");
                            orderItem.setUnit(listObj.has("unit") ? listObj.get("unit").toString() : "");
                            orderItem.setPrice(listObj.has("price") ? listObj.get("price").toString() : "");

                            mOrderItems.add(orderItem);
                        }
                    }

                    final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                    TabAdapter tabAdapter = new TabAdapter(mMyTabs);
                    RecyclerView rvTab = findViewById(R.id.rv_tab);
                    rvTab.setLayoutManager(mLayoutManager);
                    rvTab.setAdapter(tabAdapter);

                    rvTab.addOnItemTouchListener(new RecyclerTouchListener(mContext, rvTab, new RecyclerTouchListener.ClickListener() {

                        @Override
                        public void onClick(View view, int position) {
                            tabAdapter.clearSelectedItem();

//                            AddItemDialog.ItemsTask itemsTask = new AddItemDialog.ItemsTask();
//                            itemsTask.execute(API_ACTION, GBN_ITEMS, uid, "계육");
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));

                    AddItemAdapter itemAdapter = new AddItemAdapter(mContext, mOrderItems, mActivity);
                    RecyclerView target = findViewById(R.id.rvAddItem);
                    target.setLayoutManager(new LinearLayoutManager(mContext));
                    target.setAdapter(itemAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (mContext != null) {
                    new android.app.AlertDialog.Builder(mContext)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }
}
