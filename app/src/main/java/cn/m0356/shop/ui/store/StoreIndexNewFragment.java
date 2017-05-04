package cn.m0356.shop.ui.store;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.baidu.mobstat.StatService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.HomeNotice;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.MyProgressDialog;
import cn.m0356.shop.custom.ViewFlipperScrollView;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.RemoteDataHandler.Callback;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.mvp.presenter.HomePresenter;
import cn.m0356.shop.mvp.view.IHomeView;
import cn.m0356.shop.ui.home.SubjectWebActivity;
import cn.m0356.shop.ui.type.GoodsDetailsActivity;
import cn.m0356.shop.ui.type.NoticeActivity;

/**
 * 首页
 *
 * @author dqw
 * @Time 2015-8-17
 */
public class StoreIndexNewFragment extends Fragment implements OnGestureListener, IHomeView {
    private MyShopApplication myApplication;
    private static final String ARG_STORE_ID = "store_id";
    private Intent intent = null;

    //private TextView tvSearch;
    //private Button btnCamera;
    //private LinearLayout llIm;
    /*private TextView tvSearchD;
    private Button btnCameraD;
    private LinearLayout llImD;*/
/*
    private LinearLayout llHomeGoodsClassify;//商品分类
    private LinearLayout llHomeCart;//购物车
    private LinearLayout llHomeMine;//我的商城
    private LinearLayout llHomeSignin;//每日签到*/

    //private TextSwitcher tsBanner; // 动态公告轮播

    //private PullToRefreshScrollView mPullRefreshScrollView;
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
    //private LinearLayout search;

    //private Button toTopBtn;// 返回顶部的按钮
    private int scrollY = 0;// 标记上次滑动位置
    private View contentView;
    private ScrollView scrollView;
    private MyProgressDialog progressDialog;
    private String store_id;

