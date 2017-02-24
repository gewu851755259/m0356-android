package cn.m0356.shop.ui.type;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.pay.PayDemoActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.m0356.shop.BaseActivity;
import cn.m0356.shop.R;
import cn.m0356.shop.adapter.RpacketListSpinnerAdapter;
import cn.m0356.shop.adapter.StoreVoucherListViewAdapter;
import cn.m0356.shop.bean.AddressDetails;
import cn.m0356.shop.bean.BuyBean;
import cn.m0356.shop.bean.BuyStep1;
import cn.m0356.shop.bean.CartList;
import cn.m0356.shop.bean.InvoiceInFO;
import cn.m0356.shop.bean.ManSongRulesInFo;
import cn.m0356.shop.bean.PlayGoodsList;
import cn.m0356.shop.bean.RpacketInfo;
import cn.m0356.shop.bean.StoreVoucherList;
import cn.m0356.shop.bean.UpdateAddress;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.MathConvert;
import cn.m0356.shop.common.MyExceptionHandler;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.RemoteDataHandler.Callback;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.mine.OrderListActivity;
import cn.m0356.shop.ui.mine.PayMentWebActivity;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 购买第一步
 *
 * @author dqw
 * @Time 2015/8/19
 */
public class BuyStep1Activity extends BaseActivity implements OnClickListener {
    private FrameLayout flMain;

    private String is_fcode;//是否为F码商品 1是 0否

    private String ifcart;//购物车购买标志 1购物车 0不是

    private String cart_id;//购买参数

    private boolean showAvailableRCBalance = false;//标识是否显示充值卡

    private boolean showAvailablePredeposit = false;//标识是否显示预存款

    private double goods_total = 0.00;//总价

    private double goods_freight = 0.00;//运费

    private double goods_voucher = 0.00;//折扣价格

    private double rpacket = 0.00;

    private String rpacketId = "";

    private String freight_hash; //记录运费hash

    private String offpay_hash; //货到付款hash

    private String offpay_hash_batch; //店铺是否支持货到付款hash

    private String inv_id;//记录发票ID

    private String address_id;//记录收货地址ID

    private String vat_hash;//记录发票信息hash

    //private String if_pd_pay = "0";//记录是否充值卡支付  1-使用 0-不使用

    //private String if_rcb_pay = "0";//记录是否预存款支付 1-使用 0-不使用

    private String pay_name = "online";//记录付款方式，可选值 online(线上付款) offline(货到付款)

    private MyShopApplication myApplication;

    private TextView areaInfoID, addressID, trueNameID, mobPhoneID, invInfoID, noAreaInfoID, tvGoodsFreight, textViewGoodsTotal, textVoucher, textviewAllPrice, tvRpacket, tvRpacketButton;

    private RadioButton ifshowOnpayID, ifshowOffpayID;

    private LinearLayout predepositLayoutID, storeCartListID, addressInFoLayoutID, llRpacket;

    //private CheckBox availablePredepositID, availableRCBalanceID;

    private Button commitID;

    private EditText /*editPasswordID, */editFCodeID;

    private PopupWindow popupWindow; // 声明PopupWindow对象的引用

    private HashMap<String, StoreVoucherList> storeVoucherLists = new HashMap<String, StoreVoucherList>();//记录选中代金券

    private ArrayList<RpacketInfo> rpacketList;
    private ArrayList<RpacketInfo> rpacketListUseable;
    private RpacketListSpinnerAdapter rpacketAdapter;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private CheckBox checkZt; // hxf

    JSONObject jsonObj;
    JSONObject jsonZt;   //hxf

    private Map<String, String> fre_map = new HashMap<String, String>();  // hxf

    private Map<String, String> ziti_map = new HashMap<String, String>();  // hxf

