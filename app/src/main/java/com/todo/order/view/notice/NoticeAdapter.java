package com.todo.order.view.notice;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.ortiz.touchview.TouchImageView;
import com.todo.order.R;
import com.todo.order.util.Etc;
import com.bumptech.glide.Glide;
import com.todo.order.view.common.FullScreenActivity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoticeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<NoticeItem> mNoticeItem;
    private Context mContext;
    private Etc etc = new Etc();
    private String selectedItem;
    private String seq;
    boolean isImageFitToScreen;

    public NoticeAdapter(ArrayList<NoticeItem> mNoticeItem, Context mContext) {
        this.mNoticeItem = mNoticeItem;
        this.mContext = mContext;
        selectedItem = "";
    }

    // viewHolder Class
    class NoticeViewHolder extends RecyclerView.ViewHolder {
        private TextView title, date, content, tv_notice_category;
        private ImageView ivNoticeArrow, ivNew, ivNoticeImage;
        private TouchImageView tivNoticeImage;
        private PhotoView photoView;
        ConstraintLayout expandableLayout;

        public NoticeViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_board_title);
            date = itemView.findViewById(R.id.tv_board_date);
            content = itemView.findViewById(R.id.tv_board_content);
            ivNew = itemView.findViewById(R.id.iv_new);
            ivNoticeArrow = itemView.findViewById(R.id.iv_notice_arrow);
            //ivNoticeImage = itemView.findViewById(R.id.iv_notice_image);
            //tivNoticeImage = (TouchImageView) itemView.findViewById(R.id.tiv_notice_image);
            photoView = (PhotoView) itemView.findViewById(R.id.photo_view);
            tv_notice_category = itemView.findViewById(R.id.tv_notice_category);

            expandableLayout = itemView.findViewById(R.id.expandableLayout);

            //title.setSingleLine(true); //한줄로 나오게 하기.
            //title.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
            //title.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기

            ivNoticeArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoticeItem noticeItem = mNoticeItem.get(getAdapterPosition());
                    noticeItem.setExpanded(!noticeItem.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoticeItem noticeItem = mNoticeItem.get(getAdapterPosition());
                    noticeItem.setExpanded(!noticeItem.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), FullScreenActivity.class);
                    intent.putExtra("image_url", mNoticeItem.get(getAdapterPosition()).getUrl());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });

            /*
            ivNoticeImage.setAdjustViewBounds(true);
            ivNoticeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoticeItem noticeItem = mNoticeItem.get(getAdapterPosition());
                }
            });
            */

            //Glide.with(mContext).load("http://www.mangomap.co.kr/upload/mobile/20201110/20201110175108").into(ivNoticeImage);
            //ivNoticeImage.setVisibility(View.VISIBLE);

            //tivNoticeImage.getCurrentZoom();

            Glide.with(mContext).load("http://www.mangomap.co.kr/upload/mobile/20201110/20201110175108").into(photoView);
            photoView.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_board, viewGroup, false);
        return new NoticeViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        NoticeViewHolder holder = (NoticeViewHolder) viewHolder;

        selectedItem = mNoticeItem.get(position).getPosition();

        holder.title.setText(mNoticeItem.get(position).getTitle());
        holder.date.setText(etc.dateFormat(mNoticeItem.get(position).getDate(), "yyyyMMdd", "yyyy-MM-dd"));
        holder.content.setText(mNoticeItem.get(position).getContent());

//        holder.title.setSingleLine(true); //한줄로 나오게 하기.
//        holder.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
//        holder.title.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date noticeDate = new Date();
        try {
            noticeDate = sdf.parse(mNoticeItem.get(position).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        Date currentDate = new Date();
//
//        long diff = currentDate.getTime() - noticeDate.getTime();
//        long seconds = diff / 1000;
//        long minutes = seconds / 60;
//        long hours = minutes / 60;
//        long days = hours / 24;
//
//        if (days < 7) {
//            holder.ivNew.setVisibility(View.VISIBLE);
//        } else {
//            holder.ivNew.setVisibility(View.GONE);
//        }

        if (mNoticeItem.get(position).getN().equals("1")) {
            holder.ivNew.setVisibility(View.VISIBLE);
        } else {
            holder.ivNew.setVisibility(View.GONE);
        }

        if (mNoticeItem.get(position).getGbn().equals("0")) {
            holder.tv_notice_category.setText("전체");
        } else if (mNoticeItem.get(position).getGbn().equals("1")) {
            holder.tv_notice_category.setText("브랜드");
        }

        //Glide.with(mContext).load(mNoticeItem.get(position).getUrl()).into(holder.ivNoticeImage);
        //holder.ivNoticeImage.setVisibility(View.VISIBLE);

        Glide.with(mContext).load(mNoticeItem.get(position).getUrl()).into(holder.photoView);
        holder.photoView.setVisibility(View.VISIBLE);

        Log.e("selectedItem", selectedItem);
        Log.e("seq", mNoticeItem.get(position).getSeq());

        boolean isExpanded = mNoticeItem.get(position).isExpanded();
        if (String.valueOf(position).equals(mNoticeItem.get(position).getPosition())) {
            isExpanded = true;
        }
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mNoticeItem.size();
    }
}
