package com.todo.order.util;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.todo.order.R;

import java.util.List;

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.MyViewHolder> {

    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);

    private List<MyTab> mTab;
    private int recyclecount = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private View v_underline;

        public MyViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tab_name);
            v_underline = (View) view.findViewById(R.id.tab_underline);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(mSelectedItems.get(position, false)) {
                        mSelectedItems.put(position, false);
                        v_underline.setVisibility(View.GONE);
                    } else {
                        mSelectedItems.put(position, true);
                        v_underline.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public TabAdapter(List<MyTab> mTab) { this.mTab = mTab; }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_tab_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyTab tab = mTab.get(position);
        holder.tv_name.setText(tab.getName());

        if (mSelectedItems.get(position, false)) {
            holder.v_underline.setVisibility(View.VISIBLE);
        } else {
            holder.v_underline.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mTab.size();
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        recyclecount++;
    }

    public void clearSelectedItem() {
        int position;

        for (int i = 0; i < mSelectedItems.size(); i++) {
            position = mSelectedItems.keyAt(i);
            mSelectedItems.put(position, false);
            notifyItemChanged(position);
        }

        mSelectedItems.clear();
    }
}
