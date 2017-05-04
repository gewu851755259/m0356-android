package cn.m0356.shop.ui.mine;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.readystatesoftware.viewbadger.BadgeView;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.IsSellerBean;
import cn.m0356.shop.bean.Mine;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.SellerLoginDialog;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.type.AddressListActivity;
import cn.m0356.shop.ui.type.GoodsBrowseActivity;

/**
 * 我的
 *
 * @author dqw
 * @Time 2015-7-6
 */
public class MineFragment extends Fragment {

    private MyShopApplication myApplication;

    private LinearLayout llLogin;
    private LinearLayout llMemberInfo;
    private ImageView ivMemberAvatar;
    private TextView tvMemberName;
    private TextView tvMemberInvite;
    private ImageView ivFavGoods;
    private TextView tvFavGoodsCount;
    private ImageView ivFavStore;
    private TextView tvFavStoreCount;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getRoundedBitmapDisplayImageOptions(100);
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private LinearLayout llOrderNew;
    private View viewLayout;

    // 小圆点需要
    private View view1;
    private View view2;
    private View view3;
    private View view4;

    private BadgeView noPayView;
    private BadgeView noReceiptView;
    private BadgeView noTakesView;
    private BadgeView noEvalView;

    private SellerLoginDialog dialog;
    private RelativeLayout rlSeller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewLayout = inflater.inflate(R.layout.main_mine_view, container, false);
        MyExceptionHandler.getInstance().setContext(getActivity());
        myApplication = (MyShopApplication) getActivity().getApplicationContext();

        initSettingButton(viewLayout);
        initMemberButton(viewLayout);
        initOrderButton(viewLayout);
        initAssetButton(viewLayout);

        // 初始化小圆点
        initBadge();

        return viewLayout;
    }


    private void initBadge() {
        view1 = viewLayout.findViewById(R.id.ll_wrap_1);
        view2 = viewLayout.findViewById(R.id.ll_wrap_2);
        view3 = viewLayout.findViewById(R.id.ll_wrap_3);
        view4 = viewLayout.findViewById(R.id.ll_wrap_4);
        noPayView = new BadgeView(getActivity(), view1);
        noPayView.setTextSize(10);
        noPayView.setVisibility(View.GONE);
        noPayView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//        noPayView.show();

        noReceiptView = new BadgeView(getActivity(), view2);
        noReceiptView.setTextSize(10);
        noReceiptView.setVisibility(View.GONE);
        noReceiptView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//        noReceiptView.show();

        noTakesView = new BadgeView(getActivity(), view3);
        noTakesView.setTextSize(10);
        noTakesView.setVisibility(View.GONE);
        noTakesView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//        noTakesView.show();

        noEvalView = new BadgeView(getActivity(), view4);
        noEvalView.setTextSize(10);
        noEvalView.setVisibility(View.GONE);
        noEvalView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//        noEvalView.show();
    }

    /**
     * 初始化设置相关按钮
     *
     * @param viewLayout
     */
    private void initSettingButton(View viewLayout) {
        //设置
        Button btnSetting = (Button) viewLayout.findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                }
            }
        });

        //设置2
        RelativeLayout rlSetting = (RelativeLayout) viewLayout.findViewById(R.id.rlSetting);
        rlSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                }
            }
        });

        //IM
        Button btnIm = (Button) viewLayout.findViewById(R.id.btnIm);
        btnIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), IMFriendsListActivity.class));
                }
            }
        });

        //登录
        llLogin = (LinearLayout) viewLayout.findViewById(R.id.llLogin);
        llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        // 后台
        rlSeller = (RelativeLayout) viewLayout.findViewById(R.id.rlSeller);
        rlSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterSellerManager();
            }
        });

        llMemberInfo = (LinearLayout) viewLayout.findViewById(R.id.llMemberInfo);
        ivMemberAvatar = (ImageView) viewLayout.findViewById(R.id.ivMemberAvatar);
        tvMemberName = (TextView) viewLayout.findViewById(R.id.tvMemberName);
        tvMemberInvite=(TextView) viewLayout.findViewById(R.id.tvMemberInvite);
        // 会员头像暂时作为积分中心入口
//        ivMemberAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), PointCenterActivity.class));
//            }
//        });
    }

    private void enterSellerManager() {
        if(dialog == null)
            dialog = new SellerLoginDialog(getActivity());
        dialog.show();
    }

    /**
     * 初始化用户信息相关按钮
     *
     * @param viewLayout
     */
    private void initMemberButton(View viewLayout) {
        //商品收藏
        LinearLayout llFavGoods = (LinearLayout) viewLayout.findViewById(R.id.llFavGoods);
        llFavGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    //startActivity(new Intent(getActivity(), FavGoodsListActivity.class));
                    startActivity(new Intent(getActivity(), FavListActivity.class).putExtra("page", 0));
                }
            }
        });
        ivFavGoods = (ImageView) viewLayout.findViewById(R.id.ivFavGoods);
        tvFavGoodsCount = (TextView) viewLayout.findViewById(R.id.tvFavGoodsCount);

        //收藏店铺
        LinearLayout llFavStore = (LinearLayout) viewLayout.findViewById(R.id.llFavStore);
        llFavStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), FavListActivity.class).putExtra("page", 1));
