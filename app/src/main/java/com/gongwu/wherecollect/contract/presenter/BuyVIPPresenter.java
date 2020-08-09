package com.gongwu.wherecollect.contract.presenter;

import com.gongwu.wherecollect.base.BasePresenter;
import com.gongwu.wherecollect.contract.IBuyVIPContract;
import com.gongwu.wherecollect.contract.model.BuyVIPModel;
import com.gongwu.wherecollect.interfacedef.RequestCallback;
import com.gongwu.wherecollect.net.entity.response.BuyVIPResultBean;
import com.gongwu.wherecollect.net.entity.response.VIPBean;

public class BuyVIPPresenter extends BasePresenter<IBuyVIPContract.IBuyVIPView> implements IBuyVIPContract.IBuyVIPPresenter {
    private static final String TAG = "BuyVIPPresenter";

    private IBuyVIPContract.IBuyVIPModel mModel;

    private BuyVIPPresenter() {
        mModel = new BuyVIPModel();
    }

    public static BuyVIPPresenter getInstance() {
        return BuyVIPPresenter.Inner.instance;
    }

    private static class Inner {
        private static final BuyVIPPresenter instance = new BuyVIPPresenter();
    }


    @Override
    public void getVIPPrice(String uid) {
        if (getUIView() != null) {
            getUIView().showProgressDialog();
        }
        mModel.getVIPPrice(uid, new RequestCallback<VIPBean>() {
            @Override
            public void onSuccess(VIPBean data) {
                if (getUIView() != null) {
                    getUIView().hideProgressDialog();
                    getUIView().getVIPPriceSuccess(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                if (getUIView() != null) {
                    getUIView().hideProgressDialog();
                    getUIView().onError(msg);
                }
            }
        });
    }

    @Override
    public void buyVipWXOrAli(String uid, int price, String type, String couponId) {
        if (getUIView() != null) {
            getUIView().showProgressDialog();
        }
        mModel.buyVipWXOrAli(uid, price, type, couponId, new RequestCallback<BuyVIPResultBean>() {
            @Override
            public void onSuccess(BuyVIPResultBean data) {
                if (getUIView() != null) {
                    getUIView().hideProgressDialog();
                    getUIView().buyVipWXOrAliSuccess(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                if (getUIView() != null) {
                    getUIView().hideProgressDialog();
                    getUIView().onError(msg);
                }
            }
        });
    }
}
