package com.todo.order.view.talk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.order.R;
import com.todo.order.view.common.ImageActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class TalkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<TalkItem> mItem;
    private static Typeface typeface;
    public TalkAdapter(Context mContext, ArrayList<TalkItem> mItem) {
        this.mContext = mContext;
        this.mItem = mItem;
        if(typeface == null) {
            //typeface = Typeface.createFromAsset(mContext.getAssets(), "Iropkebatangm.ttf");
        }
    }

    public static class TalkAdapterViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout position1,position2,main_layout;
        TextView answerTv, questionTv, regdt,regdt2;
        ImageView answerIv, questionIv, profile;

        public TalkAdapterViewHolder(@NonNull View view) {
            super(view);
            position1 = view.findViewById(R.id.position1);
            position2 = view.findViewById(R.id.position2);
            answerIv = view.findViewById(R.id.answer_iv);
            answerTv = view.findViewById(R.id.answer_tv);
            regdt = view.findViewById(R.id.regdt);
            profile = view.findViewById(R.id.profile);
            questionIv = view.findViewById(R.id.question_iv);
            questionTv = view.findViewById(R.id.question_tv);
            regdt2 = view.findViewById(R.id.regdt2);
            main_layout = view.findViewById(R.id.main_layout);


        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_talk,parent,false);
        return new TalkAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TalkAdapterViewHolder holder1 = (TalkAdapterViewHolder)holder;

        holder1.questionTv.setIncludeFontPadding(false);
        holder1.answerTv.setIncludeFontPadding(false);

        holder1.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TalkActivity)mContext).click();
            }
        });

        final int pos = position;
        holder1.answerIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putExtra("path", mItem.get(pos).getUrl());
                intent.putExtra("download",mItem.get(pos).getUrl());
                intent.putExtra("fileName",mItem.get(pos).getFileName());
                mContext.startActivity(intent);
            }
        });
        holder1.questionIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putExtra("path", mItem.get(pos).getUrl());
                intent.putExtra("download",mItem.get(pos).getUrl());
                intent.putExtra("fileName",mItem.get(pos).getFileName());
                mContext.startActivity(intent);
            }
        });

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int size = Math.round(15 * dm.density);

        if(mItem.get(position).getPosition()==1) {
            holder1.position1.setVisibility(View.GONE);
            holder1.position2.setVisibility(View.VISIBLE);
            holder1.questionTv.setText(mItem.get(position).getContent());
            holder1.regdt.setText(mItem.get(position).getRegdt());
            if(mItem.get(position).getUrl() == null) {
                holder1.questionIv.setVisibility(View.GONE);
            } else {
                if(mItem.get(position).getUrl().isEmpty()) {
                    holder1.questionIv.setVisibility(View.GONE);
                } else {
                    Glide.with(mContext).load(mItem.get(position).getUrl()).into(holder1.questionIv);
                    holder1.questionIv.setVisibility(View.VISIBLE);
                }
            }
            if(mItem.get(position).getContent() == null) {
                holder1.questionTv.setVisibility(View.GONE);
            } else {
                if(mItem.get(position).getContent().trim().isEmpty()) {
                    holder1.questionTv.setVisibility(View.GONE);
                } else {
                    holder1.questionTv.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder1.regdt2.setText(mItem.get(position).getRegdt());
            holder1.position2.setVisibility(View.GONE);
            holder1.position1.setVisibility(View.VISIBLE);
            holder1.answerTv.setText(mItem.get(position).getContent());
            Glide.with(mContext).load(R.drawable.ic_btntalksend).apply(RequestOptions.circleCropTransform()).into(holder1.profile);
            if(mItem.get(position).getUrl() == null) {
                holder1.answerIv.setVisibility(View.GONE);
            } else {
                if(mItem.get(position).getUrl().isEmpty()) {
                    holder1.answerIv.setVisibility(View.GONE);
                } else {
                    Glide.with(mContext).load(mItem.get(position).getUrl()).into(holder1.answerIv);
                    holder1.answerIv.setVisibility(View.VISIBLE);
                }
            }

            if(mItem.get(position).getContent() == null) {
                holder1.answerTv.setVisibility(View.GONE);
            } else {
                if(mItem.get(position).getContent().trim().isEmpty()) {
                    holder1.answerTv.setVisibility(View.GONE);
                } else {
                    holder1.answerTv.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }
}
