package cn.m0356.shop.ui.type;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.readystatesoftware.viewbadger.BadgeView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import cn.m0356.shop.MainFragmentManager;
import cn.m0356.shop.R;
import cn.m0356.shop.adapter.CommendGridViewAdapter;
import cn.m0356.shop.adapter.ContractDetailGridViewAdapter;
import cn.m0356.shop.adapter.CouListViewAdapter;
import cn.m0356.shop.adapter.GiftListViewAdapter;
import cn.m0356.shop.adapter.GoodsEvaluateListViewAdapter;
import cn.m0356.shop.adapter.ImageSwitchPagerAdapter;
import cn.m0356.shop.adapter.ManSongRuleListViewAdapter;
import cn.m0356.shop.bean.CommendList;
import cn.m0356.shop.bean.ContractDetail;
import cn.m0356.shop.bean.CouInFo;
import cn.m0356.shop.bean.GiftArrayList;
import cn.m0356.shop.bean.GoodsDetailStoreVoucherInfo;
import cn.m0356.shop.bean.GoodsDetails;
import cn.m0356.shop.bean.GoodsEvaluateInfo;
import cn.m0356.shop.bean.GoodsHairInfo;
import cn.m0356.shop.bean.GpsInfo;
import cn.m0356.shop.bean.ManSongInFo;
import cn.m0356.shop.bean.ManSongRules;
import cn.m0356.shop.bean.StoreInfo;
import cn.m0356.shop.bean.StoreO2oAddressInfo;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.ChildViewPager;
import cn.m0356.shop.custom.MyGridView;
import cn.m0356.shop.custom.MyListView;
import cn.m0356.shop.custom.MyProgressDialog;
import cn.m0356.shop.custom.NCAddressDialog;
import cn.m0356.shop.custom.NCGoodsSpecPopupWindow;
import cn.m0356.shop.custom.NCStoreVoucherPopupWindow;
import cn.m0356.shop.custom.ScrollViewContainer;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ncinterface.INCOnAddressDialogConfirm;
import cn.m0356.shop.ncinterface.INCOnNumModify;
import cn.m0356.shop.ncinterface.INCOnStringModify;
import cn.m0356.shop.ui.store.newStoreInFoActivity;

/**
 * 商品详细信息Fragment
 *
 * @author dqw
 * @Time 2015-7-14
 */
