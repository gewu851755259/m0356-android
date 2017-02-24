package cn.m0356.shop.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.pay.PayDemoActivity;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.AddressDetails;
import cn.m0356.shop.bean.BuyBean;
import cn.m0356.shop.bean.BuyStep1;
import cn.m0356.shop.bean.InvoiceInFO;
import cn.m0356.shop.bean.MyPayInfoBean;
import cn.m0356.shop.bean.OrderGoodsList;
import cn.m0356.shop.bean.OrderGroupList;
import cn.m0356.shop.bean.OrderList;
import cn.m0356.shop.bean.RpacketInfo;
import cn.m0356.shop.bean.UpdateAddress;
import cn.m0356.shop.common.AnimateFirstDisplayListener;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.LogHelper;
import cn.m0356.shop.common.MathConvert;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.RemoteDataHandler.Callback;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.mine.OrderDeliverDetailsActivity;
import cn.m0356.shop.ui.mine.OrderExchangeActivity;
import cn.m0356.shop.ui.mine.PayMentWebActivity;
import cn.m0356.shop.ui.type.CashierActivity;
import cn.m0356.shop.ui.type.EvaluateActivity;
import cn.m0356.shop.ui.mine.OrderDetailsActivity;
import cn.m0356.shop.ui.type.EvaluateAddActivity;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 我的订单列表适配器
 *
 * @author KingKong·HE
 * @Time 2014-1-6 下午12:06:09
 * @E-mail hjgang@bizpoer.com
 */
public class OrderGroupListViewAdapter extends BaseAdapter {
    private Context context;

    private LayoutInflater inflater;

    private ArrayList<OrderGroupList> orderLists;

    private MyShopApplication myApplication;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options = SystemHelper.getDisplayImageOptions();

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private AlertDialog menuDialog;// menu菜单Dialog

    private GridView menuGrid;

    private View menuView;

    private OrderGroupList groupList2FU;
    private String freight_hash;
    private String offpay_hash;
    private String offpay_hash_batch;
    private double goods_freight;
    private String inv_id;
    private String vat_hash;
    private String ziti;
    private String cart_id;
    private String address_id;
    private String pay_sn;
    private String price;


    public OrderGroupListViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        myApplication = (MyShopApplication) context.getApplicationContext();
        // 创建AlertDialog
        menuView = View.inflate(context, R.layout.gridview_menu, null);
        menuDialog = new AlertDialog.Builder(new ContextThemeWrapper( context,R.style.SampleTheme_Light)).create();
        menuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        menuDialog.setView(menuView,0,0,0,0);
        menuGrid = (GridView) menuView.findViewById(R.id.gridview);
    }

    @Override
    public int getCount() {
        return orderLists == null ? 0 : orderLists.size();
    }

    @Override
    public Object getItem(int position) {
        return orderLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<OrderGroupList> getOrderLists() {
        return orderLists;
    }

    public void setOrderLists(ArrayList<OrderGroupList> orderLists) {
        this.orderLists = orderLists;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.listivew_order_item, null);
            holder = new ViewHolder();
            holder.linearLayoutFLag = (LinearLayout) convertView.findViewById(R.id.linearLayoutFLag);
            holder.buttonFuKuan = (Button) convertView.findViewById(R.id.buttonFuKuan);
            holder.addViewID = (LinearLayout) convertView.findViewById(R.id.addViewID);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final OrderGroupList bean = orderLists.get(position);

        if (!bean.getPay_amount().equals("")
                && !bean.getPay_amount().equals("null")
                && !bean.getPay_amount().equals("0")
                && bean.getPay_amount() != null) {
            holder.linearLayoutFLag.setVisibility(View.VISIBLE);
        } else {
            holder.linearLayoutFLag.setVisibility(View.GONE);
        }
        if (!bean.getPay_amount().equals("0") && !bean.getPay_amount().equals("null") && bean.getPay_amount() != null) {
            price = new DecimalFormat("#0.00").format(Double.parseDouble((bean.getPay_amount() == null ? "0.00" : bean.getPay_amount()) == "" ? "0.00" : bean.getPay_amount()));
            holder.buttonFuKuan.setText("订单支付(￥ " + price + ")");
            holder.buttonFuKuan.setTag(price);
        }

        holder.buttonFuKuan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(context,PayMentWebAcivity.class);
                // intent.putExtra("pay_sn", bean.getPay_sn());
                // context.startActivity(intent);

               loadInnerMoney(bean.getPay_sn(), (String) holder.buttonFuKuan.getTag());

                // 20161202
                /*try {
                    test(bean.getOrder_list(), v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                /*groupList2FU = orderLists.get(position);
                menuDialog.show();
                loadingPaymentListData();*/
            }
        });

        ArrayList<OrderList> orderLists = OrderList.newInstanceList(bean.getOrder_list());

        holder.addViewID.removeAllViews();

        for (int i = 0; i < orderLists.size(); i++) {
            OrderList orderList = orderLists.get(i);
            View orderListView = inflater.inflate(R.layout.listivew_order2_item, null);

            initUIOrderList(orderListView, orderList);

            holder.addViewID.addView(orderListView);
        }

        menuGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                menuDialog.dismiss();
                HashMap<String, Object> map = (HashMap<String, Object>) arg0
                        .getItemAtPosition(arg2);
                switch (Integer.parseInt(map.get("itemImage").toString())) {
                    case R.drawable.sns_weixin_icon:// "微信"

                        loadingWXPaymentData(groupList2FU.getPay_sn());

                        break;
                    case R.drawable.zhifubao_appicon:// "支付宝"
                        Intent intent = new Intent(context,
                                PayMentWebActivity.class);
                        intent.putExtra("pay_sn", groupList2FU.getPay_sn());
                        context.startActivity(intent);
                        break;
                    case R.drawable.pay:// "支付宝原生支付"
                        loadingAlipayNativePaymentData(groupList2FU.getPay_sn());

                        break;
            }
            }
        });

        return convertView;
    }


    private void loadInnerMoney(final String paySn, final String allPrice){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", myApplication.getLoginKey());
        map.put("pay_sn", paySn);
        RemoteDataHandler.asyncPostDataString(Constants.URL_PAY_LIST, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    Gson gson = new Gson();
                    MyPayInfoBean myPayInfoBean = gson.fromJson(data.getJson(), MyPayInfoBean.class);
                    Intent intent = new Intent(context,CashierActivity.class);
                    intent.putExtra("paySn", paySn);
                    intent.putExtra("pre", myPayInfoBean.pay_info.member_available_pd);
                    intent.putExtra("rc", myPayInfoBean.pay_info.member_available_rcb);
                    intent.putExtra("AllPrice", Double.parseDouble(allPrice));
                    context.startActivity(intent);
                } else {
                    ShopHelper.showApiError(MyShopApplication.context, data.getJson());
                }
            }
        });
    }




