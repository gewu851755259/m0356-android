package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.SellerConsultBean;
import cn.m0356.shop.bean.SellerMenuItem;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;
import cn.m0356.shop.ui.mine.IMFriendsListActivity;

/**
 * Created by jiangtao on 2016/12/5.
 */
public class SellerMainActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView gv_menu;
    private MyShopApplication app;

    /**
     * 待付款数
     */
    private TextView tv_payment;
    /**
     * 待发货数
     */
    private TextView tv_delivery;
    /**
     * 待收货数
     */
    private TextView tv_receipt;
    /**
     * 店铺名称
     */
    private TextView tv_store_name;
    private ImageView iv_store_profile;
    private LinearLayout ll_nopay, ll_nosend, ll_noreceipt;

    // 售后
    private LinearLayout ll_refund, ll_refund_lock, ll_return, ll_return_lock;
    private TextView tv_refund, tv_refund_lock, tv_return, tv_return_lock;

    private boolean hasMsg = false;
    private MyGridAdapter adapter;
    private MediaPlayer mediaPlayer;

    public static void startSellerMainActivity(Context context){
        Intent intent = new Intent(context, SellerMainActivity.class);
        context.startActivity(intent);
    }


    private void findView() {
        gv_menu = (GridView) findViewById(R.id.gv_menu);
        tv_receipt = (TextView) findViewById(R.id.tv_receipt);
        tv_delivery = (TextView) findViewById(R.id.tv_delivery);
        tv_payment = (TextView) findViewById(R.id.tv_payment);
        tv_store_name = (TextView) findViewById(R.id.tv_store_name);
        iv_store_profile = (ImageView) findViewById(R.id.iv_profile);

        ll_nopay = (LinearLayout) findViewById(R.id.ll_nopay);
        ll_nosend = (LinearLayout) findViewById(R.id.ll_nosend);
        ll_noreceipt = (LinearLayout) findViewById(R.id.ll_noreceipt);

        ll_refund = (LinearLayout) findViewById(R.id.ll_refund);
        ll_refund_lock = (LinearLayout) findViewById(R.id.ll_refund_lock);
        ll_return = (LinearLayout) findViewById(R.id.ll_return);
        ll_return_lock = (LinearLayout) findViewById(R.id.ll_return_lock);

        tv_refund = (TextView) findViewById(R.id.tv_refund);
        tv_refund_lock = (TextView) findViewById(R.id.tv_refund_lock);
        tv_return = (TextView) findViewById(R.id.tv_return);
        tv_return_lock = (TextView) findViewById(R.id.tv_return_lock);
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);
        findView();
        initData();
        addListener();

    }

    private void initData() {
        app = (MyShopApplication) getApplication();
        List<SellerMenuItem> sellerMenuItems = initMenuInfo();
        mediaPlayer = MediaPlayer.create(SellerMainActivity.this, R.raw.new_msg001);
        try {
            mediaPlayer.prepare();//提示音准备
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setLooping(false); //提示音不循环
        adapter = new MyGridAdapter(sellerMenuItems);
        gv_menu.setAdapter(adapter);
        loadStoreInfo();

    }

    private void loadStoreInfo() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getSeller_key());
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_STORE_INFO, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){
                    // 取出店铺id
                    try {
                        JSONObject obj = new JSONObject(data.getJson()).getJSONObject("store_info");
                        String store_id = obj.getString("store_id");
                        tv_store_name.setText(obj.getString("store_name"));
                        app.setStore_id(store_id);
                        loadStoreAvatar(obj.getString("store_avatar"));
                        loadStatistics(store_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    ShopHelper.showApiError(SellerMainActivity.this, data.getJson());
                }
            }
        });
    }

    /**
     * 加载店铺头像
     * @param url
     */
    private void loadStoreAvatar(String url) {
        //带缓存的商品图
        Glide.with(SellerMainActivity.this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_launcher)  //设置占位图
                .error(R.drawable.ic_launcher)      //加载错误图
                .into(iv_store_profile);
    }

    // 加载统计类信息
    private void loadStatistics(String storeId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getSeller_key());
        map.put("store_id", storeId);
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_STORE_STATISTICS, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){
                    try {
                        JSONObject obj = new JSONObject(data.getJson());
                        JSONObject info = obj.getJSONObject("info");
                        // 待付款 待发货 待收获
                        updateStatisticsUI(info.getString("payment"), info.getString("delivery"),
                                info.getString("receipt"), info.getString("refund"), info.getString("refund_lock"),
                                info.getString("return"), info.getString("return_lock"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(SellerMainActivity.this, data.getJson());
                }
            }
        });
    }

    /**
     * 更新ui
     * @param payment 待付款数
     * @param delivery 待发货数
     * @param receipt 待收获数
     */
    private void updateStatisticsUI(String payment, String delivery, String receipt,
                                    String refund, String refund_lock, String return_, String return_lock) {
        tv_payment.setText(payment);
        tv_delivery.setText(delivery);
        tv_receipt.setText(receipt);

        tv_refund.setText(refund);
        tv_refund_lock.setText(refund_lock);
        tv_return.setText(return_);
        tv_return_lock.setText(return_lock);

    }

    private void addListener() {
        gv_menu.setOnItemClickListener(this);
        ll_nopay.setOnClickListener(this);
        ll_nosend.setOnClickListener(this);
        ll_noreceipt.setOnClickListener(this);

        ll_refund.setOnClickListener(this);
        ll_refund_lock.setOnClickListener(this);
        ll_return.setOnClickListener(this);
        ll_return_lock.setOnClickListener(this);

    }

    /**
     * 主页面菜单信息
     */
    private List<SellerMenuItem> initMenuInfo() {
        SellerMenuItem goodsManage = new SellerMenuItem("商品管理",
                "商品上下架，分享到朋友圈",
                R.drawable.home_goods_new);

        SellerMenuItem orderManage = new SellerMenuItem("订单管理",
                "订单跟踪与发货",
                R.drawable.home_list_new);

        SellerMenuItem countAnalysis = new SellerMenuItem("统计分析",
                "商品销售统计分析",
                R.drawable.home_count_new);

        SellerMenuItem customerService = new SellerMenuItem("客服IM",
                "在线交易中，即时通信",
                R.drawable.home_service_new);

        SellerMenuItem consultService = new SellerMenuItem("咨询",
                "处理客户咨询",
                R.drawable.home_consult);
        SellerMenuItem complainService = new SellerMenuItem("投诉",
                "处理客户投诉",
                R.drawable.home_complain);

        SellerMenuItem evaluate = new SellerMenuItem("评价",
                "管理评价",
                R.drawable.home_evaluate);
        SellerMenuItem refund = new SellerMenuItem("退货换货",
                "退货换货",
                R.drawable.home_refund);

        List<SellerMenuItem> items = new ArrayList<SellerMenuItem>();
        items.add(goodsManage);
        items.add(orderManage);
        items.add(countAnalysis);
        items.add(customerService);
        items.add(consultService);
        items.add(complainService);
        items.add(evaluate);
        items.add(refund);
        return items;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0: // 商品管理
                enterGoodsManage();
                break;
            case 1: // 订单管理
                enterOrderManage();
                break;
            case 2: // 统计分析
                enterAnalysis();
                break;
            case 3: // 客服IM
                startActivityForResult(new Intent(this, IMFriendsListActivity.class), 0);
                break;
            case 4: // 咨询
                enterConsult();
                break;
            case 5: //投诉
                enterComplain();
                break;
            case 6: // 评价
                Toast.makeText(SellerMainActivity.this, "开发中~~~", Toast.LENGTH_SHORT).show();
                break;
            case 7: // 退换货
                RefundReturnActivity.start(this, 0);
                break;
        }
    }

    private void enterComplain() {
        ComplainListActivity.start(this);
    }

    private void enterConsult() {
        ConsultListActivity.start(this, ConsultListActivity.CONSULT_TYPE_ALL);
    }

    private void enterAnalysis() {
//        CountAnalysisActivity.start(this);
        SellerStatisticsActivity.start(this);
    }

    private void enterGoodsManage() {
        GoodsManageActivity.start(this, GoodsManageActivity.GOODS_TYPE_ONLINE);
    }

    /**
     * 进入订单管理
     */
    private void enterOrderManage() {
        OrderManageActivity.start(this, OrderManageActivity.ORDER_TYPE_STATE_ALL);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.ll_nopay:
                OrderManageActivity.start(this, OrderManageActivity.ORDER_TYPE_STATE_NEW);
                break;
            case R.id.ll_nosend:
                OrderManageActivity.start(this, OrderManageActivity.ORDER_TYPE_STATE_PAY);
                break;
            case R.id.ll_noreceipt:
                OrderManageActivity.start(this, OrderManageActivity.ORDER_TYPE_STATE_SEND);
                break;
            case R.id.ll_return:
                RefundReturnActivity.start(this, 3);
                break;
            case R.id.ll_return_lock:
                RefundReturnActivity.start(this, 2);
                break;
            case R.id.ll_refund:
                RefundReturnActivity.start(this, 1);
                break;
            case R.id.ll_refund_lock:
                RefundReturnActivity.start(this, 0);
                break;
        }
    }


    private class MyGridAdapter extends BaseAdapter{

        private List<SellerMenuItem> list;

        public MyGridAdapter(List<SellerMenuItem> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(SellerMainActivity.this, R.layout.item_seller_menu, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            TextView tvDesc = (TextView) view.findViewById(R.id.tv_desc);
            View point = view.findViewById(R.id.view_point);

            if(position == 3){
                if(hasMsg){
                    point.setVisibility(View.VISIBLE);
                } else {
                    point.setVisibility(View.GONE);
                }
            } else {
                point.setVisibility(View.GONE);
            }

            SellerMenuItem sellerMenuItem = list.get(position);
            iv.setImageResource(sellerMenuItem.menuRes);
            tvDesc.setText(sellerMenuItem.menuDesc);
            tvName.setText(sellerMenuItem.menuName);

            return view;
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.IM_UPDATA_UI)) {
                String message = intent.getStringExtra("message");
                if (message != null && !TextUtils.isEmpty(message)) {
                    hasMsg = true;
                    adapter.notifyDataSetChanged();

                    mediaPlayer.start();//提示音播放
                    Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                    long[] pattern = {100, 400, 100, 400}; // 停止 开启 停止 开启
                    vib.vibrate(pattern, -1); //重复两次上面的pattern

                }
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.IM_UPDATA_UI);
        myIntentFilter.addAction(Constants.IM_FRIENDS_LIST_UPDATA_UI);
        registerReceiver(mBroadcastReceiver, myIntentFilter); //注册广播
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBoradcastReceiver();
        app.setIMNotification(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        app.setIMNotification(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            hasMsg = false;
            adapter.notifyDataSetChanged();
        }
    }
}
