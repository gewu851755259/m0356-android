package cn.m0356.shop.presenter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.m0356.shop.R;
import cn.m0356.shop.adapter.GridViewAdapter;
import cn.m0356.shop.adapter.HomeActivityMyGridViewListAdapter;
import cn.m0356.shop.adapter.HomeGoodsMyGridViewListAdapter;
import cn.m0356.shop.adapter.HomeNavMyGridViewListAdapter;
import cn.m0356.shop.adapter.HomePreGoodsRecyclerAdapter;
import cn.m0356.shop.bean.AdvertList;
import cn.m0356.shop.bean.Home1Data;
import cn.m0356.shop.bean.Home2Data;
import cn.m0356.shop.bean.Home3Data;
import cn.m0356.shop.bean.Home5Data;
import cn.m0356.shop.bean.Home8Data;
import cn.m0356.shop.bean.Home9Data;
import cn.m0356.shop.bean.HomeGoodsList;
import cn.m0356.shop.bean.NavigationList;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.controller.HomeModel;
import cn.m0356.shop.controller.IHomeModel;
import cn.m0356.shop.custom.MyGridView;
import cn.m0356.shop.ui.home.HomeFragment;
import cn.m0356.shop.viewinterface.IHomeView;

/**
 * Created by minla on 2017/3/3.
 */
public class HomePresenter {

    private IHomeView iHomeView;
    private IHomeModel iHomeModel;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public HomePresenter(IHomeView iHomeView) {
        this.iHomeView = iHomeView;
        this.iHomeModel = new HomeModel();
    }

    /**
     * 显示首页轮播列表
     */
    public void showAdvList(JSONObject jsonObj) throws IOException, JSONException {
        ArrayList<AdvertList> advertList = iHomeModel.analysisAdvList(jsonObj, "adv_list");
        if (advertList != null && advertList.size() > 0) {
            initHeadImage(iHomeView.getAdvListView(), iHomeView.getDianView(), iHomeView.getDianViewList(), advertList);
        }
    }

    /**
     * 显示首页高度低的轮播列表
     */
    public void showAdvListLow(JSONObject jsonObj) throws IOException, JSONException {
        ArrayList<AdvertList> advertList = iHomeModel.analysisAdvList(jsonObj, "adv_list2");
        if (advertList != null && advertList.size() > 0) {
            initHeadImage(iHomeView.getAdvListLowView(), iHomeView.getDianLowView(), iHomeView.getDianViewLowList(), advertList);
        }
    }

    // 展示广告列表的公用方法
    public void initHeadImage(ViewFlipper view, LinearLayout dian, ArrayList<ImageView> viewList, ArrayList<AdvertList> list) {
        iHomeView.getHandler().removeMessages(HomeFragment.SHOW_NEXT);

        //清除已有视图防止重复
        view.removeAllViews();
        dian.removeAllViews();
        viewList.clear();

        for (int i = 0; i < list.size(); i++) {
            AdvertList bean = list.get(i);
            ImageView imageView = new ImageView(iHomeView.getMainActivity());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundResource(R.drawable.dic_av_item_pic_bg);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            LogHelper.d("HomeFragment", "adv image url" + bean.getImage());
            imageLoader.displayImage(bean.getImage(), imageView, options, animateFirstListener);
            view.addView(imageView);
            iHomeView.onImageViewClick(imageView, bean.getType(), bean.getData(), true);

            ImageView dianimageView = new ImageView(iHomeView.getMainActivity());
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 3, 1);
            dianimageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            dianimageView.setBackgroundResource(R.drawable.dian_select);
            dian.addView(dianimageView);
            viewList.add(dianimageView);
        }

        //mGestureDetector = new GestureDetector(this);
        view.setOnTouchListener(iHomeView.getAdvTouchListener());
        //myScrollView.setGestureDetector(mGestureDetector);

