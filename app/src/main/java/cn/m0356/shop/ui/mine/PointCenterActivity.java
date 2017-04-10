package cn.m0356.shop.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.adapter.ProdgoodsListViewAdapter;
import cn.m0356.shop.adapter.RptVoucherListViewAdapter;
import cn.m0356.shop.bean.Mine;
import cn.m0356.shop.bean.ProdgoodsBean;
import cn.m0356.shop.bean.VoucherRedpacketBean;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.MyListView;
import cn.m0356.shop.mvp.presenter.PointCenterPresenter;
import cn.m0356.shop.mvp.view.IPointCenterView;

/**
 * 积分中心
 * Created by yml on 2017/3/30.
 */

public class PointCenterActivity extends BaseActivity implements View.OnClickListener, IPointCenterView {

    private MyShopApplication myApplication;
    private ImageView memberAvatarImg, hotGiftImg;
    private TextView memberNameTxt, memberLevelTxt, memberExpNextTxt, memberExpValueTxt,
            memberExpRuleTxt, memberExpLogTxt, memberPointValueTxt, memberPointRuleTxt, memberPointLogTxt,
            hotVoucherMoreTxt, hotVoucherAmountTxt, hotVoucherTitleTxt, hotVoucherLevelTxt, hotVoucherLimitTxt, hotVoucherPointTxt, hotVoucherTimeTxt,
            hotGiftMoreTxt, hotGiftTitleTxt, hotGiftLevelTxt, hotGiftPriceTxt, hotGiftPointTxt, hotGiftLimitTxt,
            hotRedpacketMoreTxt, hotRedpacketAmountTxt, hotRedpacketTitleTxt, hotRedpacketLevelTxt, hotRedpacketLimitTxt, hotRedpacketPointTxt, hotRedpacketTimeTxt;
    private ProgressBar memberExpPrg;
    private RelativeLayout memberGiftRlt, memberVoucherRlt, memberRedpacketRlt;
    private Button hotGiftBtn, hotVoucherBtn, hotRedpacketBtn;
    private MyListView hotGiftList, hotVoucherList, hotRedpacketList;

