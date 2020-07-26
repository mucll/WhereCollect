package com.gongwu.wherecollect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.net.entity.response.FamilyBean;
import com.gongwu.wherecollect.net.entity.response.ObjectBean;
import com.gongwu.wherecollect.object.AddMoreGoodsActivity;
import com.gongwu.wherecollect.view.GoodsImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Function:主页物品查看列表
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class EditMoveFurnitureAdapter extends RecyclerView.Adapter<EditMoveFurnitureAdapter.CustomViewHolder> {
    private Context mContext;
    private List<FamilyBean> mlist;
    private boolean isSelectMode = false;

    public EditMoveFurnitureAdapter(Context context, List<FamilyBean> list) {
        this.mContext = context;
        this.mlist = list;
    }

    public void setSelectMode(boolean isSelectMode) {
        this.isSelectMode = isSelectMode;
    }

    public boolean getSelectMode() {
        return isSelectMode;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_edit_move_furniture_view, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int i) {
        FamilyBean bean = mlist.get(i);
        holder.nameTv.setText(bean.getName());
        holder.mImageView.setVisibility(isSelectMode ? View.GONE : View.VISIBLE);
        holder.mCheckBox.setVisibility(isSelectMode ? View.VISIBLE : View.GONE);
        holder.mCheckBox.setChecked(bean.isSelect());
    }

    @Override
    public int getItemCount() {
        return mlist == null ? 0 : mlist.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.edit_move_name)
        TextView nameTv;
        @BindView(R.id.edit_move_iv)
        ImageView mImageView;
        @BindView(R.id.edit_move_check_box)
        CheckBox mCheckBox;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setTag(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    public MyOnItemClickListener onItemClickListener;

    public void setOnItemClickListener(MyOnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