//                    startActivity(new Intent(getActivity(), FavStoreListActivity.class).putExtra("page", 1));
                }
            }
        });
        ivFavStore = (ImageView) viewLayout.findViewById(R.id.ivFavStore);
        tvFavStoreCount = (TextView) viewLayout.findViewById(R.id.tvFavStoreCount);

        //我的足迹
        LinearLayout llZuji = (LinearLayout) viewLayout.findViewById(R.id.llZuji);
        llZuji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), GoodsBrowseActivity.class));
                }
            }
        });

        //收货地址
        RelativeLayout rlAddress = (RelativeLayout) viewLayout.findViewById(R.id.rlAddress);
        rlAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    Intent intent = new Intent(getActivity(), AddressListActivity.class);
                    intent.putExtra("addressFlag", "0");
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 初始化订单相关按钮
     *
     * @param viewLayout
     */
    private void initOrderButton(View viewLayout) {
        //全部订单
        Button btnOrderAll = (Button) viewLayout.findViewById(R.id.btnOrderAll);
        btnOrderAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrderList("");
            }
        });
        //待付款订单
        llOrderNew = (LinearLayout) viewLayout.findViewById(R.id.llOrderNew);
        setOrderButtonEvent(llOrderNew, "state_new");
        //待收货
        LinearLayout llOrderSend = (LinearLayout) viewLayout.findViewById(R.id.llOrderSend);
        setOrderButtonEvent(llOrderSend, "state_send");
        //待自提订单
        LinearLayout llOrderNotakes = (LinearLayout) viewLayout.findViewById(R.id.llOrderNotakes);
        setOrderButtonEvent(llOrderNotakes, "state_notakes");
        //待评价订单
        LinearLayout llOrderNoeval = (LinearLayout) viewLayout.findViewById(R.id.llOrderNoeval);
        setOrderButtonEvent(llOrderNoeval, "state_noeval");
        //退款退货
        LinearLayout llRefund = (LinearLayout) viewLayout.findViewById(R.id.llRefund);
        llRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getActivity(),OrderExchangeListActivity.class));
                //验证用户是否登录
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(),OrderExchangeListActivity.class));
                }
            }
        });
    }

    /**
     * 初始化财产相关按钮
     *
     * @param viewLayout
     */
    private void initAssetButton(View viewLayout) {
        //全部财产
        Button btnMyAsset = (Button) viewLayout.findViewById(R.id.btnMyAsset);
        btnMyAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), MyAssetActivity.class));
                }
            }
        });

        //预存款
        LinearLayout llPredeposit = (LinearLayout) viewLayout.findViewById(R.id.llPredeposit);
        llPredeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), PredepositActivity.class));
                }
            }
        });

        //充值卡
        LinearLayout llRechargeCard = (LinearLayout) viewLayout.findViewById(R.id.llRechargeCard);
        llRechargeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), RechargeCardLogActivity.class));
                }
            }
        });

        //代金券
        LinearLayout llVoucherList = (LinearLayout) viewLayout.findViewById(R.id.llVoucherList);
        llVoucherList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), VoucherListActivity.class));
                }
            }
        });

        //红包
        LinearLayout llRedpacket = (LinearLayout) viewLayout.findViewById(R.id.llRedpacketList);
        llRedpacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