    private RptVoucherListViewAdapter hotVoucherAdapter, hotRedpacketAdapter;
    private ProdgoodsListViewAdapter hotGiftAdapter;
    private PointCenterPresenter mPointCenterPresenter;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getRoundedBitmapDisplayImageOptions(100);
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_center);
        myApplication = (MyShopApplication) getApplicationContext();
        mPointCenterPresenter = new PointCenterPresenter(this);
        mPointCenterPresenter.attachView(this);
        initView();
        mPointCenterPresenter.loadMemberInfo();
        mPointCenterPresenter.loadPoint(); // 读取积分
        mPointCenterPresenter.loadExp();
        mPointCenterPresenter.loadHotVoucherList(true); // 加载热门代金券列表
        mPointCenterPresenter.loadHotRptList(true); // 加载热门红包列表
        mPointCenterPresenter.loadHotProdgoodsList(true); // 加载热门红包列表
    }

    @Override
    protected void onDestroy() {
        mPointCenterPresenter.detachView();
        super.onDestroy();
    }

    private void initView() {
        setCommonHeader("积分中心");
        memberAvatarImg = (ImageView) findViewById(R.id.iv_member_avatar);
        memberNameTxt = (TextView) findViewById(R.id.tv_member_name);
        memberLevelTxt = (TextView) findViewById(R.id.tv_member_level);
        memberExpNextTxt = (TextView) findViewById(R.id.tv_member_exp_next);
        memberExpValueTxt = (TextView) findViewById(R.id.tv_member_exp_value);
        memberExpPrg = (ProgressBar) findViewById(R.id.pb_member_exp);
        memberExpRuleTxt = (TextView) findViewById(R.id.tv_member_exp_rule);
        memberExpLogTxt = (TextView) findViewById(R.id.tv_member_exp_log);
        memberPointValueTxt = (TextView) findViewById(R.id.tv_member_point_value);
        memberPointRuleTxt = (TextView) findViewById(R.id.tv_member_point_rule);
        memberPointLogTxt = (TextView) findViewById(R.id.tv_member_point_log);
        memberGiftRlt = (RelativeLayout) findViewById(R.id.rl_member_gift);
        memberVoucherRlt = (RelativeLayout) findViewById(R.id.rl_member_voucher);
        memberRedpacketRlt = (RelativeLayout) findViewById(R.id.rl_member_redpacket);
        // 热门代金券
        hotVoucherMoreTxt = (TextView) findViewById(R.id.tv_voucher_more);
        hotVoucherAmountTxt = (TextView) findViewById(R.id.tv_voucher_amount);
        hotVoucherTitleTxt = (TextView) findViewById(R.id.tv_voucher_title);
        hotVoucherLevelTxt = (TextView) findViewById(R.id.tv_voucher_level);
        hotVoucherLimitTxt = (TextView) findViewById(R.id.tv_voucher_limit);
        hotVoucherPointTxt = (TextView) findViewById(R.id.tv_voucher_point);
        hotVoucherTimeTxt = (TextView) findViewById(R.id.tv_voucher_time);
        hotVoucherBtn = (Button) findViewById(R.id.btn_voucher);
        hotVoucherList = (MyListView) findViewById(R.id.hot_voucher_list);
        // 热门礼品
        hotGiftMoreTxt = (TextView) findViewById(R.id.tv_gift_more);
        hotGiftImg = (ImageView) findViewById(R.id.iv_gift_img);
        hotGiftTitleTxt = (TextView) findViewById(R.id.tv_gift_title);
        hotGiftLevelTxt = (TextView) findViewById(R.id.tv_gift_level);
        hotGiftPriceTxt = (TextView) findViewById(R.id.tv_gift_price);
        hotGiftPointTxt = (TextView) findViewById(R.id.tv_gift_point);
        hotGiftLimitTxt = (TextView) findViewById(R.id.tv_gift_limit);
        hotGiftBtn = (Button) findViewById(R.id.btn_gift);
        hotGiftList = (MyListView) findViewById(R.id.hot_gift_list);
        // 热门红包
        hotRedpacketMoreTxt = (TextView) findViewById(R.id.tv_redpacket_more);
        hotRedpacketAmountTxt = (TextView) findViewById(R.id.tv_redpacket_amount);
        hotRedpacketTitleTxt = (TextView) findViewById(R.id.tv_redpacket_title);
        hotRedpacketLevelTxt = (TextView) findViewById(R.id.tv_redpacket_level);
        hotRedpacketLimitTxt = (TextView) findViewById(R.id.tv_redpacket_limit);
        hotRedpacketPointTxt = (TextView) findViewById(R.id.tv_redpacket_point);
        hotRedpacketTimeTxt = (TextView) findViewById(R.id.tv_redpacket_time);
        hotRedpacketBtn = (Button) findViewById(R.id.btn_redpacket);
        hotRedpacketList = (MyListView) findViewById(R.id.hot_redpacket_list);

        memberExpRuleTxt.setOnClickListener(this);
        memberExpLogTxt.setOnClickListener(this);
        memberPointRuleTxt.setOnClickListener(this);
        memberPointLogTxt.setOnClickListener(this);
        memberVoucherRlt.setOnClickListener(this);
        memberGiftRlt.setOnClickListener(this);
        memberRedpacketRlt.setOnClickListener(this);
        hotVoucherBtn.setOnClickListener(this);
        hotGiftBtn.setOnClickListener(this);
        hotRedpacketBtn.setOnClickListener(this);

        hotVoucherAdapter = new RptVoucherListViewAdapter(PointCenterActivity.this, null);
        hotRedpacketAdapter = new RptVoucherListViewAdapter(PointCenterActivity.this, null);
        hotGiftAdapter = new ProdgoodsListViewAdapter(PointCenterActivity.this, null);
        hotVoucherList.setAdapter(hotVoucherAdapter);
        hotRedpacketList.setAdapter(hotRedpacketAdapter);
        hotGiftList.setAdapter(hotGiftAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_member_exp_rule:
                goToExpRule();
                break;

            case R.id.tv_member_exp_log:
                goToExpLog();
                break;

            case R.id.tv_member_point_rule:
                goToPointRule();
                break;

            case R.id.tv_member_point_log:
                goToPointLog();
                break;

            case R.id.rl_member_gift:
                goToUserGift();
                break;

            case R.id.rl_member_voucher:
                goToUserVoucher();
                break;

            case R.id.rl_member_redpacket:
                goToUserRedpacket();
                break;

            case R.id.btn_voucher:

                break;

            case R.id.btn_gift:

                break;

            case R.id.btn_redpacket:

                break;

        }
    }

    /**
     * 跳转到经验规则界面
     */
    private void goToExpRule() {
        Intent helpIntent = new Intent(PointCenterActivity.this, HelpActivity.class);
        helpIntent.putExtra("loadUrl", Constants.URL_MEMBER_EXP_HELP);
        startActivity(helpIntent);
    }

    /**
     * 跳转到经验Log界面
     */
    private void goToExpLog() {
        startActivity(new Intent(PointCenterActivity.this, ExpLogActivity.class));
    }

    /**
     * 跳转到积分获取规则界面
     */
    private void goToPointRule() {
        Intent helpIntent = new Intent(PointCenterActivity.this, HelpActivity.class);
        helpIntent.putExtra("loadUrl", Constants.URL_MEMBER_POINT_HELP);
        startActivity(helpIntent);
    }

    /**
     * 跳转到积分Log界面
     */
    private void goToPointLog() {
        startActivity(new Intent(PointCenterActivity.this, PointLogActivity.class));
    }

    /**
     * 跳转到我的红包列表
     */
    private void goToUserRedpacket() {
        if (ShopHelper.isLogin(PointCenterActivity.this, myApplication.getLoginKey())) {
//                    startActivity(new Intent(getActivity(), RedpacketListActivity.class));
            startActivity(new Intent(PointCenterActivity.this, RedpacketListActivityNew.class));
        }
    }

    /**
     * 跳转到我的代金券列表
     */
    private void goToUserVoucher() {
        if (ShopHelper.isLogin(PointCenterActivity.this, myApplication.getLoginKey())) {
            startActivity(new Intent(PointCenterActivity.this, VoucherListActivity.class));
        }
    }

    /**
     * 跳转到我的礼品列表
     */
    private void goToUserGift() {
    }

    @Override
    public void setMemberInfo(Mine bean) {
        imageLoader.displayImage(bean.getMemberAvatar(), memberAvatarImg, options, animateFirstListener);
        memberNameTxt.setText(bean.getMemberName());
        memberLevelTxt.setText("L" + bean.getLevelName());
    }

    @Override
    public void setPointValue(String pointValue) {
        memberPointValueTxt.setText(pointValue);
    }

    @Override
    public void setExpInfo(String exp, String less_exp, String upgrade, String upgrade_exp) {
        memberExpNextTxt.setText(String.format(getString(R.string.pc_empiric_to_next_level), less_exp, upgrade));
        memberExpValueTxt.setText(exp + "/" + upgrade_exp);
        double memExp = Double.parseDouble(exp);
        int memUpgradeExp = Integer.parseInt(upgrade_exp);
        int expProgress = (int) (memExp / memUpgradeExp * 100);
        memberExpPrg.setProgress(expProgress > 100 ? 100 : expProgress);
    }

    @Override
    public void updateVoucherList(List<VoucherRedpacketBean> vouchers) {
        hotVoucherAdapter.update(vouchers);
    }

    @Override
    public void updateRedpacketList(List<VoucherRedpacketBean> redpackets) {
        hotRedpacketAdapter.update(redpackets);
    }

    @Override
    public void updateProdgoodsList(List<ProdgoodsBean> prodgoods) {
        hotGiftAdapter.update(prodgoods);
    }
}