    private String owndelivery; // hxf  自提
    private ArrayList<String> noSendId = new ArrayList<String>();
    int storeNum = 0;
    private AlertDialog menuDialog;
    private GridView menuGrid;
    private BuyStep1 buySetp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_step1_view);
        MyExceptionHandler.getInstance().setContext(this);
        myApplication = (MyShopApplication) getApplicationContext();

        flMain = (FrameLayout) findViewById(R.id.flMain);
        flMain.getForeground().setAlpha(0);

        is_fcode = getIntent().getStringExtra("is_fcode");
        int ic = getIntent().getIntExtra("ifcart", 0);
        ifcart = String.valueOf(ic);
        cart_id = getIntent().getStringExtra("cart_id");

        initViewID();

        setCommonHeader("提交订单");
    }

    /**
     * 初始化注册控件ID
     */
    public void initViewID() {
        editFCodeID = (EditText) findViewById(R.id.editFCodeID);
        areaInfoID = (TextView) findViewById(R.id.areaInfoID);
        addressID = (TextView) findViewById(R.id.addressID);
        trueNameID = (TextView) findViewById(R.id.trueNameID);
        mobPhoneID = (TextView) findViewById(R.id.mobPhoneID);
        invInfoID = (TextView) findViewById(R.id.invInfoID);
        noAreaInfoID = (TextView) findViewById(R.id.noAreaInfoID);
        textVoucher = (TextView) findViewById(R.id.textVoucher);
        //editPasswordID = (EditText) findViewById(R.id.editPasswordID);
        textviewAllPrice = (TextView) findViewById(R.id.textviewAllPrice);
        tvGoodsFreight = (TextView) findViewById(R.id.tvGoodsFreight);
        textViewGoodsTotal = (TextView) findViewById(R.id.textViewGoodsTotal);
        ifshowOffpayID = (RadioButton) findViewById(R.id.ifshowOffpayID);
        ifshowOnpayID = (RadioButton) findViewById(R.id.ifshowOnpayID);
        predepositLayoutID = (LinearLayout) findViewById(R.id.predepositLayoutID);
        storeCartListID = (LinearLayout) findViewById(R.id.storeCartListID);
        addressInFoLayoutID = (LinearLayout) findViewById(R.id.addressInFoLayoutID);
        //availablePredepositID = (CheckBox) findViewById(R.id.availablePredepositID);
        //availableRCBalanceID = (CheckBox) findViewById(R.id.availableRCBalanceID);

        LinearLayout fCodeLayoutID = (LinearLayout) findViewById(R.id.fCodeLayoutID);

        commitID = (Button) findViewById(R.id.commitID);

        if (is_fcode != null && is_fcode.equals("1")) {
            fCodeLayoutID.setVisibility(View.VISIBLE);
            editFCodeID.setVisibility(View.VISIBLE);
        } else {
            fCodeLayoutID.setVisibility(View.GONE);
            editFCodeID.setVisibility(View.GONE);
        }

        MyifshowOnpayRadioButtonClickListener onpayRadioButtonClickListener = new MyifshowOnpayRadioButtonClickListener();
        ifshowOffpayID.setOnClickListener(onpayRadioButtonClickListener);
        ifshowOnpayID.setOnClickListener(onpayRadioButtonClickListener);

        commitID.setOnClickListener(this);
        invInfoID.setOnClickListener(this);
        noAreaInfoID.setOnClickListener(this);
        addressInFoLayoutID.setOnClickListener(this);

        loadingBuyStep1Data();//加载购买一数据

/*        availablePredepositID.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if_pd_pay = "1";
                } else {
                    if_pd_pay = "0";
                }
            }
        });

        availableRCBalanceID.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if_rcb_pay = "1";
                } else {
                    if_rcb_pay = "0";
                }
            }
        });*/

        //红包
        llRpacket = (LinearLayout) findViewById(R.id.llRpacket);
        tvRpacket = (TextView) findViewById(R.id.tvRpacket);
        tvRpacketButton = (TextView) findViewById(R.id.tvRpacketButton);
        rpacketListUseable = new ArrayList<RpacketInfo>();
        tvRpacketButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showRpacketWindow();
            }
        });

    }

    //红包选择弹出窗口
    private void showRpacketWindow() {
        View popupView = BuyStep1Activity.this.getLayoutInflater().inflate(R.layout.popupwindow_rpacket_view, null);

        final PopupWindow mPopupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        //点空白关闭窗口
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        ListView llRpacketList = (ListView) popupView.findViewById(R.id.lvRpacketList);
        rpacketAdapter = new RpacketListSpinnerAdapter(BuyStep1Activity.this);
        rpacketAdapter.setRpacketLists(rpacketListUseable);
        llRpacketList.setAdapter(rpacketAdapter);
        llRpacketList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RpacketInfo rpacketInfo = rpacketListUseable.get(i);
                rpacket = rpacketInfo.getRpacketPrice();
                rpacketId = rpacketInfo.getRpacketId();
                tvRpacketButton.setText(rpacketInfo.getRpacketDesc());
                upPriceUIData();
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                flMain.getForeground().setAlpha(0);
            }
        });

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        flMain.getForeground().setAlpha(150);
    }

    /**
     * 加载购买一数据
     */
    public void loadingBuyStep1Data() {
        String url = Constants.URL_BUY_STEP1;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("cart_id", cart_id);
        params.put("ifcart", ifcart);

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {

                    BuyStep1 buyStep1 = BuyStep1.newInstanceList(json);
                    if (buyStep1 != null) {
                        BuyStep1Activity.this.buySetp1 = buyStep1;
                        AddressDetails addressDetails = AddressDetails.newInstanceDetails(buyStep1.getAddress_info());

                        //记录运费hash
                        freight_hash = buyStep1.getFreight_hash();

                        //记录发票hash
                        vat_hash = buyStep1.getVat_hash();

                        //判断显示隐藏收货地址
                        if (addressDetails != null) {
                            noAreaInfoID.setVisibility(View.GONE);
                            addressInFoLayoutID.setVisibility(View.VISIBLE);

                            //记录地址ID
                            address_id = addressDetails.getAddress_id();

                            //显示收货信息
                            areaInfoID.setText(addressDetails.getArea_info() == null ? "" : addressDetails.getArea_info());
                            addressID.setText(addressDetails.getAddress() == null ? "" : addressDetails.getAddress());
                            trueNameID.setText(addressDetails.getTrue_name() == null ? "" : addressDetails.getTrue_name());
                            mobPhoneID.setText(addressDetails.getMob_phone() == null ? "" : addressDetails.getMob_phone());
                            //更新收货地址
                            updataAddress(addressDetails.getCity_id(), addressDetails.getArea_id());
                        } else {
                            noAreaInfoID.setVisibility(View.VISIBLE);
                            addressInFoLayoutID.setVisibility(View.GONE);
                        }

                        InvoiceInFO inv_info = InvoiceInFO.newInstanceList(buyStep1.getInv_info());

                        if (inv_info != null) {

                            //记录发票ID
                            inv_id = inv_info.getInv_id() == null ? "0" : inv_info.getInv_id();

                            //显示发票信息
                            invInfoID.setText(inv_info.getContent() == null ? "" : inv_info.getContent());
                        }
//                        Log.d("BuyStep1",buyStep1.getIfshow_offpay());
                        //判断是否显示货到付款
                        if (buyStep1.getIfshow_offpay().equals("true") && buyStep1.getIfshow_offpay() != null) {
                            ifshowOffpayID.setVisibility(View.VISIBLE);

                        } else {
                            ifshowOffpayID.setVisibility(View.GONE);
                        }

                        //判断是否显示预存款
                        /*if (buyStep1.getAvailable_predeposit() != null && !buyStep1.getAvailable_predeposit().equals("null") && !buyStep1.getAvailable_predeposit().equals("")
                                && !buyStep1.getAvailable_predeposit().equals("0") && !buyStep1.getAvailable_predeposit().equals("0.00")) {
                            showAvailablePredeposit = true;
                            availablePredepositID.setVisibility(View.VISIBLE);
                        } else {
                            showAvailablePredeposit = false;
                            availablePredepositID.setVisibility(View.GONE);
                        }*/

                        //判断是否显示充值卡
                        /*if (buyStep1.getAvailable_rc_balance() != null && !buyStep1.getAvailable_rc_balance().equals("null") && !buyStep1.getAvailable_rc_balance().equals("")
                                && !buyStep1.getAvailable_rc_balance().equals("0") && !buyStep1.getAvailable_rc_balance().equals("0.00")) {
                            showAvailableRCBalance = true;
                            availableRCBalanceID.setVisibility(View.VISIBLE);
                        } else {
                            showAvailableRCBalance = false;
                            availableRCBalanceID.setVisibility(View.GONE);
                        }*/

                        if (showAvailablePredeposit || showAvailableRCBalance) {
                            predepositLayoutID.setVisibility(View.VISIBLE);
                        } else {
                            predepositLayoutID.setVisibility(View.GONE);
                        }

                        //显示购买商品列表
                        try {
                            jsonObj = new JSONObject(buyStep1.getStore_cart_list());
                            jsonZt = new JSONObject(buyStep1.getZiti());

                            showGoodsList();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    //判断是否显示红包
                    if (buyStep1.getRpt_list() != null && !buyStep1.getRpt_list().equals("null") && !buyStep1.getRpt_list().equals("")) {
                        rpacketList = RpacketInfo.newInstanceList(buyStep1.getRpt_list());
                        updateRpacketUseable();
                    }


                    //更新价格UI
                    upPriceUIData();
                } else {
                    ShopHelper.showApiError(BuyStep1Activity.this, json);
                    LogHelper.d("huting--error1:", json);
                }
            }
        });
    }

    private void showGoodsList() {
        try {

            storeCartListID.removeAllViews();
            goods_freight = 0.00;
            goods_total = 0.00;
            Log.d("buy", jsonZt.toString());
            Iterator<?> iterator = jsonObj.keys();
            ArrayList<PlayGoodsList> storeCartLists = new ArrayList<PlayGoodsList>();
            while (iterator.hasNext()) {
                storeNum++;
                final String storeID = iterator.next().toString();
                String Value = jsonObj.getString(storeID);
                PlayGoodsList storecart = PlayGoodsList.newInstanceList(Value);
                ArrayList<CartList> goodList = CartList.newInstanceList(storecart.getGoods_list());
                storecart.setStore_id(storeID);
                int isziti = jsonZt.getInt(storeID);

                //添加显示店铺信息
                LinearLayout playListView = (LinearLayout) getLayoutInflater().inflate(R.layout.buy_step1_playgoods_view, null);
                LinearLayout goodsListLayoutID = (LinearLayout) playListView.findViewById(R.id.goodsListLayoutID);
                final Button selectVoucheID = (Button) playListView.findViewById(R.id.selectVoucheID);
                final TextView voucherPriceID = (TextView) playListView.findViewById(R.id.voucherPriceID);
                final TextView manJianID = (TextView) playListView.findViewById(R.id.manJianID);

                //hxf start  自提布局
                LinearLayout zitiView = (LinearLayout) playListView.findViewById(R.id.ll_ziti);
                // 判断店铺是否开启到店自提
                if (isziti == 1) {
                    zitiView.setVisibility(View.VISIBLE);
                } else {
                    zitiView.setVisibility(View.GONE);
                }


                checkZt = (CheckBox) playListView.findViewById(R.id.check_zt);
                checkZt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (fre_map.get(storeID) != null) {
                            if (isChecked) {
                                goods_freight = goods_freight - Double.parseDouble(fre_map.get(storeID));
                                ziti_map.put(storeID, "1");
                            } else {
                                goods_freight = goods_freight + Double.parseDouble(fre_map.get(storeID));
                                ziti_map.put(storeID, "0");
                            }
                        }
                        upPriceUIData();
                        updateRpacketUseable();
                    }
                });
                //hxf end

                TextView storeNameID = (TextView) playListView.findViewById(R.id.storeNameID);
                storeNameID.setText(storecart.getStore_name() == null ? "" : storecart.getStore_name());


                //判断显示优惠券
                String storeVoucher = storecart.getStore_voucher_list();
                if (!storeVoucher.contains("[]")) {
                    selectVoucheID.setVisibility(View.VISIBLE);
                } else {
                    selectVoucheID.setVisibility(View.GONE);
                }

                if (storeVoucher != null && !storeVoucher.equals("") && !storeVoucher.contains("[]")) {
                    JSONObject jsonVoucher = new JSONObject(storeVoucher);
                    Iterator<?> iteratorVoucher = jsonVoucher.keys();
                    final ArrayList<StoreVoucherList> Voucherlist = new ArrayList<StoreVoucherList>();
                    Voucherlist.add(new StoreVoucherList("0", storecart.getStore_id(), "0", "暂时不使用"));
                    while (iteratorVoucher.hasNext()) {
                        String voucherID = iteratorVoucher.next().toString();
                        String voucherValue = jsonVoucher.getString(voucherID);
                        StoreVoucherList Voucherbean = StoreVoucherList.newInstanceList(voucherValue);
                        Voucherlist.add(Voucherbean);
                    }

                    voucherPriceID.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getPopupWindow(v, Voucherlist, selectVoucheID, voucherPriceID);//获取PopupWindow实例
                        }
                    });

                    selectVoucheID.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getPopupWindow(v, Voucherlist, selectVoucheID, voucherPriceID);//获取PopupWindow实例
                        }
                    });

                }

                //添加显示购买商品
                for (int i = 0; i < goodList.size(); i++) {

                    CartList bean = goodList.get(i);

                    LinearLayout playListItem = (LinearLayout) getLayoutInflater().inflate(R.layout.buy_step1_playgoods_view_item, null);
                    TextView goodsNameID = (TextView) playListItem.findViewById(R.id.goodsNameID);
                    TextView goodsPriceID = (TextView) playListItem.findViewById(R.id.goodsPriceID);
                    TextView goodsNumID = (TextView) playListItem.findViewById(R.id.goodsNumID);
                    ImageView goodsPicID = (ImageView) playListItem.findViewById(R.id.goodsPicID);
                    ImageView zengpinID = (ImageView) playListItem.findViewById(R.id.zengpinID);
                    TextView tvNoSend = (TextView) playListItem.findViewById(R.id.tvNoSend);

                    goodsNameID.setText(bean.getGoods_name() == null ? "" : bean.getGoods_name());
                    goodsPriceID.setText("价格：￥" + (bean.getGoods_price() == null ? "" : bean.getGoods_price()));
                    goodsNumID.setText("数量：" + (bean.getGoods_num() == null ? "" : bean.getGoods_num()));
                    imageLoader.displayImage(bean.getGoods_image_url(), goodsPicID, options, animateFirstListener);

                    if (bean.getPremiums().equals("true")) {
                        zengpinID.setVisibility(View.VISIBLE);
                    } else {
                        zengpinID.setVisibility(View.GONE);
                    }

                    String transportId = bean.getTransport_id();
                    tvNoSend.setVisibility(View.INVISIBLE);
                    for (String tId : noSendId) {
                        if (tId.equals(transportId)) {
                            tvNoSend.setVisibility(View.VISIBLE);
                        }
                    }

                    goodsListLayoutID.addView(playListItem);

                }

                storeCartListID.addView(playListView);

                ManSongRulesInFo bean = ManSongRulesInFo.newInstanceList(storecart.getStore_mansong_rule_list());
                double allprice = Double.parseDouble(storecart.getStore_goods_total());
                double price = Double.parseDouble(bean.getPrice() == null ? "0" : bean.getPrice());
                double discount = Double.parseDouble(bean.getDiscount() == null ? "0" : bean.getDiscount());

                if (bean != null && price > 0 && discount > 0) {
                    manJianID.setText(Html.fromHtml("订单满<font color='#FF3300'>" + price + "元</font>，立减现金<font color='#339900'>" + discount + "元</font>"));
                    manJianID.setVisibility(View.VISIBLE);
                } else {
                    manJianID.setVisibility(View.GONE);
                }

                if (allprice >= price) {
                    allprice = allprice - discount;
                }

                goods_total += allprice;
                storeCartLists.add(storecart);

            }
