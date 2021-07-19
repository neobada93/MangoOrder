package com.todo.order.view.favoirtes;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import com.todo.order.util.Etc;
import com.todo.order.view.order.OrderItem;

import java.util.ArrayList;
import java.util.HashMap;

public class FavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<OrderItem> mOrderItem;
    private ArrayList<OrderItem> changedOrderItem;
    private Context mContext;
    private Activity mActivity;
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
    private Etc etc = new Etc();
    private ArrayList<String> changedItem = new ArrayList<String>();

    public FavoritesAdapter(Context mContext, ArrayList<OrderItem> mOrderItem, Activity activity) {
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
                    int position = getAdapterPosition();

                    if(mSelectedItems.get(position, false)) {
                        mSelectedItems.put(position, false);
                        cb_item.setChecked(false);
                    } else {
                        mSelectedItems.put(position, true);
                        cb_item.setChecked(true);
                    }

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(mOrderItem.get(position).getCd(), mOrderItem.get(position).getChk());

                    if(mActivity.getClass().equals(FavoritesActivity.class)) {
                        ((FavoritesActivity) mActivity).addChangedItem(map);
                    }
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

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(mOrderItem.get(position).getCd(), mOrderItem.get(position).getChk());
                    if(mActivity.getClass().equals(FavoritesActivity.class)) {
                        ((FavoritesActivity) mActivity).addChangedItem(map);
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
                    String chk = mOrderItem.get(position).getChk();

                    OrderItem orderItem = new OrderItem();
                    orderItem.setCd(cd);
                    orderItem.setName(item);
                    orderItem.setPrice(price);
                    orderItem.setUnit(unit);
                    orderItem.setQty("0");
                    if(isChecked) {
                        orderItem.setChk("1");
                        mOrderItem.get(position).setChk("1");
                    } else {
                        orderItem.setChk("0");
                        mOrderItem.get(position).setChk("0");
                    }

                    if(mActivity.getClass().equals(FavoritesActivity.class)) {
                        ((FavoritesActivity) mActivity).addmOrderItemSelected(orderItem);
                    }

                    Log.e("mOrderItemSelected :: ", String.valueOf(((FavoritesActivity) mActivity).getmOrderItemSelected()));
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
        return new FavoritesAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        FavoritesAdapter.ItemViewHolder holder = (FavoritesAdapter.ItemViewHolder)viewHolder;
        holder.name.setText(mOrderItem.get(position).getName());
        holder.unit.setText(mOrderItem.get(position).getUnit());

        if(mSelectedItems.get(position, false)) {
            holder.cb_item.setChecked(true);
        } else {
            holder.cb_item.setChecked(false);
        }

        if(mOrderItem.get(position).getChk().equals("1")) {
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
