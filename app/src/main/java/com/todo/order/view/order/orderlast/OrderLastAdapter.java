package com.todo.order.view.order.orderlast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.view.order.OrderItem;


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

import static android.app.Activity.RESULT_OK;
import static com.todo.order.config.MangoPreferences.GBN_ORDER_LAST_DETAIL;
import static com.todo.order.config.ServerAddress.API_ACTION_ORDER;

public class OrderLastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemOrderLast> mItemOrderLasts;
    private ArrayList<OrderItem> mOrderItems;
    private Context mContext;
    private String result_txt;
    private MangoPreferences pref;
    private Activity mActivity;

    public OrderLastAdapter(Context mContext, ArrayList<ItemOrderLast> mItemOrderLasts, Activity activity) {
        this.mContext = mContext;
        this.mItemOrderLasts = mItemOrderLasts;
        this.pref = new MangoPreferences(mContext);
        this.mActivity = activity;
    }

    public static class OrderLastViewHolder extends RecyclerView.ViewHolder {
        private TextView orderDate, item;
        public OrderLastViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.tv_order_last_date);
            item = itemView.findViewById(R.id.tv_order_last_item);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_last, parent, false);
        return new OrderLastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OrderLastViewHolder holder = (OrderLastViewHolder)viewHolder;
        holder.orderDate.setText(mItemOrderLasts.get(position).getOrderDate());
        holder.item.setText(mItemOrderLasts.get(position).getItemName());

        final int i = position;
        holder.itemView.setOnClickListener(v -> {
            String uid = pref.getString("uid", "");
            String udt = mItemOrderLasts.get(i).getOrderDate().replace("-", "");

            OrderLastTask orderLastTask = new OrderLastTask();
            orderLastTask.execute(API_ACTION_ORDER, GBN_ORDER_LAST_DETAIL, uid, udt);
        });
    }

    @Override
    public int getItemCount() {
        return mItemOrderLasts.size();
    }

    private class OrderLastTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            String udt = params[3];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("udt", udt));

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
                mOrderItems = new ArrayList<OrderItem>();
                try {
                    JSONArray array = new JSONArray(result_txt);

                    for(int i = 0; i < array.length(); i++) {
                        JSONObject listObj = array.getJSONObject(i);

                        OrderItem orderItem = new OrderItem();

                        String name = listObj.has("item") ? listObj.get("item").toString() : "";
                        String cd = listObj.has("itemcd") ? listObj.get("itemcd").toString() : "";
                        String price = listObj.has("price") ? listObj.get("price").toString() : "";
                        String unit = listObj.has("unit") ? listObj.get("unit").toString() : "";
                        String qty = listObj.has("qty") ? listObj.get("qty").toString() : "";

                        orderItem.setName(name);
                        orderItem.setCd(cd);
                        orderItem.setPrice(price);
                        orderItem.setUnit(unit);
                        orderItem.setQty(qty);

                        mOrderItems.add(orderItem);
                    }

                    Log.e("mOrderItems", mOrderItems.toString());

                    Intent intent = new Intent();
                    intent.putExtra("EXTRA_SELECTED_ITEM", mOrderItems);
                    mActivity.setResult(RESULT_OK, intent);
                    mActivity.finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