    private OnTouchListener advTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mGestureDetector.onTouchEvent(event);
        }
    };
    /**
     * 公告通知集合
     */
    //private List<HomeNotice> homeNotices;

    // mvp模式主导其P,主要用于处理
    private HomePresenter homePresenter;

    public static StoreIndexNewFragment newInstance(String store_id) {
        StoreIndexNewFragment fragment = new StoreIndexNewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STORE_ID, store_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewLayout = inflater.inflate(R.layout.fragment_store_index_new, container, false);

        MyExceptionHandler.getInstance().setContext(getActivity());
        long starttime = System.currentTimeMillis();
        progressDialog = new MyProgressDialog(getActivity(),"正在加载中...",R.anim.progress_round);
        progressDialog.show();

        store_id = getArguments().getString(ARG_STORE_ID);

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
        viewflipper.setOnTouchListener(advTouchListener);
        myScrollView.setGestureDetector(mGestureDetector);
        myScrollView.setGestureDetector(mGestureDetector);

        // 实例化主导器
        homePresenter = new HomePresenter();
        homePresenter.attachView(this);
        return viewLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homePresenter.detachView();
    }

    /**
     * 初始化注册控件ID
     */
    public void initViewID(View view) {
        myApplication = (MyShopApplication) getActivity().getApplicationContext();


//        mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pullToRefreshScrollView);
        viewflipper = (ViewFlipper) view.findViewById(R.id.viewflipper);
        dian = (LinearLayout) view.findViewById(R.id.dian);
        myScrollView = (ViewFlipperScrollView) view.findViewById(R.id.viewFlipperScrollViewID);

        HomeView = (LinearLayout) view.findViewById(R.id.homeViewID);

        left_in = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);
        left_out = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out);
        right_in = AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in);
        right_out = AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_out);


        loadUIData();


    }

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

    private void doOnBorderListener() {
        /*LogHelper.i("huting----1111", ScreenUtil.getScreenViewBottomHeight(scrollView) + "  "
                + scrollView.getScrollY() + " " + ScreenUtil
                .getScreenHeight(getActivity()));*/
        // 底部判断
        if (contentView != null
                && contentView.getMeasuredHeight() <= scrollView.getScrollY()
                + scrollView.getHeight()) {
            //toTopBtn.setVisibility(View.VISIBLE);
            //search.setVisibility(View.VISIBLE);
            //homeSearch.setVisibility(View.GONE);
        }else if (scrollView.getScrollY() == 0) {//顶部判断
            //toTopBtn.setVisibility(View.GONE);
            //homeSearch.setVisibility(View.VISIBLE);
            //search.setVisibility(View.GONE);
        } else if (scrollView.getScrollY() > 13) {
            //toTopBtn.setVisibility(View.VISIBLE);
            //search.setVisibility(View.VISIBLE);
            //homeSearch.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化加载数据
     */
    public void loadUIData() {
        RemoteDataHandler.asyncDataStringGet(Constants.URL_STORE_INDEX + store_id, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
               // mPullRefreshScrollView.onRefreshComplete();//加载完成下拉控件取消显示
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
                                homePresenter.showHome1(JsonObj); // 一张大图,640*260，模版A
                            } else if (!JsonObj.isNull("home2")) {
                                homePresenter.showHome2(JsonObj); //  左边一个大图，右边上下两个小图，模版B
                            } else if (!JsonObj.isNull("home3")) {
                                homePresenter.showHome3(JsonObj); // 原来两列总共十个分类那个模版，模版C
                            } else if (!JsonObj.isNull("home4")) {
                                homePresenter.showHome4(JsonObj); // 左边上下两个小图，右边一个大图，模版D
                            } else if (!JsonObj.isNull("home5")) {  // 20160829
                                homePresenter.showHome5(JsonObj);
                            } else if (!JsonObj.isNull("adv_list")) {
                                homePresenter.showAdvList(JsonObj, "adv_list", 340); // 高度较高(340)的广告轮播图，广告条版块
                            } else if (!JsonObj.isNull("goods")) {
                                homePresenter.showGoods(JsonObj); // 展示商品列表，商品版块
                            } else if (!JsonObj.isNull("adv_list2")) {
                                homePresenter.showAdvList(JsonObj, "adv_list2", 280); // 高度较低(280)的广告轮播图，广告条版块2
                            } else if (!JsonObj.isNull("preferential_goods")) {
                                homePresenter.showPreferentialGoods(JsonObj); // 限时特惠，特惠商品版块
                            } else if (!JsonObj.isNull("navigation")) {
                                homePresenter.showNavigation(JsonObj);  // 显示导航选项，导航版块
                            } else if (!JsonObj.isNull("home6")) {
                                homePresenter.showHome6(JsonObj);  // 显示一张图片的home6,模版F，复用原来home1代码
                            } else if (!JsonObj.isNull("home7")) {
                                homePresenter.showHome7(JsonObj);  // 两行两列的那个模版,模版G
                            } else if (!JsonObj.isNull("home8")) {
                                homePresenter.showHome8(JsonObj);  // 一排1：1：1：1模版，模版H
                            } else if (!JsonObj.isNull("home9")) {
                                homePresenter.showHome9(JsonObj);  // 一排2：1：1模版，模版I
                            } else if (!JsonObj.isNull("home10")) {
                                homePresenter.showHome10(JsonObj);  // 类似模版C，高度不同，模版J
                            } else if (!JsonObj.isNull("home11")) {
                                homePresenter.showHome11(JsonObj);  // 一排1：1：1模版，模版K
                            } else if (!JsonObj.isNull("home12")) {
                                homePresenter.showHome12(JsonObj);  // 一排1：1模版，模版L
                            } else if (!JsonObj.isNull("home13")) {
                                homePresenter.showHome13(JsonObj);  // 一排2：1：1：1模版，模版M
                            } else if (!JsonObj.isNull("home14")) {
                                homePresenter.showHome14(JsonObj);  //  两行两列，模版N，复用原来home7代码
                            } else if (!JsonObj.isNull("home15")) {
                                homePresenter.showHome15(JsonObj);  //  一个大格，模版O，复用原来home1代码
                            } else if (!JsonObj.isNull("home16")) {
                                homePresenter.showHome16(JsonObj);  //  一排1：1：1：1模版，模版P，复用原来home8代码
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
                                //替换为店铺搜索关键字
                                intent =new Intent(getActivity(),StoreGoodsListFragmentManager.class);
                                intent.putExtra("keyword", data);
                                intent.putExtra("store_id", store_id);
                                getActivity().startActivity(intent);
                                /*Intent intent = new Intent(getActivity(), GoodsListFragmentManager.class);
                                intent.putExtra("keyword", data);
                                intent.putExtra("gc_name", data);
                                startActivity(intent);*/
                            } else if (type.equals("special")) {//专题编号
                                Intent intent = new Intent(getActivity(), SubjectWebActivity.class);
                                // 店铺专题
                                intent.putExtra("data", Constants.URL_STORE_SPECIAL + "&special_id=" + data + "&type=html&store_id=" + store_id);
                                // 通过新店铺进入专题
                                intent.putExtra("forNewStore", true);
                                intent.putExtra("storeId", store_id);
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
                                intent =new Intent(getActivity(),StoreGoodsListFragmentManager.class);
                                intent.putExtra("stc_id", data);
                                intent.putExtra("store_id", store_id);
                                getActivity().startActivity(intent);
                                /*Intent intent = new Intent(getActivity(), GoodsListFragmentManager.class);
                                intent.putExtra("gc_id", data);
                                startActivity(intent);*/
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
                            //tvSearch.setHint(searchHotName);
                        } else {
                            //tvSearch.setHint(R.string.default_search_text);
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
    public void onResume() {
        super.onResume();
        StatService.onPageStart(MyShopApplication.context, "主界面-首页");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(MyShopApplication.context, "主界面-首页");
    }

    /* -----------------------------IHomeView------------------------------  */
    @Override
    public Activity getMainActivity() {
        return getActivity();
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public LinearLayout getDianView() {
        return dian;
    }

    @Override
    public LinearLayout getDianLowView() {
        return null;
    }

    @Override
    public ArrayList<ImageView> getDianViewList() {
        return viewList;
    }

    @Override
    public ArrayList<ImageView> getDianViewLowList() {
        return null;
    }

    @Override
    public ViewFlipper getAdvListView() {
        return viewflipper;
    }

    @Override
    public ViewFlipper getAdvListLowView() {
        return null;
    }

    @Override
    public OnTouchListener getAdvTouchListener() {
        return advTouchListener;
    }

    @Override
    public void onImageViewClick(ImageView imageView, String type, String data, boolean b) {
        OnImageViewClick(imageView, type, data, b);
    }

    @Override
    public void dianSelect() {
        dian_select(currentPage);
    }

    @Override
    public LinearLayout getHomeView() {
        return HomeView;
    }
}