public class GoodsDetailFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String ARG_GOODS_ID = "goods_id";
    private String goodsId;
    private String goodsName;
    private String goodsPrice;
    private String goodsStorage;
    private int goodsNum = 1; //商品数量
    private int goodsLimit = 0;
    private String is_fcode;//是否为F码商品 1是 0否
    private String is_virtual;//是否为虚拟商品 1-是 0-否
    private String ifcart = "0";//购物车购买标志 1购物车 0不是
    private String store_id = "";//记录店铺ID
    private String storeName = "";//记录店铺名称
    private String t_id, t_name; //记录商家ID 名称
    private int goodsBodyFLag = 0;
    private OnFragmentInteractionListener mListener;

    //商品数量修改回调
    private INCOnNumModify incOnNumModify;
    //商品规格修改回调
    private INCOnStringModify incOnStringModify;

    //主layout
    private ScrollView svMain;
    private Button btnShowGoodsDetail;
    //商品图切换
    private ChildViewPager vpImage;
    private ViewGroup group;
    private ArrayList<String> imageList;
    private ImageView[] tips;
    //分享、收藏
    private ImageButton btnGoodsShare, btnGoodsFav;

    private String goodsWapUrl;//记录图片地址

    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    //促销
    private LinearLayout llPromotion, llJiaJiaGou, llManSong, llGift;
    private MyListView lvManSong, lvGift, lvJiaJiaGou;
    //店铺代金券
    private LinearLayout llStoreVoucher;
    private NCStoreVoucherPopupWindow pwVoucher;
    //配送区域
    private LinearLayout llHairInfo;
    private Button btnHairAreaName;
    private TextView tvHairIfStoreCn, tvHairContent;
    //商家信
    String storeO2oAddressString;
    String storeO2oPhone;
    private LinearLayout llStoreO2o, llStoreO2oItem;
    private TextView tvStoreO2oName, tvStoreO2oAddress;
    private ImageButton btnStoreO2oPhone;
    private Button btnStoreO2oAllAdress;
    //消费保障
    private LinearLayout llService;
    private TextView tvService;
    private MyGridView gvContract;
    //店铺信息
    private LinearLayout llStoreInfo;
    private TextView tvStoreDescPoint;
    private TextView tvStoreDescText;
    private TextView tvStoreServicePoint;
    private TextView tvStoreServiceText;
    private TextView tvStoreDeliveryPoint;
    private TextView tvStoreDeliveryText;
    //评价
    private TextView tvEvaluateGoodPercent, tvEvaluateCount;
    private LinearLayout llEvaluateList;
    private RelativeLayout rlEvaluate;
    private MyListView lvEvaluateList;
    private GoodsEvaluateListViewAdapter goodsEvaluateListViewAdapter;
    //推荐商品
    private MyGridView gridViewCommend;
    private CommendGridViewAdapter commendAdapter;
    //底部按钮
    private TextView imID, showCartLayoutID;
    private BadgeView badge;
    private Button addCartID, buyStepID;


    //规格
    private String specString;
    private TextView specNameID;
    private NCGoodsSpecPopupWindow pwSpec;

    private TextView goodsNameID, goodsJingleID, goodsSalenumID,
            goodsPriceID, tvGoodsMarketPrice, tvStoreName, tvGoodsType;


    private MyShopApplication myApplication;


    private String mobile_body;//商品手机版详情

    private String goods_attr;//产品规格参数
    private StringBuilder image;

    //设置规格的参数
    private GoodsDetails goodsDetails;
    private String specList;

    //判断商品能否购买
    private boolean flag = false;
    private String errorMsg = "";

    private MyProgressDialog progressDialog;

    // 20160902
    private WebView wvDetail;
    private ScrollViewContainer svContainer;

    private RelativeLayout rl;

    //20161103 添加top按钮
    private Button top_btn;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 1) {
                badge.setText((String) message.obj);
                badge.setVisibility(View.VISIBLE);
                badge.show();
            }
        }
    };

    private ArrayList<ImageView> viewList = new ArrayList<ImageView>();

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private int currentVpIndex;
    private GoodsHairInfo goodsHairInfo;

    public static GoodsDetailFragment newInstance(String goodsId) {
        GoodsDetailFragment fragment = new GoodsDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GOODS_ID, goodsId);
        fragment.setArguments(args);
        return fragment;
    }

    public GoodsDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            goodsId = getArguments().getString(ARG_GOODS_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_goods_detail, container, false);
        MyExceptionHandler.getInstance().setContext(getActivity());
        myApplication = (MyShopApplication) getActivity().getApplicationContext();

        initViewID(layout);
        // 移至onStart方法
        //loadingGoodsDetailsData();//初始化加载数据

        return layout;
    }


    /**
     * 初始化注册控件ID
     */
    public void initViewID(View layout) {

        rl = (RelativeLayout) layout.findViewById(R.id.rl);

        //主layout
        svMain = (ScrollView) layout.findViewById(R.id.svMain);
        //滑动到底部加载商品描述，体验不好暂时停用，后期进行优化
        /*svMain.setOnBottomReachedListener(new MyScrollView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                goodsBodyFLag += 1;
                if (goodsBodyFLag > 1) {
                    ((GoodsDetailsActivity) getActivity()).showGoodsBody();
                    goodsBodyFLag = 0;
                }
            }
        });*/

        btnShowGoodsDetail = (Button) layout.findViewById(R.id.btnShowGoodsDetail);
        btnShowGoodsDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GoodsDetailsActivity) getActivity()).showGoodsBody();
            }
        });
        //商品图切换
        vpImage = (ChildViewPager) layout.findViewById(R.id.vpImage);
        group = (ViewGroup) layout.findViewById(R.id.viewGroup);
        //商品数据
        goodsNameID = (TextView) layout.findViewById(R.id.goodsNameID);
        goodsJingleID = (TextView) layout.findViewById(R.id.goodsJingleID);
        goodsSalenumID = (TextView) layout.findViewById(R.id.goodsSalenumID);
        goodsPriceID = (TextView) layout.findViewById(R.id.goodsPriceID);
        tvGoodsMarketPrice = (TextView) layout.findViewById(R.id.tvGoodsMarketPrice);
        tvGoodsType = (TextView) layout.findViewById(R.id.tvGoodsType);
        //分享按钮

        btnGoodsShare = (ImageButton) layout.findViewById(R.id.btnGoodsShare);
        btnGoodsShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**开启默认分享面板，分享列表**/
                new ShareAction(GoodsDetailFragment.this.getActivity()).setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SMS)
                        .withText(goodsName + "     " + Constants.WAP_GOODS_URL + goodsId + "     (" + getString(R.string.app_name) + ")")
                        .withTitle(goodsName)
                        .withTargetUrl(Constants.WAP_GOODS_URL + goodsId)
                        .setShareboardclickCallback(shareBoardlistener)
                        .open();
            }
        });
        //收藏按钮
        btnGoodsFav = (ImageButton) layout.findViewById(R.id.btnGoodsFav);
        btnGoodsFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGoodsFavClick(view);
            }
        });
        //促销
        llPromotion = (LinearLayout) layout.findViewById(R.id.llPromotion);
        llJiaJiaGou = (LinearLayout) layout.findViewById(R.id.llJiaJiaGou);
        lvJiaJiaGou = (MyListView) layout.findViewById(R.id.lvJiaJiaGou);
        llManSong = (LinearLayout) layout.findViewById(R.id.llManSong);
        lvManSong = (MyListView) layout.findViewById(R.id.lvManSong);
        llGift = (LinearLayout) layout.findViewById(R.id.llGift);
        lvGift = (MyListView) layout.findViewById(R.id.lvGift);
        //店铺代金券
        llStoreVoucher = (LinearLayout) layout.findViewById(R.id.llStoreVoucher);

        //配送区域
        llHairInfo = (LinearLayout) layout.findViewById(R.id.llHairInfo);

        btnHairAreaName = (Button) layout.findViewById(R.id.btnHairAreaName);
        btnHairAreaName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NCAddressDialog ncAddressDialog = new NCAddressDialog(getActivity());
                ncAddressDialog.setOnAddressDialogConfirm(new INCOnAddressDialogConfirm() {
                    @Override
                    public void onAddressDialogConfirm(String cityId, final String areaId, final String areaInfo) {
                        String url = Constants.URL_GOODS_CALC + "&goods_id=" + goodsId + "&area_id=" + areaId;

                        RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
                            @Override
                            public void dataLoaded(ResponseData data) {
                                String json = data.getJson();
                                if (data.getCode() == HttpStatus.SC_OK) {
                                    GoodsHairInfo goodsHairInfo = GoodsHairInfo.newInstance(json);
                                    myApplication.setAreaId(areaId);
                                    myApplication.setAreaName(areaInfo);
                                    showHairInfo(goodsHairInfo);
                                } else {
                                    ShopHelper.showApiError(getActivity(), json);
                                }
                            }
                        });
                    }
                });
                ncAddressDialog.show();
            }
        });
        tvHairIfStoreCn = (TextView) layout.findViewById(R.id.tvHairIfStoreCn);
        tvHairContent = (TextView) layout.findViewById(R.id.tvHairContent);
        //商家信息
        llStoreO2o = (LinearLayout) layout.findViewById(R.id.llStoreO2o);
        llStoreO2oItem = (LinearLayout) layout.findViewById(R.id.llStoreO2oItem);
        tvStoreO2oName = (TextView) layout.findViewById(R.id.tvStoreO2oName);
        tvStoreO2oAddress = (TextView) layout.findViewById(R.id.tvStoreO2oAddress);
        btnStoreO2oPhone = (ImageButton) layout.findViewById(R.id.btnStoreO2oPhone);
        btnStoreO2oPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopHelper.call(getActivity(), storeO2oPhone);
            }
        });
        btnStoreO2oAllAdress = (Button) layout.findViewById(R.id.btnStoreO2oAllAddress);
        btnStoreO2oAllAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StoreO2oAddressActivity.class);
                intent.putExtra("address", storeO2oAddressString);
                startActivity(intent);
            }
        });
        //店铺信息
        tvStoreName = (TextView) layout.findViewById(R.id.tvStoreName);
        tvStoreName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), newStoreInFoActivity.class);
                intent.putExtra("store_id", store_id);
                startActivity(intent);
            }
        });
        //消费保障
        llService = (LinearLayout) layout.findViewById(R.id.llService);
        tvService = (TextView) layout.findViewById(R.id.tvService);
        gvContract = (MyGridView) layout.findViewById(R.id.gvContract);
        //店铺信息
        llStoreInfo = (LinearLayout) layout.findViewById(R.id.llStoreInfo);
        tvStoreDescPoint = (TextView) layout.findViewById(R.id.tvStoreDescPoint);
        tvStoreDescText = (TextView) layout.findViewById(R.id.tvStoreDescText);
        tvStoreServicePoint = (TextView) layout.findViewById(R.id.tvStoreServicePoint);
        tvStoreServiceText = (TextView) layout.findViewById(R.id.tvStoreServiceText);
        tvStoreDeliveryPoint = (TextView) layout.findViewById(R.id.tvStoreDeliveryPoint);
        tvStoreDeliveryText = (TextView) layout.findViewById(R.id.tvStoreDeliveryText);
        //评价
        tvEvaluateGoodPercent = (TextView) layout.findViewById(R.id.tvEvaluateGoodPercent);
        tvEvaluateCount = (TextView) layout.findViewById(R.id.tvEvaluateCount);
        rlEvaluate = (RelativeLayout) layout.findViewById(R.id.rlEvaluate);
        rlEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GoodsDetailsActivity) getActivity()).showGoodsEvaluate();
            }
        });
        llEvaluateList = (LinearLayout) layout.findViewById(R.id.llEvaluateList);
        lvEvaluateList = (MyListView) layout.findViewById(R.id.lvEvaluateList);
        goodsEvaluateListViewAdapter = new GoodsEvaluateListViewAdapter(getActivity());
        lvEvaluateList.setAdapter(goodsEvaluateListViewAdapter);
        //推荐商品
        commendAdapter = new CommendGridViewAdapter(getActivity());
        gridViewCommend = (MyGridView) layout.findViewById(R.id.gridViewCommend);
        gridViewCommend.setAdapter(commendAdapter);
        gridViewCommend.setFocusable(false);
        //底部按钮
        imID = (TextView) layout.findViewById(R.id.imID);
        showCartLayoutID = (TextView) layout.findViewById(R.id.showCartLayoutID);
        badge = new BadgeView(layout.getContext(), showCartLayoutID);
        badge.setTextSize(10);
        badge.setVisibility(View.GONE);
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        if (!(myApplication.getLoginKey().equals("null") || myApplication.getLoginKey().equals(""))) {
            setCartNumShow();
        }
        //规格
        specNameID = (TextView) layout.findViewById(R.id.specNameID);

        addCartID = (Button) layout.findViewById(R.id.addCartID);
        buyStepID = (Button) layout.findViewById(R.id.buyStepID);
        addCartID.setActivated(true);
        buyStepID.setActivated(true);
        specNameID.setOnClickListener(this);

        //推荐商品列表点击监听
        gridViewCommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                CommendList bean = (CommendList) gridViewCommend.getItemAtPosition(arg2);
                if (bean != null) {
                    goodsId = bean.getGoods_id();
                    loadingGoodsDetailsData();
                    ((GoodsDetailsActivity) getActivity()).changeGoods(goodsId);
                }

            }
        });


        // 20160902
        wvDetail = (WebView) layout.findViewById(R.id.wvDetail);
        wvDetail.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        wvDetail.loadUrl(Constants.URL_GOODS_BODY + "&goods_id=" + goodsId);

        addCartID.setOnClickListener(this);
        buyStepID.setOnClickListener(this);
        showCartLayoutID.setOnClickListener(this);
        imID.setOnClickListener(this);

        svContainer = (ScrollViewContainer) layout.findViewById(R.id.svContainer);
        //详情页显示回调
        svContainer.setOnSecondViewShowListener(new ScrollViewContainer.OnSecondViewShowListener() {
            @Override
            public void onSecondViewShow() {
                ((GoodsDetailsActivity) getActivity())//.selectTab(1)   // 停用
                        // 显示图文详情tab
                        .setTabShow(false)
                        .setVpScroll(false);
                // 显示top按钮
                top_btn.setVisibility(View.VISIBLE);
                isTopPage = false;
            }
        });
        // 信息页显示回调
        svContainer.setOnUpPullHintListener(new ScrollViewContainer.OnUpPullHintListener() {
            @Override
            public void onUpPullHintListener(int state) {
                ((GoodsDetailsActivity) getActivity())//.selectTab(0)  // 停用
                        .setTabShow(true)
                        .setVpScroll(true);
                // 隐藏top按钮
                top_btn.setVisibility(View.GONE);
                isTopPage = true;
            }
        });
        // 滑动到顶部回调
        svContainer.setOnScrollToTopListener(new ScrollViewContainer.OnScrollToTopListener() {
            @Override
            public void onScrollToTopListener() {
                top_btn.setVisibility(View.GONE);
                ((GoodsDetailsActivity) getActivity()).setTabShow(true).setVpScroll(true);
                isTopPage = true;


            }
        });

        // 20161103
        top_btn = (Button) layout.findViewById(R.id.top_btn);
        top_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svContainer.smoothScrollToTop();
            }
        });
    }

    ///////////////
    private boolean isTopPage = true;

    public boolean isTopPage() {
        return isTopPage;
    }
    ///////////////


    public void setCartNumShow() {
        String url = Constants.URL_GET_CART_NUM;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncPostDataString(url, params, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        String num = obj.getString("cart_count");
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = num;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "获取购物车数量失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    //设置单个监听是否分享成功
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (platform == SHARE_MEDIA.QQ) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "QQ分享成功啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SINA) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "新浪微博分享成功啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.QZONE) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "QQ空间分享成功啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "微信分享成功啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "微信朋友圈分享成功啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SMS) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "短信分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {

            if (platform == SHARE_MEDIA.QQ) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "QQ分享失败啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SINA) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "新浪微博分享失败啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.QZONE) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "QQ空间分享失败啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "微信分享失败啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "微信朋友圈分享失败啦", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SMS) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "短信分享失败啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {

            if (platform == SHARE_MEDIA.QQ) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "QQ分享取消了", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SINA) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "新浪微博分享取消了", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.QZONE) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "QQ空间分享取消了", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "微信分享取消了", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "微信朋友圈分享取消了", Toast.LENGTH_SHORT).show();
            } else if (platform == SHARE_MEDIA.SMS) {
                Toast.makeText(GoodsDetailFragment.this.getActivity(), "短信分享取消了", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //设置多平台分享监听
    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(GoodsDetailFragment.this.getActivity(), goodsWapUrl);
            if (share_media != SHARE_MEDIA.SMS) {
                new ShareAction(GoodsDetailFragment.this.getActivity()).setPlatform(share_media).setCallback(umShareListener)
                        .withText(goodsName + "     " + Constants.WAP_GOODS_URL + goodsId + "        (" + getString(R.string.app_name) + ")")
                        .withTargetUrl(Constants.WAP_GOODS_URL + goodsId)
                        .withMedia(image)
                        .withTitle(goodsName)
                        .share();
            } else {
                new ShareAction(GoodsDetailFragment.this.getActivity()).setPlatform(share_media).setCallback(umShareListener)
                        .withText(goodsName + "     " + Constants.WAP_GOODS_URL + goodsId + "        (" + getString(R.string.app_name) + ")")
                        .withTargetUrl(Constants.WAP_GOODS_URL + goodsId)
                        .withTitle(goodsName)
                        .share();

            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** 分享回调**/
        UMShareAPI.get(GoodsDetailFragment.this.getActivity()).onActivityResult(requestCode, resultCode, data);

    }

    public void loadingGoodsDetailsData(float x, float y){

    }


    /**
     * 初始化加载数据
     */
    public void loadingGoodsDetailsData() {
        String url = Constants.URL_GOODSDETAILS + "&goods_id=" + goodsId + "&key=" + myApplication.getLoginKey();

        if (!myApplication.getAreaId().equals("")) {
            url += "&area_id=" + myApplication.getAreaId();
        }

        LogHelper.e("GoodsDetailFragment", url);

        progressDialog = new MyProgressDialog(getActivity(), "正在加载中...", R.anim.progress_round);
        progressDialog.show();
        RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                viewList.clear();
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        String goods_info = obj.getString("goods_info");// 商品信息
                        GoodsDetails goodsBean = GoodsDetails.newInstanceList(goods_info);

                        //显示商品信息
                        showGoodsInfo(goodsBean);

                        //控制收藏按钮状态
                        String is_favorate = obj.optString("is_favorate");
                        if (is_favorate.equals("true")) {
                            //已收藏
                            btnGoodsFav.setSelected(true);
                        } else {
                            //未收藏
                            btnGoodsFav.setSelected(false);
                        }

                        //显示商品图片
                        String goods_image = obj.getString("goods_image"); //商品图片
                        imageList = new ArrayList<String>(Arrays.asList(goods_image.split(",")));
                        goodsWapUrl = imageList.get(0);
                        initHeadImage();

                        //显示促销
                        String mansong_info = obj.getString("mansong_info");// 满即送信息
                        String gift_array = obj.getString("gift_array");// 赠品数组
                        String cou_info = obj.getString("cou_info");// 加价购
                        showPromotion(cou_info, mansong_info, gift_array);

                        //店铺代金券
                        String storeVoucher = obj.optString("voucher");
                        initStoreVoucher(storeVoucher);

                        //显示配送区域
                        String goodsHairInfoJson = obj.getString("goods_hair_info");
                        goodsHairInfo = GoodsHairInfo.newInstance(goodsHairInfoJson);
                        myApplication.setAreaName("晋城");

                        // 无货状态下不能购买和添加购物车
                        if(goodsHairInfo.getIfStoreCn().equals("无货")){
                            addCartID.setActivated(false);
                            buyStepID.setActivated(false);
                        } else {
                            addCartID.setActivated(true);
                            buyStepID.setActivated(true);
                        }
                        showHairInfo(goodsHairInfo);

                        //显示服务信息
                        String contractString = goodsBean.getContractlist();
                        ArrayList<ContractDetail> contractDetailArrayList = ContractDetail.newInstanceList(contractString);
                        if (contractDetailArrayList.size() > 0) {
                            ContractDetailGridViewAdapter contractDetailGridViewAdapter = new ContractDetailGridViewAdapter(getActivity());
                            contractDetailGridViewAdapter.setContractList(contractDetailArrayList);
                            gvContract.setAdapter(contractDetailGridViewAdapter);
                            llService.setVisibility(View.VISIBLE);
                        } else {
                            llService.setVisibility(View.GONE);
                        }

                        //店铺信息
                        String store_info = obj.getString("store_info");// 店铺信息
                        StoreInfo storeInfo = StoreInfo.newInstanceList(store_info);
                        tvService.setText("由“" + storeInfo.getStoreName() + "”销售和发货，并享受售后服务");
                        /*if (storeInfo.getIsOwnShop().equals("1")) {
                            //自营店铺不显示店铺信息
                            llStoreInfo.setVisibility(View.GONE);
                        } else {*/
                            tvStoreName.setText(storeInfo.getStoreName());
                            store_id = storeInfo.getStoreId();
                            storeName = storeInfo.getStoreName();
                            JSONObject creditObj = new JSONObject(storeInfo.getStoreCredit());
                            String desc = creditObj.getString("store_desccredit");
                            JSONObject objDesc = new JSONObject(desc);
                            setStoreCredit(objDesc.getString("credit"), objDesc.optString("percent_class"), tvStoreDescPoint, tvStoreDescText);
                            String service = creditObj.getString("store_servicecredit");
                            JSONObject objService = new JSONObject(service);
                            setStoreCredit(objService.getString("credit"), objService.optString("percent_class"), tvStoreServicePoint, tvStoreServiceText);
                            String delivery = creditObj.getString("store_deliverycredit");
                            JSONObject objDelivery = new JSONObject(delivery);
                            setStoreCredit(objDelivery.getString("credit"), objDelivery.optString("percent_class"), tvStoreDeliveryPoint, tvStoreDeliveryText);
                            llStoreInfo.setVisibility(View.VISIBLE);
                        //}
                        t_id = storeInfo.getMemberId();
                        t_name = storeInfo.getMemberName();

                        //评价
                        String goodsEvaluateInfoString = obj.getString("goods_evaluate_info");
                        JSONObject goodsEvaluateInfoObj = new JSONObject(goodsEvaluateInfoString);
                        tvEvaluateGoodPercent.setText("好评率 " + goodsEvaluateInfoObj.getString("good_percent") + "%");
                        tvEvaluateCount.setText("(" + goodsEvaluateInfoObj.getString("all") + "人评价)");
                        String goodsEvalListString = obj.getString("goods_eval_list");
                        ArrayList<GoodsEvaluateInfo> goodsEvaluateInfoList = GoodsEvaluateInfo.newInstanceList(goodsEvalListString);
                        if (goodsEvaluateInfoList.size() > 0) {
                            llEvaluateList.setVisibility(View.VISIBLE);
                            goodsEvaluateListViewAdapter.setList(goodsEvaluateInfoList);
                            goodsEvaluateListViewAdapter.notifyDataSetChanged();
                        } else {
                            llEvaluateList.setVisibility(View.GONE);
                        }

                        //显示推荐商品列表
                        String goods_commend_list = obj.getString("goods_commend_list");
                        ArrayList<CommendList> commendLists = CommendList.newInstanceList(goods_commend_list);
                        commendAdapter.setCommendLists(commendLists);
                        commendAdapter.notifyDataSetChanged();

                        //虚拟商品
                        if (is_virtual.equals("1")) {
                            //虚拟商品不显示配送区域
                            llHairInfo.setVisibility(View.GONE);
                            //虚拟商品显示商家分店地址
                            loadStoreO2oInfo();
                        } else {
                            llStoreO2o.setVisibility(View.GONE);
                        }


                        if (!is_virtual.equals("1")) {
                            ifCanBuyS();
                        } else {
                            ifCanBuyV();
                        }

                        //初始化规格弹出窗口
                        initSpec(goodsBean, obj.getString("spec_list"));
                        goodsDetails = goodsBean;
                        specList = obj.getString("spec_list");
                        progressDialog.dismiss();
                        //svMain.smoothScrollTo(0, 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                } else {
                    ShopHelper.showApiError(getActivity(), json);
//                    progressDialog.dismiss();
                    progressDialog.dismiss();
                }
            }
        });
    }



    /**
     * 初始化规格
     */
    private void initSpec(final GoodsDetails goodsBean, final String specList) {
        incOnNumModify = new INCOnNumModify() {
            @Override
            public void onModify(int num) {
                goodsNum = num;
                setSpecStirng();
            }
        };
        incOnStringModify = new INCOnStringModify() {
            @Override
            public void onModify(String str) {
                goodsId = str;

                // 记录位置
                loadingGoodsDetailsData();
                //((GoodsDetailsActivity) getActivity()).changeGoods(goodsId);
                // 20161108 关闭popupWindow
                if(pwSpec.getState())
                    pwSpec.closePoperWindow();
            }
        };

        //限购数量
        if (goodsBean.getUpper_limit() == null || goodsBean.getUpper_limit().equals("") || goodsBean.getUpper_limit().equals("0")) {
            goodsLimit = Integer.parseInt((goodsBean.getGoods_storage() == null ? "0" : goodsBean.getGoods_storage()) == "" ? "0" : goodsBean.getGoods_storage());
        }

        if (pwSpec == null) {
            pwSpec = new NCGoodsSpecPopupWindow(getActivity(), incOnNumModify, incOnStringModify, t_id, t_name, goodsHairInfo.getIfStoreCn());
        }
        pwSpec.setGoodsInfo(goodsName, imageList.get(0), goodsPrice, goodsStorage, goodsId, ifcart, goodsNum, goodsLimit, is_fcode, is_virtual);
        pwSpec.setSpecInfo(specList, goodsBean.getSpec_name(), goodsBean.getSpec_value(), goodsBean.getGoods_spec());

       /* specNameID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pwSpec.showPopupWindow();
            }
        });*/

    }

    /**
     * 初始化店铺代金券
     *
     * @param storeVoucher
     */
    private void initStoreVoucher(String storeVoucher) {
        final ArrayList<GoodsDetailStoreVoucherInfo> storeVoucherList = GoodsDetailStoreVoucherInfo.newInstanceList(storeVoucher);
        if (storeVoucherList.size() > 0) {
            llStoreVoucher.setVisibility(View.VISIBLE);
            llStoreVoucher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pwVoucher == null) {
                        pwVoucher = new NCStoreVoucherPopupWindow(getActivity());
                        pwVoucher.setStoreName(storeName);
                        pwVoucher.setVoucherList(storeVoucherList);
                    }
                    pwVoucher.showPopupWindow();
                }
            });
        } else {
            llStoreVoucher.setVisibility(View.GONE);
        }
    }

    public void scrollToTop(boolean isSmooth) {
        if (isSmooth)
            svContainer.scrollTop();
        else
            svContainer.scrollTopNoSmooth();
    }

    /**
     * 商品图切换
     */
    public void initHeadImage() {
        group.removeAllViews();
        tips = new ImageView[imageList.size()];
        // 如果
        if (getActivity() == null)
            return;
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.nc_page_on);
            } else {
                tips[i].setBackgroundResource(R.drawable.nc_page_off);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            group.addView(imageView, layoutParams);
        }
        vpImage.setAdapter(new ImageSwitchPagerAdapter(getActivity(), imageList));
        vpImage.setOnPageChangeListener(this);
        // 20160902
        //final GoodsDetailsActivity activity = (GoodsDetailsActivity) getActivity();
       /* vpImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
 *//*               // 如果是最后一张 请求父控件拦截
                if(currentVpIndex == imageList.size() - 1){
                    activity.setVpIsPagingEnabled(false);
                    return false;
                }*//*
                activity.setVpIsPagingEnabled(false);
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        activity.setVpIsPagingEnabled(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        activity.setVpIsPagingEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        activity.setVpIsPagingEnabled(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        activity.setVpIsPagingEnabled(true);
                        break;

                }
                return false;
            }
        });*/

    }

    /**
     * 商品图选中点
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.nc_page_on);
            } else {
                tips[i].setBackgroundResource(R.drawable.nc_page_off);
            }
        }
    }

    /**
     * 设置店铺评分
     *
     * @param credit
     * @param type
     * @param tvPoint
     * @param tvText
     */
    private void setStoreCredit(String credit, String type, TextView tvPoint, TextView tvText) {
        tvPoint.setText(credit);
        if (type.equals("low")) {
            tvPoint.setTextColor(getResources().getColor(R.color.nc_green));
            tvText.setText("低");
            tvText.setBackgroundColor(getResources().getColor(R.color.nc_green));
        }
        if (type.equals("equal")) {
            tvPoint.setTextColor(getResources().getColor(R.color.nc_red));
            tvText.setText("平");
            tvText.setBackgroundColor(getResources().getColor(R.color.nc_red));
        }
        if (type.equals("high")) {
            tvPoint.setTextColor(getResources().getColor(R.color.nc_red));
            tvText.setText("高");
            tvText.setBackgroundColor(getResources().getColor(R.color.nc_red));
        }
    }


    /**
     * 显示配送区域信息
     *
     * @param goodsHairInfo
     */
    private void showHairInfo(GoodsHairInfo goodsHairInfo) {
        btnHairAreaName.setText(myApplication.getAreaName());//地址名称
        Log.d("Goods", myApplication.getAreaName());
        tvHairIfStoreCn.setText(goodsHairInfo.getIfStoreCn());//有货无货判断
        tvHairContent.setText(goodsHairInfo.getContent());//运费信息
        // 无货状态下不能购买和添加购物车
        if(goodsHairInfo.getIfStoreCn().equals("无货")){
            addCartID.setActivated(false);
            buyStepID.setActivated(false);
        } else {
            addCartID.setActivated(true);
            buyStepID.setActivated(true);
        }
    }

    /**
     * 显示商品信息
     *
     * @param goodsBean
     * @throws JSONException
     */
    private void showGoodsInfo(GoodsDetails goodsBean) throws JSONException {
        goodsName = goodsBean.getGoods_name();
        goodsStorage = goodsBean.getGoods_storage();
        ifcart = goodsBean.getCart();
        if (!ifcart.equals("1")) {
            addCartID.setVisibility(View.GONE);
        } else {
            addCartID.setVisibility(View.VISIBLE);
        }

        mobile_body = goodsBean.getMobile_body();//
        goods_attr = goodsBean.getGoods_attr();//产品规格


        //显示商品名称
        goodsNameID.setText(goodsBean.getGoods_name() == null ? "" : goodsBean.getGoods_name());

        //判断是否显示商品说明
        if (goodsBean.getGoods_jingle() != null && !goodsBean.getGoods_jingle().equals("") && !goodsBean.getGoods_jingle().equals("null")) {
            goodsJingleID.setVisibility(View.VISIBLE);
            goodsJingleID.setText(Html.fromHtml(goodsBean.getGoods_jingle() == null ? "" : goodsBean.getGoods_jingle()));
        } else {
            goodsJingleID.setVisibility(View.GONE);
        }

        //标示商品类型
        if (goodsBean.getIs_appoint().equals("1")) {
            tvGoodsType.setText(R.string.text_appoint);
            tvGoodsType.setVisibility(View.VISIBLE);
            tvGoodsType.setBackgroundResource(R.color.text_yuyue);
        } else if (goodsBean.getIs_fcode().equals("1")) {
            tvGoodsType.setText(R.string.text_fcode);
            tvGoodsType.setVisibility(View.VISIBLE);
            tvGoodsType.setBackgroundResource(R.color.text_fcode);
        } else if (goodsBean.getIs_presell().equals("1")) {
            tvGoodsType.setText(R.string.text_presell);
            tvGoodsType.setVisibility(View.VISIBLE);
            tvGoodsType.setBackgroundResource(R.color.text_yushou);
        } else if (goodsBean.getIs_virtual().equals("1")) {
            tvGoodsType.setText(R.string.text_virtual);
            tvGoodsType.setVisibility(View.VISIBLE);
            tvGoodsType.setBackgroundResource(R.color.text_xuni);
        }

        // 20161026 默认显示市场价
        tvGoodsMarketPrice.setText("￥" + (goodsBean.getGoods_marketprice() == null ? "" : goodsBean.getGoods_marketprice()));
        tvGoodsMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        //显示商品价格
        if (goodsBean.getPromotion_price() != null && !goodsBean.getPromotion_price().equals("") && !goodsBean.getPromotion_price().equals("null")) {
            goodsPrice = goodsBean.getPromotion_price();
            goodsPriceID.setText("￥" + (goodsBean.getPromotion_price() == null ? "" : goodsBean.getPromotion_price()));
            //20161026 市场价默认跟随促销显示 修改为默认显示
            /*tvGoodsMarketPrice.setText("￥" + (goodsBean.getGoods_price() == null ? "" : goodsBean.getGoods_price()));
            tvGoodsMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);*/
            //显示促销
            if (goodsBean.getPromotion_type().equals("sole")) {
                tvGoodsType.setText("");
                tvGoodsType.setVisibility(View.VISIBLE);
                tvGoodsType.setBackgroundResource(R.drawable.nc_icon_mobile_price);
            } else if (goodsBean.getPromotion_type().equals("groupbuy")) {
                tvGoodsType.setText(R.string.text_groupbuy);
                tvGoodsType.setVisibility(View.VISIBLE);
                tvGoodsType.setBackgroundResource(R.color.text_tuangou);
            } else if (goodsBean.getPromotion_type().equals("xianshi")) {
                tvGoodsType.setText(R.string.text_xianshi);
                tvGoodsType.setVisibility(View.VISIBLE);
                tvGoodsType.setBackgroundResource(R.color.text_xianshi);
            }
        } else {
            goodsPrice = goodsBean.getGoods_price();
            goodsPriceID.setText("￥" + (goodsBean.getGoods_price() == null ? "0" : goodsBean.getGoods_price()));
        }

        //显示商品销量数量
        goodsSalenumID.setText(goodsBean.getGoods_salenum() == null ? "0" : goodsBean.getGoods_salenum() + "件");

        //是否是F吗商品
        is_fcode = goodsBean.getIs_fcode() == null ? "0" : goodsBean.getIs_fcode();

        //是否是虚拟商品
        is_virtual = goodsBean.getIs_virtual() == null ? "0" : goodsBean.getIs_virtual();


        //显示规格值
        String specValue = "";
        if (goodsBean.getGoods_spec() != null && !goodsBean.getGoods_spec().equals("") && !goodsBean.getGoods_spec().equals("null")) {
            JSONObject jsonGoods_spec = new JSONObject(goodsBean.getGoods_spec());
            Iterator<?> itName = jsonGoods_spec.keys();
            while (itName.hasNext()) {
                String specID = itName.next().toString();
                String specV = jsonGoods_spec.getString(specID);
                specValue += "," + specV;
            }
            specString = specValue.replaceFirst(",", "");
        } else {
            specString = "";
        }

        setSpecStirng();
    }

    /**
     * 加载O2o商家分店地址
     */
    private void loadStoreO2oInfo() {
        GpsInfo gpsInfo = ShopHelper.getGpsInfo(getActivity());
        String url = Constants.URL_GOODS_STORE_O2O_ADDR + "&store_id=" + store_id + "&lng=" + String.valueOf(gpsInfo.getLng()) + "&lat=" + String.valueOf(gpsInfo.getLat());
        RemoteDataHandler.asyncDataStringGet(url, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        storeO2oAddressString = obj.optString("addr_list");
                        ArrayList<StoreO2oAddressInfo> storeO2oAddressList = StoreO2oAddressInfo.newInstanceList(storeO2oAddressString);
                        if (storeO2oAddressList.size() > 0) {
                            final StoreO2oAddressInfo storeO2oAddressInfo = storeO2oAddressList.get(0);
                            llStoreO2oItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), BaiduMapActivity.class);
                                    ArrayList<GpsInfo> gpsList = new ArrayList<GpsInfo>();
                                    double lng = Double.valueOf(storeO2oAddressInfo.getLng());
                                    double lat = Double.valueOf(storeO2oAddressInfo.getLat());
                                    gpsList.add(new GpsInfo(lng, lat));
                                    intent.putParcelableArrayListExtra("gps_list", gpsList);
                                    startActivity(intent);
                                }
                            });
                            tvStoreO2oName.setText(storeO2oAddressInfo.getName());
                            tvStoreO2oAddress.setText(storeO2oAddressInfo.getAddress());
                            btnStoreO2oAllAdress.setText("查看全部" + storeO2oAddressList.size() + "家分店地址");
                            storeO2oPhone = storeO2oAddressInfo.getPhone();
                            llStoreO2o.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 设置规格文字信息
     */
    private void setSpecStirng() {
        if (specString.equals("")) {
            specNameID.setText("默认 x" + goodsNum);
        } else {
            specNameID.setText(specString + " x" + goodsNum);
        }
    }

    /**
     * 显示促销信息
     *
     * @param mansong_info
     * @param gift_array
     */
    private void showPromotion(String cou_info, String mansong_info, String gift_array) {
        boolean mansongFlag = false;
        boolean giftFlag = false;
        boolean couFlag = false;

        // 加价购
        if (cou_info != null && !cou_info.equals("") && !cou_info.equals("[]") && !cou_info.equals("null")) {
            couFlag = true;
            llJiaJiaGou.setVisibility(View.VISIBLE);

            ArrayList<CouInFo> couInFos = CouInFo.newInstanceList(cou_info);
            CouListViewAdapter couListViewAdapter = new CouListViewAdapter(getActivity());
            couListViewAdapter.setList(couInFos);
            lvJiaJiaGou.setAdapter(couListViewAdapter);
        } else {
            llJiaJiaGou.setVisibility(View.GONE);
        }

        //显示满即送
        if (mansong_info != null && !mansong_info.equals("") && !mansong_info.equals("[]") && !mansong_info.equals("null")) {
            mansongFlag = true;
            llManSong.setVisibility(View.VISIBLE);

            ManSongInFo manSongInFo = ManSongInFo.newInstanceList(mansong_info);
            ArrayList<ManSongRules> mRules = ManSongRules.newInstanceList(manSongInFo.getRules());
            ManSongRuleListViewAdapter manSongRuleListViewAdapter = new ManSongRuleListViewAdapter(getActivity());
            manSongRuleListViewAdapter.setList(mRules);
            lvManSong.setAdapter(manSongRuleListViewAdapter);
        } else {
            llManSong.setVisibility(View.GONE);
        }

        //显示赠品
        if (gift_array != null && !gift_array.equals("") && !gift_array.equals("[]") && !gift_array.equals("null")) {
            giftFlag = true;
            llGift.setVisibility(View.VISIBLE);

            ArrayList<GiftArrayList> giftArrayLists = GiftArrayList.newInstanceList(gift_array);
            final GiftListViewAdapter giftListViewAdapter = new GiftListViewAdapter(getActivity());
            giftListViewAdapter.setList(giftArrayLists);
            lvGift.setAdapter(giftListViewAdapter);
            lvGift.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    GiftArrayList gift = (GiftArrayList) giftListViewAdapter.getItem(i);
                    Intent intent = new Intent(getActivity(), GoodsDetailsActivity.class);
                    intent.putExtra("goods_id", gift.getGift_goodsid());
                    startActivity(intent);
                }
            });

            // 优惠套装


            // 限时折扣

        } else {
            llGift.setVisibility(View.GONE);
        }

        if (mansongFlag || giftFlag || couFlag) {
            llPromotion.setVisibility(View.VISIBLE);
        } else {
            llPromotion.setVisibility(View.GONE);
        }
    }

    /**
     * 收藏按钮点击
     */
    public void btnGoodsFavClick(View view) {
        if (btnGoodsFav.isSelected()) {
            //取消收藏
            setGoodsFav(Constants.URL_DELETE_FAV, false);
        } else {
            //收藏商品
            setGoodsFav(Constants.URL_ADD_FAV, true);
        }
    }

    /**
     * 设置收藏商品
     *
     * @param url      接口地址
     * @param btnState 收藏商品为true 取消收藏为false
     */
    private void setGoodsFav(String url, final Boolean btnState) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (btnState) {
            params.put("goods_id", goodsId);
        } else {
            params.put("fav_id", goodsId);
        }
        params.put("key", myApplication.getLoginKey());

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    btnGoodsFav.setSelected(btnState);
                } else {
                    ShopHelper.showApiError(getActivity(), json);
                }
            }
        });
    }


    /**
     * 添加购物车
     */
    public void addCart(final Context context, final MyShopApplication myApplication, String goodsId, int goodsNum) {
        String url = Constants.URL_ADD_CART;

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("goods_id", goodsId);
        params.put("key", myApplication.getLoginKey());
        params.put("quantity", goodsNum + "");

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    Toast.makeText(context, "添加购物车成功", Toast.LENGTH_SHORT).show();
                    setCartNumShow();
                    Intent intent = new Intent(Constants.SHOW_CART_NUM);
                    context.sendBroadcast(intent);
                } else {
                    ShopHelper.showApiError(context, json);
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.addCartID:

                if(!addCartID.isActivated()){
                    return;
                }

                if (flag == false) {
                    ShopHelper.showApiError(getActivity(), errorMsg);
                } else {
                    initSpec(goodsDetails, specList);
                    pwSpec.showPopupWindow();
                }

                /*if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {

                    addCart(getActivity(), myApplication, goodsId, goodsNum);

                    Intent i=new Intent(Constants.SHOW_CART_NUM);
                    getActivity().sendBroadcast(i);
                }*/

                break;
            case R.id.buyStepID:

                if(!buyStepID.isActivated()){
                    return;
                }

                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    if (flag == false) {
                        ShopHelper.showApiError(getActivity(), errorMsg);
                    } else {
                        initSpec(goodsDetails, specList);
                        pwSpec.showPopupWindow();
                    }

                    /*if (is_virtual.equals("1")) {
                        intent = new Intent(getActivity(), VBuyStep1Activity.class);
                        intent.putExtra("is_fcode", is_fcode);
                        intent.putExtra("ifcart", "0");
                        intent.putExtra("goodscount", goodsNum);
                        intent.putExtra("cart_id", goodsId);
                    } else {
                        intent = new Intent(getActivity(), BuyStep1Activity.class);
                        intent.putExtra("is_fcode", is_fcode);
                        intent.putExtra("ifcart", "0");
                        intent.putExtra("cart_id", goodsId + "|" + goodsNum);
                    }*/
                }
                break;

            case R.id.specNameID:
                pwSpec.showPopupWindow();
                break;

            case R.id.imID:
                ShopHelper.showIm(getActivity(), myApplication, t_id, t_name);
                break;

            case R.id.showCartLayoutID:
                //跳转购物车
                intent = new Intent(getActivity(), MainFragmentManager.class);
                //广播通知显示购物车
                myApplication.sendBroadcast(new Intent(Constants.SHOW_CART_URL));
                pwSpec.closePoperWindow();
                break;

            default:
                break;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }


    /**
     * 判断是否能够购买商品--购买实物商品
     */
    private void ifCanBuyS() {
        String url = Constants.URL_BUY_STEP1;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("cart_id", goodsId + "|" + goodsNum);
        params.put("ifcart", "0");

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    flag = true;
                } else {
                    errorMsg = json;
                }
            }
        });
    }


    /**
     * 判断是否能够购买商品--购买虚拟商品
     */
    public void ifCanBuyV() {
        String url = Constants.URL_MEMBER_VR_BUY;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("goods_id", goodsId);
        params.put("quantity", String.valueOf(goodsNum));

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    flag = true;
                } else {
                    errorMsg = json;
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadingGoodsDetailsData();//初始化加载数据
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        setImageBackground(i % imageList.size());
        currentVpIndex = i;
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
