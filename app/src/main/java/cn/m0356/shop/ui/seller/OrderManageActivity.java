package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.m0356.shop.R;
import cn.m0356.shop.adapter.OrderManageAdapter;
import cn.m0356.shop.bean.SellerOrderBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.custom.DeliverDialog;
import cn.m0356.shop.custom.ReasonDialog;
import cn.m0356.shop.custom.XListView;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * Created by jiangtao on 2016/12/6.
 */
public class OrderManageActivity extends Activity implements View.OnClickListener, ReasonDialog.OnOrderCancelListener, XListView.IXListViewListener {

    public static final String ORDER_TYPE_STATE_ALL = "";
    /**
     * 待付款
     */
    public static final String ORDER_TYPE_STATE_NEW = "state_new";
    /**
     * 已支付，待发货
     */
    public static final String ORDER_TYPE_STATE_PAY = "state_pay";
    /**
     * 已发货，待收货
     */
    public static final String ORDER_TYPE_STATE_SEND = "state_send";
    /**
     * 订单成功
     */
    public static final String ORDER_TYPE_STATE_SUCCESS = "state_success";

    private MyShopApplication app;

    private Button btnOrderAll, btnOrderNew, btnOrderSend, btnOrderUnSend, btnOrderSuccess;
    private XListView listViewID;
    private OrderManageAdapter mAdapter;

    private ReasonDialog dialog;
    private DeliverDialog mDeliverDialog;

    private String order_id;
    private int currentPage;
    private String type;

    private LinearLayout llEmptyLayout;


    /**
     * 启动activity
     *
     * @param context
     * @param type    'state_new=已产生未支付','state_pay=已支付','state_send=已发货','state_success=已收货，订单成功'
     */
    public static void start(Context context, String type) {
        Intent intent = new Intent();
        intent.putExtra("type", type);
        intent.setClass(context, OrderManageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_manage);
        findView();
        addListener();
        initData();
    }

    private void addListener() {
        btnOrderAll.setOnClickListener(this);
        btnOrderSuccess.setOnClickListener(this);
        btnOrderNew.setOnClickListener(this);
        btnOrderSend.setOnClickListener(this);
        btnOrderUnSend.setOnClickListener(this);
    }

    private void initData() {
        app = (MyShopApplication) getApplication();
        type = getIntent().getStringExtra("type");
        updateTabstate(type);
        listViewID.setEmptyView(llEmptyLayout);
        listViewID.setXListViewListener(this);
        mAdapter = new OrderManageAdapter(this);
        mAdapter.setClickListener(this);
        listViewID.setAdapter(mAdapter);
        loadOrderList(false);
    }

    private void loadOrderList(final boolean isRefresh) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getSeller_key());
        if (!TextUtils.isEmpty(type))
            map.put("state_type", type);
        // 默认跳过已取消订单