///////////////////////////////////////////////////////////////////////////////////
    // view 存放price
    private void test(String order_list, final View v) throws JSONException {
        ziti = "";
        cart_id = "";
        JSONArray array = new JSONArray(order_list);
        for(int i = 0; i < array.length(); i++){ // 店铺
            JSONObject jsonObject = array.getJSONObject(i);
            pay_sn = jsonObject.getString("pay_sn");
            String is_ziti = jsonObject.getString("is_ziti");
            String store_id = jsonObject.getString("store_id");
            if(i == 0){
                ziti += store_id + "|" + is_ziti;
            } else {
                ziti += "," + store_id + "|" + is_ziti;
            }
            JSONArray extend_order_goods = jsonObject.getJSONArray("extend_order_goods");// 商品
            for(int x = 0; x < extend_order_goods.length(); x++){
                JSONObject good = extend_order_goods.getJSONObject(x);
                String id = good.getString("goods_id");
                String num = good.getString("goods_num");
                if(i == 0){
                    cart_id += id + "|" + num;
                } else {
                    cart_id += "," + id + "|" + num;
                }
            }
        }
        // 充值卡余额 预存款余额 总价
        String url = Constants.URL_BUY_STEP1;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("cart_id", cart_id);
        params.put("ifcart", "0");


        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new Callback() {

            @Override
            public void dataLoaded(ResponseData data) {
                String json = data.getJson();
                if (data.getCode() == HttpStatus.SC_OK) {
                    BuyStep1 buyStep1 = BuyStep1.newInstanceList(json);
                    if (buyStep1 != null) {
                        //order_amount 订单总价
                        AddressDetails addressDetails = AddressDetails.newInstanceDetails(buyStep1.getAddress_info());
                        //记录运费hash
                        freight_hash = buyStep1.getFreight_hash();
                        //记录发票hash
                        vat_hash = buyStep1.getVat_hash();
                        InvoiceInFO inv_info = InvoiceInFO.newInstanceList(buyStep1.getInv_info());
                        if (inv_info != null) {
                            //记录发票ID
                            inv_id = inv_info.getInv_id() == null ? "0" : inv_info.getInv_id();
                        }
                        if (addressDetails != null) {
                            //记录地址ID
                            address_id = addressDetails.getAddress_id();
                            updataAddress(addressDetails.getCity_id(), addressDetails.getArea_id(), v);
                        } else {
                            enterCashier((String)v.getTag());
                        }
                        }
                    } else {
                    ShopHelper.showApiError(context, json);
                }
            }
        });



    }

    private void enterCashier(String price) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("cart_id", cart_id);
        params.put("ifcart", "0");
        params.put("address_id", address_id);
        params.put("vat_hash", vat_hash);
        params.put("offpay_hash", offpay_hash);
        params.put("offpay_hash_batch", offpay_hash_batch);
        params.put("pay_name", "online");
        params.put("invoice_id", inv_id);
        params.put("ownDelivery", ziti);


        Intent intent = new Intent(context, CashierActivity.class);
        BuyBean buyBean = new BuyBean(params);
        intent.putExtra("AllPrice", Double.parseDouble(price));
        intent.putExtra("params", buyBean);
        intent.putExtra("pay_sn", pay_sn);
        //buyStep1.getAvailable_predeposit() // 预存款
        //buyStep1.getAvailable_rc_balance() // 充值卡
                       /* if(buySetp1 != null){
                            intent.putExtra("pre",buySetp1.getAvailable_predeposit());
                            intent.putExtra("rc",buySetp1.getAvailable_rc_balance());
                        }*/
        context.startActivity(intent);