//							aStoreCartListViewAdapter.setStoreCartLists(storeCartLists);
//							aStoreCartListViewAdapter.notifyDataSetChanged();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }


    }

    //控制红包显示
    private void updateRpacketUseable() {
        rpacketListUseable.clear();
        RpacketInfo rpacketInfo = new RpacketInfo(0.0, 0.0, "", "不使用红包");
        rpacketListUseable.add(0, rpacketInfo);
        double totalPrice = goods_total + goods_freight - goods_voucher;
        for (RpacketInfo info : rpacketList) {
            if (totalPrice > info.getRpacketLimit()) {
                rpacketListUseable.add(info);
            }
        }

        if (rpacketListUseable.size() > 1) {
            llRpacket.setVisibility(View.VISIBLE);
        } else {
            llRpacket.setVisibility(View.GONE);
        }

        rpacket = 0.0;
        rpacketId = "";
        tvRpacketButton.setText("不使用红包");
        upPriceUIData();
    }

    /**
     * 更新收货地址
     */
    public void updataAddress(String city_id, String area_id) {
        String url = Constants.URL_UPDATE_ADDRESS;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("city_id", city_id);
        params.put("area_id", area_id);
        params.put("freight_hash", freight_hash);
//        Log.d("BuyStep1", area_id);

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();

                    UpdateAddress updateAddress = UpdateAddress.newInstanceList(json);
//                    Log.d("BuyStep1",updateAddress.toString());

                    noSendId.clear();
                    String noSendIdString = updateAddress.getNo_send_tpl_ids();
                    if (!noSendIdString.equals("[]")) {
                        try {
                            JSONArray noSendIdArray = new JSONArray(noSendIdString);

                            int size = null == noSendIdArray ? 0 : noSendIdArray.length();
                            for (int i = 0; i < size; i++) {
                                noSendId.add((String) noSendIdArray.get(i));
                            }
                        } catch (JSONException e) {
                            Log.d("exception", "no_send_tpl_ids error");

                        }
                    }
                    if (updateAddress != null) {
                        //判断是否显示货到付款
                        if (updateAddress.getAllow_offpay().equals("1")) {
                            ifshowOffpayID.setVisibility(View.VISIBLE);
                        } else {
                            ifshowOffpayID.setVisibility(View.GONE);
                        }

                        //记录货到付款hash
                        offpay_hash = updateAddress.getOffpay_hash();

                        //店铺是否支持货到付款hash
                        offpay_hash_batch = updateAddress.getOffpay_hash_batch();


                        //运费
                        try {
                            goods_freight = 0.00;

                            JSONObject jsonObj = new JSONObject(updateAddress.getContent());
//                            Log.d("Buy", jsonObj.toString());

                            Iterator<?> iterator = jsonObj.keys();

                            while (iterator.hasNext()) {
                                String storeID = iterator.next().toString();
                                String Value = jsonObj.getString(storeID);
                                fre_map.put(storeID, Value);
                                goods_freight += Double.parseDouble(Value);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //不能配送区域
                        updateRpacketUseable();
                        goods_voucher = 0.0;
                        upPriceUIData();//更新价格UI

//						updateVoucher();
                    }
                } else {
                    Toast.makeText(BuyStep1Activity.this, R.string.load_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 创建PopupWindow
     */
    protected void initPopuptWindow(View view, ArrayList<StoreVoucherList> Voucherlist, final Button selectVoucheID, final TextView voucherPriceID) {
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        View popupWindow_view = getLayoutInflater().inflate(R.layout.popupwindow_vouche_view, null, false);
        final ListView listViewID = (ListView) popupWindow_view.findViewById(R.id.listViewID);

        StoreVoucherListViewAdapter adapter = new StoreVoucherListViewAdapter(this);

        adapter.setDatas(Voucherlist);

        listViewID.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindow = new PopupWindow(popupWindow_view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        // 设置动画效果
//	    popupWindow.setAnimationStyle(R.style.PopupVoucherAnimation);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        // 点击其他地方消失
        popupWindow_view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                return false;
            }
        });

        listViewID.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }

                StoreVoucherList bean = (StoreVoucherList) listViewID.getItemAtPosition(arg2);

                if (bean != null) {
                    if (bean.getVoucher_t_id().equals("0")) {
                        storeVoucherLists.remove(bean.getStore_id());
                        selectVoucheID.setVisibility(View.VISIBLE);
                        voucherPriceID.setVisibility(View.GONE);
                    } else {
                        selectVoucheID.setVisibility(View.GONE);
                        voucherPriceID.setVisibility(View.VISIBLE);
                        storeVoucherLists.put(bean.getStore_id(), bean);
                        voucherPriceID.setText("￥ " + (bean.getVoucher_price() == null ? "0" : bean.getVoucher_price()));
                    }
                }

                //记录折扣价格
                goods_voucher = 0.00;
                Iterator iterator = storeVoucherLists.keySet().iterator();
                while (iterator.hasNext()) {

                    String store_id = (String) iterator.next();
                    StoreVoucherList vbean = storeVoucherLists.get(store_id);

                    if (vbean != null) {
                        goods_voucher += Double.parseDouble(vbean.getVoucher_price() == null ? "0.00" : vbean.getVoucher_price());
                    }
                }

                upPriceUIData();//更新价格UI
            }
        });
    }

    /**
     * 购买第二布 提交订单
     *
     */
    public void sendBuyStep2Data() {
        String url = Constants.URL_BUY_STEP2;

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("cart_id", cart_id);
        params.put("ifcart", ifcart);
        params.put("address_id", address_id);
        params.put("vat_hash", vat_hash);
        params.put("offpay_hash", offpay_hash);
        params.put("offpay_hash_batch", offpay_hash_batch);
        params.put("pay_name", pay_name);
        params.put("invoice_id", inv_id);
/*        params.put("pd_pay", if_pd_pay);
        params.put("rcb_pay", if_rcb_pay);*/
        //params.put("password", password);
        if (!rpacketId.equals("")) {
            params.put("rpt", rpacketId + "|" + rpacket);
        }
        if (is_fcode != null && is_fcode.equals("1")) {
            String fcode = editFCodeID.getText().toString();
            params.put("fcode", fcode);
        }
        if (storeVoucherLists.size() > 0) {
            String voucher = "";
            Iterator<?> iteratorVoucher = storeVoucherLists.keySet().iterator();
            while (iteratorVoucher.hasNext()) {
                String voucherID = iteratorVoucher.next().toString();
                StoreVoucherList voucherbean = storeVoucherLists.get(voucherID);
                voucher += "," + voucherbean.getVoucher_t_id() + "|" + voucherbean.getStore_id() + "|" + voucherbean.getVoucher_price();
            }
            voucher = voucher.replaceFirst("", voucher);
            params.put("voucher", voucher);
        }
        owndelivery = "";
        if (ziti_map.size() > 0) {
            for (String key : ziti_map.keySet()) {

                String value = ziti_map.get(key);
                owndelivery += "," + key + "|" + value;
            }
        }
        owndelivery = owndelivery.replaceFirst("", owndelivery);
        params.put("ownDelivery", owndelivery);
        // 待付款情况
        if (pay_name.equals("offline")) {  // 货到付款
            payForOffline(url, params);
        } else {
            // 在线支付
            payForOnline(params);
        }

    }

    /**
     * 提交订单 生成订单号
     */
    private void submitOrder(HashMap<String, String> map) {
        // 这一步主要是为了生成订单，站内余额支付由下一步完成
        map.put("pd_pay", "0");
        map.put("rcb_pay", "0");
        map.put("password", "");
        RemoteDataHandler.asyncPostDataString(Constants.URL_BUY_STEP2, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    // 提交订单成功 生成订单号
                    try {
                        String paySn = new JSONObject(data.getJson()).getString("pay_sn");
                        Intent intent = new Intent(BuyStep1Activity.this, CashierActivity.class);
                        // 商品总价
                        intent.putExtra("AllPrice", (MathConvert.round((goods_total + goods_freight - goods_voucher - rpacket), 2)));
                        intent.putExtra("paySn", paySn);
                        // 充值卡 预存款
                        if(buySetp1 != null){
                            intent.putExtra("pre",buySetp1.getAvailable_predeposit());
                            intent.putExtra("rc",buySetp1.getAvailable_rc_balance());
                        }

                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(BuyStep1Activity.this, data.getJson());
                }
            }
        });
    }


    /**
     *  20161129
     * @param params 提交订单所需参数
     */
    private void payForOnline(HashMap<String, String> params) {
        submitOrder(params);
        // 跳转收银台页面 由收银台负责提交订单
        /*Intent intent = new Intent(BuyStep1Activity.this, CashierActivity.class);
        BuyBean buyBean = new BuyBean(params);
        intent.putExtra("AllPrice", (MathConvert.round((goods_total + goods_freight - goods_voucher - rpacket), 2)));
        intent.putExtra("params", buyBean);
        //buyStep1.getAvailable_predeposit() // 预存款
        //buyStep1.getAvailable_rc_balance() // 充值卡
        if(buySetp1 != null){
            intent.putExtra("pre",buySetp1.getAvailable_predeposit());
            intent.putExtra("rc",buySetp1.getAvailable_rc_balance());
        }
        startActivity(intent);
        finish();*/
    }

    private void payForOffline(String url, HashMap<String, String> params) {
        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                Log.d("own", json);
                if (data.getCode() == HttpStatus.SC_OK) {
                    //{"payment_code":"online","pay_sn":"130530794912281000"}
                    /*try {
                        String paySn = new JSONObject(json).getString("pay_sn");
                        pay(paySn);
                    } catch (JSONException e) {
                        Toast.makeText(BuyStep1Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }*/

                    Toast.makeText(BuyStep1Activity.this, "提交订单成功", Toast.LENGTH_SHORT).show();
                    //提交订单成功则广播显示购物车数量
                    Intent intent1 = new Intent(Constants.SHOW_CART_NUM);
                    BuyStep1Activity.this.sendBroadcast(intent1);
                    Intent intent = new Intent(BuyStep1Activity.this, OrderListActivity.class);
                    BuyStep1Activity.this.startActivity(intent);
                    BuyStep1Activity.this.finish();
//						Intent mIntent = new Intent(Constants.APP_BORADCASTRECEIVER);
//						sendBroadcast(mIntent);
                } else {
                    try {
                        JSONObject obj = new JSONObject(json);
                        String error = obj.getString("error");
                        if (error != null) {
                            Toast.makeText(BuyStep1Activity.this, error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//						Toast.makeText(BuyStep1Activity.this, getString(R.string.datas_loading_fail_prompt), Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }

    private void pay(final String paySn) {

        // 加载alertdialog
        if (menuDialog == null) {
            View menuView = View.inflate(this, R.layout.gridview_menu, null);
            menuDialog = new AlertDialog.Builder(new ContextThemeWrapper(BuyStep1Activity.this, R.style.SampleTheme_Light)).create();
            menuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            menuDialog.setView(menuView, 0, 0, 0, 0);
            menuGrid = (GridView) menuView.findViewById(R.id.gridview);
        }

        menuGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                menuDialog.dismiss();
                HashMap<String, Object> map = (HashMap<String, Object>) arg0
                        .getItemAtPosition(arg2);
                switch (Integer.parseInt(map.get("itemImage").toString())) {
                    case R.drawable.sns_weixin_icon:// "微信"
                        loadingWXPaymentData(paySn);
                        break;
                    case R.drawable.zhifubao_appicon:// "支付宝"
                        Intent intent = new Intent(BuyStep1Activity.this,
                                PayMentWebActivity.class);
                        intent.putExtra("pay_sn", paySn);
                        BuyStep1Activity.this.startActivity(intent);
                        break;
                    //TODO Modify
                    case R.drawable.pay:// "支付宝原生支付"
                        loadingAlipayNativePaymentData(paySn);
                        break;
                }
            }
        });

        loadingPaymentListData();
    }


    /**
     * 获取可用支付方式
     */
    public void loadingPaymentListData() {

        menuDialog.show();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        RemoteDataHandler.asyncLoginPostDataString(
                Constants.URL_ORDER_PAYMENT_LIST, params, myApplication,
                new Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                String JosnObj = jsonObject
                                        .getString("payment_list");
                                JSONArray arr = new JSONArray(JosnObj);
                                Log.d("huting====pay", arr.toString());

                                int size = null == arr ? 0 : arr.length();
                                ArrayList<HashMap<String, Object>> hashMaps = new ArrayList<HashMap<String, Object>>();
                                for (int i = 0; i < size; i++) {
                                    String Values = arr.getString(i);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    if (Values.equals("wxpay")) {
                                        map.put("itemImage",
                                                R.drawable.sns_weixin_icon);
                                        map.put("itemText", "微信支付");
                                    } else if (Values.equals("alipay")) {
                                        map.put("itemImage",
                                                R.drawable.zhifubao_appicon);
                                        map.put("itemText", "支付宝");
                                    } else if (Values.equals("alipay_native")) {//TODO Modify 支付宝原生支付
                                        map.put("itemImage",
                                                R.drawable.pay);
                                        map.put("itemText", "原生支付");
                                    }
                                    if (!map.isEmpty()) {
                                        hashMaps.add(map);
                                    }

                                }
                                SimpleAdapter simperAdapter = new SimpleAdapter(
                                        BuyStep1Activity.this,
                                        hashMaps,
                                        R.layout.item_menu,
                                        new String[]{"itemImage", "itemText"},
                                        new int[]{R.id.item_image,
                                                R.id.item_text});
                                menuGrid.setAdapter(simperAdapter);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(MyShopApplication.context, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 验证支付密码
     *
     * @param password 支付密码
     */
    public void CheackPassword(final String password) {
        String url = Constants.URL_CHECK_PASSWORD;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("password", password);
        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    if (json.equals("1")) {
                        //sendBuyStep2Data(password);
                    }

                } else {
                    if(TextUtils.isEmpty(json)) {
                        Toast.makeText(BuyStep1Activity.this, "error:check password is empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        JSONObject obj = new JSONObject(json);
                        String error = obj.getString("error");
                        if (error != null) {
                            Toast.makeText(BuyStep1Activity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(BuyStep1Activity.this, "error:check password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 获取PopupWindow实例
     */
    private void getPopupWindow(View view, ArrayList<StoreVoucherList> voucherlist, Button selectVoucheID, TextView voucherPriceID) {
        if (null != popupWindow) {
            popupWindow.dismiss();
            return;
        } else {
            initPopuptWindow(view, voucherlist, selectVoucheID, voucherPriceID);
        }
    }

    /**
     * 更新价格UI
     */
    public void upPriceUIData() {

        //显示折扣价格
        textVoucher.setText("-￥" + MathConvert.round(goods_voucher, 2));

        //显示红包
        tvRpacket.setText("-￥" + MathConvert.round(rpacket, 2));

        //显示运费
        tvGoodsFreight.setText(" +￥" + MathConvert.round(goods_freight, 2));
        //显示商品总价
        textViewGoodsTotal.setText(" ￥" + MathConvert.round(goods_total, 2));

        //显示总价
        textviewAllPrice.setText("￥" + (MathConvert.round((goods_total + goods_freight - goods_voucher - rpacket), 2)));
    }


    ///////////////////////////////////////////////////////////////////////////////////


    /**
     * 获取微信参数
     *
     * @param pay_sn 支付编号
     */
    public void loadingWXPaymentData(String pay_sn) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("pay_sn", pay_sn);
        Log.d("dqw", Constants.URL_MEMBER_WX_PAYMENT);
        Log.d("dqw", myApplication.getLoginKey());
        Log.d("dqw", pay_sn);

        RemoteDataHandler.asyncLoginPostDataString(
                Constants.URL_MEMBER_WX_PAYMENT, params, myApplication,
                new Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                String appid = jsonObject.getString("appid");// 微信开放平台appid
                                String noncestr = jsonObject
                                        .getString("noncestr");// 随机字符串
                                String packageStr = jsonObject
                                        .getString("package");// 支付内容
                                String partnerid = jsonObject
                                        .getString("partnerid");// 财付通id
                                String prepayid = jsonObject
                                        .getString("prepayid");// 微信预支付编号
                                String sign = jsonObject.getString("sign");// 签名
                                String timestamp = jsonObject
                                        .getString("timestamp");// 时间戳

                                IWXAPI api = WXAPIFactory.createWXAPI(MyShopApplication.context, appid);

                                PayReq req = new PayReq();
                                req.appId = appid;
                                req.partnerId = partnerid;
                                req.prepayId = prepayid;
                                req.nonceStr = noncestr;
                                req.timeStamp = timestamp;
                                req.packageValue = packageStr;
                                req.sign = sign;
                                req.extData = "app data"; // optional

                                Log.d("huting----------", req.toString());

                                Toast.makeText(MyShopApplication.context, "正常调起支付",
                                        Toast.LENGTH_SHORT).show();
                                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                api.sendReq(req);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(MyShopApplication.context, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    /**
     * 获取支付宝原生支付的参数
     */
    public void loadingAlipayNativePaymentData(String pay_sn) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("pay_sn", pay_sn);

        Log.d("huting", Constants.URL_ALIPAY_NATIVE_GOODS);
        Log.d("huting", myApplication.getLoginKey());
        Log.d("huting", pay_sn);

        RemoteDataHandler.asyncLoginPostDataString(
                Constants.URL_ALIPAY_NATIVE_GOODS, params, myApplication,
                new Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                String signStr = jsonObject.optString("signStr");

                                Log.d("huting-----nativePay", signStr);
                                PayDemoActivity payDemoActivity = new PayDemoActivity(BuyStep1Activity.this, signStr);
                                payDemoActivity.doPay();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(MyShopApplication.context, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    ///////////////////////////////////////////////////////////////////////////////////

    class MyifshowOnpayRadioButtonClickListener implements OnClickListener {
        public void onClick(View v) {
            RadioButton btn = (RadioButton) v;
            switch (btn.getId()) {
                case R.id.ifshowOnpayID:

                    if (showAvailablePredeposit || showAvailableRCBalance) {
                        predepositLayoutID.setVisibility(View.VISIBLE);
                    }

                    pay_name = "online";//online(线上付款) offline(货到付款)
                    break;
                case R.id.ifshowOffpayID:

                    predepositLayoutID.setVisibility(View.GONE);
/*
                    if_pd_pay = "0";
                    if_rcb_pay = "0";*/

                    pay_name = "offline";//online(线上付款) offline(货到付款)

                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.SELECT_INVOICE) {
            inv_id = data.getStringExtra("inv_id");
            String inv_context = data.getStringExtra("inv_context");
            invInfoID.setText(inv_context == null ? "" : inv_context);
        }

        if (resultCode == Constants.SELECT_ADDRESS) {
            address_id = data.getStringExtra("address_id");
            String city_id = data.getStringExtra("city_id");
            String area_id = data.getStringExtra("area_id");
            String tureName = data.getStringExtra("tureName");
            String addressInFo = data.getStringExtra("addressInFo");
            String address = data.getStringExtra("address");
            String mobPhone = data.getStringExtra("mobPhone");

            //显示收货信息
            areaInfoID.setText(addressInFo == null ? "" : addressInFo);
            addressID.setText(address == null ? "" : address);
            trueNameID.setText(tureName == null ? "" : tureName);
            mobPhoneID.setText(mobPhone == null ? "" : mobPhone);

            addressInFoLayoutID.setVisibility(View.VISIBLE);
            noAreaInfoID.setVisibility(View.GONE);
            checkZt.setChecked(false);

            //更新收货地址
            updataAddress(city_id, area_id);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.invInfoID:

                Intent intent = new Intent(BuyStep1Activity.this, InvoiceListActivity.class);
                startActivityForResult(intent, 2);

                break;

            case R.id.commitID:

                //判断是否使用预存款或者充值卡如果使用验证密码
                /*if (if_pd_pay.equals("1") || if_rcb_pay.equals("1")) {

                    String password = editPasswordID.getText().toString();

                    if (password != null && !password.equals("") && !password.equals("null")) {
                        CheackPassword(password);
                    } else {
                        Toast.makeText(BuyStep1Activity.this, "支付密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    sendBuyStep2Data("");
                }*/
                sendBuyStep2Data();
                break;

            case R.id.noAreaInfoID:

                Intent noAddressIntent = new Intent(BuyStep1Activity.this, AddressListActivity.class);
                noAddressIntent.putExtra("addressFlag", "1");//1是提交订单跳转过去的 0或者没有是 个人中心
                startActivityForResult(noAddressIntent, 5);

                break;

            case R.id.addressInFoLayoutID:

                Intent addressIntent = new Intent(BuyStep1Activity.this, AddressListActivity.class);
                addressIntent.putExtra("addressFlag", "1");//1是提交订单跳转过去的 0或者没有是 个人中心
                startActivityForResult(addressIntent, 5);

                break;

            default:
                break;
        }
    }
}
