package cn.m0356.shop.ui.seller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.apache.http.HttpStatus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.m0356.shop.R;
import cn.m0356.shop.bean.SellerGoodsLisResultBean;
import cn.m0356.shop.common.Constants;
import cn.m0356.shop.common.MyShopApplication;
import cn.m0356.shop.common.ShopHelper;
import cn.m0356.shop.common.SystemHelper;
import cn.m0356.shop.custom.MyCustomProgressDialog;
import cn.m0356.shop.custom.SellerDelGoodsDialog;
import cn.m0356.shop.custom.SellerGoodShowOrUnShowDialog;
import cn.m0356.shop.custom.XListView;
import cn.m0356.shop.http.RemoteDataHandler;
import cn.m0356.shop.http.ResponseData;

/**
 * Created by jiangtao on 2016/12/29.
 */
public class GoodsManageActivity extends Activity implements View.OnClickListener, SellerDelGoodsDialog.OnSuccessListener, XListView.IXListViewListener, SellerGoodShowOrUnShowDialog.OnOpListener {

    private XListView lv_list;
    private MyAdapter mMyAdapter;
    private MyShopApplication app;
    private Button btnOnline, btnOffline, btnLockup;
    // 删除商品对话框
    private SellerDelGoodsDialog delDialog;
    // 等待加载对话框
    private MyCustomProgressDialog proDialog;
    // 商品上下架确定对话框
    private SellerGoodShowOrUnShowDialog goodOpDialog;

    public static final String GOODS_TYPE_ONLINE = "online";
    public static final String GOODS_TYPE_OFFLINE = "offline";
    public static final String GOODS_TYPE_LOCKUP = "lockup";

    private String type;

    private int page = 1;
    private String commonids;
    private int isShow;
    private String notice;

    public static void start(Context context, String type){
        Intent intent = new Intent(context, GoodsManageActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_manage);
        init();
        this.type = getIntent().getStringExtra("type");
        loadGoodsData(true, type);
        addState(type);

    }

