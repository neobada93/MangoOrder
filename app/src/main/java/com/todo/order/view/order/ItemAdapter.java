package com.todo.order.view.order;

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

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<OrderItem> mItem;
    private Context mContext;
    private Etc etc;

    public ItemAdapter(Context mContext, ArrayList<OrderItem> mItem) {
        this.mContext = mContext;
        this.mItem = mItem;
        etc = new Etc();
    }

    // viewHolder Class
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView name, unit, price, itemcd, amount;
        ItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_list_name);
            amount = itemView.findViewById(R.id.tv_list_amount);
            unit = itemView.findViewById(R.id.tv_list_unit);
            price = itemView.findViewById(R.id.tv_list_price);
        }
    }

    // 1. view 만든다.
    // 2. viewHolder 만든다.
    // 3. viewHolder return.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, viewGroup, false);
        return new ItemAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ItemAdapter.ItemViewHolder holder = (ItemAdapter.ItemViewHolder)viewHolder;
        holder.name.setText(mItem.get(position).getName());
        holder.amount.setText(mItem.get(position).getQty());
        holder.unit.setText(mItem.get(position).getUnit());
        holder.price.setText(etc.comma(mItem.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }
}
