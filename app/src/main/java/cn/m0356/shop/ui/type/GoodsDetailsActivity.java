package cn.m0356.shop.ui.type;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;

import java.util.ArrayList;
import java.util.List;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.adapter.GoodsDetailsPagerAdapter;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.ScreenUtil;
import cn.m0356.shop.custom.GoodsDetailViewPager;


/**
 * 商品详细页Activity
 *
 * @author dqw
 * @Time 2015-7-14
 */
public class GoodsDetailsActivity extends BaseActivity implements GoodsDetailFragment.OnFragmentInteractionListener, GoodsDetailBodyFragment.OnFragmentInteractionListener, GoodsDetailEvaluateFragment.OnFragmentInteractionListener {

    private String goods_id;
    private Button btnGoodsDetail, btnGoodsBody, btnGoodsEvaluate;
    // 20160902
    private GoodsDetailViewPager vpMain;
    private List<Fragment> fragments;
    private String[] titles = {"商品", "详情", "评价"};

    private GoodsDetailFragment goodsDetailFragment;
    private GoodsDetailBodyFragment goodsDetailBodyFragment;
    private GoodsDetailEvaluateFragment goodsDetailEvaluateFragment;

    private String title = "商品";
    private GoodsDetailsPagerAdapter adapter;

    // 20161103 详情页修改
    private RelativeLayout rlTab, rlTabDetail;

    private ImageButton ibDetailBack;

    private LinearLayout ll_new_tab;
    private TextView tvCommonTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_details_view);
        MyExceptionHandler.getInstance().setContext(this);
        goods_id = GoodsDetailsActivity.this.getIntent().getStringExtra("goods_id");

        btnGoodsDetail = (Button) findViewById(R.id.btnGoodsDetail);
        btnGoodsBody = (Button) findViewById(R.id.btnGoodsBody);
        btnGoodsEvaluate = (Button) findViewById(R.id.btnGoodsEvaluate);
        ll_new_tab = (LinearLayout) findViewById(R.id.ll_new_tab);
        tvCommonTitle = (TextView) findViewById(R.id.tvCommonTitle);
        // 20160902
        vpMain = (GoodsDetailViewPager) findViewById(R.id.vpMain);

        // 20161103
        rlTab = (RelativeLayout) findViewById(R.id.rl_tab);
        rlTabDetail = (RelativeLayout) findViewById(R.id.rl_tab_detail);

        ibDetailBack = (ImageButton) findViewById(R.id.ib_detail_back);
        ibDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsDetailFragment.scrollToTop(false);
                // 通过图文详情按钮返回时需要设置vpMain可以滑动
                vpMain.setPagingEnabled(true);
            }
        });

        goodsDetailFragment = GoodsDetailFragment.newInstance(goods_id);
        goodsDetailBodyFragment = GoodsDetailBodyFragment.newInstance(goods_id);
        goodsDetailEvaluateFragment = GoodsDetailEvaluateFragment.newInstance(goods_id);
        // 20160902
        fragments = new ArrayList<Fragment>();
        fragments.add(goodsDetailFragment);
        fragments.add(goodsDetailBodyFragment);
        fragments.add(goodsDetailEvaluateFragment);
        vpMain.setOffscreenPageLimit(2);
        vpMain.setAdapter(adapter = new GoodsDetailsPagerAdapter(getFragmentManager(), fragments));
        setTabButtonState(btnGoodsDetail);
        initAnimation();
        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        /*FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.llMain, goodsDetailFragment);
        transaction.add(R.id.llMain, goodsDetailBodyFragment);
        transaction.add(R.id.llMain, goodsDetailEvaluateFragment);
        transaction.commit();*/
        //showGoodsDetail();

