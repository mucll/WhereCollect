package com.gongwu.wherecollect.adapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.net.entity.response.ChannelBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuishuListAdapter extends BaseAdapter {
    private Context context;
    private List<ChannelBean> mList;

    public GuishuListAdapter(Context context, List<ChannelBean> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public List<ChannelBean> getmList() {
        return mList;
    }

    public void setmList(List<ChannelBean> mList) {
        this.mList = mList;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderView holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_channel_list, null);
            holder = new HolderView(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        ChannelBean item = mList.get(position);
        holder.nameTv.setText(item.getName());
        holder.guishuTv.setVisibility(View.GONE);
        if (item.getTag_count() == 0) {
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    public void notifyDate(List<ChannelBean> data) {
        this.mList = data;
        notifyDataSetChanged();
    }

    static class HolderView {
        @BindView(R.id.name_tv)
        TextView nameTv;
        @BindView(R.id.guishu_tv)
        TextView guishuTv;
        @BindView(R.id.imageview)
        ImageView imageView;

        HolderView(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
