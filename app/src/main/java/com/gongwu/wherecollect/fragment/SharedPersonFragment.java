package com.gongwu.wherecollect.fragment;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.SharePersonDetailsActivity;
import com.gongwu.wherecollect.adapter.SharePersonListAdapter;
import com.gongwu.wherecollect.base.App;
import com.gongwu.wherecollect.base.BaseFragment;
import com.gongwu.wherecollect.contract.AppConstant;
import com.gongwu.wherecollect.contract.IShareContract;
import com.gongwu.wherecollect.contract.presenter.SharePresenter;
import com.gongwu.wherecollect.net.entity.response.RequestSuccessBean;
import com.gongwu.wherecollect.net.entity.response.SharedPersonBean;
import com.gongwu.wherecollect.net.entity.response.SharedLocationBean;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 共享人fragment
 */
public class SharedPersonFragment extends BaseFragment<SharePresenter> implements IShareContract.IShareView {

    @BindView(R.id.share_person_refresh_layout)
    RefreshLayout mRefreshLayout;
    @BindView(R.id.share_person_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.share_person_empty_view)
    View mEmptyView;

    /**
     * 是否初始化布局
     */
    private boolean isViewInitiated;
    /**
     * 当前界面是否可见
     */
    private boolean isVisibleToUser;
    private boolean initData;
    private boolean init;

    private List<SharedPersonBean> mlist = new ArrayList<>();
    private SharePersonListAdapter mAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        isCanLoadData();
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_person, container, false);
    }

    @Override
    public SharePresenter initPresenter() {
        return SharePresenter.getInstance();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!init) {
            initViews();
            init = true;
        }
        EventBus.getDefault().register(this);
    }

    private void initViews() {
        mAdapter = new SharePersonListAdapter(mContext, mlist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new SharePersonListAdapter.OnItemClickListener() {
            @Override
            public void closeClick(int position, View v) {
                DialogUtil.show("", "确定断开与【" + mlist.get(position).getNickname() + "】的全部共享?", "确定", "取消", getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getPresenter().closeShareUser(App.getUser(mContext).getId(), null
                                , mlist.get(position).getId(), 0);
                    }
                }, null);
            }

            @Override
            public void onItemsClick(int position, View v) {
                SharePersonDetailsActivity.start(mContext, mlist.get(position));
            }
        });
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout mRefreshLayout) {
                getPresenter().getSharedUsersList(App.getUser(mContext).getId());
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 是否对用户可见
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            isCanLoadData();
        }
    }

    /*
     * 执行数据加载： 条件是view初始化完成并且对用户可见
     */
    private void isCanLoadData() {
        if (isViewInitiated && isVisibleToUser && !initData) {
            mRefreshLayout.autoRefresh();
            // 加载过数据后，将isViewInitiated和isVisibleToUser设置成false，防止重复加载数据
            isViewInitiated = false;
            isVisibleToUser = false;
            initData = true;
        }
    }

    @Override
    public void getSharedUsersListSuccess(List<SharedPersonBean> data) {
        refreshLayoutFinished();
        mlist.clear();
        if (data != null && data.size() > 0) {
            mlist.addAll(data);
        }
        mEmptyView.setVisibility(mlist.size() > 0 ? View.GONE : View.VISIBLE);
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.UpdateShareMsg msg) {
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
    }

    @Override
    public void getSharedLocationsSuccess(List<SharedLocationBean> data) {

    }

    @Override
    public void closeShareUserSuccess(RequestSuccessBean data) {
        if (data.getOk() == AppConstant.REQUEST_SUCCESS) {
            mRefreshLayout.autoRefresh();
            EventBus.getDefault().postSticky(new EventBusMsg.RefreshFragment());
        }
    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void onError(String result) {
        refreshLayoutFinished();
        Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
    }

    private void refreshLayoutFinished() {
        mRefreshLayout.finishRefresh(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