        if (viewList.size() > 1) {
            iHomeView.dianSelect();
            iHomeView.getHandler().sendEmptyMessageDelayed(HomeFragment.SHOW_NEXT, 3800);
        }
    }

    /**
     * 显示商品块
     *
     * @param jsonObj
     * @throws JSONException
     */
    public void showGoods(JSONObject jsonObj) throws JSONException {
        String goodsJson = jsonObj.getString("goods");
        JSONObject itemObj = new JSONObject(goodsJson);
        String item = itemObj.getString("item");
        String title = itemObj.getString("title");

        if (!item.equals("[]")) {
            ArrayList<HomeGoodsList> goodsList = HomeGoodsList.newInstanceList(item);
            View goodsView = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_goods, null);
            TextView textView = (TextView) goodsView.findViewById(R.id.TextViewTitle);
            MyGridView gridview = (MyGridView) goodsView.findViewById(R.id.gridview);
            gridview.setFocusable(false);
            HomeGoodsMyGridViewListAdapter adapter = new HomeGoodsMyGridViewListAdapter(iHomeView.getMainActivity());
            adapter.setHomeGoodsList(goodsList);
            gridview.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (title != null && !title.equals("") && !title.equals("null")) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(title);
            } else {
                textView.setVisibility(View.GONE);
            }

            iHomeView.getHomeView().addView(goodsView);
        }
    }

    /**
     * 显示特惠商品
     *
     * @param jsonObj
     * @throws IOException
     * @throws JSONException
     */
    public void showPreferentialGoods(JSONObject jsonObj) throws IOException, JSONException {
        ArrayList<HomeGoodsList> preGoodsLists = iHomeModel.analysisPreGoodsList(jsonObj, "preferential_goods");
        if (preGoodsLists != null && preGoodsLists.size() > 0) {
            View preGoodsView = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_pregoods, null);
            RecyclerView recyclerView = (RecyclerView) preGoodsView.findViewById(R.id.recyclerViewPreferentialGoods);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());//more的动画效果
            HomePreGoodsRecyclerAdapter adapter = new HomePreGoodsRecyclerAdapter(iHomeView.getMainActivity(), preGoodsLists);
            recyclerView.setAdapter(adapter);
            iHomeView.getHomeView().addView(preGoodsView);
        }
    }

    /**
     * 显示导航
     *
     * @param jsonObj
     * @throws IOException
     * @throws JSONException
     */
    public void showNavigation(JSONObject jsonObj) throws IOException, JSONException {
        String title = jsonObj.getJSONObject("navigation").getString("title");
        ArrayList<NavigationList> navLists = iHomeModel.analysisNavList(jsonObj, "navigation");
        if (navLists != null && navLists.size() > 0) {
            View navigationView = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_nav, null);
            TextView textView = (TextView) navigationView.findViewById(R.id.TextViewTitle);
            MyGridView gridview = (MyGridView) navigationView.findViewById(R.id.gridview_navigation);
            gridview.setFocusable(false);
            HomeNavMyGridViewListAdapter adapter = new HomeNavMyGridViewListAdapter(iHomeView.getMainActivity());
            adapter.setHomeNavList(navLists);
            gridview.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (title != null && !title.equals("") && !title.equals("null")) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(title);
            } else {
                textView.setVisibility(View.GONE);
            }

            iHomeView.getHomeView().addView(navigationView);
        }
    }

    /**
     * 显示Home1, 640*260, 特别大的一张图片
     *
     * @param jsonObj
     * @throws JSONException
     */
    public void showHome1(JSONObject jsonObj) throws JSONException {
        String home1Json = jsonObj.getString("home1");
        Home1Data bean = Home1Data.newInstanceList(home1Json);
        View home1View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home1, null);
        TextView textView = (TextView) home1View.findViewById(R.id.TextViewHome1Title01);
        ImageView imageView = (ImageView) home1View.findViewById(R.id.ImageViewHome1Imagepic01);

        if (!bean.getTitle().equals("") && !bean.getTitle().equals("null") && bean.getTitle() != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        imageLoader.displayImage(bean.getImage(), imageView, options, animateFirstListener);
        iHomeView.onImageViewClick(imageView, bean.getType(), bean.getData(), false);
        iHomeView.getHomeView().addView(home1View);
    }

    /**
     * 显示Home2 , 左边一个大图，右边上下两个小图
     *
     * @param jsonObj
     * @throws JSONException
     */
    public void showHome2(JSONObject jsonObj) throws JSONException {
        String home2Json = jsonObj.getString("home2");
        Home2Data bean = Home2Data.newInstanceDetelis(home2Json);
        View home2View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home2_left, null);
        TextView textView = (TextView) home2View.findViewById(R.id.TextViewTitle);

        ImageView imageViewSquare = (ImageView) home2View.findViewById(R.id.ImageViewSquare);
        ImageView imageViewRectangle1 = (ImageView) home2View.findViewById(R.id.ImageViewRectangle1);
        ImageView imageViewRectangle2 = (ImageView) home2View.findViewById(R.id.ImageViewRectangle2);

        imageLoader.displayImage(bean.getSquare_image(), imageViewSquare, options, animateFirstListener);
        imageLoader.displayImage(bean.getRectangle1_image(), imageViewRectangle1, options, animateFirstListener);
        imageLoader.displayImage(bean.getRectangle2_image(), imageViewRectangle2, options, animateFirstListener);

        iHomeView.onImageViewClick(imageViewSquare, bean.getSquare_type(), bean.getSquare_data(), false);
        iHomeView.onImageViewClick(imageViewRectangle1, bean.getRectangle1_type(), bean.getRectangle1_data(), false);
        iHomeView.onImageViewClick(imageViewRectangle2, bean.getRectangle2_type(), bean.getRectangle2_data(), false);

        if (!bean.getTitle().equals("") && !bean.getTitle().equals("null") && bean.getTitle() != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        iHomeView.getHomeView().addView(home2View);
    }

    /**
     * 显示Home3
     *
     * @param jsonObj
     * @throws JSONException
     */
    public void showHome3(JSONObject jsonObj) throws JSONException {
        String home3Json = jsonObj.getString("home3");
        Home3Data bean = Home3Data.newInstanceDetelis(home3Json);
        ArrayList<Home3Data> home3Datas = Home3Data.newInstanceList(bean.getItem());
        View home3View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home3, null);
        TextView textView = (TextView) home3View.findViewById(R.id.TextViewTitle);
        MyGridView gridview = (MyGridView) home3View.findViewById(R.id.gridview);
        gridview.setFocusable(false);
        HomeActivityMyGridViewListAdapter adapter = new HomeActivityMyGridViewListAdapter(iHomeView.getMainActivity());
        adapter.setHome3Datas(home3Datas);
        gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (!bean.getTitle().equals("") && !bean.getTitle().equals("null") && bean.getTitle() != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        iHomeView.getHomeView().addView(home3View);
    }

    /**
     * 显示Home4
     *
     * @param jsonObj
     * @throws JSONException
     */
    public void showHome4(JSONObject jsonObj) throws JSONException {
        String home2Json = jsonObj.getString("home4");
        Home2Data bean = Home2Data.newInstanceDetelis(home2Json);
        View home4View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home2_rehit, null);
        TextView textView = (TextView) home4View.findViewById(R.id.TextViewTitle);

        ImageView imageViewSquare = (ImageView) home4View.findViewById(R.id.ImageViewSquare);
        ImageView imageViewRectangle1 = (ImageView) home4View.findViewById(R.id.ImageViewRectangle1);
        ImageView imageViewRectangle2 = (ImageView) home4View.findViewById(R.id.ImageViewRectangle2);

        imageLoader.displayImage(bean.getSquare_image(), imageViewSquare, options, animateFirstListener);
        imageLoader.displayImage(bean.getRectangle1_image(), imageViewRectangle1, options, animateFirstListener);
        imageLoader.displayImage(bean.getRectangle2_image(), imageViewRectangle2, options, animateFirstListener);

        iHomeView.onImageViewClick(imageViewSquare, bean.getSquare_type(), bean.getSquare_data(), false);
        iHomeView.onImageViewClick(imageViewRectangle1, bean.getRectangle1_type(), bean.getRectangle1_data(), false);
        iHomeView.onImageViewClick(imageViewRectangle2, bean.getRectangle2_type(), bean.getRectangle2_data(), false);

        if (!bean.getTitle().equals("") && !bean.getTitle().equals("null") && bean.getTitle() != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        iHomeView.getHomeView().addView(home4View);
    }

    /**
     * 显示home5   20160829
     *
     * @param home5
     */
    public void showHome5(JSONObject home5) throws JSONException {
        String home5Json = home5.getString("home5");
        Log.d("hehe", home5Json);
        Home5Data bean = Home5Data.newInstanceDetelis(home5Json);
        ArrayList<Home5Data> home5Datas = Home5Data.newInstanceList(bean.getItem());
        View home5View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home5, null);
        GridView gridview = (GridView) home5View.findViewById(R.id.index_home_gridview_my);
        gridview.setFocusable(false);
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        GridViewAdapter adapter = new GridViewAdapter(iHomeView.getMainActivity());
        adapter.setHome5Datas(home5Datas);
        gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        iHomeView.getHomeView().addView(home5View);
    }

    /**
     * 显示Home6，640*66，模版F
     */
    public void showHome6(JSONObject jsonObj) throws IOException, JSONException {
        String home1Json = jsonObj.getString("home6");
        Home1Data bean = Home1Data.newInstanceList(home1Json);
        View home1View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home1, null);
        TextView textView = (TextView) home1View.findViewById(R.id.TextViewHome1Title01);
        ImageView imageView = (ImageView) home1View.findViewById(R.id.ImageViewHome1Imagepic01);
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        ll.height = (int) iHomeView.getMainActivity().getResources().getDimension(R.dimen.main_home6_height);
        imageView.setLayoutParams(ll);

        if (!bean.getTitle().equals("") && !bean.getTitle().equals("null") && bean.getTitle() != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        imageLoader.displayImage(bean.getImage(), imageView, options, animateFirstListener);
        iHomeView.onImageViewClick(imageView, bean.getType(), bean.getData(), false);
        iHomeView.getHomeView().addView(home1View);
    }

    /**
     * 显示Home7，两行两列，320*180，模版G
     */
    public void showHome7(JSONObject jsonObj) throws IOException, JSONException {
        JSONObject home7Json = jsonObj.getJSONObject("home7");
        ArrayList<Home3Data> home3Datas = new ArrayList<Home3Data>();
        Home3Data bean1 = new Home3Data(home7Json.getString("square1_image"), home7Json.getString("square1_type"), home7Json.getString("square1_data"));
        home3Datas.add(bean1);
        Home3Data bean2 = new Home3Data(home7Json.getString("square2_image"), home7Json.getString("square2_type"), home7Json.getString("square2_data"));
        home3Datas.add(bean2);
        Home3Data bean3 = new Home3Data(home7Json.getString("square3_image"),
                home7Json.has("square3_type") ? home7Json.getString("square3_type") : "",
                home7Json.has("square3_data") ? home7Json.getString("square3_data") : "");
        home3Datas.add(bean3);
        Home3Data bean4 = new Home3Data(home7Json.getString("square4_image"),
                home7Json.has("square4_type") ? home7Json.getString("square4_type") : "",
                home7Json.has("square4_data") ? home7Json.getString("square4_data") : "");
        home3Datas.add(bean4);

        View home7View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home3, null);
        TextView textView = (TextView) home7View.findViewById(R.id.TextViewTitle);
        MyGridView gridview = (MyGridView) home7View.findViewById(R.id.gridview);
        gridview.setFocusable(false);
        HomeActivityMyGridViewListAdapter adapter = new HomeActivityMyGridViewListAdapter(iHomeView.getMainActivity());
        adapter.setHome3Datas(home3Datas);
        gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (bean1.getTitle() != null && !bean1.getTitle().equals("") && !bean1.getTitle().equals("null")) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean1.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        iHomeView.getHomeView().addView(home7View);
    }

    /**
     * 一排1：1：1：1，大小都是160*180，模版H
     */
    public void showHome8(JSONObject jsonObj) throws IOException, JSONException {
        JSONObject home8Json = jsonObj.getJSONObject("home8");
        Home8Data bean1 = new Home8Data(home8Json.getString("square1_image"), "",
                home8Json.has("square1_type") ? home8Json.getString("square1_type") : "",
                home8Json.has("square1_data") ? home8Json.getString("square1_data") : "");
        Home8Data bean2 = new Home8Data(home8Json.getString("square2_image"), "",
                home8Json.has("square2_type") ? home8Json.getString("square2_type") : "",
                home8Json.has("square2_data") ? home8Json.getString("square2_data") : "");
        Home8Data bean3 = new Home8Data(home8Json.getString("square3_image"), "",
                home8Json.has("square3_type") ? home8Json.getString("square3_type") : "",
                home8Json.has("square3_data") ? home8Json.getString("square3_data") : "");
        Home8Data bean4 = new Home8Data(home8Json.getString("square4_image"), "",
                home8Json.has("square4_type") ? home8Json.getString("square4_type") : "",
                home8Json.has("square4_data") ? home8Json.getString("square4_data") : "");

        View home8View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home8, null);
        TextView textView = (TextView) home8View.findViewById(R.id.TextViewTitle);
        ImageView imageview1 = (ImageView) home8View.findViewById(R.id.home8ImageView1);
        ImageView imageview2 = (ImageView) home8View.findViewById(R.id.home8ImageView2);
        ImageView imageview3 = (ImageView) home8View.findViewById(R.id.home8ImageView3);
        ImageView imageview4 = (ImageView) home8View.findViewById(R.id.home8ImageView4);

        imageLoader.displayImage(bean1.getImage(), imageview1, options, animateFirstListener);
        imageLoader.displayImage(bean2.getImage(), imageview2, options, animateFirstListener);
        imageLoader.displayImage(bean3.getImage(), imageview3, options, animateFirstListener);
        imageLoader.displayImage(bean4.getImage(), imageview4, options, animateFirstListener);

        iHomeView.onImageViewClick(imageview1, bean1.getType(), bean1.getData(), false);
        iHomeView.onImageViewClick(imageview2, bean2.getType(), bean2.getData(), false);
        iHomeView.onImageViewClick(imageview3, bean3.getType(), bean3.getData(), false);
        iHomeView.onImageViewClick(imageview4, bean4.getType(), bean4.getData(), false);

        if (bean1.getTitle() != null && !bean1.getTitle().equals("") && !bean1.getTitle().equals("null")) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean1.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        iHomeView.getHomeView().addView(home8View);
    }

    /**
     * 一排2：1：1，320*180和160*180两种，模版I
     */
    public void showHome9(JSONObject jsonObj) throws IOException, JSONException {
        JSONObject home9Json = jsonObj.getJSONObject("home9");
        Home9Data bean1 = new Home9Data(home9Json.getString("square_image"), "",
                home9Json.has("square_type") ? home9Json.getString("square_type") : "",
                home9Json.has("square_data") ? home9Json.getString("square_data") : "");
        Home9Data bean2 = new Home9Data(home9Json.getString("rectangle1_image"), "",
                home9Json.has("rectangle1_type") ? home9Json.getString("rectangle1_type") : "",
                home9Json.has("rectangle1_data") ? home9Json.getString("rectangle1_data") : "");
        Home9Data bean3 = new Home9Data(home9Json.getString("rectangle2_image"), "",
                home9Json.has("rectangle2_type") ? home9Json.getString("rectangle2_type") : "",
                home9Json.has("rectangle2_data") ? home9Json.getString("rectangle2_data") : "");

        View home9View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home9, null);
        TextView textView = (TextView) home9View.findViewById(R.id.TextViewTitle);
        ImageView imageview1 = (ImageView) home9View.findViewById(R.id.home9ImageView1);
        ImageView imageview2 = (ImageView) home9View.findViewById(R.id.home9ImageView2);
        ImageView imageview3 = (ImageView) home9View.findViewById(R.id.home9ImageView3);

        imageLoader.displayImage(bean1.getImage(), imageview1, options, animateFirstListener);
        imageLoader.displayImage(bean2.getImage(), imageview2, options, animateFirstListener);
        imageLoader.displayImage(bean3.getImage(), imageview3, options, animateFirstListener);

        iHomeView.onImageViewClick(imageview1, bean1.getType(), bean1.getData(), false);
        iHomeView.onImageViewClick(imageview2, bean2.getType(), bean2.getData(), false);
        iHomeView.onImageViewClick(imageview3, bean3.getType(), bean3.getData(), false);

        if (bean1.getTitle() != null && !bean1.getTitle().equals("") && !bean1.getTitle().equals("null")) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean1.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        iHomeView.getHomeView().addView(home9View);
    }


    /**
     * 一排1：1：1，大小210*205，模版K
     */
    public void showHome11(JSONObject jsonObj) throws IOException, JSONException {
        JSONObject home11Json = jsonObj.getJSONObject("home11");
        Home8Data bean1 = new Home8Data(home11Json.getString("square1_image"), "",
                home11Json.has("square1_type") ? home11Json.getString("square1_type") : "",
                home11Json.has("square1_data") ? home11Json.getString("square1_data") : "");
        Home8Data bean2 = new Home8Data(home11Json.getString("square2_image"), "",
                home11Json.has("square2_type") ? home11Json.getString("square2_type") : "",
                home11Json.has("square2_data") ? home11Json.getString("square2_data") : "");
        Home8Data bean3 = new Home8Data(home11Json.getString("square3_image"), "",
                home11Json.has("square3_type") ? home11Json.getString("square3_type") : "",
                home11Json.has("square3_data") ? home11Json.getString("square3_data") : "");

        View home11View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home11, null);
        TextView textView = (TextView) home11View.findViewById(R.id.TextViewTitle);
        ImageView imageview1 = (ImageView) home11View.findViewById(R.id.home11ImageView1);
        ImageView imageview2 = (ImageView) home11View.findViewById(R.id.home11ImageView2);
        ImageView imageview3 = (ImageView) home11View.findViewById(R.id.home11ImageView3);

        imageLoader.displayImage(bean1.getImage(), imageview1, options, animateFirstListener);
        imageLoader.displayImage(bean2.getImage(), imageview2, options, animateFirstListener);
        imageLoader.displayImage(bean3.getImage(), imageview3, options, animateFirstListener);

        iHomeView.onImageViewClick(imageview1, bean1.getType(), bean1.getData(), false);
        iHomeView.onImageViewClick(imageview2, bean2.getType(), bean2.getData(), false);
        iHomeView.onImageViewClick(imageview3, bean3.getType(), bean3.getData(), false);

        if (bean1.getTitle() != null && !bean1.getTitle().equals("") && !bean1.getTitle().equals("null")) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean1.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        iHomeView.getHomeView().addView(home11View);
    }

    /**
     * 显示Home12，一行两列，320*248，模版L
     */
    public void showHome12(JSONObject jsonObj) throws IOException, JSONException {
        JSONObject home12Json = jsonObj.getJSONObject("home12");
        ArrayList<Home3Data> home3Datas = new ArrayList<Home3Data>();
        Home3Data bean1 = new Home3Data(home12Json.getString("square1_image"), home12Json.getString("square1_type"), home12Json.getString("square1_data"));
        home3Datas.add(bean1);
        Home3Data bean2 = new Home3Data(home12Json.getString("square2_image"), home12Json.getString("square2_type"), home12Json.getString("square2_data"));
        home3Datas.add(bean2);

        View home12View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home3, null);
        TextView textView = (TextView) home12View.findViewById(R.id.TextViewTitle);
        MyGridView gridview = (MyGridView) home12View.findViewById(R.id.gridview);
        gridview.setFocusable(false);
        HomeActivityMyGridViewListAdapter adapter = new HomeActivityMyGridViewListAdapter(iHomeView.getMainActivity());
        adapter.setHome3Datas(home3Datas);
        gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (bean1.getTitle() != null && !bean1.getTitle().equals("") && !bean1.getTitle().equals("null")) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean1.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        iHomeView.getHomeView().addView(home12View);
    }

    /**
     * 显示Home13，两行，每行2：1：1：1，240*189和130*189，模版M
     */
    public void showHome13(JSONObject jsonObj) throws IOException, JSONException {
        JSONObject home13Json = jsonObj.getJSONObject("home13");
    }

    /**
     * 显示Home14，两行两列，320*200，模版N
     */
    public void showHome14(JSONObject jsonObj) throws IOException, JSONException {
        JSONObject home14Json = jsonObj.getJSONObject("home14");
        ArrayList<Home3Data> home3Datas = new ArrayList<Home3Data>();
        Home3Data bean1 = new Home3Data(home14Json.getString("square1_image"), home14Json.getString("square1_type"), home14Json.getString("square1_data"));
        home3Datas.add(bean1);
        Home3Data bean2 = new Home3Data(home14Json.getString("square2_image"), home14Json.getString("square2_type"), home14Json.getString("square2_data"));
        home3Datas.add(bean2);
        Home3Data bean3 = new Home3Data(home14Json.getString("square3_image"),
                home14Json.has("square3_type") ? home14Json.getString("square3_type") : "",
                home14Json.has("square3_data") ? home14Json.getString("square3_data") : "");
        home3Datas.add(bean3);
        Home3Data bean4 = new Home3Data(home14Json.getString("square4_image"),
                home14Json.has("square4_type") ? home14Json.getString("square4_type") : "",
                home14Json.has("square4_data") ? home14Json.getString("square4_data") : "");
        home3Datas.add(bean4);

        View home14View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home3, null);
        TextView textView = (TextView) home14View.findViewById(R.id.TextViewTitle);
        MyGridView gridview = (MyGridView) home14View.findViewById(R.id.gridview);
        gridview.setFocusable(false);
        HomeActivityMyGridViewListAdapter adapter = new HomeActivityMyGridViewListAdapter(iHomeView.getMainActivity());
        adapter.setHome3Datas(home3Datas);
        gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (bean1.getTitle() != null && !bean1.getTitle().equals("") && !bean1.getTitle().equals("null")) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean1.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        iHomeView.getHomeView().addView(home14View);
    }

    /**
     * 显示Home15，640*150，模版O
     */
    public void showHome15(JSONObject jsonObj) throws IOException, JSONException {
        String home15Json = jsonObj.getString("home15");
        Home1Data bean = Home1Data.newInstanceList(home15Json);
        View home15View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home1, null);
        TextView textView = (TextView) home15View.findViewById(R.id.TextViewHome1Title01);
        ImageView imageView = (ImageView) home15View.findViewById(R.id.ImageViewHome1Imagepic01);
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        ll.height = (int) iHomeView.getMainActivity().getResources().getDimension(R.dimen.main_home6_height);
        imageView.setLayoutParams(ll);

        if (!bean.getTitle().equals("") && !bean.getTitle().equals("null") && bean.getTitle() != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        imageLoader.displayImage(bean.getImage(), imageView, options, animateFirstListener);
        iHomeView.onImageViewClick(imageView, bean.getType(), bean.getData(), false);
        iHomeView.getHomeView().addView(home15View);
    }

    /**
     * 一排1：1：1：1，大小都是160*180，模版H
     */
    public void showHome16(JSONObject jsonObj) throws IOException, JSONException {
        JSONObject home8Json = jsonObj.getJSONObject("home16");
        Home8Data bean1 = new Home8Data(home8Json.getString("square1_image"), "",
                home8Json.has("square1_type") ? home8Json.getString("square1_type") : "",
                home8Json.has("square1_data") ? home8Json.getString("square1_data") : "");
        Home8Data bean2 = new Home8Data(home8Json.getString("square2_image"), "",
                home8Json.has("square2_type") ? home8Json.getString("square2_type") : "",
                home8Json.has("square2_data") ? home8Json.getString("square2_data") : "");
        Home8Data bean3 = new Home8Data(home8Json.getString("square3_image"), "",
                home8Json.has("square3_type") ? home8Json.getString("square3_type") : "",
                home8Json.has("square3_data") ? home8Json.getString("square3_data") : "");
        Home8Data bean4 = new Home8Data(home8Json.getString("square4_image"), "",
                home8Json.has("square4_type") ? home8Json.getString("square4_type") : "",
                home8Json.has("square4_data") ? home8Json.getString("square4_data") : "");

        View home8View = iHomeView.getMainActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home8, null);
        TextView textView = (TextView) home8View.findViewById(R.id.TextViewTitle);
        ImageView imageview1 = (ImageView) home8View.findViewById(R.id.home8ImageView1);
        ImageView imageview2 = (ImageView) home8View.findViewById(R.id.home8ImageView2);
        ImageView imageview3 = (ImageView) home8View.findViewById(R.id.home8ImageView3);
        ImageView imageview4 = (ImageView) home8View.findViewById(R.id.home8ImageView4);

        imageLoader.displayImage(bean1.getImage(), imageview1, options, animateFirstListener);
        imageLoader.displayImage(bean2.getImage(), imageview2, options, animateFirstListener);
        imageLoader.displayImage(bean3.getImage(), imageview3, options, animateFirstListener);
        imageLoader.displayImage(bean4.getImage(), imageview4, options, animateFirstListener);

        iHomeView.onImageViewClick(imageview1, bean1.getType(), bean1.getData(), false);
        iHomeView.onImageViewClick(imageview2, bean2.getType(), bean2.getData(), false);
        iHomeView.onImageViewClick(imageview3, bean3.getType(), bean3.getData(), false);
        iHomeView.onImageViewClick(imageview4, bean4.getType(), bean4.getData(), false);

        if (bean1.getTitle() != null && !bean1.getTitle().equals("") && !bean1.getTitle().equals("null")) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean1.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        iHomeView.getHomeView().addView(home8View);
    }

}