//                    startActivity(new Intent(getActivity(), RedpacketListActivity.class));
                    startActivity(new Intent(getActivity(), RedpacketListActivityNew.class));
                }
            }
        });

        //积分
        LinearLayout llPointLog = (LinearLayout) viewLayout.findViewById(R.id.llPointLog);
        llPointLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
                    startActivity(new Intent(getActivity(), PointLogActivity.class));
                }
            }
        });
    }

    /**
     * 设置订单按钮事件
     */
    private void setOrderButtonEvent(LinearLayout btn, final String stateType) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrderList(stateType);
            }
        });
    }

    /**
     * 显示订单列表
     */
    private void showOrderList(String stateType) {
        if (ShopHelper.isLogin(getActivity(), myApplication.getLoginKey())) {
            Intent intent = new Intent(getActivity(), OrderListActivity.class);
            intent.putExtra("state_type", stateType);
            startActivity(intent);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // TODO: 2016/11/28 不合理
        registerBoradcastReceiver();
        isSeller();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.LOGIN_SUCCESS_URL)) {
                loadMemberInfo();
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.LOGIN_SUCCESS_URL);
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);  //注册广播
    }

    /**
     * 检测是否登录
     */
    public void setLoginInfo() {
        String loginKey = myApplication.getLoginKey();
        if (loginKey != null && !loginKey.equals("")) {
            llMemberInfo.setVisibility(View.VISIBLE);
            llLogin.setVisibility(View.GONE);
            ivFavGoods.setVisibility(View.GONE);
            ivFavStore.setVisibility(View.GONE);
            tvFavGoodsCount.setVisibility(View.VISIBLE);
            tvFavStoreCount.setVisibility(View.VISIBLE);
            loadMemberInfo();
        } else {
            llMemberInfo.setVisibility(View.GONE);
            llLogin.setVisibility(View.VISIBLE);
            ivFavGoods.setVisibility(View.VISIBLE);
            ivFavStore.setVisibility(View.VISIBLE);
            tvFavGoodsCount.setVisibility(View.GONE);
            tvFavStoreCount.setVisibility(View.GONE);
            updateBadge("0", "0", "0", "0");
        }
    }

    /**
     * 初始化加载我的信息
     */
    public void loadMemberInfo() {
        String url = Constants.URL_MYSTOIRE;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {

                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        String objJson = obj.getString("member_info");
                        Mine bean = Mine.newInstanceList(objJson);

                        if (bean != null) {
                            tvMemberName.setText(bean.getMemberName() == null ? "" : bean.getMemberName());
                            tvMemberInvite.setText(bean.getMemberInvite() == null ? "" : "邀请码:"+bean.getMemberInvite());
                            Log.d("login", bean.getMemberInvite());
                            imageLoader.displayImage(bean.getMemberAvatar(), ivMemberAvatar, options, animateFirstListener);
                            tvFavGoodsCount.setText(bean.getFavGoods() == null ? "0" : bean.getFavGoods());
                            tvFavStoreCount.setText(bean.getFavStore() == null ? "0" : bean.getFavStore());

                            // 添加小圆点   20161025
                            // BadgeView
                            // 待付款 order_nopay_count
                            // 待收货 order_noreceipt_count 待发货?
                            // 待自提 order_notakes_count
                            // 待评价 order_noeval_count
                            updateBadge(bean.getOrderNopay(), bean.getOrderNoreceipt(), bean.getOrderNotakes(), bean.getOrderNoeval());

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(getActivity(), json);
                }
            }
        });
    }

//intent = new Intent(getActivity(), VoucherListActivity.class);

    private void updateBadge(String noPay, String noReceipt, String noTakes, String noEval){
        if(Integer.parseInt(noPay) > 0) {
            noPayView.setText(noPay);
            noPayView.show();
        } else {
            noPayView.hide();
        }

        if(Integer.parseInt(noReceipt) > 0) {
            noReceiptView.setText(noReceipt);
            noReceiptView.show();
        } else {
            noReceiptView.hide();
        }

        if(Integer.parseInt(noTakes) > 0) {
            noTakesView.setText(noTakes);
            noTakesView.show();
        } else {
            noTakesView.hide();
        }

        if(Integer.parseInt(noEval) > 0) {
            noEvalView.setText(noEval);
            noEvalView.show();
        } else {
            noEvalView.hide();
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        setLoginInfo();
        StatService.onPageStart(MyShopApplication.context, "主界面-我");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(MyShopApplication.context, "主界面-我");
    }

    private void isSeller(){
        if(!ShopHelper.isLoginNoSkip(myApplication.getLoginKey()))
            return;
        HashMap<String, String> datas = new HashMap<String, String>();
        datas.put("member_id", myApplication.getMemberID());
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_IS_SELLER, datas, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){
                    Gson gson = new Gson();
                    IsSellerBean isSellerBean = gson.fromJson(data.getJson(), IsSellerBean.class);
                    isSellerBean.result = true;
                    if(isSellerBean.result){ // 店铺状态正常
                        if(rlSeller.getVisibility() == View.GONE){
                            rlSeller.setVisibility(View.VISIBLE);
                        }
                    } else {
                        /*Toast.makeText(MyShopApplication.context, isSellerBean.store_state_info +
                                "，原因：" + isSellerBean.store_close_info, Toast.LENGTH_SHORT).show();*/
                        if(rlSeller.getVisibility() == View.VISIBLE){
                            rlSeller.setVisibility(View.GONE);
                        }
                    }
                } else {
                    ShopHelper.showApiError(getActivity(), data.getJson() + "--");
                }
            }
        });
    }

}
