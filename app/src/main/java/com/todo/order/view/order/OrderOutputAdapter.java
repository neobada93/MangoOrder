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

import com.todo.order.R;
import com.todo.order.util.Etc;
import com.todo.order.view.favoirtes.FavoritesActivity;
import com.todo.order.view.order.status.OrderStatusActivity;
import com.todo.order.view.order.status.OrderStatusDetailActivity;

import java.util.ArrayList;

public class OrderOutputAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<OrderOutputItem> mOrderOutputItem;
    private Context mContext;
    private Activity mActivity;
    private Etc etc;

    public OrderOutputAdapter(Context mContext, ArrayList<OrderOutputItem> orderOutputItem, Activity mActivity) {
        this.mContext = mContext;
        this.mOrderOutputItem = orderOutputItem;
        this.mActivity = mActivity;
        //etc = new Etc();
    }

    public class ItemViewHolder extends  RecyclerView.ViewHolder {

        private TextView date, price;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_order_output_date);
            price = itemView.findViewById(R.id.tv_order_output_price);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String ordDt = mOrderOutputItem.get(position).getOrdDt();
                    String totalPrice = mOrderOutputItem.get(position).getPrice();

                    Bundle b = new Bundle();
                    b.putString("ordGbn", "2");
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_order_output, viewGroup, false);
        return new OrderOutputAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OrderOutputAdapter.ItemViewHolder holder = (OrderOutputAdapter.ItemViewHolder) viewHolder;

        if(mActivity.getClass().equals(OrderStatusActivity.class)) {
            holder.date.setText(((OrderStatusActivity) mActivity).getDate(mOrderOutputItem.get(position).getOrdDt()));
            holder.price.setText(((OrderStatusActivity) mActivity).getPrice(mOrderOutputItem.get(position).getPrice()));
        }
    }

    @Override
    public int getItemCount() {
        return mOrderOutputItem.size();
    }
}
