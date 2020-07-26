package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.contract.AppConstant;
import com.gongwu.wherecollect.net.entity.response.RemindBean;
import com.gongwu.wherecollect.util.DateUtil;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.view.SwipeMenuLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RemindListAdapter extends RecyclerView.Adapter<RemindListAdapter.ViewHolder> {

    private Context mContext;
    private List<RemindBean> mData;

    public RemindListAdapter(Context mContext, List<RemindBean> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_remind_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.swipeMenuLayout.setIos(false);//设置是否开启IOS阻塞式交互
        holder.swipeMenuLayout.setLeftSwipe(true);//true往左滑动,false为往右侧滑动
        holder.swipeMenuLayout.setSwipeEnable(true);//设置侧滑功能开关
        RemindBean remindBean = mData.get(i);
        //是否含有物品信息
        if (TextUtils.isEmpty(remindBean.getAssociated_object_url())) {
            holder.imgIv.setVisibility(View.GONE);
        } else {
            //图片
            holder.imgIv.setImageDrawable(null);
            holder.imgIv.setVisibility(View.VISIBLE);
            if (remindBean.getAssociated_object_url().contains("http")) {
                holder.imgIv.setImageDrawable(null);
                holder.imgIv.setBackgroundResource(0);
                ImageLoader.load(mContext, holder.imgIv, remindBean.getAssociated_object_url());
                holder.imgTv.setVisibility(View.GONE);
            } else if (remindBean.getAssociated_object_url().contains("#")) {
                holder.imgIv.setImageDrawable(null);
                holder.imgIv.setBackgroundResource(0);
                holder.imgIv.setBackgroundColor(Color.parseColor(remindBean.getAssociated_object_url()));
                holder.imgTv.setVisibility(View.VISIBLE);
                holder.imgTv.setText(remindBean.getTitle());
            } else {
                holder.imgIv.setVisibility(View.GONE);
                holder.imgTv.setVisibility(View.GONE);
            }
        }
        //超时字体颜色
        if (remindBean.getDone() != AppConstant.REMIND_FINISH_CODE && remindBean.isTimeout()) {
            holder.remindNameTv.setTextColor(mContext.getResources().getColor(R.color.color999));
            holder.remindTimeTv.setTextColor(mContext.getResources().getColor(R.color.remind_time_out_color));
        } else {
            holder.remindNameTv.setTextColor(mContext.getResources().getColor(R.color.act_relation_goods_text_color));
            holder.remindTimeTv.setTextColor(mContext.getResources().getColor(R.color.color999));
        }
        //优先tab
        if (remindBean.getFirst() == AppConstant.REMIND_FINISH_CODE && !remindBean.isTimeout()) {
            holder.firstLabelTv.setVisibility(View.VISIBLE);
        } else {
            holder.firstLabelTv.setVisibility(View.GONE);
        }
        holder.remindNameTv.setText("");
        holder.remindTimeTv.setText("");
        holder.remindDescriptionTv.setText("");
        //标题
        holder.remindNameTv.setText(remindBean.getTitle());
        //时间
        if (remindBean.getTips_time() != 0) {
            holder.remindTimeTv.setText(DateUtil.dateToString(remindBean.getTips_time(), DateUtil.DatePattern.ONLY_MINUTE));
        }
        //备注
        holder.remindDescriptionTv.setText(TextUtils.isEmpty(remindBean.getDescription()) ? "" : remindBean.getDescription());
        if (remindBean.getDone() == 0) {
            holder.editFinishedTv.setVisibility(View.VISIBLE);
        } else {
            holder.editFinishedTv.setVisibility(View.GONE);
        }
        holder.split_view.setVisibility((i == mData.size() - 1) ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.remind_content_item)
        SwipeMenuLayout swipeMenuLayout;
        @BindView(R.id.item_remind_goods_iv)
        ImageView imgIv;
        @BindView(R.id.no_url_img_tv)
        TextView imgTv;
        @BindView(R.id.item_remind_name_tv)
        TextView remindNameTv;
        @BindView(R.id.item_remind_time_tv)
        TextView remindTimeTv;
        @BindView(R.id.item_remind_description_tv)
        TextView remindDescriptionTv;
        @BindView(R.id.remind_first_label_tv)
        TextView firstLabelTv;
        @BindView(R.id.remind_edit_finished_tv)
        TextView editFinishedTv;
        @BindView(R.id.remind_delete_tv)
        TextView deleteTv;
        @BindView(R.id.remind_item_layout)
        View remindItemView;
        @BindView(R.id.split_view)
        View split_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            remindItemView.setOnClickListener(this);
            deleteTv.setOnClickListener(this);
            editFinishedTv.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                switch (view.getId()) {
                    case R.id.remind_item_layout:
                        onItemClickListener.onItemClick(getLayoutPosition(), view);
                        break;
                    case R.id.remind_delete_tv:
                        if (swipeMenuLayout != null) swipeMenuLayout.smoothClose();
                        onItemClickListener.onItemDeleteClick(getLayoutPosition(), view);
                        break;
                    case R.id.remind_edit_finished_tv:
                        if (swipeMenuLayout != null) swipeMenuLayout.smoothClose();
                        onItemClickListener.onItemEditFinishedClick(getLayoutPosition(), view);
                        break;
                }
            }
        }
    }

    public OnRemindItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnRemindItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
