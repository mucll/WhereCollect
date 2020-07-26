package com.gongwu.wherecollect.object;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.base.App;
import com.gongwu.wherecollect.base.BaseMvpActivity;
import com.gongwu.wherecollect.contract.AppConstant;
import com.gongwu.wherecollect.contract.IAddGoodsContract;
import com.gongwu.wherecollect.contract.presenter.AddGoodsPresenter;
import com.gongwu.wherecollect.net.entity.response.BaseBean;
import com.gongwu.wherecollect.net.entity.response.BookBean;
import com.gongwu.wherecollect.net.entity.response.ObjectBean;
import com.gongwu.wherecollect.util.StatusBarUtil;
import com.gongwu.wherecollect.view.PopupAddGoods;
import com.gongwu.wherecollect.view.EditTextWatcher;
import com.gongwu.wherecollect.view.GoodsImageView;
import com.gongwu.wherecollect.view.Loading;
import com.gongwu.wherecollect.view.ObjectInfoLookView;
import com.zsitech.oncon.barcode.core.CaptureActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import razerdp.basepopup.BasePopupWindow;

/**
 * 添加物品界面
 */
public class AddGoodsActivity extends BaseMvpActivity<AddGoodsActivity, AddGoodsPresenter> implements IAddGoodsContract.IAddGoodsView, PopupAddGoods.AddGoodsPopupClickListener {
    private static final String TAG = AddGoodsActivity.class.getSimpleName();
    public static final int BOOK_CODE = 0x112;
    @BindView(R.id.title_tv)
    TextView mTitleView;
    @BindView(R.id.add_img_view)
    GoodsImageView mImageView;
    @BindView(R.id.goodsInfo_view)
    ObjectInfoLookView goodsInfoView;
    //GoodsImageView控件head、name
    @BindView(R.id.head)
    ImageView head;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.add_goods_sort_tv)
    TextView sortNameTv;
    @BindView(R.id.goods_name_et)
    EditText mEditText;
    @BindView(R.id.commit_bt)
    Button mCommitBt;
    @BindView(R.id.select_location_bt)
    Button mSelectLocationBt;

    private Loading loading;

    private File imgFile;
    private File imgOldFile;

    private final String IMG_COLOR_CODE = "0";//默认图片颜色的值
    private ObjectBean objectBean;
    private PopupAddGoods popup;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_goods;
    }

    @Override
    protected void initViews() {
        mTitleView.setText(R.string.add_goods_text);
        head.setImageDrawable(getResources().getDrawable(R.drawable.icon_add_goods));
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.activity_bg));
        initEvent();
        initData();
    }

    @Override
    protected AddGoodsPresenter createPresenter() {
        return AddGoodsPresenter.getInstance();
    }

    /**
     * 初始化数据，判断是否是直接添加物品，还是编辑物品
     */
    private void initData() {
        //初始化物品实体类
        objectBean = new ObjectBean();
        //添加物品的时候，拍照获取照片
        String path = getIntent().getStringExtra("filePath");
        if (!TextUtils.isEmpty(path)) {
            imgFile = new File(path);
            imgOldFile = new File(path);
            mImageView.setHead(IMG_COLOR_CODE, "", imgFile.getAbsolutePath());
            objectBean.setObject_url(imgFile.getAbsolutePath());
            setCommitBtnEnable(true);
        }
        String code = getIntent().getStringExtra("saomaResult");
        if (!TextUtils.isEmpty(code)) {
            if (code.contains("http")) {
                //网络商城
                getPresenter().getTaobaoInfo(App.getUser(mContext).getId(), code);
            } else {
                //书本
                getPresenter().getBookInfo(App.getUser(mContext).getId(), code);
            }
        }
    }


    /**
     * 控件监听
     */
    private void initEvent() {
        mEditText.addTextChangedListener(new EditTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (mEditText.getText().toString().trim().length() > 0) {
                    setCommitBtnEnable(true);
                } else {
                    setCommitBtnEnable(false);
                }
            }
        });
    }

    @OnClick({R.id.back_btn, R.id.add_img_view, R.id.add_goods_sort_tv, R.id.add_other_content_tv, R.id.commit_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.add_img_view:
                if (popup == null) {
                    popup = new PopupAddGoods(mContext);
                    popup.setBackground(Color.TRANSPARENT);
                    popup.setPopupGravity(BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE, Gravity.LEFT | Gravity.TOP);
                    popup.setPopupClickListener(this);
                }
                popup.showPopupWindow(mImageView);
                break;
            case R.id.add_goods_sort_tv:
                SelectSortActivity.start(mContext, objectBean);
                break;
            case R.id.add_other_content_tv:
                AddGoodsPropertyActivity.start(mContext, objectBean, false);
                break;
            case R.id.commit_bt:
                onClickCommit();
                break;

        }
    }

    /**
     * 图书扫描后修改公共属性
     */
    String ISBN = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AppConstant.REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            objectBean = (ObjectBean) data.getSerializableExtra("objectBean");
            if (objectBean.getCategories() != null && objectBean.getCategories().size() > 0) {
                sortNameTv.setText(objectBean.getCategories().get(AppConstant.DEFAULT_INDEX_OF).getName());
                sortNameTv.setTextColor(getResources().getColor(R.color.color333));
            } else {
                sortNameTv.setText(R.string.add_goods_sort);
                sortNameTv.setTextColor(getResources().getColor(R.color.divider));
            }
            goodsInfoView.init(objectBean);
        } else {
            getPresenter().onActivityResult(mContext, requestCode, resultCode, data);
        }
    }


    /**
     * 跳转到NewObjectsAddActivity
     *
     * @param mContext
     */
    public static void start(Context mContext, String filePath, String saomaResult) {
        Intent intent = new Intent(mContext, AddGoodsActivity.class);
        intent.putExtra("filePath", filePath);
        intent.putExtra("saomaResult", saomaResult);
        mContext.startActivity(intent);
    }


    @Override
    public void addObjectsSuccess(List<ObjectBean> data) {
        finish();
    }

    @Override
    public void addMoreGoodsSuccess(List<ObjectBean> data) {

    }

    @Override
    public void getBookInfoSuccess(BookBean book) {
        getPresenter().downloadImg(mContext, book);
    }

    @Override
    public void getTaobaoInfoSuccess(BookBean book) {
        getPresenter().downloadImg(mContext, book);
    }


    @Override
    public void updateBeanWithBook(BookBean book) {
        if (book == null || book.getImageFile() == null) {
            return;
        }
        ISBN = book.getIsbnCode();
        if (book.getImageFile() != null) {
            imgOldFile = book.getImageFile();
            mImageView.setHead(IMG_COLOR_CODE, "", imgOldFile.getAbsolutePath());
            objectBean.setObject_url(imgOldFile.getAbsolutePath());
            setCommitBtnEnable(true);
        }
        if (!TextUtils.isEmpty(book.getTitle())) {
            mEditText.setText(book.getTitle());
        }
        if (!TextUtils.isEmpty(book.getSummary())) {
            objectBean.setDetail(book.getSummary());
        }
        if (!TextUtils.isEmpty(book.getPrice())) {
            objectBean.setPrice(book.getPrice());
        }
        if (book.getCategory() != null) {
            List<BaseBean> temp = new ArrayList<>();
            temp.add(book.getCategory());
            objectBean.setCategories(temp);
        }
        goodsInfoView.setVisibility(View.VISIBLE);
        goodsInfoView.init(objectBean);
    }

    @Override
    public void getCamareImg(File file) {
        imgOldFile = file;
    }

    @Override
    public void getSelectPhotoImg(File file) {
        imgOldFile = file;
        getPresenter().startCropBitmap(mContext, file);
    }

    @Override
    public void getCropBitmap(File file) {
        imgFile = file;
        mImageView.setHead(IMG_COLOR_CODE, "", imgFile.getAbsolutePath());
        objectBean.setObject_url(imgFile.getAbsolutePath());
        setCommitBtnEnable(true);
    }

    @Override
    public void onUpLoadSuccess(String path) {
        objectBean.setObject_url(path);
        getPresenter().addObjects(mContext, objectBean, mEditText.getText().toString(), ISBN);
    }

    @Override
    public void showProgressDialog() {
        loading = Loading.show(null, mContext,"");
    }

    @Override
    public void hideProgressDialog() {
        if (loading != null) {
            loading.dismiss();
        }
    }

    @Override
    public void onError(String result) {
        if (!TextUtils.isEmpty(getIntent().getStringExtra("saomaResult"))) {
            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击确认
     */
    private void onClickCommit() {
        //如果图片没有地址，则传一个颜色服务牌
        if (TextUtils.isEmpty(objectBean.getObject_url())) {
            objectBean.setObject_url("#E66868");
        }
        if (objectBean.getObject_url().contains("#")) {
            //调用接口
            getPresenter().addObjects(mContext, objectBean, mEditText.getText().toString(), ISBN);
        } else if (objectBean.getObject_url().contains("http")) {
            getPresenter().addObjects(mContext, objectBean, mEditText.getText().toString(), ISBN);
        } else {
            //图片有地址 直接上传
            getPresenter().uploadImg(mContext, objectBean.getObjectFiles());
        }
    }

    private void setCommitBtnEnable(boolean btnEnable) {
        mCommitBt.setEnabled(btnEnable);
        mSelectLocationBt.setEnabled(btnEnable);
    }

    @Override
    public void onCameraClick() {
        getPresenter().openCamare(mContext);
    }

    @Override
    public void onPhotoClick() {
        getPresenter().startImageGridActivity(mContext);
    }

    @Override
    public void onScanClick() {
        startActivityForResult(new Intent(mContext, CaptureActivity.class), BOOK_CODE);
    }
}