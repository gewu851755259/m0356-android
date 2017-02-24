package cn.m0356.shop.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.alibaba.fastjson.JSON;
import com.baidu.mapapi.map.Text;
import com.baidu.mobstat.StatService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import cn.m0356.shop.MainFragmentManager;
import cn.m0356.shop.R;
import cn.m0356.shop.adapter.GridViewAdapter;
import cn.m0356.shop.adapter.HomeActivityMyGridViewListAdapter;
import cn.m0356.shop.adapter.HomeGoodsMyGridViewListAdapter;
import cn.m0356.shop.bean.AdvertList;
import cn.m0356.shop.bean.Home1Data;
import cn.m0356.shop.bean.Home2Data;
import cn.m0356.shop.bean.Home3Data;
import cn.m0356.shop.bean.Home5Data;
import cn.m0356.shop.bean.HomeGoodsList;
import cn.m0356.shop.bean.HomeNotice;
import cn.m0356.shop.bean.HomeNoticeList;
import cn.m0356.shop.bracode.core.ViewfinderView;
import cn.m0356.shop.bracode.ui.CaptureActivity;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.FileUtils;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ScreenUtil;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.MyGridView;
import cn.m0356.shop.custom.MyProgressDialog;
import cn.m0356.shop.custom.ViewFlipperScrollView;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.RemoteDataHandler.Callback;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.pulltorefresh.library.PullToRefreshBase;
import cn.m0356.shop.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import cn.m0356.shop.pulltorefresh.library.PullToRefreshScrollView;
import cn.m0356.shop.ui.mine.IMFriendsListActivity;
import cn.m0356.shop.ui.mine.LoginActivity;
import cn.m0356.shop.ui.mine.RegisterMobileActivity;
import cn.m0356.shop.ui.mine.SigninActivity;
import cn.m0356.shop.ui.store.StoreInFoActivity;
import cn.m0356.shop.ui.store.newStoreInFoActivity;
import cn.m0356.shop.ui.type.GoodsDetailsActivity;
import cn.m0356.shop.ui.type.GoodsListFragmentManager;
import cn.m0356.shop.ui.type.NoticeActivity;
import cn.m0356.shop.ui.type.VoucherActivity;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 首页
 *
 * @author dqw
 * @Time 2015-8-17
 */
public class HomeFragment extends Fragment implements OnGestureListener, OnTouchListener {
    private MyShopApplication myApplication;

    private Intent intent = null;

    private TextView tvSearch;
    private Button btnCamera;
    private LinearLayout llIm;
    /*private TextView tvSearchD;
    private Button btnCameraD;
    private LinearLayout llImD;*/

    private LinearLayout llHomeGoodsClassify;//商品分类
    private LinearLayout llHomeCart;//购物车
    private LinearLayout llHomeMine;//我的商城
    private LinearLayout llHomeSignin;//每日签到

    private TextSwitcher tsBanner; // 动态公告轮播

    private PullToRefreshScrollView mPullRefreshScrollView;
    private ViewFlipper viewflipper;
    private LinearLayout dian;
    private boolean showNext = true;
    private int currentPage = 0;
    private final int SHOW_NEXT = 0011;
    private float downNub;//记录按下时的距离

    private LinearLayout HomeView;

    private static final int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 0;

    private GestureDetector mGestureDetector;
    private ViewFlipperScrollView myScrollView;
    private ArrayList<ImageView> viewList = new ArrayList<ImageView>();
    private Animation left_in, left_out, right_in, right_out;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    //private LinearLayout homeSearch;
    private LinearLayout search;

    private Button toTopBtn;// 返回顶部的按钮
    private int scrollY = 0;// 标记上次滑动位置
    private View contentView;
    private ScrollView scrollView;
    private MyProgressDialog progressDialog;

    /**
     * 公告通知集合
     */
    private List<HomeNotice> homeNotices;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewLayout = inflater.inflate(R.layout.main_home_view, container, false);

        MyExceptionHandler.getInstance().setContext(getActivity());


        long starttime = System.currentTimeMillis();
        progressDialog = new MyProgressDialog(getActivity(),"正在加载中...",R.anim.progress_round);
        progressDialog.show();

