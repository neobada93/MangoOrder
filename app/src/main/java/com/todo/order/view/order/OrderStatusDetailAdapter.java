package com.todo.order.view.order;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.order.R;
import com.todo.order.util.Etc;

import java.util.ArrayList;

public class OrderStatusDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<OrderStatusDetailItem> mOrderStatusDetailItems;
    private Context mContext;
    private Activity mActivity;
    private Etc etc;

    public OrderStatusDetailAdapter(Context mContext, ArrayList<OrderStatusDetailItem> mOrderStatusDetailItems) {
        this.mContext = mContext;
        this.mOrderStatusDetailItems = mOrderStatusDetailItems;
        etc = new Etc();
    }

    public class ItemViewHolder extends  RecyclerView.ViewHolder {

        private TextView name, amount, price;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_order_status_detail_name);
            amount = itemView.findViewById(R.id.tv_order_status_detail_amount);
            price = itemView.findViewById(R.id.tv_order_status_detail_price);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_order_status_detail, viewGroup, false);
        return new OrderStatusDetailAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OrderStatusDetailAdapter.ItemViewHolder holder = (OrderStatusDetailAdapter.ItemViewHolder) viewHolder;
        holder.name.setText(mOrderStatusDetailItems.get(position).getLarNm());
        holder.amount.setText(mOrderStatusDetailItems.get(position).getCnt());
        holder.price.setText(etc.commaDouble(mOrderStatusDetailItems.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return mOrderStatusDetailItems.size();
    }
}