    private void loadGoodsData(final boolean isRefresh, String type) {
        proDialog.show(getFragmentManager(), "progress");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getSeller_key());
        map.put("goods_type", type);
        RemoteDataHandler.asyncPostDataString(Constants.SellerManager.URL_SELLER_GOODS_LIST + page, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){
                    try {
                        if(data.isHasMore()){
                            lv_list.setPullLoadEnable(true);
                        } else {
                            lv_list.setPullLoadEnable(false);
                        }
                        if(isRefresh) {
                            mMyAdapter.clear();
                            lv_list.stopRefresh();
                            lv_list.setRefreshTime(SystemHelper.getTimeStr(System.currentTimeMillis() + ""));
                        }
                        parseData(data.getJson());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShopHelper.showApiError(MyShopApplication.context, data.getJson());
                }
                proDialog.dismiss();
            }
        });
    }

    private void parseData(String json) throws JSONException {
        Gson g = new Gson();
        SellerGoodsLisResultBean bean = g.fromJson(json, SellerGoodsLisResultBean.class);
        mMyAdapter.addData(bean.goods_list);
    }

    private void init() {
        app = (MyShopApplication) getApplication();
        delDialog = SellerDelGoodsDialog.newInstance(app.getSeller_key());
        proDialog = new MyCustomProgressDialog();
        delDialog.setListener(this);
        lv_list = (XListView) findViewById(R.id.lv_list);
        btnOnline = (Button) findViewById(R.id.btn_online);
        btnOffline = (Button) findViewById(R.id.btn_offline);
        btnLockup = (Button) findViewById(R.id.btn_lockup);
        btnOnline.setOnClickListener(this);
        btnOffline.setOnClickListener(this);
        btnLockup.setOnClickListener(this);
        mMyAdapter = new MyAdapter();
        mMyAdapter.setListener(this);
        lv_list.setAdapter(mMyAdapter);
        lv_list.setXListViewListener(this);
        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addState(String type){
        clearState();
        if(type.equals(GOODS_TYPE_ONLINE)){
            btnOnline.setActivated(true);
        } else if(type.equals(GOODS_TYPE_OFFLINE)){
            btnOffline.setActivated(true);
        } else {
            btnLockup.setActivated(true);
        }
    }

    private void clearState(){
        btnLockup.setActivated(false);
        btnOffline.setActivated(false);
        btnOnline.setActivated(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_goods_del){
            // 删除商品
            String gid = (String) ((RelativeLayout)v.getParent()).getTag();
            String name = (String)v.getTag();
            delGoods(gid, name);
        } else if(id == R.id.btn_goods_edit){
            // 编辑商品
            editGoods((String) ((RelativeLayout)v.getParent()).getTag());
        } else if(id == R.id.btn_lockup){
            // 禁售商品
            changeType(GOODS_TYPE_LOCKUP);
        } else if(id == R.id.btn_online){
            // 在售商品
            changeType(GOODS_TYPE_ONLINE);
        } else if(id == R.id.btn_offline){
            // 仓库商品
            changeType(GOODS_TYPE_OFFLINE);
        } else if(id == R.id.btn_goods_isshow){
            // 上架或下架
            Button btn = (Button) v;
            String text = btn.getText().toString();
            int pos = (Integer) btn.getTag();
            SellerGoodsLisResultBean.GoodsListBean item = mMyAdapter.getItem(pos);
            if(text.equals("上架")){
                goodShowOrUnShow(item.goods_commonid, 1, item.goods_name);
            } else if(text.equals("下架")){
                goodShowOrUnShow(item.goods_commonid, 0, item.goods_name);
            }
        }
    }

    /**
     * 商品上下架架
     * @param commonids
     */
    private void goodShowOrUnShow(String commonids, int isShow, String name) {

        this.notice = isShow == 0 ? "下架" : "上架";
        goodOpDialog = SellerGoodShowOrUnShowDialog.newInstance(name, notice);
        goodOpDialog.setListener(this);
        goodOpDialog.show(getFragmentManager(), "op");
        this.commonids = commonids;
        this.isShow = isShow;

    }

    /**
     * 切换商品类型
     * @param type
     */
    private void changeType(String type){
        this.type = type;
        addState(type);
        page = 1;
        loadGoodsData(true, type);
    }

    private void editGoods(String id) {
        SellerGoodsDetailActivity.start(this, id);
    }

    private void delGoods(String id, String name) {
        delDialog.show(getFragmentManager(), name, id);
    }

    @Override
    public void success() {
        page = 1;
        loadGoodsData(true, type);
    }

    @Override
    public void onRefresh() {
        page = 1;
        loadGoodsData(true, type);
    }

    @Override
    public void onLoadMore() {
        page++;
        loadGoodsData(false, type);
    }

    @Override
    public void op() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", app.getSeller_key());
        map.put("commonids", commonids);
        String url = (isShow == 0 ? Constants.SellerManager.URL_SELLER_GOODS_UNSHOW : Constants.SellerManager.URL_SELLER_GOODS_SHOW);
        RemoteDataHandler.asyncPostDataString(url, map, new RemoteDataHandler.Callback() {
            @Override
            public void dataLoaded(ResponseData data) {
                if(data.getCode() == HttpStatus.SC_OK){
                    Toast.makeText(GoodsManageActivity.this, notice, Toast.LENGTH_SHORT).show();
                    loadGoodsData(true, type);
                } else {
                    ShopHelper.showApiError(MyShopApplication.context, data.getJson());
                }
            }
        });
        goodOpDialog.dismiss();
    }

    private class MyAdapter extends BaseAdapter{

        private List<SellerGoodsLisResultBean.GoodsListBean> data;
        private View.OnClickListener mListener;

        public MyAdapter() {
            data = new ArrayList<SellerGoodsLisResultBean.GoodsListBean>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        public void setListener(View.OnClickListener listener) {
            mListener = listener;
        }

        @Override
        public SellerGoodsLisResultBean.GoodsListBean getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if(convertView == null){
                vh = new ViewHolder();
                convertView = View.inflate(GoodsManageActivity.this, R.layout.item_seller_goods_manage, null);
                vh.tvTime = (TextView) convertView.findViewById(R.id.tv_order_date);
                vh.tvName = (TextView) convertView.findViewById(R.id.tv_goods_name);
                vh.tvNum = (TextView) convertView.findViewById(R.id.tv_goods_storage_sum);
                vh.tvPrice = (TextView) convertView.findViewById(R.id.tv_goods_price);
                vh.btnDel = (Button) convertView.findViewById(R.id.btn_goods_del);
                vh.btnEdit = (Button) convertView.findViewById(R.id.btn_goods_edit);
                vh.iv = (ImageView) convertView.findViewById(R.id.iv_goods_pic);
                vh.rl_btn_tab = (RelativeLayout) convertView.findViewById(R.id.rl_btn_tab);
                vh.tvIsLock = (TextView) convertView.findViewById(R.id.tv_islock);
                vh.btnIsShow = (Button) convertView.findViewById(R.id.btn_goods_isshow);
                if (mListener != null){
                    vh.btnEdit.setOnClickListener(mListener);
                    vh.btnDel.setOnClickListener(mListener);
                    vh.btnIsShow.setOnClickListener(mListener);
                }
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            SellerGoodsLisResultBean.GoodsListBean bean = data.get(position);
            vh.tvPrice.setText("￥" + bean.goods_price);
            vh.tvNum.setText("库存：" + bean.goods_storage_sum);
            vh.tvName.setText(bean.goods_name);
            vh.tvTime.setText(bean.goods_addtime);
            // 商品id存入view
            vh.rl_btn_tab.setTag(bean.goods_commonid);
            vh.btnDel.setTag(bean.goods_name);

            vh.btnIsShow.setTag(position);
            // 商品图片
            Glide.with(GoodsManageActivity.this)
                    .load(bean.goods_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_launcher)  //设置占位图
                    .error(R.drawable.ic_launcher)      //加载错误图
                    .into(vh.iv);

            if(bean.goods_state.equals("0")){ // 仓库 -> 上架
                vh.btnIsShow.setText("上架");
                vh.btnIsShow.setVisibility(View.VISIBLE);
            } else if(bean.goods_state.equals("1")){ // 在售 -> 下架
                vh.btnIsShow.setText("下架");
                vh.btnIsShow.setVisibility(View.VISIBLE);
            } else {
                vh.btnIsShow.setVisibility(View.GONE);
            }
            if(bean.goods_state.equals("10")){ // 锁定
                vh.tvIsLock.setVisibility(View.VISIBLE);
            } else {
                vh.tvIsLock.setVisibility(View.GONE);
            }


            return convertView;
        }

        public void clear() {
            data.clear();
            notifyDataSetChanged();
        }

        public void addData(List<SellerGoodsLisResultBean.GoodsListBean> goods_list) {
            data.addAll(goods_list);
            notifyDataSetChanged();
        }
    }

    private class ViewHolder{
        public TextView tvTime, tvName, tvNum, tvPrice, tvIsLock;
        public Button btnEdit, btnDel, btnIsShow;
        public ImageView iv;
        public RelativeLayout rl_btn_tab;
    }

}
