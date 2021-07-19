package com.todo.order.view.order.additem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.order.R;
import com.todo.order.view.common.FullScreenActivity;
import com.todo.order.view.order.OrderItem;
import com.todo.order.util.Etc;

import java.util.ArrayList;

public class AddItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<OrderItem> mOrderItem;
    private Context mContext;
    private Activity mActivity;
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
    private Etc etc = new Etc();

    public AddItemAdapter(Context mContext, ArrayList<OrderItem> mOrderItem, Activity activity) {
        this.mContext = mContext;
        this.mOrderItem = mOrderItem;
        this.mActivity = activity;
    }

    // viewHolder Class
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView name, unit, price;
        private CheckBox cb_item;
        ItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_name);
            unit = itemView.findViewById(R.id.tv_item_unit);
            //price = itemView.findViewById(R.id.tv_item_price);
            cb_item = itemView.findViewById(R.id.cb_item);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), FullScreenActivity.class);
                    intent.putExtra("image_url", mOrderItem.get(getAdapterPosition()).getUrl());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);

                    //int position = getAdapterPosition();
//                    if(mSelectedItems.get(position, false)) {
//                        mSelectedItems.put(position, false);
//                        cb_item.setChecked(false);
//                    } else {
//                        mSelectedItems.put(position, true);
//                        cb_item.setChecked(true);
//                    }
                }
            });

            cb_item.setOnClickListener(new CompoundButton.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(mSelectedItems.get(position, false)) {
                        mSelectedItems.put(position, false);
                    } else {
                        mSelectedItems.put(position, true);
                    }
                }
            });

            cb_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int position = getAdapterPosition();

                    String cd = mOrderItem.get(position).getCd();
                    String item = mOrderItem.get(position).getName();
                    String price = "0";
                    String unit = mOrderItem.get(position).getUnit();
                    String url = mOrderItem.get(position).getUrl();

                    OrderItem orderItem = new OrderItem();
                    orderItem.setCd(cd);
                    orderItem.setName(item);
                    orderItem.setPrice(price);
                    orderItem.setUnit(unit);
                    orderItem.setQty("0");
                    orderItem.setUrl(url);

                    if(mActivity.getClass().equals(AddItemActivity.class)) {
                        ((AddItemActivity) mActivity).addmOrderItemSelected(orderItem);
                    }

                    //Log.e("mOrderItemSelected :: ", String.valueOf(((AddItemActivity)mContext).getmOrderItemSelected()));
                }
            });
        }
    }

    // 1. view 만든다.
    // 2. viewHolder 만든다.
    // 3. viewHolder return.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_add_item, viewGroup, false);
        return new AddItemAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        AddItemAdapter.ItemViewHolder holder = (AddItemAdapter.ItemViewHolder)viewHolder;
        holder.name.setText(mOrderItem.get(position).getName());
        holder.unit.setText(mOrderItem.get(position).getUnit());

        if(mSelectedItems.get(position, false)) {
            holder.cb_item.setChecked(true);
        } else {
            holder.cb_item.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return mOrderItem.size();
    }
}