//        map.put("skip_off", "1");
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_ORDER_LIST + "&curpage=" + currentPage, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if (data.getCode() == HttpStatus.SC_OK) {
                    if (data.isHasMore()) {
                        listViewID.setPullLoadEnable(true);
                    } else {
                        listViewID.setPullLoadEnable(false);
                    }
                    try {
                        JSONObject obj = new JSONObject(data.getJson());
                        JSONArray order_list = obj.getJSONArray("order_list");
                        ArrayList<SellerOrderBean> list = new ArrayList<SellerOrderBean>();
                        for (int i = 0; i < order_list.length(); i++) {
                            Object o = order_list.get(i);
                            Gson g = new Gson();
                            SellerOrderBean bean = g.fromJson(o.toString(), SellerOrderBean.class);
                            list.add(bean);
                        }
                        if(isRefresh)
                            mAdapter.clear();
                        mAdapter.addData(list);
                        listViewID.stopLoadMore();
                        listViewID.stopRefresh();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String updateTime = sdf.format(new Date(System.currentTimeMillis()));
                        listViewID.setRefreshTime(updateTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    ShopHelper.showApiError(OrderManageActivity.this, data.getJson());
                }
            }
        });
    }

    private void findView() {
        btnOrderAll = (Button) findViewById(R.id.btnOrderAll);
        btnOrderNew = (Button) findViewById(R.id.btnOrderNew);
        btnOrderSend = (Button) findViewById(R.id.btnOrderSend);
        btnOrderUnSend = (Button) findViewById(R.id.btnOrderUnSend);
        btnOrderSuccess = (Button) findViewById(R.id.btnOrderSuccess);
        listViewID = (XListView) findViewById(R.id.listViewID);
        llEmptyLayout = (LinearLayout) findViewById(R.id.llListEmpty);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnOrderAll || id == R.id.btnOrderNew ||
                id == R.id.btnOrderSend || id == R.id.btnOrderUnSend ||
                id == R.id.btnOrderSuccess) {
            // tab按钮事件
            switch (v.getId()) {
                case R.id.btnOrderAll:
                    type = ORDER_TYPE_STATE_ALL;
                    break;
                case R.id.btnOrderNew:
                    type = ORDER_TYPE_STATE_NEW;
                    break;
                case R.id.btnOrderSend:
                    type = ORDER_TYPE_STATE_SEND;
                    break;
                case R.id.btnOrderUnSend:
                    type = ORDER_TYPE_STATE_PAY;
                    break;
                case R.id.btnOrderSuccess:
                    type = ORDER_TYPE_STATE_SUCCESS;
                    break;
            }
            updateTabstate(type);
            loadOrderList(true);
        } else {

            // 列表中的按钮事件 通过判断view内容确定执行事件
            if (!(v instanceof Button))
                return;
            Button btn = (Button) v;
            if (btn.getText().equals("取消订单")) {
                order_id = (String) btn.getTag();
                showReasonDialog();
            } else if (btn.getText().equals("发货")) {
                showDeliverDialog((String)btn.getTag());
            }
        }
    }

    private void showDeliverDialog(String id) {
        if(mDeliverDialog == null)
            mDeliverDialog = DeliverDialog.newInstance(/*id, app.getStore_id(), app.getSeller_key()*/);
        mDeliverDialog.show(getFragmentManager(), "deliver", id, app.getStore_id(), app.getSeller_key());
    }

    private void showReasonDialog() {
        if(dialog == null) {
            dialog = new ReasonDialog();
            dialog.setListener(this);
        }
        dialog.show(getFragmentManager(), "reason");
    }

    /**
     * 关闭订单
     * @param orderId 订单id
     * @param reason 原因
     */
    private void cancelOrder(String orderId, String reason){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getSeller_key());
        map.put("order_id", orderId);
        map.put("reason", reason);
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_ORDER_CANCEL, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){
                    if(data.getJson().equals("1")){
                        Toast.makeText(OrderManageActivity.this, "取消订单成功", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ShopHelper.showApiError(OrderManageActivity.this, data.getJson());
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    public void cancel(String reason) {
        cancelOrder(order_id, reason);
    }

    @Override
    public void onRefresh() {
        currentPage = 0;
        loadOrderList(true);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadOrderList(false);
    }

    private void updateTabstate(String state){
        clearTabState();
        if(state.equals(ORDER_TYPE_STATE_ALL)){
            btnOrderAll.setActivated(true);
        } else if(state.equals(ORDER_TYPE_STATE_NEW)){
            btnOrderNew.setActivated(true);
        } else if(state.equals(ORDER_TYPE_STATE_PAY)){
            btnOrderUnSend.setActivated(true);
        } else if(state.equals(ORDER_TYPE_STATE_SEND)){
            btnOrderSend.setActivated(true);
        } else if(state.equals(ORDER_TYPE_STATE_SUCCESS)){
            btnOrderSuccess.setActivated(true);
        }

    }

    private void clearTabState(){
        btnOrderAll.setActivated(false);
        btnOrderUnSend.setActivated(false);
        btnOrderSend.setActivated(false);
        btnOrderNew.setActivated(false);
        btnOrderSuccess.setActivated(false);
    }

}