        initViewID(viewLayout);//注册控件ID
        long endtime = System.currentTimeMillis();
        if(endtime - starttime <= 2000){
            Handler handler =new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, 2000);
        }else {
            progressDialog.dismiss();
        }
        mGestureDetector = new GestureDetector(this);
        viewflipper.setOnTouchListener(this);
        myScrollView.setGestureDetector(mGestureDetector);

        // 20161011
        ((MainFragmentManager)getActivity()).setListener(new MainFragmentManager.OnScrollTopListener() {
            @Override
            public void onScrollTop() {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                toTopBtn.setVisibility(View.GONE);
            }
        });

        return viewLayout;
    }

    /**
     * 初始化注册控件ID
     */
    public void initViewID(View view) {
        myApplication = (MyShopApplication) getActivity().getApplicationContext();
        //搜索
        tvSearch = (TextView) view.findViewById(R.id.tvSearch);
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
        /*tvSearchD = (TextView) view.findViewById(R.id.tvSearchD);
        tvSearchD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });*/
        //摄像头
        btnCamera = (Button) view.findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CaptureActivity.class));
            }
        });
        /*btnCameraD = (Button) view.findViewById(R.id.btnCameraD);
        btnCameraD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CaptureActivity.class));
            }
        });*/
        //IM
        llIm = (LinearLayout) view.findViewById(R.id.llIm);
        llIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), IMFriendsListActivity.class));
                }
            }
        });
        /*llImD = (LinearLayout) view.findViewById(R.id.llImD);
        llImD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), IMFriendsListActivity.class));
                }
            }
        });*/

        //商品分类
        llHomeGoodsClassify = (LinearLayout) view.findViewById(R.id.llHomeFavGoods);
        llHomeGoodsClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), FavGoodsListActivity.class));
                }*/

                intent = new Intent(getActivity(), MainFragmentManager.class);
                myApplication.sendBroadcast(new Intent(Constants.SHOW_Classify_URL));
                startActivity(intent);
            }
        });
        //购物车
        llHomeCart = (LinearLayout) view.findViewById(R.id.llHomeMyOrder);
        llHomeCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    //startActivity(new Intent(getActivity(), OrderListActivity.class));
                    /*intent = new Intent(getActivity(), MainFragmentManager.class);
                    myApplication.sendBroadcast(new Intent(Constants.SHOW_CART_URL));
                    startActivity(intent);*/
                    VoucherActivity.start(getActivity());
                } else {
                    /*// 未登录  跳转至登录界面
                    startActivity(new Intent(getActivity(), LoginActivity.class));*/
                }
            }
        });
        //我的商城
        llHomeMine = (LinearLayout) view.findViewById(R.id.llHomeMyAsset);
        llHomeMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), MyAssetActivity.class));
                }*/
                intent = new Intent(getActivity(), MainFragmentManager.class);
                myApplication.sendBroadcast(new Intent(Constants.SHOW_Mine_URL));
                startActivity(intent);
            }
        });
        //每日签到
        llHomeSignin = (LinearLayout) view.findViewById(R.id.llHomeSignin);
        llHomeSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