/*        vpMain.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GoodsDetailsActivity.this, "ok", 0).show();
                vpMain.setCurrentItem(1);
            }
        }, 5000);*/

    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
    }


    public GoodsDetailsActivity selectTab(int position) {
        Button btn;
        switch (position) {
            case 0:
                btn = btnGoodsDetail;
                break;
            case 1:
                btn = btnGoodsBody;
                break;
            case 2:
                btn = btnGoodsEvaluate;
                break;
            default:
                btn = btnGoodsDetail;
                break;
        }
        setTabButtonState(btn);
        title = titles[position];
        return this;
    }


    public void setDetaiBodylTab() {
        setTabButtonState(btnGoodsBody);
    }

    public void setDetailTab() {
        setTabButtonState(btnGoodsDetail);
    }

    /**
     * 设置是否显示tab
     *
     * @param isShow
     */
    public GoodsDetailsActivity setTabShow(boolean isShow) {
        if (isShow) {
            rlTab.setVisibility(View.VISIBLE);
            rlTabDetail.setVisibility(View.INVISIBLE);
            rlTabShowDetailHind();
        } else {
            rlTab.setVisibility(View.INVISIBLE);
            rlTabDetail.setVisibility(View.VISIBLE);
            rlTabHideDetailShow();
        }
        return GoodsDetailsActivity.this;
    }

    /**
     * 设置是否显示tab，tab不显示动画
     *
     * @param isShow
     */
    public GoodsDetailsActivity setTabOnlyShow(boolean isShow) {
        if (isShow) {
            rlTab.setVisibility(View.VISIBLE);
            rlTabDetail.setVisibility(View.INVISIBLE);
        } else {
            rlTab.setVisibility(View.INVISIBLE);
            rlTabDetail.setVisibility(View.VISIBLE);
        }
        return GoodsDetailsActivity.this;
    }

    private void rlTabShowDetailHind() {
        // rltab 从下到上显示
        TranslateAnimation mRlTabAnimationShow = new TranslateAnimation(0, 0, -ScreenUtil.dip2px(48), 0);
        mRlTabAnimationShow.setDuration(200);
        mRlTabAnimationShow.setFillAfter(true);
//        rlTab.setAnimation(rlTabAnimation);
        ll_new_tab.setAnimation(mRlTabAnimationShow);
        mRlTabAnimationShow.start();


        // rltabdetail 从下到上隐藏
        TranslateAnimation mRlTabDetailAnimationHide = new TranslateAnimation(0, 0, 0, ScreenUtil.dip2px(48));
        mRlTabDetailAnimationHide.setDuration(200);
        mRlTabDetailAnimationHide.setFillAfter(true);
//        rlTabDetail.setAnimation(rlTabDetailAnimation);
        tvCommonTitle.setAnimation(mRlTabDetailAnimationHide);
        mRlTabDetailAnimationHide.start();
    }

    private void rlTabHideDetailShow(){
        // rltab 从下到上隐藏
        TranslateAnimation mRlTabAnimationHide = new TranslateAnimation(0, 0, 0, -ScreenUtil.dip2px(48));
        mRlTabAnimationHide.setDuration(200);
        mRlTabAnimationHide.setFillAfter(true);
//        rlTab.setAnimation(rlTabAnimation);
        ll_new_tab.setAnimation(mRlTabAnimationHide);
        mRlTabAnimationHide.start();

        // rltabdetail 从下到上显示
        TranslateAnimation mRlTabDetailAnimationShow = new TranslateAnimation(0, 0, ScreenUtil.dip2px(48), 0);
        mRlTabDetailAnimationShow.setDuration(200);
        mRlTabDetailAnimationShow.setFillAfter(true);
//        rlTabDetail.setAnimation(rlTabDetailAnimation);
        tvCommonTitle.setAnimation(mRlTabDetailAnimationShow);
        mRlTabDetailAnimationShow.start();
    }



    /**
     * 更换新商品
     */
    public void changeGoods(String goodsId) {
        Intent intent = new Intent(this, GoodsDetailsActivity.class);
        intent.putExtra("goods_id", goodsId);
        startActivity(intent);
        finish();
    }

    /**
     * 返回按钮点击
     */
    public void btnBackClick(View view) {
        if (title.equals("商品")) {
            finish();
        } else {
            (GoodsDetailsActivity.this).showGoodsDetail();
        }
    }

    @Override
    public void onBackPressed() {
        // 判断是否在信息页
        if(goodsDetailFragment.isTopPage()) {
            btnBackClick(null);
        } else {
            // 滑动到顶部
            goodsDetailFragment.scrollToTop(false);
            vpMain.setPagingEnabled(true);
        }
        //super.onBackPressed();
    }

    /**
     * 商品详细按钮点击
     */
    public void btnGoodsDetailClick(View view) {
        showGoodsDetail();
    }

    /**
     * 商品描述按钮点击
     */
    public void btnGoodsBodyClick(View view) {
        showGoodsBody();
    }

    /**
     * 商品评价按钮点击
     */
    public void btnGoodsEvaluateClick(View view) {
        showGoodsEvaluate();
    }

    /**
     * 设置Tab按钮状态
     */
    public void setTabButtonState(Button btn) {
        btnGoodsDetail.setActivated(false);
        btnGoodsBody.setActivated(false);
        btnGoodsEvaluate.setActivated(false);
        btn.setActivated(true);
    }

    /**
     * 显示商品Fragement
     */
    public void showGoodsDetail() {
        /*setTabButtonState(btnGoodsDetail);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(goodsDetailFragment);
        transaction.hide(goodsDetailBodyFragment);
        transaction.hide(goodsDetailEvaluateFragment);
        title = "商品";
        transaction.commit();*/
        setTabButtonState(btnGoodsDetail);
        title = "商品";
        vpMain.setCurrentItem(0);
        goodsDetailFragment.scrollToTop(false);
        vpMain.setPagingEnabled(true);
    }

    /**
     * 显示商品描述
     */
    public void showGoodsBody() {
        /*setTabButtonState(btnGoodsBody);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(goodsDetailFragment);
        transaction.show(goodsDetailBodyFragment);
        transaction.hide(goodsDetailEvaluateFragment);
        title = "详情";
        transaction.commit();*/
        setTabButtonState(btnGoodsBody);
        title = "详情";
        vpMain.setCurrentItem(1);
    }

    /**
     * 显示商品评价
     */
    public void showGoodsEvaluate() {
        /*setTabButtonState(btnGoodsEvaluate);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(goodsDetailFragment);
        transaction.hide(goodsDetailBodyFragment);
        transaction.show(goodsDetailEvaluateFragment);
        title = "详情";
        transaction.commit();*/
        setTabButtonState(btnGoodsEvaluate);
        title = "详情";
        vpMain.setCurrentItem(2);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "商品详情界面");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "商品详情界面");
    }

    public void setVpScroll(boolean b) {
        vpMain.setPagingEnabled(b);
    }
}