//                        finish();
    }

    /**
     * 更新收货地址
     */
    public void updataAddress(String city_id, String area_id, final View v) {
        String url = Constants.URL_UPDATE_ADDRESS;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("city_id", city_id);
        params.put("area_id", area_id);
        params.put("freight_hash", freight_hash);
//        Log.d("BuyStep1", area_id);
//http://192.168.1.137/mobile/index.php?act=member_order&op=is_ziti
        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication, new Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    String json = data.getJson();

                    UpdateAddress updateAddress = UpdateAddress.newInstanceList(json);
//                    Log.d("BuyStep1",updateAddress.toString());

                    String noSendIdString = updateAddress.getNo_send_tpl_ids();
                    if (!noSendIdString.equals("[]")) {
                        try {
                            JSONArray noSendIdArray = new JSONArray(noSendIdString);

                            int size = null == noSendIdArray ? 0 : noSendIdArray.length();
                            for (int i = 0; i < size; i++) {
                                //noSendId.add((String) noSendIdArray.get(i));
                            }
                        } catch (JSONException e) {
                            Log.d("exception", "no_send_tpl_ids error");

                        }
                    }
                    if (updateAddress != null) {

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
                                goods_freight += Double.parseDouble(Value);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        
                        ///////////
                        enterCashier((String) v.getTag());
                        ///////////
                        
                    }
                } else {
                    Toast.makeText(MyShopApplication.context, R.string.load_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
///////////////////////////////////////////////////////////////////////////////////

    /**
     * 生成界面
     */
    public void initUIOrderList(View view, final OrderList orderList) {

        TextView textOrderStoreName = (TextView) view.findViewById(R.id.textOrderStoreName);
        TextView textOrderAllPrice = (TextView) view.findViewById(R.id.textOrderAllPrice);
        TextView textOrderShippingFee = (TextView) view.findViewById(R.id.textOrderShippingFee);
        final Button textOrderOperation = (Button) view.findViewById(R.id.textOrderOperation);
        final Button buttonQueRen=(Button)view.findViewById(R.id.buttonQueRen);
        TextView textOrderSuccess = (TextView) view.findViewById(R.id.textOrderSuccess);
        LinearLayout addViewID = (LinearLayout) view.findViewById(R.id.addViewID);
        TextView textOrderGoodsNum=(TextView)view.findViewById(R.id.textOrderGoodsNum);
        TextView textOrderDel=(TextView)view.findViewById(R.id.textOrderDel);
        TextView textTui=(TextView)view.findViewById(R.id.textTui);

        textOrderStoreName.setText(orderList.getStore_name());
        textOrderAllPrice.setText("￥" + orderList.getOrder_amount());
        textOrderShippingFee.setText("(含运费￥" + orderList.getShipping_fee()+")");
        ArrayList<OrderGoodsList> goodsDatas = OrderGoodsList.newInstanceList(orderList.getExtend_order_goods());

        textOrderGoodsNum.setText("共"+goodsDatas.size()+"件商品，合计");


        if (orderList.getIf_cancel().equals("true")) {
            textOrderOperation.setVisibility(View.VISIBLE);
            textOrderOperation.setText("取消订单");
        }
        if (orderList.getIf_receive().equals("true")) {
            buttonQueRen.setVisibility(View.VISIBLE);
            buttonQueRen.setText("确认收货");
        }
        if (orderList.getIf_lock().equals("true")) {
            textTui.setVisibility(View.VISIBLE);
        }
        if(orderList.getIf_evaluation().equals("true")){
            buttonQueRen.setVisibility(View.VISIBLE);
            buttonQueRen.setText("评价");
        }
        if (orderList.getIf_evaluation_again().equals("true")) {
            if (orderList.getEvaluation_state().equals("1")) {
                buttonQueRen.setVisibility(View.VISIBLE);
                buttonQueRen.setText("追加评价");
            }
        }

        if(orderList.getIf_refund_cancel().equals("true")){
            textOrderOperation.setVisibility(View.VISIBLE);
            textOrderOperation.setText("退款");
        }
        if (orderList.getIf_deliver().equals("true")){
            textOrderOperation.setVisibility(View.VISIBLE);
            textOrderOperation.setText("查看物流");
        }


//        if (orderList.getIf_deliver().equals("true")) {
//            textOrderOperation2.setText(Html.fromHtml("<a href='#'>查看物流</a>"));
//            textOrderOperation2.setVisibility(View.VISIBLE);
//        } else {
//            textOrderOperation2.setVisibility(View.GONE);
//        }

        if (orderList.getState_desc() != null
                && !orderList.getState_desc().equals("")) {
            textOrderSuccess.setVisibility(View.VISIBLE);
            textOrderSuccess.setText(orderList.getState_desc());
            if(orderList.getState_desc().equals("已取消")){
                textOrderDel.setVisibility(View.VISIBLE);
                textOrderDel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadingSaveOrderData(Constants.URL_ORDER_DEL, orderList.getOrder_id());
                    }
                });
            }
        } else {
            textOrderSuccess.setVisibility(View.GONE);
        }

        buttonQueRen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=buttonQueRen.getText().toString();
                if(s.equals("确认收货")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("操作提示")
                            .setMessage("是否确认操作")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .setPositiveButton("确认",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            loadingSaveOrderData(Constants.URL_ORDER_RECEIVE, orderList.getOrder_id());
                                        }
                                    }).create().show();
                }else if (s.equals("评价")){
                    Intent i = new Intent(context,EvaluateActivity.class);
                    i.putExtra("order_id", orderList.getOrder_id());
                    context.startActivity(i);
                }else if (s.equals("追加评价")){
                    Intent i = new Intent(context,EvaluateAddActivity.class);
                    i.putExtra("order_id", orderList.getOrder_id());
                    context.startActivity(i);
                }
            }
        });

        textOrderOperation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               final String key = textOrderOperation.getText().toString();
                if(key.equals("查看物流")){
                    Intent intent=new Intent(context,OrderDeliverDetailsActivity.class);
                    intent.putExtra("order_id",orderList.getOrder_id());
                    context.startActivity(intent);
                    return;
                }

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("操作提示")
                            .setMessage("是否确认操作")
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    })
                            .setPositiveButton("确认",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int whichButton) {
                                            if (key.equals("取消订单")) {
                                                loadingSaveOrderData(Constants.URL_ORDER_CANCEL, orderList.getOrder_id());
                                            }
                                            if(key.equals("退款")){
                                                Intent intent=new Intent(context,OrderExchangeActivity.class);
                                                intent.putExtra("order_id",orderList.getOrder_id());
                                                context.startActivity(intent);
                                            }
                                        }
                                    }).create().show();

            }
        });
