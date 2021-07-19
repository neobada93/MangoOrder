package com.todo.order.view.selfcheck;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.order.R;
import com.todo.order.util.Etc;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;

public class SelfCheckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SelfCheckItem> mSelfCheckItems;
    private Context mContext;
    private Activity mActivity;
    private Etc etc;

    public SelfCheckAdapter(ArrayList<SelfCheckItem> mSelfCheckItems, Context mContext, Activity mActivity) {
        this.mSelfCheckItems = mSelfCheckItems;
        this.mContext = mContext;
        this.mActivity = mActivity;
        etc = new Etc();
    }

    public class SelfCheckViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private SwitchButton sbSelfCheck;

        public SelfCheckViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            sbSelfCheck = itemView.findViewById(R.id.sb_self_check);

            sbSelfCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mSelfCheckItems.get(getAdapterPosition()).setChk("1");
                    } else {
                        mSelfCheckItems.get(getAdapterPosition()).setChk("0");
                    }

                    TextView tv_self_check_count = mActivity.findViewById(R.id.tv_self_check_count);
                    tv_self_check_count.setText("점검목록(" + String.valueOf(checkedItemCount()) + "/" + String.valueOf(mSelfCheckItems.size()) + ")");

                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_self_check, parent, false);
        SelfCheckViewHolder selfCheckViewHolder = new SelfCheckViewHolder(v);
        return selfCheckViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        SelfCheckViewHolder holder = (SelfCheckViewHolder)viewHolder;
        holder.tvItemName.setText(mSelfCheckItems.get(position).getChkItem());
        if (mSelfCheckItems.get(position).getChk().equals("0")) {
            holder.sbSelfCheck.setChecked(false);
        } else {
            holder.sbSelfCheck.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return mSelfCheckItems.size();
    }

    public int checkedItemCount() {
        int count = 0;
        for (SelfCheckItem selfCheckItem : mSelfCheckItems) {
            if(selfCheckItem.getChk().equals("1")) {
                count++;
            }
        }
        return count;
    }
}