//                    startActivity(new Intent(getActivity(), SigninActivity.class));
                    startActivity(new Intent(getActivity(), RegisterMobileActivity.class));
                }
            }
        });

        mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pullToRefreshScrollView);
        viewflipper = (ViewFlipper) view.findViewById(R.id.viewflipper);
        dian = (LinearLayout) view.findViewById(R.id.dian);
        myScrollView = (ViewFlipperScrollView) view.findViewById(R.id.viewFlipperScrollViewID);

        HomeView = (LinearLayout) view.findViewById(R.id.homeViewID);

        left_in = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);
        left_out = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out);
        right_in = AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in);
        right_out = AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_out);

        //homeSearch = (LinearLayout) view.findViewById(R.id.homeSearch);
        search = (LinearLayout) view.findViewById(R.id.search);

        /*homeSearch.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });*/

        //下拉刷新监听
        mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                loadUIData();
            }
        });

        scrollView = mPullRefreshScrollView.getRefreshableView();
        if (contentView == null) {
            contentView = scrollView.getChildAt(0);
        }

        toTopBtn = (Button) view.findViewById(R.id.top_btn);
        toTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                toTopBtn.setVisibility(View.GONE);
                //search.setVisibility(View.GONE);
                //homeSearch.setVisibility(View.VISIBLE);
            }
        });

        scrollView.setOnTouchListener(new OnTouchListener() {
            private int lastY = 0;
            private int touchEventId = -9983761;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastY == scroller.getScrollY()) {
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(
                                    touchEventId, scroller), 5);
                            lastY = scroller.getScrollY();
                        }
                    }
                }
            };

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(
                            handler.obtainMessage(touchEventId, view), 5);
                }
                return false;
            }

            private void handleStop(Object view) {
                ScrollView scroller = (ScrollView) view;
                scrollY = scroller.getScrollY();
                doOnBorderListener();
            }
        });

        loadUIData();

        //读取热门关键词
        getSearchHot();

        //读取搜素关键词列表
        getSearchKeyList();

        // jt 公告轮播代码
        //getNoticeList();
        //tsBanner = (TextSwitcher) view.findViewById(R.id.ts_banner);
        /*tsBanner.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                *//*TextView tv = new TextView(getActivity());
                tv.setGravity(Gravity.CENTER_VERTICAL);*//*
                View view = View.inflate(getActivity(), R.layout.view_home_notice, null);
                return view;
            }
        });*/



    }

    //private int i=0;
    /*private void postRunNotice(){
        tsBanner.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tsBanner.getNextView().getLayoutParams();
                params.setMargins(30, 0, 0, 0);
                params.gravity = Gravity.CENTER_VERTICAL;
                tsBanner.setText(homeNotices.get(i % homeNotices.size()).article_title);
                tsBanner.setOnClickListener(new HomeNoticeBannerListener(homeNotices.get(i % homeNotices.size())));
                i++;
                tsBanner.postDelayed(this, 3500);
            }
        });
    }*/

    private class HomeNoticeBannerListener implements View.OnClickListener{

        private HomeNotice notice;

        public HomeNoticeBannerListener(HomeNotice notice){
            this.notice = notice;
        }
        @Override
        public void onClick(View v) {
            if(TextUtils.isEmpty(notice.article_url)){ // 普通公告
                Intent intent = new Intent();
                intent.setClass(getActivity(), NoticeActivity.class);
                intent.putExtra("notice_id", notice.article_id);
                startActivity(intent);
            } else{ // 店铺
                Intent intent = new Intent();
                intent.setClass(getActivity(), newStoreInFoActivity.class);
                String[] arr = notice.article_url.split("=");
                intent.putExtra("store_id", arr[arr.length-1]);
                startActivity(intent);
            }
        }
    }

   /* private void getNoticeList() {
        RemoteDataHandler.asyncDataStringGet(Constants.URL_LIST_NOTICE, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == 200){
                    try {
                        JSONObject jo = new JSONObject(data.getJson());
                        JSONArray noticeList =  jo.getJSONObject("article_list")
                                .getJSONObject("notice")
                                .getJSONArray("list");
                        homeNotices = HomeNoticeList.newInstance(noticeList);
                        postRunNotice();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }*/

    private void doOnBorderListener() {
        /*LogHelper.i("huting----1111", ScreenUtil.getScreenViewBottomHeight(scrollView) + "  "
                + scrollView.getScrollY() + " " + ScreenUtil
                .getScreenHeight(getActivity()));*/
        // 底部判断
        if (contentView != null
                && contentView.getMeasuredHeight() <= scrollView.getScrollY()
                + scrollView.getHeight()) {
            toTopBtn.setVisibility(View.VISIBLE);
            //search.setVisibility(View.VISIBLE);
            //homeSearch.setVisibility(View.GONE);
        }else if (scrollView.getScrollY() == 0) {//顶部判断
            toTopBtn.setVisibility(View.GONE);
            //homeSearch.setVisibility(View.VISIBLE);
            //search.setVisibility(View.GONE);
        } else if (scrollView.getScrollY() > 13) {
            toTopBtn.setVisibility(View.VISIBLE);
            //search.setVisibility(View.VISIBLE);
            //homeSearch.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化加载数据
     */
    public void loadUIData() {
        RemoteDataHandler.asyncDataStringGet(Constants.URL_HOME, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                mPullRefreshScrollView.onRefreshComplete();//加载完成下拉控件取消显示
                if (data.getCode() == HttpStatus.SC_OK) {
                    HomeView.removeAllViews(); //删除homeview所有View
                    try {
                        String json = data.getJson();
                        JSONArray arr = new JSONArray(json);

                        int size = null == arr ? 0 : arr.length();

                        for (int i = 0; i < size; i++) {

                            JSONObject obj = arr.getJSONObject(i);
                            JSONObject JsonObj = new JSONObject(obj.toString());

                            if (!JsonObj.isNull("home1")) {
                                showHome1(JsonObj);
                            } else if (!JsonObj.isNull("home2")) {
                                showHome2(JsonObj);
                            } else if (!JsonObj.isNull("home4")) {
                                showHome4(JsonObj);
                            } else if (!JsonObj.isNull("home3")) {
                                showHome3(JsonObj);
                            } else if (!JsonObj.isNull("adv_list")) {
                                showAdvList(JsonObj);
                            } else if (!JsonObj.isNull("goods")) {
                                showGoods(JsonObj);
                            } else if (!JsonObj.isNull("home5")) {  // 20160829
                                showHome5(JsonObj);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getActivity(), R.string.load_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 显示商品块
     *
     * @param jsonObj
     * @throws JSONException
     */
    private void showGoods(JSONObject jsonObj) throws JSONException {
        String goodsJson = jsonObj.getString("goods");
        JSONObject itemObj = new JSONObject(goodsJson);
        String item = itemObj.getString("item");
        String title = itemObj.getString("title");

        if (!item.equals("[]")) {

            ArrayList<HomeGoodsList> goodsList = HomeGoodsList.newInstanceList(item);
            View goodsView = getActivity().getLayoutInflater().inflate(R.layout.tab_home_item_goods, null);
            TextView textView = (TextView) goodsView.findViewById(R.id.TextViewTitle);
            MyGridView gridview = (MyGridView) goodsView.findViewById(R.id.gridview);
            gridview.setFocusable(false);
            HomeGoodsMyGridViewListAdapter adapter = new HomeGoodsMyGridViewListAdapter(getActivity());
            adapter.setHomeGoodsList(goodsList);
            gridview.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (!title.equals("") && !title.equals("null") && title != null) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(title);
            } else {
                textView.setVisibility(View.GONE);
            }

            HomeView.addView(goodsView);

        }
    }

    /**
     * 显示广告块
     *
     * @param jsonObj
     * @throws JSONException
     */
    private void showAdvList(JSONObject jsonObj) throws JSONException, IOException {
        String advertJson = jsonObj.getString("adv_list");
        JSONObject itemObj = new JSONObject(advertJson);
        String item = itemObj.getString("item");
        FileUtils.saveToLocal(item);
        ////
        if (!item.equals("[]")) {

            ArrayList<AdvertList> advertList = AdvertList.newInstanceList(item);

            if (advertList.size() > 0 && advertList != null) {
                initHeadImage(advertList);
            }

        }
    }


    /**
     * 显示Home3
     *
     * @param jsonObj
     * @throws JSONException
     */
    private void showHome3(JSONObject jsonObj) throws JSONException {
        String home3Json = jsonObj.getString("home3");
        Home3Data bean = Home3Data.newInstanceDetelis(home3Json);
        ArrayList<Home3Data> home3Datas = Home3Data.newInstanceList(bean.getItem());
        View home3View = getActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home3, null);
        TextView textView = (TextView) home3View.findViewById(R.id.TextViewTitle);
        MyGridView gridview = (MyGridView) home3View.findViewById(R.id.gridview);
        gridview.setFocusable(false);
        HomeActivityMyGridViewListAdapter adapter = new HomeActivityMyGridViewListAdapter(getActivity());
        adapter.setHome3Datas(home3Datas);
        gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (!bean.getTitle().equals("") && !bean.getTitle().equals("null") && bean.getTitle() != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        HomeView.addView(home3View);
    }
    /**
     * 显示home5   20160829
     * @param home5
     */
    private void showHome5(JSONObject home5) throws JSONException {
        String home5Json = home5.getString("home5");
        Log.d("hehe",home5Json);
        Home5Data bean = Home5Data.newInstanceDetelis(home5Json);
        ArrayList<Home5Data> home5Datas = Home5Data.newInstanceList(bean.getItem());
        View home5View = getActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home5, null);
        GridView gridview = (GridView) home5View.findViewById(R.id.index_home_gridview_my);
        gridview.setFocusable(false);
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        GridViewAdapter adapter = new GridViewAdapter(getActivity());
        adapter.setHome5Datas(home5Datas);
        gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        HomeView.addView(home5View);
    }
    /**
     * 显示Home4
     *
     * @param jsonObj
     * @throws JSONException
     */
    private void showHome4(JSONObject jsonObj) throws JSONException {
        String home2Json = jsonObj.getString("home4");
        Home2Data bean = Home2Data.newInstanceDetelis(home2Json);
        View home4View = getActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home2_rehit, null);
        TextView textView = (TextView) home4View.findViewById(R.id.TextViewTitle);

        ImageView imageViewSquare = (ImageView) home4View.findViewById(R.id.ImageViewSquare);
        ImageView imageViewRectangle1 = (ImageView) home4View.findViewById(R.id.ImageViewRectangle1);
        ImageView imageViewRectangle2 = (ImageView) home4View.findViewById(R.id.ImageViewRectangle2);

        imageLoader.displayImage(bean.getSquare_image(), imageViewSquare, options, animateFirstListener);
        imageLoader.displayImage(bean.getRectangle1_image(), imageViewRectangle1, options, animateFirstListener);
        imageLoader.displayImage(bean.getRectangle2_image(), imageViewRectangle2, options, animateFirstListener);

        OnImageViewClick(imageViewSquare, bean.getSquare_type(), bean.getSquare_data());
        OnImageViewClick(imageViewRectangle1, bean.getRectangle1_type(), bean.getRectangle1_data());
        OnImageViewClick(imageViewRectangle2, bean.getRectangle2_type(), bean.getRectangle2_data());

        if (!bean.getTitle().equals("") && !bean.getTitle().equals("null") && bean.getTitle() != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        HomeView.addView(home4View);
    }

    /**
     * 显示Home2
     *
     * @param jsonObj
     * @throws JSONException
     */
    private void showHome2(JSONObject jsonObj) throws JSONException {
        String home2Json = jsonObj.getString("home2");
        Home2Data bean = Home2Data.newInstanceDetelis(home2Json);
        View home2View = getActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home2_left, null);
        TextView textView = (TextView) home2View.findViewById(R.id.TextViewTitle);

        ImageView imageViewSquare = (ImageView) home2View.findViewById(R.id.ImageViewSquare);
        ImageView imageViewRectangle1 = (ImageView) home2View.findViewById(R.id.ImageViewRectangle1);
        ImageView imageViewRectangle2 = (ImageView) home2View.findViewById(R.id.ImageViewRectangle2);

        imageLoader.displayImage(bean.getSquare_image(), imageViewSquare, options, animateFirstListener);
        imageLoader.displayImage(bean.getRectangle1_image(), imageViewRectangle1, options, animateFirstListener);
        imageLoader.displayImage(bean.getRectangle2_image(), imageViewRectangle2, options, animateFirstListener);

        OnImageViewClick(imageViewSquare, bean.getSquare_type(), bean.getSquare_data());
        OnImageViewClick(imageViewRectangle1, bean.getRectangle1_type(), bean.getRectangle1_data());
        OnImageViewClick(imageViewRectangle2, bean.getRectangle2_type(), bean.getRectangle2_data());

        if (!bean.getTitle().equals("") && !bean.getTitle().equals("null") && bean.getTitle() != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        HomeView.addView(home2View);
    }

    /**
     * 显示Home1
     *
     * @param jsonObj
     * @throws JSONException
     */
    private void showHome1(JSONObject jsonObj) throws JSONException {
        String home1Json = jsonObj.getString("home1");
        Home1Data bean = Home1Data.newInstanceList(home1Json);
        View home1View = getActivity().getLayoutInflater().inflate(R.layout.tab_home_item_home1, null);
        TextView textView = (TextView) home1View.findViewById(R.id.TextViewHome1Title01);
        ImageView imageView = (ImageView) home1View.findViewById(R.id.ImageViewHome1Imagepic01);

        if (!bean.getTitle().equals("") && !bean.getTitle().equals("null") && bean.getTitle() != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(bean.getTitle());
        } else {
            textView.setVisibility(View.GONE);
        }

        imageLoader.displayImage(bean.getImage(), imageView, options, animateFirstListener);
        OnImageViewClick(imageView, bean.getType(), bean.getData());
        HomeView.addView(home1View);
    }

    public void initHeadImage(ArrayList<AdvertList> list) {

        mHandler.removeMessages(SHOW_NEXT);

        //清除已有视图防止重复
        viewflipper.removeAllViews();
        dian.removeAllViews();
        viewList.clear();

        for (int i = 0; i < list.size(); i++) {
            AdvertList bean = list.get(i);
            ImageView imageView = new ImageView(HomeFragment.this.getActivity());
            imageView.setScaleType(ScaleType.FIT_XY);
            imageView.setBackgroundResource(R.drawable.dic_av_item_pic_bg);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageLoader.displayImage(bean.getImage(), imageView, options, animateFirstListener);
            viewflipper.addView(imageView);
            OnImageViewClick(imageView, bean.getType(), bean.getData(), true);

            ImageView dianimageView = new ImageView(HomeFragment.this.getActivity());
            LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 3, 1);
            dianimageView.setLayoutParams(params);
            imageView.setScaleType(ScaleType.FIT_XY);
            dianimageView.setBackgroundResource(R.drawable.dian_select);
            dian.addView(dianimageView);
            viewList.add(dianimageView);
        }

        //mGestureDetector = new GestureDetector(this);
        viewflipper.setOnTouchListener(this);
        //myScrollView.setGestureDetector(mGestureDetector);

        if (viewList.size() > 1) {
            dian_select(currentPage);
            mHandler.sendEmptyMessageDelayed(SHOW_NEXT, 3800);
        }
    }

    /**
     * 20121008 百度移动统计 首页广告栏 插入自定义事件
     * @param imageView
     * @param type
     * @param data
     * @param b
     */
    private void OnImageViewClick(ImageView imageView, String type, String data, boolean b) {
        OnImageViewClick(imageView, type, data);
        // 发送统计
        StatService.onEvent(getActivity(), "101", (TextUtils.isEmpty(myApplication.getUserName()) ? "游客" : myApplication.getUserName()) + "点击-广告栏");
    }

    public void OnImageViewClick(View view, final String type, final String data) {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean flag = false;
                if (-1 != SystemHelper.getNetworkType(getActivity())) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        //  触摸时按下
                        downNub = event.getX();
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        // 触摸时移动
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        //  触摸时抬起
                        if (downNub == event.getX()) {
                            if (type.equals("keyword")) {//搜索关键字
                                Intent intent = new Intent(getActivity(), GoodsListFragmentManager.class);
                                intent.putExtra("keyword", data);
                                intent.putExtra("gc_name", data);
                                startActivity(intent);
                            } else if (type.equals("special")) {//专题编号
                                Intent intent = new Intent(getActivity(), SubjectWebActivity.class);
                                // http://www.m0356.com/mobile/index.php?act=index&op=special&&special_id=xxx&&type=html
                                intent.putExtra("data", Constants.URL_SPECIAL + "&special_id=" + data + "&type=html");
                                startActivity(intent);
                            } else if (type.equals("goods")) {//商品编号
                                Intent intent = new Intent(getActivity(), GoodsDetailsActivity.class);
                                intent.putExtra("goods_id", data);
                                startActivity(intent);
                            } else if (type.equals("url")) {//地址
                                Intent intent = new Intent(getActivity(), SubjectWebActivity.class);
                                intent.putExtra("data", data);
                                startActivity(intent);
                            } else if(type.equals("store")){ // 店铺
                                //Toast.makeText(getActivity(), "store", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), newStoreInFoActivity.class);
                                intent.putExtra("store_id", data);
                                startActivity(intent);
                            } else if(type.equals("category")){ // 分类
                                Intent intent = new Intent(getActivity(), GoodsListFragmentManager.class);
                                intent.putExtra("gc_id", data);
                                startActivity(intent);
                            }
                        }
                    }
                    flag = true;
                }
                return flag;
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_NEXT:
                    if (showNext) {
                        // 从右向左滑动
                        showNextView();
                    } else {
                        // 从左向右滑动
                        showPreviousView();
                    }
                    mHandler.sendEmptyMessageDelayed(SHOW_NEXT, 3800);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    /**
     * 向左滑动
     */
    private void showNextView() {
            viewflipper.setInAnimation(left_in);
            viewflipper.setOutAnimation(left_out);
            viewflipper.showNext();
            currentPage++;
            if (currentPage == viewflipper.getChildCount()) {
                dian_unselect(currentPage - 1);
                currentPage = 0;
                dian_select(currentPage);
            } else {
                dian_select(currentPage);//第currentPage页
                dian_unselect(currentPage - 1);
            }
    }

    /**
     * 向右滑动
     */
    private void showPreviousView() {
            dian_select(currentPage);
            viewflipper.setInAnimation(right_in);
            viewflipper.setOutAnimation(right_out);
            viewflipper.showPrevious();
            currentPage--;
            if (currentPage == -1) {
                dian_unselect(currentPage + 1);
                currentPage = viewflipper.getChildCount() - 1;
                dian_select(currentPage);
            } else {
                dian_select(currentPage);
                dian_unselect(currentPage + 1);
            }
    }

    /**
     * 对应被选中的点的图片
     *
     * @param id
     */
    private void dian_select(int id) {
        ImageView img = viewList.get(id);
        img.setSelected(true);
    }

    /**
     * 对应未被选中的点的图片
     *
     * @param id
     */
    private void dian_unselect(int id) {
        ImageView img = viewList.get(id);
        img.setSelected(false);
    }

    /**
     * 获取搜索热词
     */
    private void getSearchHot() {
        RemoteDataHandler.asyncDataStringGet(Constants.URL_SEARCH_HOT, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();
                    try {
                        JSONObject obj = new JSONObject(json);
                        String hotInfoString = obj.getString("hot_info");
                        String searchHotName = "";
                        if (!hotInfoString.equals("[]")) {
                            JSONObject hotInfoObj = new JSONObject(hotInfoString);
                            searchHotName = hotInfoObj.getString("name");
                            myApplication.setSearchHotName(searchHotName);
                            myApplication.setSearchHotValue(hotInfoObj.getString("value"));
                        }
                        if (searchHotName != null && !searchHotName.equals("")) {
                            tvSearch.setHint(searchHotName);
                        } else {
                            tvSearch.setHint(R.string.default_search_text);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 获取搜索关键词列表
     */
    private void getSearchKeyList() {
        RemoteDataHandler.asyncDataStringGet(Constants.URL_SEARCH_KEY_LIST, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();
                    try {
                        ArrayList<String> searchKeyList = new ArrayList<String>();
                        JSONObject obj = new JSONObject(json);
                        String searchKeyListString = obj.getString("list");
                        if (!searchKeyListString.equals("[]")) {
                            JSONArray searchKeyListArray = new JSONArray(searchKeyListString);
                            int size = null == searchKeyListArray ? 0 : searchKeyListArray.length();
                            for (int i = 0; i < size; i++) {
                                String key = searchKeyListArray.getString(i);
                                searchKeyList.add(key);
                            }
                        }
                        myApplication.setSearchKeyList(searchKeyList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {//开始向左滑动了
            if (viewList.size() > 1) {
                showNextView();
                showNext = true;
            }

        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {//开始向右滑动了
            if (viewList.size() > 1) {
                showPreviousView();
                showNext = true;
            }
        }/*else if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE
                &&  Math.abs(velocityY) > FLING_MIN_VELOCITY){
            search.setVisibility(View.VISIBLE);
            homeSearch.setVisibility(View.GONE);

        }else if(e2.getY() - e1.getY() >= 300
                && Math.abs(velocityY) > FLING_MIN_VELOCITY){
            homeSearch.setVisibility(View.VISIBLE);
            search.setVisibility(View.GONE);
        }*/
        return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
                            float arg3) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(MyShopApplication.context, "主界面-首页");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(MyShopApplication.context, "主界面-首页");
    }

}
