package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.net.entity.response.ObjectBean;
import com.gongwu.wherecollect.util.ImageLoader;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StackAdapter extends BaseAdapter {

    private Context context;
    private List<ObjectBean> mlist;
    private int cardWidth;
    private int cardHeight;

    public StackAdapter(Context mContext, List<ObjectBean> list) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        float density = dm.density;
        cardWidth = (int) (dm.widthPixels - (6 * 12 * density));
        cardHeight = (int) (dm.heightPixels - (360 * density));
        this.context = mContext;
        this.mlist = list;
    }

    public void remove(int index) {
        if (index > -1 && index < mlist.size()) {
            mlist.remove(index);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int i) {
        return mlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_stack_adapter_layout, viewGroup, false);
            view.getLayoutParams().width = cardWidth;
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final ObjectBean bean = mlist.get(i);
        if (bean.getObject_url().contains("http")) {
            ImageLoader.placeholderLoad(context, holder.image, bean.getObject_url(), R.drawable.ic_img_error);
        } else if (!bean.getObject_url().contains("#")) {
            ImageLoader.loadFromFile(context, new File(bean.getObject_url()), holder.image);
        }
        holder.submitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(true, holder.goodsNameEt.getText().toString().trim(), bean.getObject_url());
            }
        });
        holder.cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(false, "", bean.getObject_url());
            }
        });
        holder.code_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCamera();
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(i);
            }
        });
        return view;
    }

    public void selectItem(boolean select, String name, String url) {
    }

    public void onClickCamera() {}

    public void selectImage(int position){}

    public class ViewHolder {
        @BindView(R.id.add_goods_iv)
        ImageView image;
        @BindView(R.id.submit_tv)
        TextView submitTv;
        @BindView(R.id.cancel_tv)
        TextView cancelTv;
        @BindView(R.id.goods_name_et)
        EditText goodsNameEt;
        @BindView(R.id.code_layout)
        View code_layout;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