//        textOrderOperation2.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, OrderDeliverDetailsActivity.class);
//                intent.putExtra("order_id", orderList.getOrder_id());
//                context.startActivity(intent);
//            }
//        });

        LinearLayout llGift=null;
        LinearLayout llGiftList=null;
        TextView imgZeng=null;
        for (int j = 0; j < goodsDatas.size(); j++) {
            final OrderGoodsList ordergoodsList = goodsDatas.get(j);
            View orderGoodsListView = inflater.inflate(
                    R.layout.listivew_order_goods_item, null);
            addViewID.addView(orderGoodsListView);

            ImageView imageGoodsPic = (ImageView) orderGoodsListView
                    .findViewById(R.id.imageGoodsPic);
            TextView textGoodsName = (TextView) orderGoodsListView
                    .findViewById(R.id.textGoodsName);
            TextView textGoodsPrice = (TextView) orderGoodsListView
                    .findViewById(R.id.textGoodsPrice);
            TextView textGoodsNUM = (TextView) orderGoodsListView
                    .findViewById(R.id.textGoodsNUM);
            imgZeng=(TextView)orderGoodsListView.findViewById(R.id.imgZeng);
            TextView textGoodsSPec=(TextView)orderGoodsListView.findViewById(R.id.textGoodsSPec);
            llGift = (LinearLayout) orderGoodsListView.findViewById(R.id.llGift);
            llGiftList = (LinearLayout) orderGoodsListView.findViewById(R.id.llGiftList);

            textGoodsName.setText(ordergoodsList.getGoods_name());
            textGoodsPrice.setText("￥" + ordergoodsList.getGoods_price());
            textGoodsNUM.setText("×"+ordergoodsList.getGoods_num());
            textGoodsName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context, OrderDetailsActivity.class);
                    i.putExtra("order_id",orderList.getOrder_id());
                    i.putExtra("evaluation_state", orderList.getEvaluation_state());
                    i.putExtra("evaluation_again_state", orderList.getEvaluation_again_state());
                    context.startActivity(i);
                }
            });
            if(ordergoodsList.getGoods_spec().equals("null")||ordergoodsList.getGoods_spec().equals("")){
                textGoodsSPec.setVisibility(View.GONE);
            }else{
                textGoodsSPec.setText(ordergoodsList.getGoods_spec());
            }


            imageLoader.displayImage(ordergoodsList.getGoods_image_url(),
                    imageGoodsPic, options, animateFirstListener);

        }


        //赠品
        String giftListString = orderList.getZengpin_list();
        if (giftListString.equals("") || giftListString.equals("[]")) {
            llGift.setVisibility(View.GONE);
        } else {
            try{
                imgZeng.setVisibility(View.VISIBLE);
                JSONArray giftArray = new JSONArray(giftListString);
                for (int j = 0; j < giftArray.length(); j++) {
                    View giftView = inflater.inflate(R.layout.cart_list_gift_item, null);
                    TextView tvGiftInfo = (TextView) giftView.findViewById(R.id.tvGiftInfo);
                    JSONObject giftObj = (JSONObject) giftArray.get(j);
                    tvGiftInfo.setText(giftObj.optString("goods_name") + "x" + giftObj.optString("goods_num"));
                    llGiftList.addView(giftView);
            }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        // goodsListView.setOnItemClickListener(new OnItemClickListener() {
        // @Override
        // public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        // long arg3) {
        // OrderGoodsList bean =(OrderGoodsList)
        // goodsListView.getItemAtPosition(arg2);
        // if(bean != null){
        // Intent intent =new Intent(context,GoodsDetailsActivity.class);
        // intent.putExtra("goods_id", bean.getGoods_id());
        // context.startActivity(intent);
        // }
        // }
        // });
    }

    /**
     * 获取可用支付方式
     */
    public void loadingPaymentListData() {
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
                                    if(!map.isEmpty()){
                                        hashMaps.add(map);
                                    }

                                }
                                SimpleAdapter simperAdapter = new SimpleAdapter(
                                        context,
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
                                    Toast.makeText(context, error,
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

                                IWXAPI api = WXAPIFactory.createWXAPI(context, appid);

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

                                Toast.makeText(context, "正常调起支付",
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
                                    Toast.makeText(context, error,
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
     *
     *  获取支付宝原生支付的参数
     */
    public void loadingAlipayNativePaymentData(String pay_sn){
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
                                PayDemoActivity payDemoActivity = new PayDemoActivity(context, signStr);
                                payDemoActivity.doPay();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(context, error,
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
     * 确认收货、取消订单
     *
     * @param url
     * @param //orderID 订单ID
     */
    public void loadingSaveOrderData(String url, String order_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", myApplication.getLoginKey());
        params.put("order_id", order_id);

        RemoteDataHandler.asyncLoginPostDataString(url, params, myApplication,
                new Callback() {
                    @Override
                    public void dataLoaded(ResponseData data) {
                        String json = data.getJson();
                        if (data.getCode() == HttpStatus.SC_OK) {
                            if (json.equals("1")) {
                                // Toast.makeText(context, "",
                                // Toast.LENGTH_SHORT).show();;
                                // 刷新界面
                                Intent mIntent = new Intent(
                                        Constants.PAYMENT_SUCCESS);
                                context.sendBroadcast(mIntent);
                            }

                        } else {
                            try {
                                JSONObject obj2 = new JSONObject(json);
                                String error = obj2.getString("error");
                                if (error != null) {
                                    Toast.makeText(context, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    class ViewHolder {
        LinearLayout linearLayoutFLag;
        Button buttonFuKuan;
        LinearLayout addViewID;
    }
}
