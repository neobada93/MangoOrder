package com.todo.order.view.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.order.R;
import com.todo.order.util.Etc;
import com.todo.order.view.common.FullScreenActivity;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<OrderItem> mOrderItem;
    private Context mContext;
    private Activity mActivity;
    private Etc etc;

    public OrderAdapter(Context mContext, ArrayList<OrderItem> mItem, Activity activity) {
        this.mContext = mContext;
        this.mOrderItem = mItem;
        this.mActivity = activity;
        etc = new Etc();
    }

    // viewHolder Class
    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView name, unit, price, qty, cd, tv_order_name;
        private TextView btnOrderDeleteItem;
        private EditText etOrderAmount;
        public OrderViewHolder(View itemView) {
            super(itemView);
            btnOrderDeleteItem = itemView.findViewById(R.id.ib_order_delete_item);
            etOrderAmount = itemView.findViewById(R.id.et_order_amount);
            name = itemView.findViewById(R.id.tv_order_name);
            unit = itemView.findViewById(R.id.tv_order_unit);
            price = itemView.findViewById(R.id.tv_order_price);
            qty = itemView.findViewById(R.id.et_order_amount);
            tv_order_name = itemView.findViewById(R.id.tv_order_name);

            tv_order_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), FullScreenActivity.class);
                    intent.putExtra("image_url", mOrderItem.get(getAdapterPosition()).getUrl());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });

            qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    LinearLayout ly_calendar = (LinearLayout) mActivity.findViewById(R.id.ly_calendar);
                    LinearLayout lyOrder = (LinearLayout) mActivity.findViewById(R.id.ly_order);
                    if(hasFocus) {
                        if(ly_calendar.getVisibility() == View.VISIBLE) {
                            ly_calendar.setVisibility(View.GONE);
                            lyOrder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 340f));
                        }
                    }
                }
            });

            btnOrderDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOrderItem.remove(getAdapterPosition());
                    btnOrderDeleteItem.setVisibility(View.GONE);
                    notifyDataSetChanged();
                }
            });

            etOrderAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int amt = 0;
                    if(!etOrderAmount.getText().toString().isEmpty()) {
                        amt = Integer.parseInt(etOrderAmount.getText().toString());
                    }
                    mOrderItem.get(getAdapterPosition()).setQty(String.valueOf(amt));
                    int sum = 0;
                    for (int i = 0; i < getItemCount(); i++) {
                        if(!mOrderItem.get(i).getQty().isEmpty() && !mOrderItem.get(i).getPrice().isEmpty()) {
                            sum += Integer.parseInt(mOrderItem.get(i).getQty()) * Integer.parseInt(mOrderItem.get(i).getPrice().replace(",", ""));
                        }
                    }
                    TextView tvOrderTotalAmount = (TextView) mActivity.findViewById(R.id.tv_order_total_amount);
                    tvOrderTotalAmount.setText(etc.comma(String.valueOf(sum)));
                }
            });
        }
    }

    // 1. view 만든다.
    // 2. viewHolder 만든다.
    // 3. viewHolder return.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_order, viewGroup, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OrderViewHolder holder = (OrderViewHolder)viewHolder;
        holder.name.setText(mOrderItem.get(position).getName());
        holder.unit.setText(mOrderItem.get(position).getUnit());
        holder.qty.setText(mOrderItem.get(position).getQty());
        holder.price.setText(etc.comma(mOrderItem.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return mOrderItem.size();
    }

    public void appendData(ArrayList<OrderItem> mItem) {
        this.mOrderItem.addAll(mItem);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mOrderItem.clear();
    }

    public void clearInputs() {
        for(OrderItem orderItem: mOrderItem) {
            orderItem.setQty("0");
        }
        notifyDataSetChanged();
    }
}
