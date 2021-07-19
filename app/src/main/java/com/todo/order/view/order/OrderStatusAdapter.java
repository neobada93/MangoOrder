package com.todo.order.view.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.order.MainActivity;
import com.todo.order.R;
import com.todo.order.util.Etc;
import com.todo.order.view.login.LoginActivity;
import com.todo.order.view.order.additem.AddItemActivity;
import com.todo.order.view.order.status.OrderStatusActivity;
import com.todo.order.view.order.status.OrderStatusDetailActivity;

import java.util.ArrayList;

public class OrderStatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<OrderStatusItem> mOrderStatusItem;
    private Context mContext;
    private Activity mActivity;
    private Etc etc;

    public OrderStatusAdapter(Context mContext, ArrayList<OrderStatusItem> mOrderStatusItem) {
        this.mContext = mContext;
        this.mOrderStatusItem = mOrderStatusItem;
        //this.mActivity = mActivity;
        etc = new Etc();
    }

    public class ItemViewHolder extends  RecyclerView.ViewHolder {

        private TextView date, price;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_order_status_date);
            price = itemView.findViewById(R.id.tv_order_status_price);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String ordDt = mOrderStatusItem.get(position).getOrdDt();
                    String totalPrice = mOrderStatusItem.get(position).getPrice();

                    Bundle b = new Bundle();
                    b.putString("ordGbn", "1");
                    b.putString("ordDt", ordDt);
                    b.putString("totalPrice", totalPrice);

                    Intent intent;
                    intent = new Intent(v.getContext(), OrderStatusDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(b);

                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_order_status, viewGroup, false);
        return new OrderStatusAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OrderStatusAdapter.ItemViewHolder holder = (OrderStatusAdapter.ItemViewHolder) viewHolder;
        holder.date.setText(etc.dateFormat(mOrderStatusItem.get(position).getOrdDt(), "yyyyMMdd", "yyyy-MM-dd"));
        holder.price.setText(etc.commaDouble(mOrderStatusItem.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return mOrderStatusItem.size();
    }
}
